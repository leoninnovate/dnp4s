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
package com.automatak.dnp4s.conformance.procedures.section7

import com.automatak.dnp4s.conformance.procedures.{ RequireIIN, AppSteps, CommonSteps }
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.conformance.{ TestOptions, TestStep }
import com.automatak.dnp4s.conformance.procedures.AppSteps._
import com.automatak.dnp4s.dnp3.Apdus
import com.automatak.dnp4s.conformance.IntTestOption
import com.automatak.dnp4s.conformance.procedures.AppSteps.VerifyExactResponseWithSeq
import scala.Some

object Operation {

  val selectTimeout = IntTestOption("command.selectTimeout", "Device select timeout in milliseconds", Some(0), None)

  def getSelectTimeout(options: TestOptions): Int = options.getInteger(selectTimeout.id)

  val expectOperation = CommonSteps.PromptForUserYesNo("Did the DUT operate?", true)
  val expectNoOperation = expectOperation.negate

  case class ExpectNullResponseWithIIN21(seq: Byte) extends ReadAnyValidResponseUnconfirmed with SingleFragment with NoHeaders with RequireIIN {
    def requiredMasks = List(IIN(0, IIN.IIN21_OBJECT_UNKNOWN))
  }

  def stepsBadSBO(header1: ObjectHeader, header2: ObjectHeader, failure: ObjectHeader) = {

    List(
      "1.a" -> List(AppSteps.SendHeader(AppFunctions.select, 0.toByte, header1)),
      "1.b" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0.toByte, header1)),
      "1.c" -> List(AppSteps.SendHeader(AppFunctions.operate, 1.toByte, header2)),
      "1.d" -> List(AppSteps.VerifyExactResponseWithSeq(1.toByte, failure)),
      "1.e" -> List(expectNoOperation))
  }

  def stepsUnsupportedOrUninstalled(function: Byte, header: ObjectHeader) = {
    List(
      "1.a" -> List(AppSteps.SendHeader(function, 0.toByte, header)),
      "1.b" -> List(ExpectNullResponseWithIIN21(0)))
  }

  def stepsValidSBO(header: ObjectHeader): List[(String, List[TestStep])] = List(
    "2.a" -> List(AppSteps.SendHeader(AppFunctions.select, 0.toByte, header)),
    "2.b" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0, header)),
    "2.c" -> List(AppSteps.SendHeader(AppFunctions.operate, 1.toByte, header)),
    "2.d" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(1, header)),
    "2.e" -> List(expectOperation))

  def stepsSelectUninstalledControl(header: ObjectHeader, reply: ObjectHeader) = {

    object VerifyFailure extends VerifyExactResponseWithSeq(0, reply) with RequireIIN {
      def requiredMasks = List(IIN(0x00, IIN.IIN22_PARAM_ERROR))
    }

    List(
      "1.a" -> List(AppSteps.SendHeader(AppFunctions.select, 0.toByte, header)),
      "1.b" -> List(VerifyFailure),
      "1.c" -> List(Operation.expectNoOperation))
  }

  def stepsSelectTimeout(timeoutMs: Int, header: ObjectHeader, reply: ObjectHeader) = List(
    "1.a" -> List(AppSteps.SendHeader(AppFunctions.select, 0.toByte, header)),
    "1.b" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0.toByte, header)),
    "1.c" -> List(CommonSteps.Wait(timeoutMs + 1000)),
    "1.d" -> List(AppSteps.SendHeader(AppFunctions.operate, 1.toByte, header)),
    "1.e" -> List(AppSteps.VerifyExactResponseWithSeq(1.toByte, reply)),
    "1.f" -> List(Operation.expectNoOperation))

  def stepsSBOInterruption(header: ObjectHeader, reply: ObjectHeader) = List(
    "1.b" -> List(AppSteps.SendApdu(Apdus.readEvents(0)), AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0)),
    "1.c" -> List(AppSteps.SendHeader(AppFunctions.select, 0, header)),
    "1.d" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0.toByte, header)),
    "1.e" -> List(AppSteps.SendApdu(Apdus.readEvents(1))),
    "1.f" -> List(AppSteps.ReadNullResponseWithOKIIN(1)),
    "1.g" -> List(AppSteps.SendHeader(AppFunctions.operate, 1, header)),
    "1.h" -> List(AppSteps.VerifyExactResponseWithSeq(1, reply)),
    "1.i" -> List(Operation.expectNoOperation))

  object VerifyErrorResponse extends ReadAnyValidResponseUnconfirmedWithSeq(0.toByte) with NoHeaders with RequireIIN {
    override def description = Nil
    override def requiredMasks = List(IIN(0.toByte, IIN.IIN21_OBJECT_UNKNOWN), IIN(0.toByte, IIN.IIN22_PARAM_ERROR))
  }

  def stepsUninstalledOrDisabled(function: Byte, header: ObjectHeader) = List(
    "1.b" -> List(AppSteps.SendHeader(function, 0.toByte, header)),
    "1.c" -> List(VerifyErrorResponse),
    "1.d" -> List(Operation.expectNoOperation))

  def stepsSelectRetrySameSequenceNumber(header: ObjectHeader) = List(
    "1.a" -> List(AppSteps.SendHeader(AppFunctions.select, 0.toByte, header)),
    "1.b" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0.toByte, header)),
    "1.c" -> List(AppSteps.SendHeader(AppFunctions.select, 0.toByte, header)),
    "1.d" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0.toByte, header)),
    "1.e" -> List(AppSteps.SendHeader(AppFunctions.operate, 1.toByte, header)),
    "1.f" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(1.toByte, header)),
    "1.g" -> List(Operation.expectOperation))

  def stepsSelectRetryIncrementedSequenceNumber(header: ObjectHeader) = List(
    "1.a" -> List(AppSteps.SendHeader(AppFunctions.select, 0.toByte, header)),
    "1.b" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0.toByte, header)),
    "1.c" -> List(AppSteps.SendHeader(AppFunctions.select, 1.toByte, header)),
    "1.d" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(1.toByte, header)),
    "1.e" -> List(AppSteps.SendHeader(AppFunctions.operate, 2.toByte, header)),
    "1.f" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(2.toByte, header)),
    "1.g" -> List(Operation.expectOperation))

  def stepsOperateRetrySameSequenceNumber(header: ObjectHeader) = List(
    "1.a" -> List(AppSteps.SendHeader(AppFunctions.select, 0.toByte, header)),
    "1.b" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0.toByte, header)),
    "1.c" -> List(AppSteps.SendHeader(AppFunctions.operate, 1.toByte, header)),
    "1.d" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(1.toByte, header)),
    "1.e" -> List(Operation.expectOperation),
    "1.f" -> List(AppSteps.SendHeader(AppFunctions.operate, 1.toByte, header)),
    "1.g" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(1.toByte, header)),
    "1.h" -> List(Operation.expectNoOperation))

  def stepsOperationRetryIncrementedSequenceNumber(header: ObjectHeader, reply: ObjectHeader) = List(
    "1.a" -> List(AppSteps.SendHeader(AppFunctions.select, 0.toByte, header)),
    "1.b" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0.toByte, header)),
    "1.c" -> List(AppSteps.SendHeader(AppFunctions.operate, 1.toByte, header)),
    "1.d" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(1.toByte, header)),
    "1.e" -> List(Operation.expectOperation),
    "1.f" -> List(AppSteps.SendHeader(AppFunctions.operate, 2.toByte, header)),
    "1.g" -> List(AppSteps.VerifyExactResponseWithSeq(2.toByte, reply)),
    "1.h" -> List(Operation.expectNoOperation))

  def stepsOperateWithInvalidSequenceNumber(header: ObjectHeader, reply: ObjectHeader) = List(
    "1.a" -> List(AppSteps.SendHeader(AppFunctions.select, 0.toByte, header)),
    "1.b" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0.toByte, header)),
    "1.c" -> List(AppSteps.SendHeader(AppFunctions.operate, 2.toByte, header)),
    "1.d" -> List(AppSteps.VerifyExactResponseWithSeq(2.toByte, reply)),
    "1.e" -> List(Operation.expectNoOperation),
    "1.f" -> List(AppSteps.SendHeader(AppFunctions.operate, 1.toByte, header)),
    "1.g" -> List(AppSteps.VerifyExactResponseWithSeq(1.toByte, reply)),
    "1.h" -> List(Operation.expectNoOperation))

}
