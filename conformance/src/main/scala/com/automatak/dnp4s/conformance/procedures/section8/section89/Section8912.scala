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
package com.automatak.dnp4s.conformance.procedures.section8.section89

import com.automatak.dnp4s.conformance.{ IntTestOption, TestProcedure, TestOptions }
import com.automatak.dnp4s.conformance.procedures.{ AppSteps, CommonSteps }
import com.automatak.dnp4s.dnp3.app.Apdu
import com.automatak.dnp4s.dnp3.Apdus
import com.automatak.dnp4s.dnp3.app.objects.{ Group60Var2, Group60Var3, Group60Var4, Group60Var1 }

object Section8912 extends TestProcedure {

  private val maxFragSizeOption = IntTestOption("maxFragSize", "Maximum application layer fragment size", Some(100), Some(8192))

  def id = "8.9.1.2"

  def description = "Use of FIR, FIN and SEQUENCE in Fragmentation"

  override def prompts = List(
    "If the DUT has a configurable fragment size, configure it to be no more than 2048 octets",
    "If the DUT has a configurable fragment size, you should repeat this test for a different fragment size",
    "If there is not way to make the DUT generate more than 1 fragment, you may skip this section",
    "Cycle power to the DUT")

  override def options = List(maxFragSizeOption)

  private def allData(seq: Byte) = Apdus.readAllObjects(0, Group60Var1, Group60Var2, Group60Var3, Group60Var4)

  // All of the validations in steps 4-11 are already performed by the base class except for expecting more than 1 fragment
  private case class ValidateFragments(seq: Byte, maxSize: Int) extends AppSteps.ReadAnyValidResponseUnconfirmed {
    override def validate(apdus: List[Apdu]): Unit = {
      if (apdus.size < 2) throw new Exception("Expected multiple fragments")
      apdus.foreach { a =>
        val size = a.toBytes.size
        if (size > maxSize) throw new Exception("Size exceeds max fragment size")
      }
    }
  }

  def steps(options: TestOptions) = {

    val maxFragSize = options.getInteger(maxFragSizeOption.id)

    List(
      "3" -> List(
        CommonSteps.PromptForUserAction("If the DUT doesn't generate more than 1 fragment with a Class0 poll, then generate sufficient event data to do so"),
        AppSteps.SendApdu(allData(0))),
      "4" -> List(ValidateFragments(0, maxFragSize)))
  }
}

/*
8.9.1.2 Test Procedure
1. If the DUT has a configurable fragment size, configure it to be no more than 2048 octets.
2. Cycle power to the DUT.
3. Request Class 0 data (Object 60 Variation 1) using Qualifier Code 0x06, if this will generate a multi-fragment response.
Alternately, generate enough event data to fill more than one fragment and request the appropriate class of data with
Qualifier Code 0x06.
4. Verify that the DUT responds with valid data.
5. Verify that the sequence number of the first fragment matches the request.
6. Verify that each fragment of the response contains no more than the configured fragment size.
7. If the DUT’s reply contains only a single fragment then verify the FIR bit is set to one and the FIN bit is also set to one.
If there is no way to cause the DUT to generate multiple fragments, i.e. its Class 0 response is less than a fragment and it
does not support event data, no further testing is required in the Application Layer Fragmentation section.
8. If the DUT’s reply contains more than one fragment then verify that the first response message sets the FIR bit to a one,
the FIN bit is set to zero.
9. Verify subsequent fragments to assure that the FIR bit is zero, the FIN bit is zero, and the sequence number increments
by one.
10. Verify that the last fragment has the FIR bit cleared and the FIN bit set, signifying the last data fragment.
11. Verify each fragment is properly segmented by the transport layer as described in that section of this document.
12. If the DUT’s fragment size is configurable, repeat this test with a different fragment size and verify the data for each
fragment does not exceed the configured size.
*/
