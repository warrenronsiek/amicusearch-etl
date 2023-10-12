package com.amicusearch.etl.utils

object CourtMetadata {

  val federalAppealToDistrict: Map[String, Any] = Map(
    "federal_appeal_1" -> List(
      "district_of_maine",
      "district_of_massachusetts",
      "district_of_new_hampshire",
      "district_of_puerto_rico",
      "district_of_rhode_island"
    ),
    "federal_appeal_2" -> List(
      "district_of_connecticut",
      "eastern_district_of_new_york",
      "northern_district_of_new_york",
      "southern_district_of_new_york",
      "western_district_of_new_york",
      "district_of_vermont"
    ),
    "federal_appeal_3" -> List(
      "district_of_delaware",
      "district_of_new_jersey",
      "eastern_district_of_pennsylvania",
      "middle_district_of_pennsylvania",
      "western_district_of_pennsylvania",
      "district_of_the_virgin_islands"
    ),
    "federal_appeal_4" -> List(
      "district_of_maryland",
      "eastern_district_of_north_carolina",
      "middle_district_of_north_carolina",
      "western_district_of_north_carolina",
      "district_of_south_carolina",
      "eastern_district_of_virginia",
      "western_district_of_virginia",
      "northern_district_of_west_virginia",
      "southern_district_of_west_virginia"
    ),
    "federal_appeal_5" -> List(
      "eastern_district_of_louisiana",
      "middle_district_of_louisiana",
      "western_district_of_louisiana",
      "northern_district_of_mississippi",
      "southern_district_of_mississippi",
      "eastern_district_of_texas",
      "northern_district_of_texas",
      "southern_district_of_texas",
      "western_district_of_texas"
    ),
    "federal_appeal_6" -> List(
      "eastern_district_of_kentucky",
      "western_district_of_kentucky",
      "eastern_district_of_michigan",
      "western_district_of_michigan",
      "northern_district_of_ohio",
      "southern_district_of_ohio",
      "eastern_district_of_tennessee",
      "middle_district_of_tennessee",
      "western_district_of_tennessee"
    ),
    "federal_appeal_7" -> List(
      "central_district_of_illinois",
      "northern_district_of_illinois",
      "southern_district_of_illinois",
      "northern_district_of_indiana",
      "southern_district_of_indiana",
      "eastern_district_of_wisconsin",
      "western_district_of_wisconsin"
    ),
    "federal_appeal_8" -> List(
      "eastern_district_of_arkansas",
      "western_district_of_arkansas",
      "northern_district_of_iowa",
      "southern_district_of_iowa",
      "district_of_minnesota",
      "eastern_district_of_missouri",
      "western_district_of_missouri",
      "district_of_nebraska",
      "district_of_north_dakota",
      "district_of_south_dakota"
    ),
    "federal_appeal_9" -> List(
      "district_of_alaska",
      "district_of_arizona",
      "central_district_of_california",
      "eastern_district_of_california",
      "northern_district_of_california",
      "southern_district_of_california",
      "district_of_guam",
      "district_of_hawaii",
      "district_of_idaho",
      "district_of_montana",
      "district_of_nevada",
      "district_of_the_northern_mariana_islands",
      "district_of_oregon",
      "eastern_district_of_washington",
      "western_district_of_washington"
    ),
    "federal_appeal_10" -> List(
      "district_of_colorado",
      "district_of_kansas",
      "district_of_new_mexico",
      "eastern_district_of_oklahoma",
      "northern_district_of_oklahoma",
      "western_district_of_oklahoma",
      "district_of_utah",
      "district_of_wyoming"
    ),
    "federal_appeal_11" -> List(
      "middle_district_of_alabama",
      "northern_district_of_alabama",
      "southern_district_of_alabama",
      "middle_district_of_florida",
      "northern_district_of_florida",
      "southern_district_of_florida",
      "middle_district_of_georgia",
      "northern_district_of_georgia",
      "southern_district_of_georgia"
    ),
    "federal_appeal_dc" -> List(
      "district_of_columbia"
    ),
    "federal_appeal_fed" -> Map(
      "courts" -> List(
        "court_of_appeals_for_veterans_claims",
        "court_of_federal_claims",
        "court_of_international_trade"
      ),
      "administrative_agencies" -> List(
        "armed_services_board_of_contract_appeals",
        "bureau_of_justice_assistance",
        "civilian_board_of_contract_appeals",
        "international_trade_commission",
        "merit_systems_protection_board",
        "office_of_congressional_workplace_rights",
        "patent_trial_and_appeal_board",
        "personnel_appeals_board",
        "trademark_trial_and_appeal_board"
      )
    )
  )

