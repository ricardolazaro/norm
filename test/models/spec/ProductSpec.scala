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

      val optionId = Product.create(
        Map(
          "name"        ->  productName,
          "description" ->  productDescription,
          "price"       ->  price.toString,
          "taxiRange"   ->  2l
        )
      )

      val product = Product.find(optionId.get)
      product.id          must equalTo(optionId.get)
      product.name        must equalTo(productName)
      product.description must equalTo(productDescription)
      product.price       must equalTo(price)
    }

  }



}
