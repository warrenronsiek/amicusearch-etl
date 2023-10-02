package com.amicusearch.etl.datatypes

case class CasetextCase(document_type: String
                        , document: String
                        , title: Option[String]
                        , court: Option[String]
                        , date: String
                        , citation: String
                        , url: String)
