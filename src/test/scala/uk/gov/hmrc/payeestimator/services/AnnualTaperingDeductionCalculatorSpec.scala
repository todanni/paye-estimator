package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain._

class AnnualTaperingDeductionCalculatorSpec extends WordSpecLike with Matchers with TaxYearChanges {

  val TaxYear_2017_2018 = new TaxYear_2017_2018(false)
  val TaxYear_2018_2019 = new TaxYear_2018_2019(false)
  val Scottish_TaxYear_2017_2018 = new TaxYear_2017_2018(true)
  val Scottish_TaxYear_2018_2019 = new TaxYear_2018_2019(true)

  import org.scalatest.prop.TableDrivenPropertyChecks._

  val input = Table(
    ("taxCode", "grossPay", "taxCalcResource", "success", "isTapered", "result", "isScottish"),
    ("11L"   , Money(123000), TaxYear_2017_2018         , true, false, "11L"    , false),
    ("1150L" , Money(110000), TaxYear_2017_2018         , true, true , "650.00L", false),
    ("1150L" , Money(123000), TaxYear_2017_2018         , true, true , "ZERO"   , false),
    ("1150L" , Money(100000), TaxYear_2017_2018         , true, false, "1150L"  , false),
    ("11L"   , Money(123000), TaxYear_2018_2019         , true, false, "11L"    , false),
    ("1185L" , Money(110000), TaxYear_2018_2019         , true, true , "685.00L", false),
    ("1185L" , Money(123700), TaxYear_2018_2019         , true, true , "ZERO"   , false),
    ("1185L" , Money(100000), TaxYear_2018_2019         , true, false, "1185L"  , false),
    ("11L"   , Money(123000), Scottish_TaxYear_2017_2018, true, false, "11L"    , true),
    ("1150L" , Money(110000), Scottish_TaxYear_2017_2018, true, true , "650.00L", true),
    ("1150L" , Money(123000), Scottish_TaxYear_2017_2018, true, true , "ZERO"   , true),
    ("1150L" , Money(100000), Scottish_TaxYear_2017_2018, true, false, "1150L"  , true),
    ("11L"   , Money(123000), Scottish_TaxYear_2018_2019, true, false, "11L"    , true),
    ("1185L" , Money(110000), Scottish_TaxYear_2018_2019, true, true , "685.00L", true),
    ("1185L" , Money(123700), Scottish_TaxYear_2018_2019, true, true , "ZERO"   , true),
    ("1185L" , Money(100000), Scottish_TaxYear_2018_2019, true, false, "1185L"  , true)
  )

  forAll(input) {

    (taxCode, grossPay, taxCalcResource, success, isTapered, answer, isScottish) =>

      val append = if(isScottish) " for Scottish Rates" else ""
      s"AnnualTaperingDeductionCalculator calculate(taxCode[$taxCode], grossPay[${grossPay.value}], taxYear[${taxCalcResource.taxYear}]) $append" should {
        s"return success[$success], isTapered[$isTapered], answer[$answer]" in {

          val result = AnnualTaperingDeductionCalculator(taxCode, grossPay, taxCalcResource).calculate()
          result.success shouldBe success
          result.isTapered shouldBe isTapered
          result.result shouldBe answer
        }
      }
  }
}
