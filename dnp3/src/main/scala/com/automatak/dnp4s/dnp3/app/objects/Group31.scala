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

object Group31 extends ObjectGroup {
  def objects = Nil
  def group: Byte = 31
  def typ: ObjectType = Static
}

object Group31Var1 extends FixedSizeGroupVariation(Group31, 1, 5) with StaticGroupVariation
object Group31Var2 extends FixedSizeGroupVariation(Group31, 2, 3) with StaticGroupVariation
object Group31Var3 extends FixedSizeGroupVariation(Group31, 3, 11) with StaticGroupVariation
object Group31Var4 extends FixedSizeGroupVariation(Group31, 4, 9) with StaticGroupVariation
object Group31Var5 extends FixedSizeGroupVariation(Group31, 5, 4) with StaticGroupVariation
object Group31Var6 extends FixedSizeGroupVariation(Group31, 6, 2) with StaticGroupVariation
object Group31Var7 extends FixedSizeGroupVariation(Group31, 7, 5) with StaticGroupVariation
object Group31Var8 extends FixedSizeGroupVariation(Group31, 8, 9) with StaticGroupVariation
