package models

import models.frameworks.{NormCompanion, Norm}
import java.math.BigDecimal

/**
 * Created by ricardo on 4/22/14.
 */
case class Product(
  id:          Long,
  name:        String,
  description: Option[String],
  price:       BigDecimal,
  taxiRange:   Int) extends Norm[Product]


object Product extends NormCompanion[Product]
