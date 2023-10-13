package com.amicusearch.etl.utils

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

class CourtStructureTest extends AnyFlatSpec with Matchers with TableDrivenPropertyChecks {

  private val courtNumbers = Table(
    ("court", "number"),
    ("Supreme Court of the United States", None),
    ("Supreme Court of Florida", None),
    ("District Court of Appeal of Florida", None),
    ("United States District Court for the District of Oklahoma", None),
    ("Court of Appeals for the Tenth Circuit", Some(10)),
    ("District Court, S.D. Florida", None),
    ("United States Bankruptcy Court, M.D. Florida", None),
    ("Court of Appeals for the Fourth Circuit", Some(4)),
    ("Court of Appeals for the Ninth Circuit", Some(9)),
    ("District Court, E.D. Pennsylvania", None),
    ("Court of Appeals for the Second Circuit", Some(2)),
    ("Court of Appeals for the Fifth Circuit", Some(5)),
    ("Bankruptcy Appellate Panel of the Second Circuit", Some(2)),
    ("United States Bankruptcy Court, N.D. West Virginia", None),
    ("Navy-Marine Corps Court of Criminal Appeals", None),
    ("Court of Appeals for the Armed Forces", None),
    ("United States Bankruptcy Court, D. Connecticut", None),
    ("U S Air Force Court of Military Review", None),
    ("U.S. Army Court of Military Review", None),
    ("United States Court of Claims", None),
    ("United States Court of Federal Claims", None),
    ("Circuit Court of the 20th Judicial Circuit of Florida, Lee County", Some(20)),
    ("United States Judicial Panel on Multidistrict Litigation", None),
    ("United States Court of International Trade", None),
    ("District Court, District of Columbia", None),
    ("District Court, Virgin Islands", None),
    ("United States Customs Court", None),
    ("Court of Appeals for the D.C. Circuit", None)
  )

  "CourtStructure" should "parse court numbers" in {
    forAll(courtNumbers) { (court, number) =>
      CourtStructure.getNumber(court) should be(number)
    }
  }

  private val courtStates = Table(
    ("court", "state"),
    ("Supreme Court of the United States", None),
    ("Supreme Court of Florida", Some("fl")),
    ("District Court of Appeal of Florida", Some("fl")),
    ("United States District Court for the District of Oklahoma", Some("ok")),
    ("Court of Appeals for the Tenth Circuit", None),
    ("District Court, S.D. Florida", Some("fl")),
    ("United States Bankruptcy Court, M.D. Florida", Some("fl")),
    ("Court of Appeals for the Fourth Circuit", None),
    ("Court of Appeals for the Ninth Circuit", None),
    ("District Court, E.D. Pennsylvania", Some("pa")),
    ("Court of Appeals for the Second Circuit", None),
    ("Court of Appeals for the Fifth Circuit", None),
    ("Bankruptcy Appellate Panel of the Second Circuit", None),
    ("United States Bankruptcy Court, N.D. West Virginia", Some("wv")),
    ("Navy-Marine Corps Court of Criminal Appeals", None),
    ("Court of Appeals for the Armed Forces", None),
    ("United States Bankruptcy Court, D. Connecticut", Some("ct")),
    ("U S Air Force Court of Military Review", None),
    ("U.S. Army Court of Military Review", None),
    ("United States Court of Claims", None),
    ("United States Court of Federal Claims", None),
    ("Circuit Court of the 20th Judicial Circuit of Florida, Lee County", Some("fl")),
    ("United States Judicial Panel on Multidistrict Litigation", None),
    ("United States Court of International Trade", None),
    ("District Court, District of Columbia", None),
    ("District Court, Virgin Islands", Some("vi")),
    ("United States Customs Court", None),
    ("Court of Appeals for the D.C. Circuit", None)
  )

  it should "parse court states" in {
    forAll(courtStates) { (court, state) =>
      CourtStructure.getRegion(court) should be(state)
    }
  }

  private val courtCardinalOrientation = Table(
    ("court", "cardinal orientation"),
    ("Superior Court, S. D. Florida", Some("sd")),
    ("District Court, N.D. Florida", Some("nd")),
    ("Court of Appeals for the Ninth Circuit", None),
    ("United States Bankruptcy Court, E.D. West Virginia", Some("ed")),
    ("United States Bankruptcy Court, M.D. Florida", Some("md"))
  )

