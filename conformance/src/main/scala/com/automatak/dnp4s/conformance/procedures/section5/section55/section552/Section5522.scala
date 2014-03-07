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

import com.automatak.dnp4s.conformance.{ TestOptions, LinkSteps, TestProcedure, TestStep }
import com.automatak.dnp4s.dnp3.link.{ LinkCtrl, Lpdu }
import com.automatak.dnp4s.dnp3.{ Apdus, TestDriver }
import com.automatak.dnp4s.dnp3.transport.Transport
import com.automatak.dnp4s.conformance.procedures.CommonSteps.Description
import com.automatak.dnp4s.conformance.procedures.CommonSteps

object Section5522 extends TestProcedure {

  def id: String = "5.5.2.2"
  def description = "Invalid Start Octets"

  case class ReadClass0CustomStartOctets(first: Byte, second: Byte) extends TestStep {
    def description = List("Send the request")
    def run(driver: TestDriver) = {
      val tpdu = Transport.function(true, true, 0) :: Apdus.readClass0(0).toBytes
      val lpdu = Lpdu.withData(LinkCtrl(0xD3.toByte), driver.remote, driver.local, tpdu)
      val bytes = lpdu.toBytesWithStart(first, second)
      driver.writePhys(bytes)
      Nil
    }
  }

  def steps(options: TestOptions) = List(
    "1" -> List(Description("Prepare a READ request for Class 0 data (Object 60 Variation 1) using Qualifier Code 0x06 and a link control block 0xD3")),
    "2" -> List(Description("Modify the frame so it begins with an invalid initial start octet (e.g. 0x09) instead of 0x05",
      "Modify the CRC of the data link layer header so the CRC is correct for the invalid start octet")),
    "3" -> List(ReadClass0CustomStartOctets(0x09.toByte, 0x64.toByte)),
    "4" -> List(CommonSteps.Wait(3000)),
    "5" -> List(LinkSteps.ExpectLpduTimeout),
    "6" -> List(Description("Modify the frame so it begins with 0x05, but the second start octet is invalid (e.g. 0xff)",
      "Modify the CRC of the data link layer header so the CRC is correct for the invalid start octet")),
    "7" -> List(ReadClass0CustomStartOctets(0x05.toByte, 0xFF.toByte)),
    "8" -> List(CommonSteps.Wait(3000)),
    "9" -> List(LinkSteps.ExpectLpduTimeout))

  /*
1. Prepare a CLASS 0 POLL request with a Data Link Layer Control octet of 0xD3.
2. Modify the frame so that it begins with an invalid initial start octet (e.g. 0x09) instead of 0x05.
Also, modify the CRC of the Data Link Layer header so that the CRC is correct for the invalid
start octet.
3. Send the request.
4. Wait several seconds.
5. Verify that the DUT does not send a Data Link Layer Confirmation or Application Layer
response.
6. Modify the frame so it begins with 0x05, but the second start octet is invalid (e.g. 0xff). Also,
modify the CRC of the Data Link Layer header so that the CRC is correct for the invalid start
octet.
7. Send the request.
8. Wait several seconds.
9. Verify that the DUT does not send a Data Link Layer Confirmation or Application Layer
response.
10. Repeat this test once using different start octet values.
5.5.2.3 Invalid
*/

}

