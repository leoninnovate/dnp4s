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
package com.automatak.dnp4s.conformance.procedures.section9

import com.automatak.dnp4s.conformance._
import com.automatak.dnp4s.conformance.procedures.{ AppSteps }

object Section9 extends TestSection {

  def id = "9"
  def description = "Custom App Tests"
  override def subProcedures = List(Section91)

}

object Section91 extends TestProcedure {

  def id = "9.1"
  def description = "Read during Null Unsol"

  def steps(options: TestOptions): List[(String, List[TestStep])] = {

    List(
      "1" -> List(AppSteps.ReadNullUnsol(None, false)),
      "2" -> List(AppSteps.RequestClass0(0)),
      "3" -> List(LinkSteps.ExpectLpduTimeout),
      "4" -> List(AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0)))
  }

}