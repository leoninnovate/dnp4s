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
package com.automatak.dnp4s.conformance.procedures.section5.section55.section552

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure, TestStep }
import com.automatak.dnp4s.dnp3.link._
import com.automatak.dnp4s.dsl.UInt8
import com.automatak.dnp4s.dnp3.{ Apdus, TestDriver }
import com.automatak.dnp4s.dnp3.transport.Transport
import com.automatak.dnp4s.conformance.LinkSteps._
import com.automatak.dnp4s.dnp3.link.LinkHeader
import com.automatak.dnp4s.conformance.procedures.CommonSteps.Description
import com.automatak.dnp4s.conformance.procedures.CommonSteps

object Section5525 extends TestProcedure {

  def id = "5.5.2.5"
  def description = "Invalid CRC"

  case class SendInvalidHeaderCrc(offset: Short) extends TestStep {
    def description = List("Send the request")
    def run(driver: TestDriver) = {
      val apdu = Apdus.readClass0(0)
      val tpdu = Transport.function(true, true, 0) :: apdu.toBytes
      val lpdu = Lpdu(LinkHeader(UInt8((5 + tpdu.size).toShort), LinkCtrl(0xD3.toByte), driver.remote, driver.local), tpdu)
      val bytes = lpdu.toBytesWithGen(InvalidCrc(offset), Crc)
      driver.writePhys(bytes)
      Nil
    }
  }

  case class SendInvalidBodyCrc(offset: Short) extends TestStep {
    def description = List("Send the request")
    def run(driver: TestDriver) = {
      val apdu = Apdus.readClass0(0)
      val tpdu = Transport.function(true, true, 0) :: apdu.toBytes
      val lpdu = Lpdu(LinkHeader(UInt8((5 + tpdu.size).toShort), LinkCtrl(0xD3.toByte), driver.remote, driver.local), tpdu)
      val bytes = lpdu.toBytesWithGen(Crc, InvalidCrc(offset))
      driver.writePhys(bytes)
      Nil
    }
  }

  def steps(options: TestOptions) = stepsWithCrcOffset(1) ::: stepsWithCrcOffset(2)

  private def stepsWithCrcOffset(offset: Short) = {
    List(
      "1" -> List(Description("Prepare a READ request for Class 0 data (Object 60 Variation 1) using Qualifier Code 0x06 and a link control block 0xD3")),
      "2" -> List(Description("Modify the frame so the CRC of the data link layer header is incorrect")),
      "3" -> List(SendInvalidHeaderCrc(offset)),
      "4" -> List(CommonSteps.Wait(3000)),
      "5" -> List(ExpectLpduTimeout),
      "6" -> List(Description("Modify the frame so the CRC of the data link layer header is correct but the CRC of the application layer request is incorrect")),
      "7" -> List(SendInvalidBodyCrc(offset)),
      "8" -> List(CommonSteps.Wait(3000)),
      "9" -> List(ExpectLpduTimeout))
  }

  /*
  5.5.2.5 Invalid CRC
  1. Prepare a CLASS 0 POLL request with a Data Link Layer Control octet of 0xD3.
  2. Modify the frame so the CRC of the Data Link Layer header is incorrect.
  3. Send the request.
  4. Wait several seconds.
  5. Verify that the DUT does not send a Data Link Layer Confirmation or Application Layer
  response.
  6. Modify the frame so the CRC of the Data Link Layer header is correct but the CRC of the
  Application Layer request is incorrect.
  7. Send the request.
  8. Wait several seconds.
  9. Verify that the DUT does not send a Data Link Layer Confirmation or Application Layer
  response.
  10. Repeat this test once with different incorrect CRC values.
  */

}

