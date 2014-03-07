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

object Group22 extends ObjectGroup {
  def objects = List(Group22Var1, Group22Var2, Group22Var3, Group22Var4, Group22Var5, Group22Var6, Group22Var7, Group22Var8)
  def group: Byte = 22
  def typ: ObjectType = Event
}

object Group22Var1 extends FixedSizeGroupVariation(Group22, 1, 5) with EventGroupVariation
object Group22Var2 extends FixedSizeGroupVariation(Group22, 2, 3) with EventGroupVariation
object Group22Var3 extends FixedSizeGroupVariation(Group22, 3, 5) with EventGroupVariation
object Group22Var4 extends FixedSizeGroupVariation(Group22, 4, 3) with EventGroupVariation
object Group22Var5 extends FixedSizeGroupVariation(Group22, 5, 11) with EventGroupVariation
object Group22Var6 extends FixedSizeGroupVariation(Group22, 6, 9) with EventGroupVariation
object Group22Var7 extends FixedSizeGroupVariation(Group22, 7, 11) with EventGroupVariation
object Group22Var8 extends FixedSizeGroupVariation(Group22, 8, 9) with EventGroupVariation