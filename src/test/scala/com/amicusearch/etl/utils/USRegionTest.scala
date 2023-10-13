package com.amicusearch.etl.utils

import org.scalatest.flatspec.AnyFlatSpec

class USRegionTest extends AnyFlatSpec {

  "USRegion" should "parse state names" in {
    assert(USRegion.fromString("North Carolina").contains(USRegion.north_carolina))
  }

  it should "parse state abbreviations" in {
    assert(USRegion.fromString("NC").contains(USRegion.north_carolina))
  }

  it should "parse state names with periods" in {
    assert(USRegion.fromString("N.C.").contains(USRegion.north_carolina))
  }

  it should "parse state names with periods and spaces" in {
    assert(USRegion.fromString("N. C.").contains(USRegion.north_carolina))
  }

  it should "parse west virginia" in {
    assert(USRegion.fromString("West Virginia").contains(USRegion.west_virginia))
  }

  it should "parse virginia" in {
    assert(USRegion.fromString("Virginia").contains(USRegion.virginia))
  }

  it should "extract location from arbitrary string" in {
    assert(USRegion.fromString("Supreme Court of North Carolina").contains(USRegion.north_carolina))
  }

}
