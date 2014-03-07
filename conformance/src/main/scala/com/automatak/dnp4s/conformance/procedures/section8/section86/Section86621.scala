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
package com.automatak.dnp4s.conformance.procedures.section8.section86

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.conformance.procedures._
import com.automatak.dnp4s.conformance.procedures.AppSteps.SingleFragment
import com.automatak.dnp4s.dnp3.Apdus
import objects.{ Group60Var3, Group60Var4, Group60Var2 }
import com.automatak.dnp4s.conformance.procedures.section8.EventVerification
import com.automatak.dnp4s.conformance.procedures.section8.EventVerification.EventDataOnly
import com.automatak.dnp4s.dsl.UInt8
import com.automatak.dnp4s.dnp3.app.OneByteCountHeader
import scala.Some

object Section86621 extends TestProcedure {

  def id = "8.6.6.2.1"
  def description = "Buffer Overflow, Binary Input Change Event Buffers"
  override def prompts = List("If the device does not support Binary Input Change Events, end testing of Buffer Overflow, Binary Input Change Event Buffers")

  case class ReadEventsNoConfirm(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with EventVerification.EventDataOnly with AllowIIN {
    override def eventMax = None
    override def eventMin = None
    override def autoConfirm = false
    override def allowedMask = IIN(IIN.field(IIN.IIN14_NEED_TIME, IIN.IIN17_DEVICE_RESTART), 0)
  }

  def readAllEvents(seq: Byte) = AppSteps.SendApdu(Apdus.readAllObjects(seq, Group60Var2, Group60Var3, Group60Var4))
  def readClass1(seq: Byte) = AppSteps.SendApdu(Apdus.readAllObjects(seq, Group60Var2))
  def readClass1WithLimit(seq: Byte, limit: Short) = {
    val header = OneByteCountHeader(Group60Var2, UInt8(limit), Nil)
    val apdu = Apdu(AppCtrl(true, true, false, false, seq), AppFunctions.read, None, List(header))
    AppSteps.SendApdu(apdu)
  }

  trait ReadSingleEvent extends AppSteps.ReadAnyValidResponseUnconfirmed with SingleFragment with EventDataOnly {
    final override def eventMax: Option[Int] = Some(1)
    final override def eventMin: Option[Int] = Some(1)
  }

  case class ReadSingleEventWithBufferOverflowSet(seq: Byte) extends ReadSingleEvent with RequireIIN {
    override def requiredMasks = List(IIN(0, IIN.IIN23_EVENT_BUFFER_OVERFLOW))
  }

  case class ReadSingleEventWithNoOverflow(seq: Byte) extends ReadSingleEvent with ProhibitIIN {
    override def prohibitedMask = IIN(0, IIN.IIN23_EVENT_BUFFER_OVERFLOW)
  }

  private val generatePrompt = List(
    "Obtain the maximum Binary Input Change Event buffer size from the device documentation",
    "Generate this number of corresponding Class1 events on the DUT",
    "If this number is configurable, it may be easiest to reduce it to a small number, i.e. 3, for the purposes of testing")

  def steps(options: TestOptions) = List(
    "2" -> List(
      readAllEvents(0),
      AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0)),
    "3" -> List(CommonSteps.PromptForUserAction(generatePrompt)),
    "4" -> List(readClass1(0)),
    "5" -> List(ReadEventsNoConfirm(0)),
    "6" -> List(CommonSteps.PromptForUserAction(List("Generate 1 additional Binary Input Change event"))),
    "7" -> List(readClass1WithLimit(0, 1)),
    "8" -> List(ReadSingleEventWithBufferOverflowSet(0)),
    "10" -> List(readClass1WithLimit(0, 1)),
    "11" -> List(ReadSingleEventWithNoOverflow(0)))
}

/*
8.6.6.2.1 Buffer Overflow, Binary Input Change Event Buffers
1. If the device does not support Binary Input Change Events, end testing of Buffer Overflow, Binary Input Change Event
Buffers.
2. Issue a request for Object 60 Variations 2, 3 and 4 using the all data qualifier 0x06 to empty the device of pending
events.
3. Obtain the maximum Binary Input Change Event buffer size from the device documentation and generate this number of
corresponding events on the DUT.
4. Issue a request for Object 60 Variation x (appropriate Class) using the all data qualifier 0x06, but DO NOT issue an
application confirm to the device.
5. Verify that IIN2-3 is NOT set.
6. Generate 1 additional change event of this type on the DUT.
7. Issue a request for Object 60 Variation x using the limit data qualifier 0x07 requesting a single change.
8. Verify that the device returns a single Binary Input Change Event and IIN2-3 is set.
9. Issue an application layer confirmation.
10. Issue a request for Object 60 Variation x using the limit data qualifier 0x07 requesting a single change.
11. Verify that the device returns a single Binary Input Change Event and IIN2-3 is not set.
12. Issue an application layer confirmation.
*/
