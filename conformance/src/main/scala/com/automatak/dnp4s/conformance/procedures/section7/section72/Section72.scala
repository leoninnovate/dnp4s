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
package com.automatak.dnp4s.conformance.procedures.section7.section72

import com.automatak.dnp4s.conformance.TestSection
import com.automatak.dnp4s.dnp3.app.objects.Group12
import section721.Section721
import section722.Section722
import section723.Section723
import section724.Section724
import section725.Section725

object Section72 extends TestSection {

  def id = "7.2"
  def description = "Binary Output Controls"
  override def subProcedures = List(Section721, Section722, Section723, Section724, Section725)

  def validCrobCodes = Group12.FuncCodes.descriptions.map(d => d.byte).toSet
}
