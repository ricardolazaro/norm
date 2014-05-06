Norm
=======

Norm is an utility built on top of Anorm to be used in playframework scala projects. It's not a complete ORM with a lot of complex things.

Why
===============

1. Because repeating yourself is boring. For simple cases, when you create 2 or 3 classes(models?) to access the database, you'll end up writting a lot of the same things.
2. Because to learn even a new idiomatic way to write the same old sql is not a great innovation

Should I use it?
================

1. If you're writting a very complex model (joins, inheritance,...) - for now - No
2. If you're not comfortable with unchecked type - No
3. If you want transaction between models - for now - No
4. If you have a lot of models with simple access pattern and don't want to learn a new way to access SQL dbs - yes


Getting Start
-------------

1.  Add Norm to your project:

This project is not published yet, so just copy the file [norm.scala](https://github.com/ricardolazaro/norm/blob/master/app/franeworks/norm/norm.scala) to your project.

2. Create a model class extending Norm:

  ```scala
    import models.frameworks.{NormCompanion, Norm}
    import java.math.BigDecimal

    case class Product(
      id:              Long,
      var name:        String,
      var description: Option[String],
      var price:       BigDecimal,
      var taxRange:    Int,
      var inStock:     Boolean) extends Norm[Product]


    object Product extends NormCompanion[Product]
  ```

3. Inserting a product:

    ```scala
    val optionId = Product.create(
        Map(
          "name"        ->  "productName",
          "description" ->  "productDescription",
          "price"       ->  new BigDecimal("10.00"),
          "taxRange"    ->  2,
          "inStock"     ->  true
        )
      )
    ```

4. Updating a product

    ```scala
    product.name        = newProductName
    product.description = newDescription
    product.price       = newPrice
    product.taxRange    = newTaxRange
    product.inStock     = newInStock
    product.update()
    ```

5. Partial update

    ```scala
    product.update(
      Map(
        "name"        -> newProductName,
        "description" -> newDescription
      )
    )
    ```
    
6. Find a product

  ```scala
  val product = Product.find(2l) // by id 2l
  ```
  
  ```scala
  val productOption = Product.findOption(2l) // by id 2l
  ```
  ```scala
  val products = Product.findByProperty("name", someName)
  ```

More details see the [tests](https://github.com/ricardolazaro/norm/blob/master/test/models/spec/ProductSpec.scala)  
