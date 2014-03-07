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

object Group30 extends ObjectGroup {
  def objects = List(Group30Var1, Group30Var2, Group30Var3, Group30Var4, Group30Var5, Group30Var6)
  def group: Byte = 30
  def typ: ObjectType = Static
}

object Group30Var1 extends FixedSizeGroupVariation(Group30, 1, 5) with StaticGroupVariation
object Group30Var2 extends FixedSizeGroupVariation(Group30, 2, 3) with StaticGroupVariation
object Group30Var3 extends FixedSizeGroupVariation(Group30, 3, 4) with StaticGroupVariation
object Group30Var4 extends FixedSizeGroupVariation(Group30, 4, 2) with StaticGroupVariation
object Group30Var5 extends FixedSizeGroupVariation(Group30, 5, 5) with StaticGroupVariation
object Group30Var6 extends FixedSizeGroupVariation(Group30, 6, 9) with StaticGroupVariation
