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

import com.automatak.dnp4s.dnp3.app.{ TwoByteStartStopHeader, OneByteStartStopHeader, ObjectHeader, StaticGroupVariation }
import com.automatak.dnp4s.dnp3.app.objects._

/*
1 1* Binary Input—Packed format 129 (response) 00, 01 (start-stop)
1 2 Binary Input—With flags 129 (response) 00, 01 (start-stop)
10 2 Binary Output—Output status with flags 129 (response) 00, 01(start-stop)
20 1 Counter—32-bit with flag 129 (response) 00, 01 (start-stop)
20 2 Counter—16-bit with flag 129 (response) 00, 01 (start-stop)
20 5* Counter—32-bit without flag 129 (response) 00, 01 (start-stop)
20 6* Counter—16-bit without flag 129 (response) 00, 01 (start-stop)
21 1 Frozen Counter—32-bit with flag 129 (response) 00, 01 (start-stop)
21 2 Frozen Counter—16-bit with flag 129 (response) 00, 01 (start-stop)
21 9* Frozen Counter—32-bit without flag 129 (response) 00, 01 (start-stop)
21 10* Frozen Counter—16-bit without flag 129 (response) 00, 01 (start-stop)
30 1 Analog Input—32-bit with flag 129 (response) 00, 01 (start-stop)
30 2 Analog Input—16-bit with flag 129 (response) 00, 01 (start-stop)
30 3* Analog Input—32-bit without flag 129 (response) 00, 01 (start-stop)
30 4* Analog Input—16-bit without flag 129 (response) 00, 01 (start-stop)
40 2 Analog Output Status—16-bit with flag 129 (response) 00, 01 (start-stop)
 */

object StaticVerification {

  val allowGroupVariation: Set[StaticGroupVariation] = Set(
    Group1Var1,
    Group1Var2,
    Group10Var2,
    Group20Var1,
    Group20Var2,
    Group20Var5,
    Group20Var6,
    Group21Var1,
    Group21Var2,
    Group21Var9,
    Group21Var10,
    Group30Var1,
    Group30Var2,
    Group30Var3,
    Group30Var4,
    Group40Var2)

  private def validate(gv: StaticGroupVariation): Option[String] = {
    if (StaticVerification.allowGroupVariation.contains(gv)) None
    else Some("Unexpected group/variation")
  }

  def validate(header: ObjectHeader): Option[String] = header match {
    case OneByteStartStopHeader(gv: StaticGroupVariation, start, stop, data) => validate(gv)
    case TwoByteStartStopHeader(gv: StaticGroupVariation, start, stop, data) => validate(gv)
    case _ => Some("Unexpected header: " + header)
  }

}
