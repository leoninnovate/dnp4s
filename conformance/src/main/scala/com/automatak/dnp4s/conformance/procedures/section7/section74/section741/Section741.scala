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
package com.automatak.dnp4s.conformance.procedures.section7.section74.section741

import com.automatak.dnp4s.conformance.TestSection

object Section741 extends TestSection {

  def id = "7.4.1"
  def description = "Analog Output Controls, Select Before Operate"

  override def subProcedures = List(
    Section74121a,
    Section74121b,
    Section74122a,
    Section74122b,
    Section74123,
    Section74124,
    Section74125,
    Section74126,
    Section74127,
    Section74128,
    Section74129,
    Section741210,
    Section741211,
    Section741212,
    Section741213,
    Section741214)

}