  it should "parse court cardinal orientation" in {
    forAll(courtCardinalOrientation) { (court, cardinalOrientation) =>
      CourtStructure.getCardinalOrientation(court) should be(cardinalOrientation)
    }
  }

  private val courtTrees = Table(
    ("court", "ltree"),
    ("Supreme Court of the United States", "federal_supreme"),
    ("Supreme Court of Florida", "federal_supreme.fl_supreme"),
    ("District Court of Appeal of Florida", "federal_supreme.fl_supreme.fl_appeal"),
    ("United States District Court for the District of Oklahoma",
      "federal_supreme.federal_depreciated_appeals_ok.federal_deprecated_district_ok"),
    ("Court of Appeals for the Tenth Circuit", "federal_supreme.federal_appeal_10"),
    ("District Court, S.D. Florida", "federal_supreme.federal_appeal_11.federal_district_fl_sd"),
    ("United States Bankruptcy Court, M.D. Florida",
      "federal_supreme.federal_appeal_11.federal_district_fl.federal_bankruptcy_fl_md"),
    ("Court of Appeals for the Fourth Circuit", "federal_supreme.federal_appeal_4"),
    ("Court of Appeals for the Ninth Circuit", "federal_supreme.federal_appeal_9"),
    ("District Court, E.D. Pennsylvania", "federal_supreme.federal_appeal_3.federal_district_pa_ed"),
    ("Court of Appeals for the Second Circuit", "federal_supreme.federal_appeal_2"),
    ("Court of Appeals for the Fifth Circuit", "federal_supreme.federal_appeal_5"),
    ("Bankruptcy Appellate Panel of the Second Circuit",
      "federal_supreme.federal_appeal_2.federal_bankruptcy_appeal_panel_2"),
    ("United States Bankruptcy Court, N.D. West Virginia",
      "federal_supreme.federal_appeal_4.federal_district_wv.federal_bankruptcy_wv_nd"),
    ("Navy-Marine Corps Court of Criminal Appeals",
      "federal_supreme.executive_military_appeal.executive_military_appeal_marine"),
    ("Court of Appeals for the Armed Forces", "federal_supreme.executive_military_appeal"),
    ("United States Bankruptcy Court, D. Connecticut",
      "federal_supreme.federal_appeal_2.federal_district_ct.federal_bankruptcy_ct_d"),
    ("U S Air Force Court of Military Review",
      "federal_supreme.executive_military_appeal.executive_military_appeal_air"),
    ("U.S. Army Court of Military Review", "federal_supreme.executive_military_appeal.executive_military_appeal_army"),
    ("United States Court of Claims", "federal_supreme.federal_circuit.federal_deprecated"),
    ("United States Court of Federal Claims", "federal_supreme.federal_circuit.federal_claims"),
    ("Circuit Court of the 20th Judicial Circuit of Florida, Lee County",
      "federal_supreme.fl_supreme.fl_appeal.fl_circuit_20"),
    ("United States Judicial Panel on Multidistrict Litigation",
      "federal_supreme.federal_circuit.federal_multidistrict_litigation"),
    ("United States Court of International Trade", "federal_supreme.federal_circuit.federal_international_trade"),
    ("District Court, District of Columbia",
      "federal_supreme.federal_appeal_district_columbia.federal_district_columbia"),
    ("District Court, Virgin Islands", "federal_supreme.federal_appeal.federal_virgin_islands"),
    ("United States Customs Court", "federal_supreme.federal_circuit.federal_customs"),
    ("Court of Appeals for the D.C. Circuit", "federal_supreme.federal_appeal_district_columbia"),
    ("United States Court of Military Appeals", "federal_supreme.executive_military_appeal"),
    ("Court of Appeals for the Federal Circuit", "federal_supreme.federal_circuit"),
    ("United States Circuit Court for the Northern District of Florida", "federal_supreme.federal_pre1981_circuit_5"),
    ("U.S. Circuit Court for New York", "federal_supreme.federal_pre1981_circuit_2"),
    ("Superior Court, S. D. Florida", "federal_supreme.federal_appeal_11.federal_district_fl_sd"),
    ("Court of Appeals of Arizona", "federal_supreme.az_supreme.az_appeal"),
    ("District Court, E.D. South Carolina", "federal_supreme.federal_appeal_4.federal_district_sc_ed"))
  it should "parse court trees" in {
    forAll(courtTrees) { (court, ltree) =>
      CourtStructure.getCourtLtree(court) should be(Some(ltree))
    }
  }

}
