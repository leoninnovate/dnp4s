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

import com.automatak.dnp4s.conformance._
import com.automatak.dnp4s.conformance.procedures.{ AppSteps, CommonSteps }
import com.automatak.dnp4s.dnp3.app.AppCtrl
import com.automatak.dnp4s.dnp3.{ TestDriver, Apdus }
import com.automatak.dnp4s.dnp3.app.objects.{ Group60Var2, Group60Var3, Group60Var4, Group60Var1 }
import com.automatak.dnp4s.conformance.IntTestOption
import scala.Some

object Section8922 extends TestProcedure {

  private val appConfTimeoutOption = IntTestOption("appConfTimeout", "Application confirmation timeout in milliseconds", Some(1), Some(30000))

  def id = "8.9.2.2"
  def description = "Use of Confirmation in Fragmentation"
  override def options = List(appConfTimeoutOption)

  private def allData(seq: Byte) = Apdus.readAllObjects(0, Group60Var1, Group60Var2, Group60Var3, Group60Var4)

  private case class ReadNoConfirmation(seq: Byte) extends TestStep {
    def description = List(
      "Read a single APDU",
      "Verify that the con bit is set in the first fragment",
      "Verify the sequence number matches the request")

    def run(driver: TestDriver) = {
      driver.readAPDU() match {
        case Some(a) =>
          val expected = AppCtrl(true, false, true, false, seq)
          if (a.ctrl != expected) throw new Exception("Unexpected control field: " + a.ctrl)
          Nil
        case None => throw new Exception("Unexpected app timeout")
      }
    }
  }

  private val promptAndRequestAllData = List(
    CommonSteps.PromptForUserAction("If the DUT doesn't generate more than 1 fragment with a Class0 poll, then generate sufficient event data to do so"),
    AppSteps.SendApdu(allData(0)))

  def steps(options: TestOptions) = {

    val appConfTimeout = options.getInteger(appConfTimeoutOption.id)

    List(
      "1" -> promptAndRequestAllData,
      "2" -> List(ReadNoConfirmation(0)),
      "5" -> List(CommonSteps.Wait(appConfTimeout)),
      "6" -> List(LinkSteps.ExpectLpduTimeout),
      "7" -> List(AppSteps.SendConfirm(0)),
      "8" -> List(LinkSteps.ExpectLpduTimeout),
      "9" -> promptAndRequestAllData,
      "10" -> List(ReadNoConfirmation(0)),
      "11" -> List(CommonSteps.Wait(appConfTimeout / 2), LinkSteps.ExpectLpduTimeout),
      "12" -> List(AppSteps.SendConfirm(0)),
      "13" -> List(VerifyNextFragment(1, appConfTimeout)),
      "16" -> (promptAndRequestAllData ::: List(ReadNoConfirmation(0), AppSteps.SendConfirm(14))),
      "17" -> List(CommonSteps.Wait(appConfTimeout)),
      "18" -> List(LinkSteps.ExpectLpduTimeout),
      "19" -> List(AppSteps.SendConfirm(0)),
      "20" -> List(LinkSteps.ExpectLpduTimeout))

  }

  case class VerifyNextFragment(seq: Byte, timeout: Int) extends TestStep {
    def description = List("Verify the DUT sends the next fragment. Allow remaining fragments to time out")
    def run(driver: TestDriver) = {
      val second = driver.readAPDU().getOrElse(throw new Exception("Expected APDU"))
      if (second.ctrl.fir) throw new Exception("Unexpected fir bit")
      if (second.ctrl.seq != seq) throw new Exception("Unexpected sequence: " + second.ctrl.seq)
      if (!second.ctrl.fin) {
        if (!second.ctrl.con) throw new Exception("Expected con bit to be set")
      }
      driver.reporter.prompt("Allowing remaining fragments to timeout")
      Thread.sleep(timeout)
      Nil
    }
  }
}

/*
8.9.2.2 § Test Procedure
1. Request Class 0 data (Object 60 Variation 1) using Qualifier Code 0x06, if this will generate a multi-fragment response.
Alternately, generate enough event data to fill more than one fragment and request the appropriate class of data with
Qualifier Code 0x06.
2. Verify that the DUT responds with valid data.
3. Verify that the CON bit is set in the first fragment.
4. Verify the sequence number matches the request.
5. Wait the application confirmation timeout for the DUT. Do not send an application confirmation.
6. Verify the DUT does not send the next fragment because it did not receive confirmation of the first fragment.
7. Send the valid application layer confirmation.
8. Verify the DUT does not send the next fragment because it has timed out.
9. Request Class 0 data (Object 60 Variation 1) using Qualifier Code 0x06, if this will generate a multi-fragment response.
Alternately, generate enough event data to fill more than one fragment and request the appropriate class of data with
Qualifier Code 0x06.
10. Verify that the CON bit is set in the first fragment.
11. Wait a time period less than the application confirmation timeout. Verify the DUT does not send the second fragment
yet.
12. Send the valid application layer confirmation before the application confirmation timeout.
13. Verify that the DUT sends the second fragment (with FIR, FIN and sequence number set correctly).
14. Verify that the CON bit is set in the second fragment if a third fragment is expected.
15. If a third fragment is expected, skip to step 16. If not, first send a valid application layer confirmation and then a request
for multi-fragment data as in steps 1-3.
16. Send an application layer confirmation with an incorrect sequence number.
17. Wait the application layer confirmation timeout for the DUT. Do not send an application confirmation.
18. Verify the DUT does not send the next fragment because the confirmation it received was invalid.
19. Send the valid application layer confirmation.
20. Verify the DUT does not send the next fragment because it has timed out.
*/
