package com.amicusearch.etl.datatypes

case class CasetextEnriched(document_type: String
                            , document: Option[String]
                            , title: Option[String]
                            , court: Option[String]
                            , date: Option[String]
                            , citation: Option[String]
                            , url: String)