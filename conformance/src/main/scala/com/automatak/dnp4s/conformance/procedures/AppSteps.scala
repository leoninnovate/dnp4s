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
package com.automatak.dnp4s.conformance.procedures

import com.automatak.dnp4s.conformance.TestStep
import com.automatak.dnp4s.dnp3.{ Apdus, TestDriver }
import com.automatak.dnp4s.dsl._
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.dnp3.link.{ Lpdu, LinkCtrl }
import scala.Some
import com.automatak.dnp4s.conformance.procedures.CommonSteps.NullDescription

object AppSteps {

  val OkIIN = IIN(IIN.IIN14_NEED_TIME, 0) | IIN(IIN.IIN17_DEVICE_RESTART, 0)

  private val unconfirmed = LinkCtrl(true, true, false, false, Lpdu.UNCONFIRMED_USER_DATA)

  case class SendConfirm(seq: Byte, link: Byte = unconfirmed.toByte) extends TestStep {
    def description = List("Send an application layer confirmation with link control block 0x" + toHex(link))
    def run(driver: TestDriver) = {
      val apdu = Apdu(AppCtrl(true, true, false, false, seq), AppFunctions.confirm, None, Nil)
      driver.writeAPDU(apdu, Some(link))
      Nil
    }
  }

  case class SendApdu(apdu: Apdu, link: Byte = unconfirmed.toByte) extends TestStep {
    def description = List("Request " + apdu.toString + " with link control block 0x" + toHex(link))
    def run(driver: TestDriver) = {
      driver.writeAPDU(apdu, Some(link))
      Nil
    }
  }

  case class RequestClass0(seq: Byte, link: Byte = unconfirmed.toByte) extends TestStep {
    def description = List("Request Class 0 data (Object 60 Variation 1) using Qualifier Code 0x06 and link control block 0x" + toHex(link))
    def run(driver: TestDriver) = {
      driver.writeAPDU(Apdus.readClass0(seq), Some(link))
      Nil
    }
  }

  case class SendHeader(func: Byte, seq: Byte, headers: ObjectHeader*) extends TestStep {
    def description = List("Request a " + AppFunctions.funcToString(func) + " using " + headers.map(_.toString).mkString(", "))
    def run(driver: TestDriver) = {
      val apdu = Apdu(AppCtrl(true, true, false, false, seq), func, None, headers.toList)
      driver.writeAPDU(apdu)
      Nil
    }
  }

  trait ApduValidation {

    def validate(apdus: List[Apdu]): Unit = apdus.foreach(apdu => validate(apdu))
    def validate(apdu: Apdu): Unit = {}

  }

  trait VerifyResponseRequestsConfirmation extends ApduValidation {
    abstract override def validate(apdu: Apdu) = {
      super.validate(apdu)
      if (!apdu.ctrl.con) throw new Exception("Response does not request confirmation")
    }
  }

  case class ReadAnyValidResponseUnconfirmedWithSeq(seq: Byte) extends ReadAnyValidResponseUnconfirmed

  trait ReadAnyValidResponseUnconfirmed extends TestStep with ApduValidation {

    def seq: Byte

    def autoConfirm: Boolean = true

    override def description = List("Verify that the DUT responds with a valid application message")

    final def run(driver: TestDriver) = {
      val apdus = TestDriver.readAllResponses(driver)(seq, autoConfirm, false, AppFunctions.rsp, false)
      if (apdus.isEmpty) throw new Exception("No response")
      validate(apdus)
      Nil
    }
  }

  trait ReadAnyValidUnsolicitedResponse extends TestStep with ApduValidation {

    override def description = List(
      Some("Verify the DUT sends a valid unsolicited response"),
      seq.map(byte => "Verify the response has sequence number: 0x" + toHex(byte))).flatten

    def seq: Option[Byte]

    def autoConfirm: Boolean = true

