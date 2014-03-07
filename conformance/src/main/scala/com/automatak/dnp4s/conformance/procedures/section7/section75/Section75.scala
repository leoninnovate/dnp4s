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
package com.automatak.dnp4s.conformance.procedures.section7.section75

import com.automatak.dnp4s.conformance._
import com.automatak.dnp4s.dnp3.app.GroupVariation
import com.automatak.dnp4s.dnp3.app.objects._
import com.automatak.dnp4s.conformance.IntTestOption
import section751.Section751
import section752.Section752
import com.automatak.dnp4s.conformance.IntTestOption
import scala.Some

object Section75 extends TestSection {

  def id = "7.5"
  def description = "Class Data"
  override def subProcedures = List(Section751, Section752)

  val eventCount1 = IntTestOption("events.generateCount", "Number of events to expect in response", Some(2), Some(5))
  val eventCount2 = IntTestOption("events.generateCount2", "Number of additional events to generate", Some(2), Some(5))

  private def eventSubProcedures(section: String, gv: EventClassGroupVariation) = List(
    EventClassProcedures.AllObjects(section + ".1", gv),
    EventClassProcedures.OneByteCount(section + ".2", gv),
    EventClassProcedures.TwoByteCount(section + ".3", gv),
    EventClassProcedures.WithoutConfirm(section + ".4", gv))

  private val section852 = EventTestSection("8.5.2", "Class1", eventSubProcedures("8.5.2.2", Group60Var2))
  private val section853 = EventTestSection("8.5.3", "Class2", eventSubProcedures("8.5.3.2", Group60Var3))
  private val section854 = EventTestSection("8.5.4", "Class3", eventSubProcedures("8.5.4.2", Group60Var4))

  private case class EventTestSection(id: String, description: String, override val subProcedures: List[TestProcedure]) extends TestSection

}

