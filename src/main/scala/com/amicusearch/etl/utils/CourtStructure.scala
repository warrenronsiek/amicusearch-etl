package com.amicusearch.etl.utils

import com.amicusearch.etl.utils.CourtMetadata.{federalDistrictToAppeal, pre1891Circuit}
import com.typesafe.scalalogging.LazyLogging

import scala.util.Try

object CourtStructure extends LazyLogging{
  private val ordinalMap: Map[String, Int] = Map(
    "first" -> 1,
    "second" -> 2,
    "third" -> 3,
    "fourth" -> 4,
    "fifth" -> 5,
    "sixth" -> 6,
    "seventh" -> 7,
    "eighth" -> 8,
    "ninth" -> 9,
    "tenth" -> 10,
    "eleventh" -> 11,
    "twelfth" -> 12,
    "thirteenth" -> 13,
    "fourteenth" -> 14,
    "fifteenth" -> 15,
    "sixteenth" -> 16,
    "seventeenth" -> 17,
    "eighteenth" -> 18,
    "nineteenth" -> 19,
    "twentieth" -> 20,
    "twenty-first" -> 21,
    "twenty-second" -> 22,
    "twenty-third" -> 23,
    "twenty-fourth" -> 24,
    "twenty-fifth" -> 25,
    "twenty-sixth" -> 26,
    "twenty-seventh" -> 27,
    "twenty-eighth" -> 28,
    "twenty-ninth" -> 29,
    "thirtieth" -> 30,
  )
  private val stateAbbreviations: Map[String, String] = Map(
    "alabama" -> "al",
    "alaska" -> "ak",
    "arizona" -> "az",
    "arkansas" -> "ar",
    "california" -> "ca",
    "colorado" -> "co",
    "connecticut" -> "ct",
    "delaware" -> "de",
    "florida" -> "fl",
    "georgia" -> "ga",
    "hawaii" -> "hi",
    "idaho" -> "id",
    "illinois" -> "il",
    "indiana" -> "in",
    "iowa" -> "ia",
    "kansas" -> "ks",
    "kentucky" -> "ky",
    "louisiana" -> "la",
    "maine" -> "me",
    "maryland" -> "md",
    "massachusetts" -> "ma",
    "michigan" -> "mi",
    "minnesota" -> "mn",
    "mississippi" -> "ms",
    "missouri" -> "mo",
    "montana" -> "mt",
    "nebraska" -> "ne",
    "nevada" -> "nv",
    "new_hampshire" -> "nh",
    "new_jersey" -> "nj",
    "new_mexico" -> "nm",
    "new_york" -> "ny",
    "north_carolina" -> "nc",
    "north_dakota" -> "nd",
    "ohio" -> "oh",
    "oklahoma" -> "ok",
    "oregon" -> "or",
    "pennsylvania" -> "pa",
    "rhode_island" -> "ri",
    "south_carolina" -> "sc",
    "south_dakota" -> "sd",
    "tennessee" -> "tn",
    "texas" -> "tx",
    "utah" -> "ut",
    "vermont" -> "vt",
    "virginia" -> "va",
    "washington" -> "wa",
    "west_virginia" -> "wv",
    "wisconsin" -> "wi",
    "wyoming" -> "wy",
    "virgin_islands" -> "vi",
    "puerto_rico" -> "pr",
    "northern_mariana_islands" -> "mp",
    "guam" -> "gu",
    "district_of_columbia" -> "dc",
    "district_columbia" -> "dc",
  )
  private val locations: List[String] = List(
    "alabama",
    "alaska",
    "arizona",
    "arkansas",
    "california",
    "colorado",
    "connecticut",
    "delaware",
    "florida",
    "georgia",
    "hawaii",
    "idaho",
    "illinois",
    "indiana",
    "iowa",
    "kansas",
    "kentucky",
    "louisiana",
    "maine",
    "maryland",
    "massachusetts",
    "michigan",
    "minnesota",
    "mississippi",
    "missouri",
    "montana",
    "nebraska",
    "nevada",
    "new_hampshire",
    "new_jersey",
    "new_mexico",
    "new_york",
    "north_carolina",
    "north_dakota",
    "ohio",
    "oklahoma",
    "oregon",
    "pennsylvania",
    "rhode_island",
    "south_carolina",
    "south_dakota",
    "tennessee",
    "texas",
    "utah",
    "vermont",
    "west_virginia",
    // important that this comes before virginia in the list. otherwise, virginia will be matched first
    "virginia",
    "washington",
    "wisconsin",
    "wyoming",
    "the_northern_mariana_islands",
    "northern_mariana_islands",
    "guam",
    "virgin_islands",
    "puerto_rico"
  )
  private val militaryCourts = List("Marine", "Navy", "Army", "Air", "Coast")

