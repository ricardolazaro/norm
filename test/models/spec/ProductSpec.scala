package models.spec

import org.specs2.mutable.Specification
import play.api.test.{FakeApplication, WithApplication}
import models.Product
import java.math.BigDecimal

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

      val optionId = Product.create(
        Map(
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
    }

    "create a valid product with option properties equals to None" in new WithApplication(FakeApplication()) {
      val productName        = "ProductName"
      val price              = new BigDecimal("11.00")
      val taxRange           = 3

      val optionId = Product.create(
        Map(
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

    "update a database entry" in new WithApplication(FakeApplication()) {

      val productName    = "ProductName"
      val price          = new BigDecimal("11.00")
      val taxRange       = 3
      val description    = Some("description")
      val inStock        = false

      val newProductName = "NewProductName"
      val newPrice       = new BigDecimal("12.00")
      val newTaxRange    = 4
      val newDescription = Some("NewDescription")
      val newInStock     = true

      val optionId = Product.create(
        Map(
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
      product.price       = newPrice
      product.taxRange    = newTaxRange
      product.inStock     = newInStock
      product.update()

      val updatedProduct = Product.find(optionId.get)
      updatedProduct.name        must equalTo(newProductName)
      updatedProduct.description must equalTo(newDescription)
      updatedProduct.price       must equalTo(newPrice)
      updatedProduct.taxRange    must equalTo(newTaxRange)
      updatedProduct.inStock     must equalTo(newInStock)

    }


    "update partially a database entry" in new WithApplication(FakeApplication()) {

      val productName    = "ProductName"
      val price          = new BigDecimal("11.00")
      val taxRange       = 3
      val description    = Some("description")
      val inStock        = false

      val newProductName = "NewProductName"
      val newDescription = Some("NewDescription")

      val optionId = Product.create(
        Map(
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

      product.update(
        Map(
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


}
