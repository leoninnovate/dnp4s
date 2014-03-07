/**
 * Copyright 2013 Automatak, LLC
 *
 * Licensed to Automatak, LLC under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Automatak, LLC licenses this file to you
 * under the GNU Affero General Public License Version 3.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/agpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.automatak.dnp4s.conformance

import procedures.TestFilter

object TestProcedure {

  def repeat[A](list: List[A], count: Int): List[A] = {
    if (count <= 0) Nil
    else (1 to count).foldLeft(List.empty[A])((sum, i) => list ::: sum)
  }

}

trait TestOptions {
  def getByte(id: String): Byte
  def getHexdecimalByte(id: String): Byte
  def getInteger(id: String): Int
  def getShort(id: String): Short
}

case class MapTestOption(map: Map[String, String]) extends TestOptions {

  def getByte(id: String): Byte = map.get(id).map(Integer.parseInt).getOrElse(throw new Exception("Option not defined: " + id)).toByte

  def getHexdecimalByte(id: String): Byte =
    map.get(id).map(Integer.parseInt(_, 16)).getOrElse(throw new Exception("Option not defined: " + id)).toByte

  def getInteger(id: String): Int =
    map.get(id).map(Integer.parseInt).getOrElse(throw new Exception("Option not defined: " + id))

  def getShort(id: String): Short = {
    map.get(id).map(Integer.parseInt).getOrElse(throw new Exception("Option not defined: " + id)).toShort
  }

}

trait TestProcedure {

  def id: String
  def description: String
  def prompts: List[String] = Nil
  def subProcedures: List[TestProcedure] = Nil
  def options: List[TestOption] = Nil
  def steps(options: TestOptions): Iterable[(String, List[TestStep])]

  final def filters: List[TestFilter] = positiveFilters ::: negativeFilters.map(_.negate)

  def positiveFilters: List[TestFilter] = Nil
  def negativeFilters: List[TestFilter] = Nil

}

trait TestSection extends TestProcedure {

  final override def steps(options: TestOptions) = Nil

}

trait TestProcedureRequiringRetries extends TestProcedure {

  override def options = List(IntTestOption("retries", "Number of retries to expect", Some(1), Some(10)))

  def steps(retries: Int): Iterable[(String, List[TestStep])]

  final override def steps(options: TestOptions) = steps(options.getInteger("retries"))
}