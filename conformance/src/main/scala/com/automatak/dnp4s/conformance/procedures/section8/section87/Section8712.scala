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

import com.automatak.dnp4s.conformance.{ TestStep, IntTestOption, TestOptions, TestProcedure }
import com.automatak.dnp4s.conformance.procedures.{ CommonSteps, AllowIIN, AppSteps }
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.conformance.procedures.AppSteps.{ ApduValidation, SingleFragment }
import objects.Group52Var2
import com.automatak.dnp4s.dsl.{ UInt16LE, UInt8 }

object Section8712 extends TestProcedure {

  private val maxDelayOption = IntTestOption("maxDelay", "Maximum delay measurement error in ms", Some(0), Some(1000))

  def id = "8.7.1.2"
  def description = "Delay Measurement"
  override def options = List(maxDelayOption)

  private def delayMeas(seq: Byte) = AppSteps.SendApdu(Apdu(AppCtrl(true, true, false, false, 0), AppFunctions.delayMeas, None, Nil))

  trait VerifyTimeDelay extends ApduValidation with TestStep {

    def maxDelay: Int

    abstract override def description =
      super.description ::: List("Verify the response is a single Group52Var2 with error less than the maximum specified")

    abstract override def validate(apdu: Apdu): Unit = {
      super.validate(apdu)
      apdu.headers match {
        case OneByteCountHeader(Group52Var2, UInt8(1), data) :: Nil =>
          val delay = UInt16LE(data(0), data(1))
          if (delay.i > maxDelay) throw new Exception("Delay exceeds maximum specified")
        case _ => throw new Exception("Unexpected headers: " + apdu.headers)
      }
    }
  }

  case class VerifyResponse(seq: Byte, maxDelay: Int) extends AppSteps.ReadAnyValidResponseUnconfirmed with SingleFragment with VerifyTimeDelay with AllowIIN {
    def allowedMask = IIN(IIN.field(IIN.IIN17_DEVICE_RESTART, IIN.IIN14_NEED_TIME), 0)
  }

  def steps(options: TestOptions) = {

    val maxDelay = options.getInteger(maxDelayOption.id)

    val delay = delayMeas(0)
    val verify = VerifyResponse(0, maxDelay)
    val repeats = CommonSteps.Description("Repeat 2 more times to guarantee repeatability") :: TestProcedure.repeat(List(delay, verify), 2)

    List(
      "1" -> List(delay),
      "2" -> List(verify),
      "4" -> repeats)
  }
}

/*
8.7.1.2 Test Procedure
1. Issue a request for Delay Measurement using Function Code 23, to the DUT.
2. Verify that the device replies with Object 52 Variation 2 Qualifier 0x07 Quantity 1 and a millisecond time stamp of the
measured delay.
3. Verify that the accuracy of the delay reported is within the maximum error specified in the Device Profile Document.
4. Perform the test two more times to guarantee repeatability.
*/
