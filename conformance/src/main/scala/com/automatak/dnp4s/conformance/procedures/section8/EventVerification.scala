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
package com.automatak.dnp4s.conformance.procedures.section8

import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.dnp3.app.objects._
import com.automatak.dnp4s.conformance.procedures.AppSteps._
import com.automatak.dnp4s.conformance.TestStep
import annotation.tailrec
import com.automatak.dnp4s.dsl.UInt8
import com.automatak.dnp4s.conformance.procedures.{ AppSteps, AllowIIN }
import com.automatak.dnp4s.dnp3.app.OneByteCountOneByteIndexHeader
import com.automatak.dnp4s.dnp3.app.OneByteCountHeader
import com.automatak.dnp4s.dnp3.app.TwoByteCountTwoByteIndexHeader
import com.automatak.dnp4s.dnp3.app.TwoByteCountHeader

object EventVerification {
  /*
   Binary Input Change w/o Time 2 1 0x17,0x28
   Binary Input Change with Time 2 2 0x17,0x28
   Binary Input Change with Relative Time 2 3 † 0x17,0x28
   Double-bit Input Change Event w/o Time 4 1 0x17,0x28
   Double-bit Input Change Event with Time 4 2 0x17,0x28
   Double-bit Input Change Event with Relative time 4 3 0x17,0x28
   32-Bit Counter Change Event w/o Time 22 1 0x17,0x28
   16-Bit Counter Change Event w/o Time 22 2 0x17,0x28
   32-Bit Analog Change Event w/o Time 32 1 0x17,0x28
   16-Bit Analog Change Event w/o Time 32 2 0x17,0x28
   Time and Date CTO* 51 1 0x07
   Unsynchronized Time and Date CTO* 51 2 0x07
  */

  private val allowedWithIndexHeaders: Set[GroupVariation] = Set(
    Group2Var1,
    Group2Var2,
    Group4Var1,
    Group4Var2,
    Group22Var1,
    Group22Var2,
    Group32Var1,
    Group32Var2)

  private def validateIndexed(gv: GroupVariation): Boolean = allowedWithIndexHeaders.contains(gv)

  @tailrec
  def validate(total: Int, headers: List[ObjectHeader]): (List[ObjectHeader], Int) = headers match {
    // special case to to match CTO + Event w/ relative time sequences
    case OneByteCountHeader(_: CTOGroupVariation, UInt8(1), _) :: TwoByteCountHeader(_: CTOEventGroupVariation, count, _) :: remainder =>
      validate(total + count, remainder)
    case OneByteCountHeader(_: CTOGroupVariation, UInt8(1), _) :: OneByteCountHeader(_: CTOEventGroupVariation, count, _) :: remainder =>
      validate(total + count, remainder)
    case OneByteCountOneByteIndexHeader(gv: EventGroupVariation, count, data) :: remainder =>
      if (validateIndexed(gv)) validate(total + count, remainder)
      else (headers, total)
    case TwoByteCountTwoByteIndexHeader(gv: EventGroupVariation, count, data) :: remainder =>
      if (validateIndexed(gv)) validate(total + count, remainder)
      else (headers, total)
    case Nil =>
      (Nil, total)
    case _ =>
      (headers, total)
  }

  trait EventDataOnly extends ApduValidation with SingleFragment with TestStep {

    abstract override def description = super.description ::: List(myDescription)

    def myDescription: String = {
      "Verify the response only contains event data headers and objects with max of " + eventMax + " and a min of " + eventMin
    }

    def eventMax: Option[Int]
    def eventMin: Option[Int]

    abstract override def validate(apdu: Apdu): Unit = {
      super.validate(apdu)
      val count = EventVerification.validate(0, apdu.headers) match {
        case (Nil, total) => total
        case (remainder, total) => throw new Exception("Couldn't validate all headers: " + remainder)
      }
      eventMax.foreach(max => if (count > max) throw new Exception("Event limit of " + max + " exceeded"))
      eventMin.foreach(min => if (count < min) throw new Exception("Event minimum of " + min + " not reached"))
    }
  }

  trait OkIIN extends AllowIIN {
    def allowedMask = IIN(IIN.field(IIN.IIN14_NEED_TIME, IIN.IIN17_DEVICE_RESTART), 0.toByte)
  }

  case class VerifyEventData(seq: Byte, val eventMin: Option[Int] = None, val eventMax: Option[Int] = None) extends AppSteps.ReadAnyValidResponseUnconfirmed with EventDataOnly with OkIIN
  case class VerifyEventDataWithAtLeast1Header(seq: Byte, val eventMin: Option[Int] = None, val eventMax: Option[Int] = None, override val autoConfirm: Boolean = true) extends AppSteps.ReadAnyValidResponseUnconfirmed
    with EventDataOnly with AtLeast1Header with VerifyResponseRequestsConfirmation with OkIIN

  case class VerifyNullResponse(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with NoHeaders with OkIIN

}
