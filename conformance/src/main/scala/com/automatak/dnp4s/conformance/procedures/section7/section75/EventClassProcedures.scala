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

import com.automatak.dnp4s.dnp3.app.{ TwoByteCountHeader, OneByteCountHeader, GroupVariation }
import com.automatak.dnp4s.conformance.{ LinkSteps, TestOptions, TestProcedure }
import com.automatak.dnp4s.conformance.procedures.{ CommonSteps, AppSteps }
import com.automatak.dnp4s.dnp3.Apdus
import com.automatak.dnp4s.conformance.procedures.section8.EventVerification.{ VerifyNullResponse, VerifyEventDataWithAtLeast1Header, VerifyEventData }
import com.automatak.dnp4s.dnp3.app.objects.{ Group60Var2, EventClassGroupVariation, ClassGroupVariation }
import com.automatak.dnp4s.conformance.procedures.section8.EventLimitProcedure
import com.automatak.dnp4s.dsl.{ UInt16LE, UInt8 }

object EventClassProcedures {

  case class AllObjects(id: String, gv: ClassGroupVariation) extends TestProcedure {

    def description = gv.synonym + " Data, Qualifier 0x06"
    override def options = List(Section75.eventCount1)

    def steps(options: TestOptions) = {

      val numEvents = options.getInteger(Section75.eventCount1.id)

      List(
        "1" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, gv))),
        "3" -> List(VerifyEventData(0)),
        "6" -> List(CommonSteps.PromptForUserAction("Generate " + numEvents + " " + gv.synonym + " events")),
        "7" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, gv))),
        "8" -> List(VerifyEventDataWithAtLeast1Header(0, Some(numEvents), Some(numEvents))),
        "13" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, gv))),
        "14" -> List(VerifyNullResponse(0)))
    }

  }

  case class OneByteCount(id: String, eventClass: EventClassGroupVariation) extends EventLimitProcedure {
    def description = eventClass.synonym + " Data, 0x07"
    def read1Event = Apdus.readHeaders(0, OneByteCountHeader(Group60Var2, UInt8(1), Nil))
    def readLotsOfEvents = Apdus.readHeaders(0, OneByteCountHeader(Group60Var2, UInt8(255), Nil))

  }

  case class TwoByteCount(id: String, eventClass: EventClassGroupVariation) extends EventLimitProcedure {

    def description = eventClass.synonym + " Data, 0x08"
    def read1Event = Apdus.readHeaders(0, TwoByteCountHeader(eventClass, UInt16LE(1), Nil))
    def readLotsOfEvents = Apdus.readHeaders(0, TwoByteCountHeader(eventClass, UInt16LE(255), Nil))

  }

  case class WithoutConfirm(id: String, gv: EventClassGroupVariation) extends TestProcedure {

    def description = gv.synonym + " Data Without Confirm"
    override def options = List(Section75.eventCount1, Section75.eventCount2)

    def steps(options: TestOptions) = {

      val count1 = options.getInteger(Section75.eventCount1.id)
      val count2 = options.getInteger(Section75.eventCount2.id)
      val total = count1 + count2

      List(
        "1" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, gv))),
        "3" -> List(VerifyEventData(0)),
        "4" -> List(CommonSteps.PromptForUserAction("Generate " + count1 + " " + gv.synonym + " events")),
        "5" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, gv))),
        "6" -> List(VerifyEventDataWithAtLeast1Header(0, Some(count1), Some(count1), false)),
        "12" -> List(CommonSteps.Wait(3000)), // TODO: make this configurable
        "13" -> List(LinkSteps.ExpectLpduTimeout),
        "14" -> List(CommonSteps.PromptForUserAction("Generate " + count2 + " additional " + gv.synonym + " events")),
        "15" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, gv))),
        "16" -> List(VerifyEventDataWithAtLeast1Header(0, Some(total), Some(total))),
        "19" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, gv))),
        "20" -> List(VerifyNullResponse(0)))
    }
  }
}
