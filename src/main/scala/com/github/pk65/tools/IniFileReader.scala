package com.github.pk65.tools

import scala.annotation.tailrec

object IniFileReader {
  def read(lines: Array[String]): Map[String, Map[String, String]] = {
    parseLines(lines)
    .foldLeft(Map.empty[String, Map[String, String]]) { (acc, list) =>
      acc.updated(list(0), acc.getOrElse(list(0), Map.empty).updated(list(1), list(2)))
    }
  }

  @tailrec
  private def parseLines(lines: Array[String], acc: Array[List[String]] = Array.empty, current: String = ""): Array[List[String]] =
    lines.headOption match {
      case None => acc
      case Some(line) =>
        val nowCurrent = if line.startsWith("[") then line.substring(1, line.indexOf("]")).trim else current
        val parts = if line.isEmpty() then Array("", "") else if line.startsWith("[") then Array("", "") else line.split("=")
        if parts.length < 2 || parts(0).isEmpty
        then parseLines(lines.tail, acc, nowCurrent)
        else parseLines(lines.tail, acc :+ List(nowCurrent, parts(0).trim, parts(1).trim), nowCurrent)
    }

}
