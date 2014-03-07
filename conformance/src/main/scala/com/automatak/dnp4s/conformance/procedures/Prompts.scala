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
package com.automatak.dnp4s.conformance.procedures

object Prompts {
  val cyclePower = "Cycle power to the DUT"
  val configDataLinkNoConfirmation = "Configure the DUT to NOT request data link confirm when transmitting"
  val configDataLinkConfirmation = "Configure the DUT to request data link confirm when transmitting"
  val configDataLinkRetryReasonable = "Set retries to a reasonable value (1-5)"
  val enableSelfAddressSupport = "ENABLE data link layer self-address"
  val disableSelfAddressSupport = "DISABLE data link layer self-address"
  val disableControl = "Configure the DUT such that all Binary Output points are uninstalled or disabled"
  val disableAnalogOutput = "Configure the DUT such that all Analog Output points are uninstalled or disabled"

  val assignAllPointsToEvents = List("Configure the device such that all installed Binary Input, Counter, and Analog Input points",
    "are assigned to event classes, ensuring that points are assigned to each supported event class",
    "If possible, configure the device such that all installed Binary Output Status, Analog Output",
    "Status, and Frozen Counter points are assigned to event classes")

  val generate10EventsWithBinaryAnalogCounter = List(
    "Generate at least ten known Level 2-supported events from all supported classes, including:",
    "At least one Binary Input event if the DUT supports Binary Input points",
    "At least one Counter event if the DUT supports Binary Counter points",
    "At least one Analog Input event if the DUT supports Analog Input points")

  val assignNoClass = "Configure the device such that some inputs are removed from all Classes (i.e. assign them to no Class)."

  val verify10ValueStateFlags = "For at least ten of the reported points of each point type: Verify that the value/state and flags are correct"
}
