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
package com.automatak.dnp4s.conformance.procedures.section7.section73

import com.automatak.dnp4s.conformance.{ TestProcedure, TestSection, TestOptions }
import com.automatak.dnp4s.conformance.procedures.{ AppSteps, DisabledByAnalogOutputStatusSupport, RequiresAnalogOutputStatusSupport }
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.dnp3.Apdus
import objects.{ Group40Var2, Group40Var0 }
import com.automatak.dnp4s.conformance.procedures.AppSteps.{ AtLeast1Header, ApduValidation, ReadNullResponseWithAllowedIIN }
import com.automatak.dnp4s.conformance.procedures.CommonSteps.PromptForUserYesNo

object Section73 extends TestSection {
  def id = "7.3"
  def description = "Analog Output Status"
  override def subProcedures = List(Section732a, Section732b)

  def analogPoll(seq: Byte) = AppSteps.SendApdu(Apdus.readAllObjects(seq, Group40Var0))
}

object Section732a extends TestProcedure with RequiresAnalogOutputStatusSupport {
  def id = "7.3.2.a"
  def description = "Test Procedure (supports AO status)"

  trait ValidateAnalogOutputs extends ApduValidation {

    def validateHeader(header: ObjectHeader) = header match {
      case OneByteStartStopHeader(Group40Var2, _, _, _) =>
      case TwoByteStartStopHeader(Group40Var2, _, _, _) =>
      case _ =>
        throw new Exception("Unexpected header in response: " + header)
    }

    abstract override def validate(apdu: Apdu): Unit = {
      super.validate(apdu)
      apdu.headers.foreach(validateHeader)
    }
  }

  case class VerifyAnalogOutputStatus(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with AtLeast1Header with ValidateAnalogOutputs

  def steps(options: TestOptions) = List(
    "2.b" -> List(Section73.analogPoll(0)),
    "2.c" -> List(VerifyAnalogOutputStatus(0)),
    "2.d" -> List(PromptForUserYesNo("Were all AO points reported?", true)),
    "2.e" -> List(PromptForUserYesNo("Are the values within the tolerances specified by the manufacturer, and are the flags are correct?", true)))
}

/*
2. If the DUT supports Analog Output operations:
a. Issue a DIRECT OPERATE request with Function Code 5 using Object Group 41 Variation
2 to an installed point using Qualifier 0x28 and an appropriate value.
b. Issue an ANALOG OUTPUT POLL request.
c. Verify that the device responds using only the object group, variation and qualifier
combinations specified in Table 7 .
d. Verify that all installed Analog Output points are included in the response.
e. For at least ten of the reported points: Verify that the value is within the tolerances specified
by the manufacturer, and that the flags are correct.
*/

object Section732b extends TestProcedure with DisabledByAnalogOutputStatusSupport {
  def id = "7.3.2.b"
  def description = "Test Procedure (no AO status support)"
  def steps(options: TestOptions) = List(
    "1.b" -> List(Section73.analogPoll(0)),
    "1.c" -> List(ReadNullResponseWithAllowedIIN(0, AppSteps.OkIIN | IIN(0, IIN.IIN21_OBJECT_UNKNOWN))))
}

/*
1. If the DUT does not support Analog Output operations:
a. Issue a Direct Operate with Function Code 5 to Object Group 41 Variation 2 to point 0 using
Qualifier 0x28 and an appropriate value.
b. Issue an ANALOG OUTPUT POLL request.
c. Verify that the device responds with either a Null Response or a Null Error Response with
IIN2.1 set.
*/

