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
package com.automatak.dnp4s.conformance.procedures.section8.section87

import com.automatak.dnp4s.conformance.procedures.{ CommonSteps, AllowIIN, RequireIIN, AppSteps }
import com.automatak.dnp4s.dnp3.app._
import objects.{ Group60Var3, Group60Var4, Group50Var1, Group60Var2 }
import com.automatak.dnp4s.dnp3.Apdus
import com.automatak.dnp4s.conformance.{ TestProcedure, TestOptions }
import com.automatak.dnp4s.conformance.procedures.section8.EventVerification.{ EventDataOnly, OkIIN }
import com.automatak.dnp4s.dsl.{ UInt48LE, UInt8 }
import com.automatak.dnp4s.conformance.procedures.AppSteps.{ NoHeaders, SingleFragment }

object Section8722 extends TestProcedure {

  def id = "8.7.2.2"
  def description = "Synchronization"
  override def prompts = List("Reset the DUT", "Wait the specified maximum time from reset to IIN1-4 assertion")

  def writeTimeApdu(seq: Byte) = {
    val ctrl = AppCtrl(true, true, false, false, seq)
    val timestamp = UInt48LE(System.currentTimeMillis())
    val header = OneByteCountHeader(Group50Var1, UInt8(1), timestamp.toBytes)
    Apdu(ctrl, AppFunctions.write, None, List(header))
  }

  case class VerifyEventsWithNeedTime(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with OkIIN with RequireIIN {
    def requiredMasks = List(IIN(IIN.IIN14_NEED_TIME, 0))
  }

  case class VerifySuccessWithClearIIN(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with SingleFragment with NoHeaders with AllowIIN {
    def allowedMask = IIN(IIN.IIN17_DEVICE_RESTART, 0)
  }

  case class VerifySingleEvent(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with SingleFragment with EventDataOnly with AllowIIN {
    def eventMax: Option[Int] = Some(1)
    def eventMin: Option[Int] = Some(1)
    def allowedMask = IIN(IIN.IIN17_DEVICE_RESTART, 0)
  }

  // TODO - automate this step
  private val verifyTimestamp = CommonSteps.PromptForUserYesNo("Does the timestamp look reasonable?", true)

  override def steps(options: TestOptions) = {
    List(
      "3" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, Group60Var2))),
      "4" -> List(VerifyEventsWithNeedTime(0)),
      "5" -> List(AppSteps.SendApdu(writeTimeApdu(1))),
      "6" -> List(VerifySuccessWithClearIIN(1)),
      "7" -> List(CommonSteps.PromptForUserAction("Generate a supported time tagged event at a known time")),
      "8" -> List(AppSteps.SendApdu(Apdus.readAllObjects(2, Group60Var2, Group60Var3, Group60Var4))),
      "9" -> List(VerifySingleEvent(2), verifyTimestamp))
  }
}

/*
8.7.2.2 Test Procedure
1. Reset the DUT.
2. Wait the specified maximum time from reset to IIN1-4 assertion.
3. Issue a request for Object 60 Variation 2 using the all data qualifier 0x06.
DNP3 IED Certification Procedure Page 45
Subset Level 2 Rev 2.6 rev1 – 28-Oct-2010
4. Verify that the device responds with a valid message and that IIN1-4 is set indicating that the device is requesting time
synchronization.
5. Set the time and date using a WRITE request and Object 50 Variation 1 with Qualifier 0x07 taking into account the
delay measured in the previous section.
6. Verify that the device responds with a Null Response and that IIN1-4 is cleared indicating the device no longer needs
time.
7. Generate a supported time tagged event at a known time.
8. Issue a request for Object 60 Variation 2, 3, or 4 as appropriate, using the all data qualifier 0x06.
9. Verify that the device responds with a valid time tagged event and that the time reported is within the maximum error
specified in the Device Profile Document.
*/
