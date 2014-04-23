package models.spec

import org.specs2.mutable.Specification
import play.api.test.{FakeApplication, WithApplication}
import models.Product

/**
 * Created by ricardo on 4/22/14.
 */
class ProductSpec extends Specification {

  "Product.create" should {

    "create a valid product with name and description" in new WithApplication(FakeApplication()) {

      val productName        = "ProductName"
      val productDescription = Some("Text")

      val id = Product.create(
        Map(
          "name"        ->  "ProductName",
          "description" ->  Some("Text")
        )
      )

      val product = Product.findByProperty("id", id.get).head
      product.id          must equalTo(id.get)
      product.name        must equalTo(productName)
      product.description must equalTo(productDescription)
    }

  }



}
