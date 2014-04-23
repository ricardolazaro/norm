package models

import models.frameworks.{NormCompanion, Norm}

/**
 * Created by ricardo on 4/22/14.
 */
case class Product(id: Long, name: String, description: Option[String]) extends Norm[Product]

object Product extends NormCompanion[Product]
