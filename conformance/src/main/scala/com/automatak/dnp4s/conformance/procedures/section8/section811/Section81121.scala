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
package com.automatak.dnp4s.conformance.procedures.section8.section811

import com.automatak.dnp4s.conformance.{ LinkSteps, TestOptions, TestProcedure }
import com.automatak.dnp4s.conformance.procedures.AppSteps.{ SingleFragment, ReadAnyValidUnsolicitedResponse, NoHeaders }
import com.automatak.dnp4s.conformance.procedures._
import com.automatak.dnp4s.dnp3.app.IIN
import com.automatak.dnp4s.dnp3.Apdus

object Section81121 extends TestProcedure {

  def id = "8.11.2.1"
  def description = "Unsolicited Response Configuration/Startup (Steps 9 -> 30)"
  override def prompts = List(
    """Verify that the DUT has off-line capability to configure the unsolicited response mode (either 'on' or 'off')""",
    """Use this capability to configure the unsolicited response mode to 'on'""",
    """Verify the DUT has off-line capability to configure the unsolicited confirmation timeout""",
    """Verify that it can be set to at least as small as 1 second, and to at least as large as 1 minute""",
    """For the remainder of this section, set this time to be 5 seconds""",
    """Verify the DUT has off-line capability to configure the destination address of the Master device to which unsolicited responses are to be sent""",
    """Use this capability to configure the destination address of the master set in this application""",
    """Cycle power to the DUT""")

  case class ReadNullUnsolWithRestart(seq: Option[Byte], override val autoConfirm: Boolean) extends ReadAnyValidUnsolicitedResponse with NoHeaders with RequireIIN {
    def requiredMasks = List(IIN(IIN.IIN17_DEVICE_RESTART, 0))
  }

  case class ReadNullUnsolWithRestartClear(seq: Option[Byte], override val autoConfirm: Boolean) extends ReadAnyValidUnsolicitedResponse with NoHeaders with ProhibitIIN {
    def prohibitedMask = IIN(IIN.IIN17_DEVICE_RESTART, 0)
  }

  private case class VerifyNullResponseWithRestartClear(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with SingleFragment with AllowIIN with NoHeaders {
    def allowedMask = IIN(IIN.IIN14_NEED_TIME, 0)
  }

  val waitForUnsolTimeout = CommonSteps.Wait(5000)

  def steps(options: TestOptions) = List(
    "9" -> List(ReadNullUnsolWithRestart(None, false)),
    "15" -> List(
      waitForUnsolTimeout,
      ReadNullUnsolWithRestart(None, false),
      waitForUnsolTimeout,
      ReadNullUnsolWithRestart(None, false)),
    "19" -> List(AppSteps.SendApdu(Apdus.clearIIN(1))),
    "20" -> List(VerifyNullResponseWithRestartClear(1)),
    "21" -> List(
      waitForUnsolTimeout,
      ReadNullUnsolWithRestartClear(None, false)),
    "23" -> List(AppSteps.RequestClass0(0), waitForUnsolTimeout),
    "24" -> List(AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0)),
    "25" -> List(
      waitForUnsolTimeout,
      ReadNullUnsolWithRestartClear(None, false)),
    "26" -> List(AppSteps.SendApdu(Apdus.confirm(9, true))),
    "27" -> List(
      waitForUnsolTimeout,
      ReadNullUnsolWithRestartClear(None, true)),
    "29" -> List(CommonSteps.PromptForUserAction("Generate known events")),
    "30" -> List(LinkSteps.ExpectLpduTimeout))
}

/*
8.11.2.1 Unsolicited Response Configuration/Startup
1. Verify that the DUT has off-line capability to configure the unsolicited response mode (either 'on' or 'off'). Use this
capability to configure the unsolicited response mode to 'on'
2. § Verify the DUT has off-line capability to configure the unsolicited confirmation timeout. Verify that it can be set to at
least as small as 1 second, and to at least as large as 1 minute.
3. For the remainder of Section 8.11.2.1, set this time to be 5 seconds. (The value of 5 seconds is arbitrary, and is used to
simplify the description and execution of this test. As a further simplification for the purposes of this test, when the test
steps below exercise and rely upon this value, measurement accuracy can be within 0.5 seconds. It is not the purpose of
this test to verify the full range of this parameter, nor is it the purpose of this test to verify the accuracy of the time
keeping capability of the DUT. It is the purpose of this test, however, to verify that the parameter has been
implemented.)
4. Verify the DUT has off-line capability to configure the destination address of the Master device to which unsolicited
responses are to be sent. Use this capability to configure a specific destination address.
5. Cycle power to the DUT.
6. Verify that the DUT transmits an initial unsolicited response to the configured Master destination address.
7. Use the off-line configuration capability to configure the destination address to a different destination address.
8. Cycle power to the DUT.
9. Verify that an initial unsolicited response is transmitted by the DUT.
10. Verify that the unsolicited response contains the restart bit (IIN1-7) set.
11. § Verify that the unsolicited response is null (contains no data).
12. § Verify that the unsolicited response requests an application layer confirmation.
13. Verify that the application sequence number is in the correct range.
14. Verify that it has been transmitted to the configured Master destination address.
15. Wait at least 10 seconds, and verify that two or more unsolicited responses are transmitted by the DUT. Verify that these
new unsolicited responses are transmitted no more often, and no less often, than once every 5 seconds.
16. Verify that the restart bit (IIN1-7) remains set in these unsolicited responses.
17. § Verify that the unsolicited responses are null (contains no data).
18. § Verify that the unsolicited responses request application layer confirmations.
19. Issue a request to clear the restart bit (IIN1-7).
20. Verify that the DUT responds with a Null Response that has the restart bit (IIN1-7) cleared.
21. Wait at least 5 seconds, and verify that another unsolicited response is transmitted by the DUT.
22. Verify that the restart bit (IIN1-7) is clear in the unsolicited responses.
23. Issue a READ request (function code 0x01) for Object 60 Variation 1 (class 0) using the all data qualifier 0x06.
24. § Verify that the DUT responds to the READ request with class 0 data.
25. Wait at least 5 seconds, and verify that another unsolicited response is transmitted by the DUT.
26. Issue an application layer confirmation of the unsolicited response, but use an incorrect application-layer sequence
number.
27. Wait at least 5 seconds, and verify that another unsolicited response is transmitted by the DUT.
DNP3 IED Certification Procedure Page 50
Subset Level 2 Rev 2.6 rev1 – 28-Oct-2010
28. Issue an application-layer confirmation of the unsolicited response, and use the correct application-layer sequence
number.
29. Generate known events.
30. § Verify that no further unsolicited responses are transmitted by waiting for such responses for at least 5 seconds.
 */
