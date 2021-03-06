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

object Group34 extends ObjectGroup {
  def objects = List(Group34Var1, Group34Var2, Group34Var3)
  def group: Byte = 34
  def typ: ObjectType = Static
}

object Group34Var1 extends FixedSizeGroupVariation(Group34, 1, 2) with StaticGroupVariation
object Group34Var2 extends FixedSizeGroupVariation(Group34, 2, 4) with StaticGroupVariation
object Group34Var3 extends FixedSizeGroupVariation(Group34, 3, 4) with StaticGroupVariation
