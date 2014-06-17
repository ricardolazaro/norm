package norm

import scala.reflect.runtime.universe._
import play.api.db.DB
import anorm._
import play.api.Play.current
import scala.collection.mutable.ListBuffer

/**
 * Created by ricardo on 4/18/14.
 */

/**
 * Utility Methods to retrieve class metadata
 */
private object NormProcessor {

  /**
   * Discover the class properties with its types
   * @tparam T
   *  the class to inspect
   * @return
   *  (PropertyName -> PropertyType)
   **/
  def constructorProperties[T: TypeTag] = synchronized {
    val tpe = typeOf[T]
    val constructor = tpe.declaration(nme.CONSTRUCTOR).asMethod
    constructor.paramss.reduceLeft(_ ++ _).map {
      sym => sym.name.toString -> tpe.member(sym.name).asMethod.returnType
    }
  }

  /**
   * Find the class constructor
   * @tparam T
   *  the class to inspect
   * @return
   *  constructor of T
   */
  private def classConstructorFor[T: TypeTag] = {
    val tpe = typeOf[T]
    val mirror = runtimeMirror(Thread.currentThread().getContextClassLoader)
    val classType = tpe.typeSymbol.asClass
    val cm = mirror.reflectClass(classType)
    val ctor = tpe.declaration(nme.CONSTRUCTOR).asMethod
    cm.reflectConstructor(ctor)
  }

  /**
   * List with values to be applied to the constructor
   * of T
   * @param row
   *  the Anorm row
   * @param tableName
   *  The table Name
   * @tparam T
   *  The class to be applied
   * @return
   *  The value list
   */
  private def propListFrom[T: TypeTag](row: Row, tableName: Option[String]) = {
    val properties = NormProcessor.constructorProperties[T]
    val values = ListBuffer[Any]()
    val rowValuesMap = row.asMap

    val normalizedRowValuesMap = scala.collection.mutable.LinkedHashMap[String, Any]()

    rowValuesMap.toIndexedSeq.foreach[Unit] { (entry) =>
      normalizedRowValuesMap += entry._1.toLowerCase -> rowValuesMap.get(entry._1).get
    }

    val prefix = NormProcessor.tableName[T](tableName).toLowerCase
    properties.foreach { property =>
      normalizedRowValuesMap.get(s"${prefix}.${property._1}".toLowerCase) match {
        case Some(a: Option[Any]) if property._2 <:< typeOf[Option[Any]] => values += a
        case Some(a: Option[Any]) => values += a.get
        case Some(a: Any) => values += a
        case None => throw new RuntimeException
      }
    }
    values
  }


  /**
   * Retrieves a instance of T from database represented by Row
   * @param row
   * @param tableName
   * @tparam T
   * @return
   *  The database as a model of T
   */
  def instance[T: TypeTag](row: Row, tableName: Option[String]) = {
    val ctorm = classConstructorFor[T]
    val seqValues = propListFrom[T](row, tableName).toSeq
    ctorm(seqValues: _*)
  }


  /**
   * Finds the table name of class T
   * @param tableName
   * @tparam T
   * @return
   *
   */
  def tableName[T: TypeTag](tableName: Option[String]) = {
    if (tableName.isEmpty) typeOf[T].typeSymbol.name + "s" else tableName.get
  }

  /**
   * The property representing the database id
   * TODO: get the right property
   */
  val id = "id"

}


class Norm[T: TypeTag](tableName: Option[String] = None) {


  /**
   * Updates a database entry
   *
   * @param attributes atrributes to update, default is empty. if empty, updates all fields
   *                   in the model
   * @return (TODO) number of the affected rows
   */
  def update(attributes: Map[String, ParameterValue[_]] = Map()) = {
    val providedProperties = if (attributes.isEmpty) NormProcessor.constructorProperties[T].map(_._1).toSet else attributes.keys.toSet
    val propertiesToUpdate = (providedProperties diff Set(NormProcessor.id)).toArray
    val defaultAttributes = scala.collection.mutable.Map[String, ParameterValue[_]]()

    val rm  = runtimeMirror(this.getClass.getClassLoader)
    val tpe = typeOf[T]

    val updateContent = ListBuffer[String]()
    propertiesToUpdate.foreach { prop =>
      updateContent += s"${prop}={${prop}}"
      val propTerm = tpe.declaration(newTermName(prop)).asTerm
      defaultAttributes += prop -> (if (attributes.isEmpty) rm.reflect(this).reflectField(propTerm).get else attributes.get(prop).get)
    }

    val idTerm = tpe.declaration(newTermName(NormProcessor.id)).asTerm
    defaultAttributes += NormProcessor.id -> rm.reflect(this).reflectField(idTerm).get

    val updateBuilder = new StringBuilder(s"update ${NormProcessor.tableName[T](tableName)}")
    updateBuilder.append(" set ")
    updateBuilder.append(updateContent.mkString(","))
    updateBuilder.append(s" where ${NormProcessor.id}={${NormProcessor.id}}")
    val forUpdate = updateBuilder.mkString

    DB.withConnection { implicit c =>
      SQL(forUpdate).on(defaultAttributes.toSeq: _*).executeUpdate()
    }
  }

  //TODO
  //  def refresh

  //  def delete

  // ...

}


/**
 * class to be extended in the companion objects.
 * This class adds some common database methods such as create,
 * find, etc...
 *
 * @param tableName
 *  the database table name - defaults is the pluralization of the class name
 * @tparam T
 *  model class to be represented
 */
class NormCompanion[T: TypeTag](tableName: Option[String] = None) {

  /**
   * Creates a new database entry
   * @param attributes
   *   map containing the values to be added to the new database entry
   * @return
   *   the do for the new database entry
   */
  def create(attributes: Map[String, ParameterValue[_]]): Option[Long] = {
    val properties = ((attributes.keySet diff Set(NormProcessor.id))).toArray
    val values     = properties.seq.map(p => s"{${p}}")

    val creationBuilder = new StringBuilder(s"insert into ${NormProcessor.tableName[T](tableName)}")
    creationBuilder.append(s"(${properties.mkString(",")})")
    creationBuilder.append(" values ")
    creationBuilder.append(s"(${values.mkString(",")})")
    val forCreation = creationBuilder.toString

    DB.withConnection { implicit c =>
      SQL(forCreation).on(attributes.toSeq: _*).executeInsert()
    }
  }

  /**
   * Finds a database entry having the provided property value
   * @param propertyName
   *  the name of the property
   * @param propertyValue
   *  the name of the property
   * @return a list with the matched entries
   */
  def findByProperty(propertyName: String, propertyValue: Any): List[T] = DB.withConnection { implicit c =>
    val forSelect = s"select * from ${NormProcessor.tableName[T](tableName)} where ${propertyName} = {${propertyName}}"

    val query = SQL(forSelect).on(s"$propertyName" -> propertyValue)
    val result = query().collect {
      case r:Row => NormProcessor.instance[T](r, tableName).asInstanceOf[T]
    }
    result.toList
  }

  /**
   * Perform a action for each entry
   * @param f
   * @return
   */
  def foreach(f: T => Unit) = DB.withConnection { implicit c =>
    val forSelect = s"select * from ${NormProcessor.tableName[T](tableName)}"
    SQL(forSelect).apply().foreach {
      case r:Row => f(NormProcessor.instance[T](r, tableName).asInstanceOf[T])
    }
  }

  def find(id: Long) = findByProperty(NormProcessor.id, id).head

  def findOption(id: Long) = findByProperty(NormProcessor.id, id).headOption

}
