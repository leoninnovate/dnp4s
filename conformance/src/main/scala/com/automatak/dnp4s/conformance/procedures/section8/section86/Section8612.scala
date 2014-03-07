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
import com.automatak.dnp4s.conformance.procedures.{ RequireIIN, AllowIIN, AppSteps }
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.conformance.procedures.AppSteps.{ NoHeaders, SingleFragment }
import com.automatak.dnp4s.dnp3.Apdus

object Section8612 extends TestProcedure {

  def id = "8.6.1.2"
  def description = "Restart"
  override def prompts = List("Cycle power to DUT")

  private case class VerifyDeviceRestart(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with AllowIIN with RequireIIN {
    def allowedMask = IIN(IIN.field(IIN.IIN14_NEED_TIME, IIN.IIN17_DEVICE_RESTART), 0)
    def requiredMasks = List(IIN(IIN.IIN17_DEVICE_RESTART, 0))
  }

  private case class VerifyNullResponseWithRestartClear(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with SingleFragment with AllowIIN with NoHeaders {
    def allowedMask = IIN(IIN.IIN14_NEED_TIME, 0)
  }

  def steps(options: TestOptions) = List(
    "2" -> List(AppSteps.RequestClass0(0)),
    "3" -> List(VerifyDeviceRestart(0)),
    "4" -> List(AppSteps.SendApdu(Apdus.clearIIN(1))),
    "5" -> List(VerifyNullResponseWithRestartClear(1)))
}

/*
8.6.1.2 Test Procedure
1. Cycle the power to the DUT.
DNP3 IED Certification Procedure Page 40
Subset Level 2 Rev 2.6 rev1 – 28-Oct-2010
2. Issue a request for Object 60 Variation 2.
3. Verify that the device responds with IIN1-7 set indicating that the device has been restarted.
4. Issue a Write to Object 80 Variation 1 using the qualifier 0x00. Use a start index of 7 and a stop index of 7, followed by
the value 0.
5. Verify that the device responds with a Null Response and that IIN1-7 is cleared.
*/
