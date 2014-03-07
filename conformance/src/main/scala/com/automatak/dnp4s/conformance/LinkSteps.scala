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
package com.automatak.dnp4s.conformance

import com.automatak.dnp4s.dnp3.TestDriver
import com.automatak.dnp4s.dnp3.link.{ LinkCtrl, Lpdu }
import com.automatak.dnp4s.dsl._
import com.automatak.dnp4s.dnp3.link.LinkHeader

object LinkSteps {

  private def getString(link: LinkCtrl) = Lpdu.convertFuncToString(link.pri, link.func) + " (0x" + toHex(link.toByte) + ")"

  object ExpectLpduTimeout extends TestStep {
    def description = List("Receive no lpdus - expect link layer timeout")
    def run(driver: TestDriver) = {
      driver.readLPDU().foreach(lpdu => throw new Exception("Expecting timeout, but received lpdu: " + lpdu))
      Nil
    }
  }

  case class ExpectLpduTimeoutOr(allowCtrl: Byte*) extends TestStep {
    def description = {
      val options = allowCtrl.map(byte => getString(LinkCtrl(byte))).mkString(" or ")
      List("Expect link layer timeout or " + options)
    }
    def run(driver: TestDriver) = {
      val allowedLpdus = allowCtrl.map(byte => Lpdu(LinkHeader(UInt8(5), LinkCtrl(byte), driver.local, driver.remote)))
      driver.readLPDU().foreach { lpdu =>
        if (allowedLpdus.forall(_ != lpdu)) throw new Exception("Recieved unexpected lpdu: " + lpdu)
      }
      Nil
    }
  }

  object ReadNackOrIgnore extends ExpectLpduTimeoutOr(LinkCtrl(false, false, false, false, Lpdu.NACK).toByte)

  case class SendHeaderOnlyLpdu(ctrl: Byte) extends TestStep {
    def description = {
      val link = LinkCtrl(ctrl)
      List("Issue a " + getString(link) + " using link control block 0x" + toHex(ctrl))
    }
    def run(driver: TestDriver) = {
      driver.writeLPDU(Lpdu(LinkHeader(UInt8(5), LinkCtrl(ctrl), driver.remote, driver.local)))
      Nil
    }
  }

  case class ReadHeaderOnlyLpdu(expected: Byte*) extends TestStep {

    def description = {
      val options = expected.map(ctrl => getString(LinkCtrl(ctrl))).mkString(" or ")
      List("Verify that the DUT responds with a " + options)
    }

    def run(driver: TestDriver) = driver.readLPDU() match {
      case Some(lpdu) =>
        val allowedLpdus = expected.map(ctrl => Lpdu(LinkHeader(UInt8(5), LinkCtrl(ctrl), driver.local, driver.remote)))
        if (allowedLpdus.forall(_ != lpdu)) throw new Exception("Received unexpected lpdu: " + lpdu)
        Nil
      case None =>
        throw new Exception("Timed out waiting for lpdu")
    }
  }

  case class ExpectConfirmedUserData(link: Byte*) extends TestStep {
    def description = {
      val options = link.map(byte => getString(LinkCtrl(byte))).mkString(" or ")
      List("Verify that the DUT responds using " + options)
    }
    def run(driver: TestDriver) = driver.readLPDU() match {
      case Some(lpdu) =>
        if (link.forall(_ != lpdu.header.ctrl.toByte) || lpdu.header.dest != driver.local || lpdu.header.src != driver.remote) {
          throw new Exception("Unexpected lpdu: " + lpdu)
        }
        Nil
      case None => throw new Exception("Timeout waiting for confirmed user data")
    }
  }

  private val fcbLow = LinkCtrl(false, true, false, true, Lpdu.CONFIRMED_USER_DATA).toByte
  private val fcbHigh = LinkCtrl(false, true, true, true, Lpdu.CONFIRMED_USER_DATA).toByte

  object ExpectConfirmedUserDataFCBLow extends ExpectConfirmedUserData(fcbLow)
  object ExpectConfirmedUserDataFCBHigh extends ExpectConfirmedUserData(fcbHigh)
  object ExpectConfirmedUserDataFCBAny extends ExpectConfirmedUserData(fcbLow, fcbHigh)

}
