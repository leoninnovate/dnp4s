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

import com.automatak.dnp4s.dsl.SerializableToBytes
import com.automatak.dnp4s.dnp3.app._

object Group1 extends ObjectGroup {
  def objects = List(Group1Var0, Group1Var2, Group1Var1)
  def group: Byte = 1
  def typ: ObjectType = Static
}

object Group1Var0 extends SizelessGroupVariation(Group1, 0) with StaticGroupVariation

object Group1Var1 extends BasicGroupVariation(Group1, 1) with SingleBitfield with StaticGroupVariation

object Group1Var2 extends FixedSizeGroupVariation(Group1, 2, 1) with StaticGroupVariation

case class Group1Var2(flags: Byte) extends SerializableToBytes {
  override def toBytes: List[Byte] = List(flags)
}
