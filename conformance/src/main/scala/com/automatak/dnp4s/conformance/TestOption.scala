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

import com.automatak.dnp4s.dsl.toHex

trait TestOption {
  def id: String
  def description: String
  def validate(s: String): Option[String]
}

object MinMax {
  def validate[A](value: A, min: Option[A], max: Option[A])(implicit ev: Ordering[A]): Option[String] = {
    if (min.exists(ev.lt(value, _))) Some("Value is less than minimum: " + min)
    else if (max.exists(ev.gt(value, _))) Some("Value is greater than maximum: " + max)
    else None
  }
}

case class IntTestOption(id: String, desc: String, min: Option[Int], max: Option[Int]) extends TestOption {

  final override def description: String = {
    val minString = min.map(m => "min: " + m)
    val maxString = max.map(m => "max: " + m)
    List(Some(desc), minString, maxString).flatten.mkString(" ")
  }

  final override def validate(s: String): Option[String] = {
    try {
      val i = Integer.parseInt(s)
      MinMax.validate(i, min, max)
    } catch {
      case ex: Exception => Some(ex.getMessage)
    }
  }
}

case class ByteSetTestOption(id: String, desc: String, options: Set[Byte]) extends TestOption {

  private def setToString: String = options.map(toHex).toString

  final override def description: String = desc + " from set " + setToString

  final override def validate(s: String): Option[String] = {
    try {
      val i = Integer.parseInt(s, 16)
      if (options.contains(i.toByte)) None
      else Some("Option not in valid set: " + setToString)
    } catch {
      case ex: Exception => Some(ex.getMessage)
    }
  }
}

