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
package com.automatak.dnp4s.dsl

/**
 * Copyright 2011 John Adam Crain (jadamcrain@gmail.com)
 *
 * This file is the sole property of the copyright owner and is NOT
 * licensed to any 3rd parties.
 */

/*
trait TypedField[A] extends Stream[A] {

  val defaults: List[A]

  def cases: List[A] = defaults

  def foreach[U](fun: A => U) = cases.foreach(fun)
}

class Field[A](input: List[A]) extends TypedField[A] {

  def this(default: A) = this(List(default))
  def this(default: A*) = this(default.toList)

  val defaults = input.distinct
}

case object BoolField extends Field[Boolean](List(true, false))
*/

object Fields {
  val boolean = List(true, false)
}