    final def run(driver: TestDriver) = {
      val apdu = driver.readAPDU().getOrElse(throw new Exception("Expected an APDU"))
      if (apdu.function != AppFunctions.unsolRsp) throw new Exception("Expected unsolcited response")
      if (!apdu.ctrl.uns) throw new Exception("Expected unsolicited bit, but received")
      if (!apdu.ctrl.con) throw new Exception("All unsolicited data must be confirmed")
      if (!apdu.ctrl.fir) throw new Exception("FIR bit not set in unsolicited response")
      if (!apdu.ctrl.fin) throw new Exception("FIN bit not set in unsolicited response")
      seq.foreach { useq =>
        if (apdu.ctrl.seq != useq) throw new Exception("Unexpected sequence number")
      }
      validate(List(apdu))
      if (autoConfirm) driver.writeAPDU(Apdus.confirm(apdu.ctrl.seq, true))
      Nil
    }

  }

  case class ReadNullResponseWithOKIIN(seq: Byte) extends ReadAnyValidResponseUnconfirmed with SingleFragment with NoHeaders with AllowIIN {
    def allowedMask = OkIIN
  }

  case class ReadNullResponseWithAllowedIIN(seq: Byte, allowedMask: IIN) extends ReadAnyValidResponseUnconfirmed with SingleFragment with NoHeaders with AllowIIN

  case class ReadNullUnsol(seq: Option[Byte], override val autoConfirm: Boolean) extends ReadAnyValidUnsolicitedResponse with NoHeaders

  trait SingleFragment extends ApduValidation with TestStep {
    abstract override def description = super.description ::: List("Verify the response is a single fragment")
    final override def validate(apdu: List[Apdu]): Unit = apdu match {
      case single :: Nil =>
        validate(single)
      case _ =>
        throw new Exception("Expected a single fragment, but received: " + apdu.size)
    }
  }

  trait AtLeast1Header extends ApduValidation with TestStep {
    abstract override def description = super.description ::: List("Verify the response contains at least 1 object header")
    abstract override def validate(apdu: Apdu): Unit = {
      if (apdu.headers.isEmpty) throw new Exception("Expected at least one object header!")
    }
  }

  trait VerifyResponseHeaders extends ApduValidation with TestStep {
    def headers: List[ObjectHeader]
    abstract override def description = super.description ::: List("Verify the response contains headers: " + headers.map(_.toString).mkString(", "))
    override def validate(apdu: Apdu) = {
      if (apdu.headers != headers.toList) {
        throw new Exception("Unexpected response: " + apdu)
      }
    }
  }

  trait VerifyExactResponse extends NullDescription with AppSteps.ReadAnyValidResponseUnconfirmed with SingleFragment with VerifyResponseHeaders

  case class VerifyExactResponseWithSeq(seq: Byte, header: ObjectHeader) extends VerifyExactResponse {
    def headers = List(header)
  }

  case class VerifyExactResponseWithNoErrorIIN(seq: Byte, header: ObjectHeader) extends VerifyExactResponse with AllowIIN {
    import IIN._
    def headers = List(header)
    def allowedMask = IIN(field(IIN17_DEVICE_RESTART, IIN14_NEED_TIME), 0)
  }

  case class VerifyExactResponseRequiringIIN(seq: Byte, header: ObjectHeader, iins: IIN*) extends VerifyExactResponse with RequireIIN {
    def headers = List(header)
    def requiredMasks = iins.toList
  }

  trait NoHeaders extends ApduValidation with TestStep {

    abstract override def description = super.description ::: List("Verify that the response contains no headers")

    abstract override def validate(apdu: Apdu) = {
      super.validate(apdu)
      if (apdu.headers.nonEmpty) throw new Exception("Not expecting headers in response")
    }
  }

  object ReadAnyValidResponseWithConfirms extends TestStep {
    def description = List("Verify the DUT responds with application layer data, requesting data link confirmation")
    def run(driver: TestDriver) = {
      val apdus = TestDriver.readAllResponses(driver)(0, true, false, AppFunctions.rsp, true)
      if (apdus.isEmpty) throw new Exception("Received no response")
      Nil
    }
  }

}
