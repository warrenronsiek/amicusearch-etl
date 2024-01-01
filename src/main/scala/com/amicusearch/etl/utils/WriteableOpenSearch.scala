package com.amicusearch.etl.utils

trait WriteableOpenSearch {
  def id: Long
  def parent_id: Option[Long]
  def toJSON: String
}
