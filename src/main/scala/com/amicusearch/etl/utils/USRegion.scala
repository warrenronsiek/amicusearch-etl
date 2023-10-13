package com.amicusearch.etl.utils

object USRegion extends Enumeration {
  val alabama, alaska, arizona, arkansas, california, colorado, connecticut, delaware, florida, georgia, hawaii,
  idaho, illinois, indiana, iowa, kansas, kentucky, louisiana, maine, maryland, massachusetts, michigan, minnesota,
  mississippi, missouri, montana, nebraska, nevada, new_hampshire, new_jersey, new_mexico, new_york, north_carolina,
  north_dakota, ohio, oklahoma, oregon, pennsylvania, rhode_island, south_carolina, south_dakota, tennessee, texas,
  utah, vermont, west_virginia,
  // important that west_virginia comes before virginia in the list. otherwise, virginia will be matched first
  virginia, washington, wisconsin, wyoming, the_northern_mariana_islands, district_of_columbia, district_columbia,
  northern_mariana_islands, guam, virgin_islands, puerto_rico = Value

  private val enumToAbbreviation = Map(
    alabama -> "AL",
    alaska -> "AK",
    arizona -> "AZ",
    arkansas -> "AR",
    california -> "CA",
    colorado -> "CO",
    connecticut -> "CT",
    delaware -> "DE",
    florida -> "FL",
    georgia -> "GA",
    hawaii -> "HI",
    idaho -> "ID",
    illinois -> "IL",
    indiana -> "IN",
    iowa -> "IA",
    kansas -> "KS",
    kentucky -> "KY",
    louisiana -> "LA",
    maine -> "ME",
    maryland -> "MD",
    massachusetts -> "MA",
    michigan -> "MI",
    minnesota -> "MN",
    mississippi -> "MS",
    missouri -> "MO",
    montana -> "MT",
    nebraska -> "NE",
    nevada -> "NV",
    new_hampshire -> "NH",
    new_jersey -> "NJ",
    new_mexico -> "NM",
    new_york -> "NY",
    north_carolina -> "NC",
    north_dakota -> "ND",
    ohio -> "OH",
    oklahoma -> "OK",
    oregon -> "OR",
    pennsylvania -> "PA",
    rhode_island -> "RI",
    south_carolina -> "SC",
    south_dakota -> "SD",
    tennessee -> "TN",
    texas -> "TX",
    utah -> "UT",
    vermont -> "VT",
    west_virginia -> "WV",
    virginia -> "VA",
    washington -> "WA",
    wisconsin -> "WI",
    wyoming -> "WY",
    the_northern_mariana_islands -> "MP",
    northern_mariana_islands -> "MP",
    guam -> "GU",
    virgin_islands -> "VI",
    puerto_rico -> "PR",
    district_of_columbia -> "DC",
    district_columbia -> "DC",
  )

  private val abbreviationToEnum = enumToAbbreviation.map(_.swap)

  def fromString(str: String): Option[USRegion.Value] =
    values.find(_.toString == str.toLowerCase) orElse abbreviationToEnum.get(str.toUpperCase)

  def toAbbreviation(region: USRegion.Value): String = enumToAbbreviation(region)
  def toStringList: List[String] = values.toList.map(_.toString)
}