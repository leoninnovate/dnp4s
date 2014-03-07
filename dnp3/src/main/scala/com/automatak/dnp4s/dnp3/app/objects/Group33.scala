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

object Group33 extends ObjectGroup {
  def objects = List(Group33Var1, Group33Var2, Group33Var3, Group33Var4, Group33Var5, Group33Var6, Group33Var7, Group33Var8)
  def group: Byte = 33
}

object Group33Var1 extends FixedSizeGroupVariation(Group33, 1, 5) with EventGroupVariation
object Group33Var2 extends FixedSizeGroupVariation(Group33, 2, 3) with EventGroupVariation
object Group33Var3 extends FixedSizeGroupVariation(Group33, 3, 11) with EventGroupVariation
object Group33Var4 extends FixedSizeGroupVariation(Group33, 4, 9) with EventGroupVariation
object Group33Var5 extends FixedSizeGroupVariation(Group33, 5, 5) with EventGroupVariation
object Group33Var6 extends FixedSizeGroupVariation(Group33, 6, 9) with EventGroupVariation
object Group33Var7 extends FixedSizeGroupVariation(Group33, 7, 11) with EventGroupVariation
object Group33Var8 extends FixedSizeGroupVariation(Group33, 8, 15) with EventGroupVariation