  val federalDistrictToAppeal: Map[String, String] = Map(
    "federal_district_me_d" -> "federal_appeal_1",
    "federal_district_ma_d" -> "federal_appeal_1",
    "federal_district_nh_d" -> "federal_appeal_1",
    "federal_district_pr_d" -> "federal_appeal_1",
    "federal_district_ri_d" -> "federal_appeal_1",

    "federal_district_ct_d" -> "federal_appeal_2",
    "federal_district_ny_ed" -> "federal_appeal_2",
    "federal_district_ny_nd" -> "federal_appeal_2",
    "federal_district_ny_sd" -> "federal_appeal_2",
    "federal_district_ny_wd" -> "federal_appeal_2",
    "federal_district_ny_d" -> "federal_appeal_2", // this is not right prior to 1891 evarts act
    "federal_district_vt_d" -> "federal_appeal_2",

    "federal_district_de_d" -> "federal_appeal_3",
    "federal_district_nj_d" -> "federal_appeal_3",
    "federal_district_pa_ed" -> "federal_appeal_3",
    "federal_district_pa_md" -> "federal_appeal_3",
    "federal_district_pa_wd" -> "federal_appeal_3",
    "federal_district_pa_d" -> "federal_appeal_3", // this is not right prior to 1891 evarts act
    "federal_district_vi_d" -> "federal_appeal_3",

    "federal_district_md_d" -> "federal_appeal_4",
    "federal_district_nc_ed" -> "federal_appeal_4",
    "federal_district_nc_md" -> "federal_appeal_4",
    "federal_district_nc_wd" -> "federal_appeal_4",
    "federal_district_nc_d" -> "federal_appeal_4", // this is not right prior to 1891 evarts act
    "federal_district_sc_d" -> "federal_appeal_4",
    "federal_district_sc_ed" -> "federal_appeal_4", // this is not right prior to 1891 evarts act
    "federal_district_sc_wd" -> "federal_appeal_4", // this is not right prior to 1891 evarts act
    "federal_district_va_ed" -> "federal_appeal_4",
    "federal_district_va_wd" -> "federal_appeal_4",
    "federal_district_va_d" -> "federal_appeal_4", // this is not right prior to 1891 evarts act
    "federal_district_wv_nd" -> "federal_appeal_4",
    "federal_district_wv_sd" -> "federal_appeal_4",
    "federal_district_wv_d" -> "federal_appeal_4", // this is not right prior to 1891 evarts act

    "federal_district_la_ed" -> "federal_appeal_5",
    "federal_district_la_md" -> "federal_appeal_5",
    "federal_district_la_wd" -> "federal_appeal_5",
    "federal_district_la_d" -> "federal_appeal_5", // this is not right prior to 1891 evarts act
    "federal_district_ms_nd" -> "federal_appeal_5",
    "federal_district_ms_sd" -> "federal_appeal_5",
    "federal_district_ms_d" -> "federal_appeal_5", // this is not right prior to 1891 evarts act
    "federal_district_tx_ed" -> "federal_appeal_5",
    "federal_district_tx_nd" -> "federal_appeal_5",
    "federal_district_tx_sd" -> "federal_appeal_5",
    "federal_district_tx_wd" -> "federal_appeal_5",
    "federal_district_tx_d" -> "federal_appeal_5", // this is not right prior to 1891 evarts act

    "federal_district_ky_ed" -> "federal_appeal_6",
    "federal_district_ky_wd" -> "federal_appeal_6",
    "federal_district_ky_d" -> "federal_appeal_6", // this is not right prior to 1891 evarts act
    "federal_district_mi_ed" -> "federal_appeal_6",
    "federal_district_mi_wd" -> "federal_appeal_6",
    "federal_district_mi_d" -> "federal_appeal_6", // this is not right prior to 1891 evarts act
    "federal_district_oh_nd" -> "federal_appeal_6",
    "federal_district_oh_sd" -> "federal_appeal_6",
    "federal_district_oh_d" -> "federal_appeal_6", // this is not right prior to 1891 evarts act

    "federal_district_tn_ed" -> "federal_appeal_6",
    "federal_district_tn_md" -> "federal_appeal_6",
    "federal_district_tn_wd" -> "federal_appeal_6",
    "federal_district_tn_d" -> "federal_appeal_6", // this is not right prior to 1891 evarts act

    "federal_district_il_cd" -> "federal_appeal_7",
    "federal_district_il_nd" -> "federal_appeal_7",
    "federal_district_il_sd" -> "federal_appeal_7",
    "federal_district_il_d" -> "federal_appeal_7", // this is not right prior to 1891 evarts act
    "federal_district_in_nd" -> "federal_appeal_7",
    "federal_district_in_sd" -> "federal_appeal_7",
    "federal_district_in_d" -> "federal_appeal_7", // this is not right prior to 1891 evarts act
    "federal_district_wi_ed" -> "federal_appeal_7",
    "federal_district_wi_wd" -> "federal_appeal_7",
    "federal_district_wi_d" -> "federal_appeal_7", // this is not right prior to 1891 evarts act

    "federal_district_ar_ed" -> "federal_appeal_8",
    "federal_district_ar_wd" -> "federal_appeal_8",
    "federal_district_ar_d" -> "federal_appeal_8", // this is not right prior to 1891 evarts act
    "federal_district_ia_nd" -> "federal_appeal_8",
    "federal_district_ia_sd" -> "federal_appeal_8",
    "federal_district_ia_d" -> "federal_appeal_8", // this is not right prior to 1891 evarts act
    "federal_district_mn_d" -> "federal_appeal_8",
    "federal_district_mo_ed" -> "federal_appeal_8",
    "federal_district_mo_wd" -> "federal_appeal_8",
    "federal_district_mo_d" -> "federal_appeal_8", // this is not right prior to 1891 evarts act
    "federal_district_ne_d" -> "federal_appeal_8",
    "federal_district_nd_d" -> "federal_appeal_8",
    "federal_district_sd_d" -> "federal_appeal_8",

    "federal_district_ak_d" -> "federal_appeal_9",
    "federal_district_az_d" -> "federal_appeal_9",
    "federal_district_ca_cd" -> "federal_appeal_9",
    "federal_district_ca_ed" -> "federal_appeal_9",
    "federal_district_ca_nd" -> "federal_appeal_9",
    "federal_district_ca_sd" -> "federal_appeal_9",
    "federal_district_ca_d" -> "federal_appeal_9", // this is not right prior to 1891 evarts act
    "federal_district_gu_d" -> "federal_appeal_9",
    "federal_district_hi_d" -> "federal_appeal_9",
    "federal_district_id_d" -> "federal_appeal_9",
    "federal_district_mt_d" -> "federal_appeal_9",
    "federal_district_nv_d" -> "federal_appeal_9",
    "federal_district_mp_d" -> "federal_appeal_9",
    "federal_district_or_d" -> "federal_appeal_9",
    "federal_district_wa_ed" -> "federal_appeal_9",
    "federal_district_wa_wd" -> "federal_appeal_9",
    "federal_district_wa_d" -> "federal_appeal_9", // this is not right prior to 1891 evarts act

    "federal_district_co_d" -> "federal_appeal_10",
    "federal_district_ks_d" -> "federal_appeal_10",
    "federal_district_nm_d" -> "federal_appeal_10",
    "federal_district_ok_ed" -> "federal_appeal_10",
    "federal_district_ok_nd" -> "federal_appeal_10",
    "federal_district_ok_wd" -> "federal_appeal_10",
    "federal_district_ok_d" -> "federal_appeal_10", // this is not right prior to 1891 evarts act
    "federal_district_ut_d" -> "federal_appeal_10",
    "federal_district_wy_d" -> "federal_appeal_10",

    "federal_district_al_md" -> "federal_appeal_11",
    "federal_district_al_nd" -> "federal_appeal_11",
    "federal_district_al_sd" -> "federal_appeal_11",
    "federal_district_al_d" -> "federal_appeal_11",
    "federal_district_fl_md" -> "federal_appeal_11",
    "federal_district_fl_nd" -> "federal_appeal_11",
    "federal_district_fl_sd" -> "federal_appeal_11",
    "federal_district_fl_d" -> "federal_appeal_11", // this is not right prior to 1891 evarts act
    "federal_district_ga_md" -> "federal_appeal_11",
    "federal_district_ga_nd" -> "federal_appeal_11",
    "federal_district_ga_sd" -> "federal_appeal_11",
    "federal_district_ga_d" -> "federal_appeal_11", // this is not right prior to 1891 evarts act

    "federal_district_dc_d" -> "federal_appeal_dc",
  )

