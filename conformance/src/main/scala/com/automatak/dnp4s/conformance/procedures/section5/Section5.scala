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
package com.automatak.dnp4s.conformance.procedures.section5

import com.automatak.dnp4s.conformance.TestSection
import section51.Section51
import section52.Section52
import section53.Section53
import section54.Section54
import section55.Section55
import section56.Section56

object Section5 extends TestSection {

  def id: String = "5"
  def description: String = "Data Link Layer"
  override def subProcedures = List(Section51, Section52, Section53, Section54, Section55, Section56)

}
