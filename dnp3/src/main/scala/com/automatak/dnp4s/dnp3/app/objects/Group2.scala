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

object Group2 extends ObjectGroup {
  val objects = List(Group2Var0, Group2Var1, Group2Var2, Group2Var3)
  def group: Byte = 2
}

object Group2Var0 extends SizelessGroupVariation(Group2, 0) with EventGroupVariation
object Group2Var1 extends FixedSizeGroupVariation(Group2, 1, 1) with EventGroupVariation
object Group2Var2 extends FixedSizeGroupVariation(Group2, 2, 7) with EventGroupVariation
object Group2Var3 extends FixedSizeGroupVariation(Group2, 3, 3) with CTOEventGroupVariation
