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
package com.automatak.dnp4s.conformance.procedures.section7.section71

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }
import com.automatak.dnp4s.conformance.procedures.{ DisabledByGroup12Var1Support, RequiresGroup12Var1Support, AppSteps }
import com.automatak.dnp4s.dnp3.Apdus
import com.automatak.dnp4s.dnp3.app.objects.{ Group10Var2, Group10Var0 }
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.dnp3.app.OneByteStartStopHeader
import com.automatak.dnp4s.dnp3.app.TwoByteStartStopHeader
import com.automatak.dnp4s.conformance.procedures.CommonSteps.PromptForUserYesNo

object Section712a extends TestProcedure with RequiresGroup12Var1Support {

  def id = "7.1.2a"
  def description = "Test Procedure"

  def steps(options: TestOptions) = {

    List(
      "1" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, Group10Var0))),
      "2" -> List(VerifyBinaryOutputStatusResponse(0)),
      "3" -> List(PromptForUserYesNo("Were all installed points reported?", true)),
      "4" -> List(PromptForUserYesNo("Randomly select 10 reported points and verify that flags/state are correct", true)))
  }

  case class VerifyBinaryOutputStatusResponse(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed {
    override def description = List("Verify that device responds with Group10Var2 and appropriate headers")
    override def validate(list: List[Apdu]) = list.foreach { apdu =>
      apdu.headers.foreach { header =>
        header match {
          case OneByteStartStopHeader(Group10Var2, _, _, _) =>
          case TwoByteStartStopHeader(Group10Var2, _, _, _) =>
          case _ =>
            throw new Exception("Unexpected object header: " + header.toString)
        }
      }
    }
  }
}

object Section712b extends TestProcedure with DisabledByGroup12Var1Support {

  def id = "7.1.2b"
  def description = "Test Procedure"

  val allowed = IIN(IIN.field(IIN.IIN14_NEED_TIME, IIN.IIN17_DEVICE_RESTART), IIN.IIN21_OBJECT_UNKNOWN)

  def steps(options: TestOptions) = {
    List(
      "2.a" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, Group10Var0))),
      "2.b" -> List(AppSteps.ReadNullResponseWithAllowedIIN(0, allowed)))
  }
}

/*
7.1.2 Test Procedure
1. Issue a BINARY OUTPUT POLL request.
2. If the DUT does not support Binary Output controls:
a. Verify that the device responds with either a Null Response or a Null Error Response with
IIN2.1 set.
3. If the DUT supports Binary Output controls:
a. Verify that the DUT responds using only the object group, variation and qualifier
combinations specified in Table 7 .
b. Verify that all installed Binary Output points are included in the response.
c. For at least ten of the reported points: Verify that the state and flags are correct.
*/