  def getState(courtName: String): Option[String] = {
    val state = locations.find(courtName.toLowerCase.replace(" ", "_").contains)
    state match {
      case Some(state) => Some(stateAbbreviations(state))
      case None => None
    }
  }

  def getNumber(courtName: String): Option[Int] = {
    val ordinalNumber: Option[Int] = "\\s(\\w+(:?st|nd|rd|th))\\s+".r.findAllIn(courtName.toLowerCase).map(_.trim)
      .collectFirst({
        case ordinal if ordinalMap.contains(ordinal) => ordinalMap(ordinal)
      })
    lazy val normalNumber: Option[Int] = "(\\d+)".r.findFirstIn(courtName).map(_.toInt)
    ordinalNumber.orElse(normalNumber)
  }

  def getCardinalOrientation(courtName: String): Option[String] = {
    "((:?[NSEWM]\\. ?)D\\.?)".r.findFirstIn(courtName).map(_.toLowerCase.replace(".", "")
      .replace(" ", ""))
  }

  def getCourtLtree(courtName:String): Option[String] = {
    lazy val location: Option[String] = getState(courtName)
    lazy val number: Option[Int] = getNumber(courtName)

    courtName match {
      case _ if "[Aa]ppeals?".r.findFirstIn(courtName).isDefined && location.isEmpty && number.isDefined =>
        Some(s"federal_supreme.federal_appeal_${number.get}")
      case _ if "[Aa]ppeals?".r.findFirstIn(courtName).isDefined && location.isDefined && number.isDefined =>
        Some(s"federal_supreme.${location.get}_supreme.${location.get}_appeal_${number.get}")
      case _ if "[Aa]ppeals?".r.findFirstIn(courtName).isDefined && location.isDefined && number.isEmpty =>
        Some(s"federal_supreme.${location.get}_supreme.${location.get}_appeal")
      case _ if courtName.contains("United States District Court for the District") && location.isDefined =>
        // this is probably not correct - this is a depreciated district type and probably had a different appeals court name
        Some(s"federal_supreme.federal_depreciated_appeals_${location.get}.federal_deprecated_district_${location.get}")
      case "U.S. Circuit Court for the District of District of Columbia" =>
        Some("federal_supreme.federal_pre1981_circuit_dc")
      case _ if courtName.contains("Circuit Court for the District") && location.isDefined =>
        Some(s"federal_supreme.federal_appeals_${location.get}")
      case _ if courtName.contains("U.S. Circuit Court for") && location.isDefined =>
        Some(s"federal_supreme.federal_pre1981_circuit_${pre1891Circuit(location.get)}")
      case _ if "[Ss]upreme".r.findFirstIn(courtName).isDefined && location.isDefined =>
        Some(s"federal_supreme.${location.get}_supreme")
      case _ if "[Ss]upreme".r.findFirstIn(courtName).isDefined && location.isEmpty =>
        Some(s"federal_supreme")
      case _ if "District Court, ([A-Z\\.]+)".r.findFirstIn(courtName).isDefined && !courtName.contains("District of Columbia") && !courtName.contains("Virgin Islands") => {
        val subDistrict = "District Court, ([A-Z\\.]+)".r.findFirstMatchIn(courtName).get.group(1).toLowerCase.replace(".", "")
        val name = s"federal_district_${location.get}_$subDistrict"
        federalDistrictToAppeal.get(name) match {
          case Some(appeal) =>
            Some(s"federal_supreme.$appeal.$name")
          case None =>
            logger.warn(s"Could not find appeal for court $courtName, subtree: $name")
            Some(s"federal_supreme.federal_appeal.$name")
        }
      }
      case _ if "Bankruptcy Court, ([A-Z.]+)".r.findFirstIn(courtName).isDefined && location.isDefined =>
        val subDistrict = "Bankruptcy Court, ([A-Z.]+)".r.findFirstMatchIn(courtName).get.group(1).toLowerCase.replace(".", "")
        val name = s"federal_bankruptcy_${location.get}_$subDistrict"
        federalDistrictToAppeal.get(name.replace("bankruptcy", "district")) match {
          case Some(appeal) =>
            val appealCtNumber = getNumber(appeal).get
            Some(s"federal_supreme.federal_appeal_$appealCtNumber.federal_district_${location.get}.$name")
          case None =>
            logger.warn(s"Could not find appeal for $courtName, subtree: $name")
            Some(s"federal_supreme.federal_appeal.$name")
        }
      case _ if courtName.contains("Bankruptcy Appellate Panel") && number.isDefined =>
        Some(s"federal_supreme.federal_appeal_${number.get}.federal_bankruptcy_appeal_panel_${number.get}")
      case _ if courtName.contains("Court of Appeals for the Armed Forces") =>
        Some("federal_supreme.executive_military_appeal")
      case _ if militaryCourts.exists(courtName.contains) =>
        val courtType = militaryCourts.find(courtName.contains).get.toLowerCase
        Some(s"federal_supreme.executive_military_appeal.executive_military_appeal_$courtType")
      case "United States Court of Claims" =>
        Some("federal_supreme.federal_circuit.federal_deprecated")
      case "United States Court of Federal Claims" =>
        Some("federal_supreme.federal_circuit.federal_claims")
      case "District Court, Canal Zone" => Some("federal_supreme.federal_appeal.federal_district_canal_zone")
      case _ if courtName.contains("County") && number.isDefined && courtName.contains("Circuit")=>
        // TODO: may not be strictly accurate to have "state_appeal" here, but most district courts should have persuasive authority over circuits, so this is probably fine
        Some(s"federal_supreme.${location.get}_supreme.${location.get}_appeal.${location.get}_circuit_${number.get}")
      case _ if courtName.contains("Appeals") && courtName.contains("D.C. Circuit")=>
        Some("federal_supreme.federal_appeal_district_columbia")
      case _ if courtName.contains("District of Columbia") && courtName.contains("District Court")=>
        Some(s"federal_supreme.federal_appeal_district_columbia.federal_district_columbia")
      case _  if courtName.contains("Virgin Islands") =>
        Some("federal_supreme.federal_appeal.federal_virgin_islands")
      case "United States Judicial Panel on Multidistrict Litigation" =>
        Some("federal_supreme.federal_circuit.federal_multidistrict_litigation")
      case "United States Court of International Trade" =>
        Some("federal_supreme.federal_circuit.federal_international_trade")
      case "United States Customs Court" =>
        Some("federal_supreme.federal_circuit.federal_customs")
      case _ if courtName.startsWith("Circuit Court for the Judicial Circuits") =>
        Some(s"federal_supreme.${location.get}_supreme.${location.get}_appeal.${location.get}_circuit")
      case _ if courtName.contains("Division of Administrative Hearings") =>
        Some(s"federal_supreme.${location.get}_supreme.${location.get}_appeal.${location.get}_administrative")
      case "United States Court of Military Appeals" =>
        Some("federal_supreme.executive_military_appeal")
      case "Court of Appeals for the Federal Circuit" =>
        Some("federal_supreme.federal_circuit")
      case _ if courtName.contains("Superior") && getCardinalOrientation(courtName).isDefined && location.isDefined =>
        val cardinal = getCardinalOrientation(courtName).get
        val canonicalName = s"federal_district_${location.get}_$cardinal"
        federalDistrictToAppeal.get(canonicalName) match {
          case Some(appeal) =>
            val appealCtNumber = getNumber(appeal).get
            Some(s"federal_supreme.federal_appeal_$appealCtNumber.$canonicalName")
          case None =>
            logger.warn(s"Could not find appeal court for canonical name: $canonicalName, raw name: $courtName")
            Some(s"federal_supreme.federal_appeal.$canonicalName")
        }
      case _ if "United States Circuit Court (:?for the)? (:?Northern|Southern|Eastern|Western)? District".r.findFirstIn(courtName).isDefined && location.isDefined =>
        Some(s"federal_supreme.federal_pre1981_circuit_${pre1891Circuit(location.get)}")
      case "United States Tax Court" =>
        Some("federal_supreme.federal_circuit.federal_tax")
      case _ =>
        logger.warn(s"Could not find ltree for court: $courtName")
        None
    }
  }
}
