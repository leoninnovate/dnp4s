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

import com.automatak.dnp4s.conformance.{ TestStep, TestOptions, TestProcedure }
import com.automatak.dnp4s.dnp3.Apdus
import com.automatak.dnp4s.dnp3.app.objects.{ Group60Var1, Group60Var3, Group60Var4, Group60Var2 }
import com.automatak.dnp4s.conformance.procedures.{ CommonSteps, AppSteps }
import com.automatak.dnp4s.conformance.procedures.section8.{ StaticVerification, EventVerification }
import com.automatak.dnp4s.conformance.procedures.section8.EventVerification.OkIIN
import com.automatak.dnp4s.conformance.procedures.AppSteps.ApduValidation
import com.automatak.dnp4s.dnp3.app.{ ObjectHeader, Apdu }

object Section85522 extends TestProcedure {
  def id = "8.5.5.2.2"
  def description = "Multiple Object Request, Class 1, 2, 3, and 0"

  private val readClass234 = Apdus.readAllObjects(0, Group60Var2, Group60Var3, Group60Var4)
  private val readClass1234 = Apdus.readAllObjects(0, Group60Var2, Group60Var3, Group60Var4, Group60Var1)

  def steps(options: TestOptions) = List(
    "1" -> List(AppSteps.SendApdu(readClass234)),
    "2" -> List(EventVerification.VerifyEventData(0)),
    "4" -> List(CommonSteps.PromptForUserAction("Generate known event data that includes events from all three classes")),
    "5" -> List(AppSteps.SendApdu(readClass1234)),
    "6" -> List(VerifyAllGroup60(0, 3)))

  trait VerifyEventDataFollowedByStaticData extends ApduValidation with TestStep {

    def minEvents: Int

    abstract override def description = super.description :::
      List("Verify the headers contain event data followed by static data, with a minimum of " + minEvents + " events")

    private def validateEventsFollowedByStatic(headers: List[ObjectHeader]): Int = {
      EventVerification.validate(0, headers) match {
        case (Nil, total) => throw new Exception("Expected some static headers, but received all event data")
        case (remainingHeaders, totalEvents) =>
          remainingHeaders.foreach { header =>
            StaticVerification.validate(header).foreach(err => throw new Exception(err))
          }
          totalEvents
      }
    }

    abstract override def validate(apdus: List[Apdu]): Unit = {
      super.validate(apdus)
      val allHeaders = apdus.map(_.headers).flatten
      val totalEvents = validateEventsFollowedByStatic(allHeaders)
      if (totalEvents < minEvents) throw new Exception("Headers only contained " + totalEvents + ", " + minEvents + " required")
    }
  }

  case class VerifyAllGroup60(seq: Byte, minEvents: Int) extends AppSteps.ReadAnyValidResponseUnconfirmed with VerifyEventDataFollowedByStaticData with OkIIN

}

/*
8.5.5.2.2 Multiple Object Request, Class 1, 2, 3, and 0
1. Issue a request for Object 60 Variations 2, 3, and 4 using the all data qualifier 0x06.
2. § If the response is not Null, verify that the device requests an application layer confirm.
3. If requested, issue an application layer confirm to empty the device of pending events.
4. Generate known event data that includes events from classes 1, 2, and 3.
5. Issue a request for Object 60 Variations 2, 3, 4, and 1 using the all data qualifier 0x06.
6. Verify that the device responds with all the event data as described in the Desired Behavior.
7. Verify that all static data is returned after all event data in the same response.
8. Verify that the response has the same application sequence number as the request. If the request generates multiple
application fragments verify that the application sequence number of each subsequent fragment increments by 1 modulo
16.
9. Verify that only objects in tables 8-3 and 8-4 are returned.
10. Verify that the data is reported using either 8 bit indexing (qualifier 0x17) or 16 bit indexing (qualifier 0x28) for event
data and 8 bit start/stop indexing (qualifier 0x00) or 16 bit start/stop indexing (qualifier 0x01) for static data.
11. Verify that flag behavior complies with Section 2, Note #5.
12. § Verify that the device requests an application layer confirm.
13. Issue an application layer confirm to empty the device of pending events. */
