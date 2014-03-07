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
package com.automatak.dnp4s.conformance.procedures.section8.section855

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }
import com.automatak.dnp4s.dnp3.Apdus
import com.automatak.dnp4s.dnp3.app.objects.{ Group60Var3, Group60Var4, Group60Var2 }
import com.automatak.dnp4s.conformance.procedures.{ CommonSteps, AppSteps }
import com.automatak.dnp4s.conformance.procedures.section8.EventVerification

object Section85521 extends TestProcedure {
  def id = "8.5.5.2.1"
  def description = "Multiple Object Request, Class 1, 2, and 3"

  private val readClass234 = Apdus.readAllObjects(0, Group60Var2, Group60Var3, Group60Var4)

  def steps(options: TestOptions) = List(
    "1" -> List(AppSteps.SendApdu(readClass234)),
    "2" -> List(EventVerification.VerifyEventData(0)),
    "4" -> List(CommonSteps.PromptForUserAction("Generate known event data that includes events from all three classes")),
    "5" -> List(AppSteps.SendApdu(readClass234)),
    "6" -> List(EventVerification.VerifyEventDataWithAtLeast1Header(0, Some(3))))
}

/*
8.5.5.2.1 Multiple Object Request, Class 1, 2, and 3
1. Issue a request for Object 60 Variations 2, 3, and 4 using the all data qualifier 0x06.
2. § If the response is not Null, verify that the device requests an application layer confirm.
3. If requested, issue an application layer confirm to empty the device of pending events.
4. Generate known event data that includes events from all three classes.
5. Issue a request for Object 60 Variations 2, 3, and 4 using the all data qualifier 0x06.
6. Verify that the device responds with all the event data as described in the Desired Behavior.
7. Verify that only objects in table 8-4 are returned and that the events are in time order (oldest first).
8. Verify that the data is reported using either 8 bit indexing (qualifier 0x17) or 16 bit indexing (qualifier 0x28).
9. Verify that flag behavior complies with Section 2, Note #5.
10. § Verify that the device requests an application layer confirm.
11. Issue an application layer confirm to empty the device of pending events.
 */