  val pre1891Circuit: Map[String, Int] = Map(
    "nh" -> 1,
    "ma" -> 1,
    "ri" -> 1,
    "ct" -> 2,
    "ny" -> 2,
    "vt" -> 2,
    "nj" -> 3,
    "pa" -> 3,
    "de" -> 4, // Later moved to 3rd Circuit.
    "md" -> 4,
    "va" -> 4,
    "nc" -> 4,
    "sc" -> 4, // Moved between 4th and 5th over time.
    "ga" -> 5,
    "al" -> 5,
    "ms" -> 5,
    "la" -> 5,
    "tx" -> 5,
    "fl" -> 5,
    "tn" -> 6,
    "ky" -> 6,
    "oh" -> 6,
    "mi" -> 6,
    "in" -> 7,
    "il" -> 7,
    "wi" -> 7,
    "mn" -> 8,
    "ia" -> 8,
    "mo" -> 8,
    "ar" -> 8,
    "ne" -> 8,
    "ks" -> 8,
    "ca" -> 9,
    "or" -> 9,
    "nv" -> 9,
    "wa" -> 9,
    "id" -> 9,
    "mt" -> 9,
    "co" -> 8, // Although part of the westward expansion, it was part of the 8th for a time.
    "dakota territory" -> 8, // Before becoming the states of North and South Dakota.
    "wy" -> 9,
    "ut" -> 9,
    "az" -> 9, // Before statehood, it was still considered part of this circuit.
    "nm" -> 9, // Similarly, before statehood.
  )

}
