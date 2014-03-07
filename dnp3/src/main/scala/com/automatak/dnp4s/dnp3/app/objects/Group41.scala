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
package com.automatak.dnp4s.dnp3.app.objects

import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.dsl.{ SerializableToBytes, SInt16LE }

object Group41 extends ObjectGroup {
  def objects = List(Group41Var1, Group41Var2, Group41Var3, Group41Var4)
  def group: Byte = 41
  def typ: ObjectType = Command
}

object Group41Var1 extends FixedSizeGroupVariation(Group41, 1, 5) with CommandGroupVariation

case class Group41Var2(value: SInt16LE, status: Byte) extends SerializableToBytes {
  def toBytes = value.toBytes ::: List(status)
}

object Group41Var2 extends FixedSizeGroupVariation(Group41, 2, 3) with CommandGroupVariation

object Group41Var3 extends FixedSizeGroupVariation(Group41, 3, 5) with CommandGroupVariation

object Group41Var4 extends FixedSizeGroupVariation(Group41, 4, 9) with CommandGroupVariation
