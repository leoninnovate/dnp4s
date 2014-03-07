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
package com.automatak.dnp4s.conformance.procedures.section7.section72.section721

import com.automatak.dnp4s.conformance.TestSection

object Section721 extends TestSection {

  def id = "7.2.1"
  def description = "Select Before Operate"

  override def subProcedures = List(
    Section72121a,
    Section72121b,
    Section72122a,
    Section72122b,
    Section72123,
    Section72124,
    Section72125,
    Section72126,
    Section72127,
    Section72128,
    Section72129,
    Section721210,
    Section721211,
    Section721212,
    Section721213,
    Section721214,
    Section721215,
    Section721216,
    Section721217)

}
