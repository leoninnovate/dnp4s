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
package com.automatak.dnp4s.conformance.procedures.section8.section856

import com.automatak.dnp4s.conformance.{ IntTestOption, TestOptions, TestProcedure }
import com.automatak.dnp4s.dnp3.Apdus
import com.automatak.dnp4s.dnp3.app.objects.{ Group60Var3, Group60Var4, Group60Var2 }
import com.automatak.dnp4s.conformance.procedures.{ CommonSteps, AppSteps }
import com.automatak.dnp4s.conformance.procedures.section8.EventVerification

object Section8562 extends TestProcedure {

  private val numClass1 = IntTestOption("section8562.numClass1", "Number of class1 events", Some(1), Some(3))
  private val numClass2 = IntTestOption("section8562.numClass2", "Number of class2 events", Some(1), Some(3))
  private val numClass3 = IntTestOption("section8562.numClass3", "Number of class3 events", Some(1), Some(3))

  def id = "8.5.6.2"
  def description = "Class Assignment Verification"
  override def options = List(numClass1, numClass2, numClass3)

  private def generateEvents(eventType: String, count: Int) =
    CommonSteps.PromptForUserAction("Generate " + count + " " + eventType + " events")

  def steps(options: TestOptions) = {

    val countClass1 = options.getInteger(numClass1.id)
    val countClass2 = options.getInteger(numClass2.id)
    val countClass3 = options.getInteger(numClass3.id)

    List(
      "1" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, Group60Var2, Group60Var3, Group60Var4))),
      "2" -> List(EventVerification.VerifyEventData(0)),
      "4" -> List(generateEvents("Class 1", countClass1)),
      "5" -> List(generateEvents("Class 2", countClass2)),
      "6" -> List(generateEvents("Class 3", countClass3)),
      "7" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, Group60Var2))),
      "8" -> List(EventVerification.VerifyEventDataWithAtLeast1Header(0, Some(countClass1), Some(countClass1))),
      "11" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, Group60Var2))),
      "12" -> List(EventVerification.VerifyNullResponse(0)),
      "13" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, Group60Var3))),
      "14" -> List(EventVerification.VerifyEventDataWithAtLeast1Header(0, Some(countClass2), Some(countClass2))),
      "17" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, Group60Var3))),
      "18" -> List(EventVerification.VerifyNullResponse(0)),
      "19" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, Group60Var4))),
      "20" -> List(EventVerification.VerifyEventDataWithAtLeast1Header(0, Some(countClass3), Some(countClass3))),
      "23" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, Group60Var4))),
      "24" -> List(EventVerification.VerifyNullResponse(0)),
      "25" -> List(AppSteps.SendApdu(Apdus.readAllObjects(0, Group60Var2, Group60Var3, Group60Var4))),
      "26" -> List(EventVerification.VerifyNullResponse(0)))
  }

}

/*
1. Issue a request for Object 60 Variations 2, 3 and 4 using the all data qualifier 0x06 to empty the device of pending
events.
2. Verify that only objects in table 8-4 are returned.
3. § If the response is not Null, verify that the device requests an application layer confirm.
4. Generate some Class 1 events.
5. Generate some Class 2 events.
6. Generate some Class 3 events.
7. Issue a request for Object 60 Variation 2 using the all data qualifier 0x06.
8. Verify that the device responds with only Class 1 events in a single response.
9. Verify that only objects in table 8-4 are returned.
10. § Verify that the device requests an application layer confirm.
11. Issue a request for Object 60 Variation 2 using the all data qualifier 0x06.
12. Verify that the device responds with a Null Response.
13. Issue a request for Object 60 Variation 3 using the all data qualifier 0x06.
14. Verify that the device responds with only Class 2 events in a single response.
15. Verify that only objects in table 8-4 are returned.
16. § Verify that the device requests an application layer confirm.
17. Issue a request for Object 60 Variation 3 using the all data qualifier 0x06.
18. Verify that the device responds with a Null Response.
19. Issue a request for Object 60 Variation 4 using the all data qualifier 0x06.
20. Verify that the device responds with only Class 3 events in a single response.
21. Verify that only objects in table 8-4 are returned.
22. § Verify that the device requests an application layer confirm.
23. Issue a request for Object 60 Variation 4 using the all data qualifier 0x06.
24. Verify that the device responds with a Null Response.
25. Issue a request for Object 60 Variations 2, 3 and 4 using the all data qualifier 0x06.
26. Verify that the device responds with a Null Response.
*/ 