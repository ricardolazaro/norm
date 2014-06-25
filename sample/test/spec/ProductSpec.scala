package models.spec

import org.specs2.mutable.Specification
import play.api.test.{FakeApplication, WithApplication}
import java.math.BigDecimal
import play.api.db.DB
import anorm._
import play.api.test.FakeApplication
import models.Product
import scala.Product

/**
 * Created by ricardo on 4/22/14.
 */
class ProductSpec extends Specification {

  "Product.create" should {

    "create a valid product with name and description" in new WithApplication(FakeApplication()) {
      val productName        = "ProductName"
      val productDescription = Some("Text")
      val price              = new BigDecimal("10.00")
      val taxRange           = 2

//      val optionId = Product.create(
//        Map(
//          "name"        ->  productName,
//          "description" ->  productDescription,
//          "price"       ->  price,
//          "taxRange"    ->  taxRange,
//          "inStock"     ->  true
//        )
//      )

      val optionId = Product.create2(
        Seq(
          "name"        ->  productName,
          "description" ->  productDescription,
          "price"       ->  price,
          "taxRange"    ->  taxRange,
          "inStock"     ->  true
        )
      )


      val product = Product.find(optionId.get)
      product.id          must equalTo(optionId.get)
      product.name        must equalTo(productName)
      product.description must equalTo(productDescription)
      product.price       must equalTo(price)
      product.taxRange    must equalTo(taxRange)
      product.inStock     must beTrue



//      DB.withConnection { implicit connection =>
//
//        val unsafe: Seq[NamedParameter] = Seq("name" -> "name", "description" -> Some("text"),"price" -> new BigDecimal("10.00"), "taxRange" -> 3, "inStock" -> true)
//
//        SQL("""insert into Products(name, description, price, taxRange, inStock)
//          values ({name}, {description}, {price}, {taxRange}, {inStock})""")
//          .on(values:_*).executeInsert()
//      }


    }

//    def values: Seq[NamedParameter] = {
//      Seq[NamedParameter]("name" -> "name", "description" -> Some("text"),"price" -> new BigDecimal("10.00"), "taxRange" -> 3, "inStock" -> true)
//    }

    "create a valid product with option properties equals to None" in new WithApplication(FakeApplication()) {
      val productName        = "ProductName"
      val price              = new BigDecimal("11.00")
      val taxRange           = 3

      val optionId = Product.create2(
        Seq(
          "name"     ->  productName,
          "price"    ->  price,
          "taxRange" ->  taxRange,
          "inStock"  ->  true
        )
      )

      val product = Product.find(optionId.get)
      product.id          must equalTo(optionId.get)
      product.name        must equalTo(productName)
      product.description must beNone
      product.price       must equalTo(price)
      product.taxRange    must equalTo(taxRange)
      product.inStock     must beTrue

    }
  }

  "Product.update" should {

//    "update a database entry" in new WithApplication(FakeApplication()) {
//
//      val productName    = "ProductName"
//      val price          = new BigDecimal("11.00")
//      val taxRange       = 3
//      val description    = Some("description")
//      val inStock        = false
//
//      val newProductName = "NewProductName"
//      val newPrice       = new BigDecimal("12.00")
//      val newTaxRange    = 4
//      val newDescription = Some("NewDescription")
//      val newInStock     = true
//
//      val optionId = Product.create2(
//        Seq(
//          "name"        ->  productName,
//          "price"       ->  price,
//          "taxRange"    ->  taxRange,
//          "description" ->  taxRange,
//          "inStock"     ->  inStock
//        )
//      )
//
//      val product = Product.find(optionId.get)
//      product.name        = newProductName
//      product.description = newDescription
//      product.price       = newPrice
//      product.taxRange    = newTaxRange
//      product.inStock     = newInStock
//      product.update2()
//
//      val updatedProduct = Product.find(optionId.get)
//      updatedProduct.name        must equalTo(newProductName)
//      updatedProduct.description must equalTo(newDescription)
//      updatedProduct.price       must equalTo(newPrice)
//      updatedProduct.taxRange    must equalTo(newTaxRange)
//      updatedProduct.inStock     must equalTo(newInStock)
//
//    }


    "update partially a database entry" in new WithApplication(FakeApplication()) {

      val productName    = "ProductName"
      val price          = new BigDecimal("11.00")
      val taxRange       = 3
      val description    = Some("description")
      val inStock        = false

      val newProductName = "NewProductName"
      val newDescription = Some("NewDescription")

      val optionId = Product.create2(
        Seq(
          "name"        ->  productName,
          "price"       ->  price,
          "taxRange"    ->  taxRange,
          "description" ->  taxRange,
          "inStock"     ->  inStock
        )
      )

      val product = Product.find(optionId.get)
      product.name        = newProductName
      product.description = newDescription
      product.price       = new BigDecimal("17.00")
      product.taxRange    = 20
      product.inStock     = true

      product.update2(
        Seq(
          "name"        -> newProductName,
          "description" -> newDescription
        )
      )

      // updates only the specified fields
      val updatedProduct = Product.find(optionId.get)
      updatedProduct.name        must equalTo(newProductName)
      updatedProduct.description must equalTo(newDescription)

      // this properties should not be updated
      updatedProduct.price       must equalTo(price)
      updatedProduct.taxRange    must equalTo(taxRange)
      updatedProduct.inStock     must equalTo(inStock)
    }
  }

  "Product.foreach" should {

    "perform action in all entries" in new WithApplication(FakeApplication()) {

      val productName1    = "ProductName1"
      val price1          = new BigDecimal("11.00")
      val taxRange1       = 3
      val description1    = Some("description")
      val inStock1        = false

      val productName2    = "ProductName2"
      val price2          = new BigDecimal("11.00")
      val taxRange2       = 3
      val description2    = Some("description")
      val inStock2        = false

      val optionId1 = Product.create2(
        Seq(
          "name"        ->  productName1,
          "price"       ->  price1,
          "taxRange"    ->  taxRange1,
          "description" ->  taxRange1,
          "inStock"     ->  inStock1
        )
      )
      val optionId2 = Product.create2(
        Seq(
          "name"        ->  productName2,
          "price"       ->  price2,
          "taxRange"    ->  taxRange2,
          "description" ->  taxRange2,
          "inStock"     ->  inStock2
        )
      )

      Product.foreach { p =>
        p.name = "nameUpdated"
        p.update2(
          Seq(
            "name" -> "nameUpdated"
          )
        )
      }

      Product.find(optionId1.get).name must equalTo("nameUpdated")
      Product.find(optionId2.get).name must equalTo("nameUpdated")
    }
  }


}
