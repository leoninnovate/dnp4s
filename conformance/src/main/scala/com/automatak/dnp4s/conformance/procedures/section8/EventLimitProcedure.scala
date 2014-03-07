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
package com.automatak.dnp4s.conformance.procedures.section8

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }
import com.automatak.dnp4s.conformance.procedures.{ CommonSteps, AppSteps }
import com.automatak.dnp4s.dnp3.Apdus
import com.automatak.dnp4s.conformance.procedures.section8.EventVerification.{ VerifyNullResponse, VerifyEventDataWithAtLeast1Header, VerifyEventData }
import com.automatak.dnp4s.dnp3.app.{ GroupVariation, Apdu }
import com.automatak.dnp4s.conformance.procedures.section7.section75.Section75
import com.automatak.dnp4s.dnp3.app.objects.EventClassGroupVariation

trait EventLimitProcedure extends TestProcedure {

  final override def prompts = Nil
  final override def subProcedures = Nil
  final override def options = List(Section75.eventCount1)

  def generateEventPrompt(count: Int) = "Generate " + count + " " + eventClass.synonym + " events"
  def eventClass: EventClassGroupVariation

  def read1Event: Apdu
  def readLotsOfEvents: Apdu

  def steps(options: TestOptions) = {

    val count = options.getInteger(Section75.eventCount1.id)

    List(
      "1" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, eventClass))),
      "3" -> List(VerifyEventData(0)),
      "4" -> List(CommonSteps.PromptForUserAction(generateEventPrompt(count))),
      "5" -> List(AppSteps.SendApdu(read1Event)),
      "8" -> List(VerifyEventDataWithAtLeast1Header(0, Some(1), Some(1))),
      "11" -> List(AppSteps.SendApdu(readLotsOfEvents)),
      "12" -> List(VerifyEventDataWithAtLeast1Header(0, Some(count - 1), Some(count - 1))),
      "14" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, eventClass))),
      "15" -> List(VerifyNullResponse(0)))
  }
}
