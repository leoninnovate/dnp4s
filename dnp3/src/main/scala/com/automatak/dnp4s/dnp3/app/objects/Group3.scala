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

object Group3 extends ObjectGroup {
  def objects = List(Group3Var0, Group3Var1, Group3Var2)
  def group: Byte = 3
  def typ: ObjectType = Static
}

object Group3Var0 extends SizelessGroupVariation(Group3, 0) with StaticGroupVariation

object Group3Var1 extends BasicGroupVariation(Group3, 1) with StaticGroupVariation {
  override def size(count: Int): Option[Int] = {
    val bits = count * 2
    val div = bits / 8
    val mod = bits % 8
    if (mod == 0) Some(div)
    else Some(div + 1)
  }

  override def size = None //cannot be used with index headers
}

object Group3Var2 extends FixedSizeGroupVariation(Group3, 2, 1) with StaticGroupVariation