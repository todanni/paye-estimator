/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain._

import scala.math.BigDecimal

class NICTaxCalculatorServiceSpec extends WordSpecLike with Matchers with TaxYearChanges {

  val TaxYear_2017_2018 = new TaxYear_2017_2018(false)
  val TaxYear_2018_2019 = new TaxYear_2018_2019(false)

  "NICTaxCalculatorService.calculateEmployeeNIC " should {
    "should calculate the annual rate for 2017/8" in new LiveNICTaxCalcServiceSuccess {
      val result = service.calculateEmployeeNIC(Money(100000.00), TaxYear_2017_2018).aggregation
      result.size shouldBe 2
      result.map{
        aggregation => aggregation.percentage.intValue() match {
          case 12 => aggregation.amount shouldBe BigDecimal(4420.32)
          case 2 => aggregation.amount shouldBe BigDecimal(1100.00)
        }
      }
    }

    "should calculate the annual rate for 2018/9" in new LiveNICTaxCalcServiceSuccess {
      val result = service.calculateEmployeeNIC(Money(119000.00), TaxYear_2018_2019).aggregation
      result.size shouldBe 2
      result.map{
        aggregation => aggregation.percentage.intValue() match {
          case 12 => aggregation.amount shouldBe BigDecimal(4551.12)
          case 2 => aggregation.amount shouldBe BigDecimal(1453.00)
        }
      }
    }
  }

  "NICTaxCalculatorService.calculateEmployerNIC " should {
    "should calculate the annual rate for 2017/8" in new LiveNICTaxCalcServiceSuccess {
      val result = service.calculateEmployerNIC(Money(100000.00), TaxYear_2017_2018)
      result.size shouldBe 1
      result.map{
        aggregation =>
          aggregation.percentage shouldBe 13.8
          aggregation.amount shouldBe BigDecimal(12673.37)
      }
    }

    "should calculate the annual rate for 2018/9" in new LiveNICTaxCalcServiceSuccess {
      val result = service.calculateEmployerNIC(Money(119000.00), TaxYear_2018_2019)
      result.size shouldBe 1
      result.map{
        aggregation =>
          aggregation.percentage shouldBe 13.8
          aggregation.amount shouldBe BigDecimal(15259.49)
      }
    }
  }
}
