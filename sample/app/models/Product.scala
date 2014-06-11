package models

import java.math.BigDecimal
import norm.{NormCompanion, Norm}


/**
 * Created by ricardo on 4/22/14.
 */
case class Product(
  id:              Long,
  var name:        String,
  var description: Option[String],
  var price:       BigDecimal,

  var taxRange:    Int,
  var inStock:     Boolean) extends Norm[Product]


object Product extends NormCompanion[Product]
