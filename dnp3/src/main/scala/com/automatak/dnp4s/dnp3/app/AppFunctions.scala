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
package com.automatak.dnp4s.dnp3.app

object AppCtrl {
  val firMask = 0x80
  val finMask = 0x40
  val conMask = 0x20
  val unsMask = 0x10
  val seqMask = 0x0F

  def nextSeq(seq: Byte): Byte = ((seq + 1) % 16).toByte

  def apply(byte: Byte): AppCtrl = {
    val fir = (byte & firMask) != 0
    val fin = (byte & finMask) != 0
    val con = (byte & conMask) != 0
    val uns = (byte & unsMask) != 0
    val seq = (byte & seqMask).toByte

    AppCtrl(fir, fin, con, uns, seq)
  }
}

case class AppCtrl(fir: Boolean, fin: Boolean, con: Boolean, uns: Boolean, seq: Byte) {
  import AppCtrl._
  import com.automatak.dnp4s.dsl.toHexString

  def toByte: Byte = {
    val firData = if (fir) firMask else 0
    val finData = if (fin) finMask else 0
    val conData = if (con) conMask else 0
    val unsData = if (uns) unsMask else 0
    val seqData = seq & seqMask

    (firData | finData | conData | unsData | seqData).toByte
  }

  override def toString: String =
    "fir: " + fir + " fin: " + fin + " con: " + con + " uns: " + uns + " seq: " + toHexString(seq)
}

object AppFunctions {

  val confirm: Byte = 0x00
  val read: Byte = 0x01
  val write: Byte = 0x02
  val select: Byte = 0x03
  val operate: Byte = 0x04
  val directOp: Byte = 0x05
  val directOpNoRsp: Byte = 0x06
  val freeze: Byte = 0x07
  val freezeNoRsp: Byte = 0x08
  val freezeClear: Byte = 0x9
  val freezeClearNoRsp: Byte = 0x0A
  val freezeAtTime: Byte = 0x0B
  val freezeAtTimeNoRsp: Byte = 0x0C
  val coldRestart: Byte = 0x0D
  val warmRestart: Byte = 0x0E
  val initData: Byte = 0x0F
  val initApp: Byte = 0x10
  val startApp: Byte = 0x11
  val stopApp: Byte = 0x12
  val saveConfig: Byte = 0x13
  val enableUnsol: Byte = 0x14
  val disableUnsol: Byte = 0x15
  val assignClass: Byte = 0x16
  val delayMeas: Byte = 0x17
  val recordCurrentTime: Byte = 0x18
  val openFile: Byte = 0x19
  val closeFile: Byte = 0x1A
  val deleteFile: Byte = 0x1B
  val getFileInfo: Byte = 0x1C
  val authFile: Byte = 0x1D
  val abortFile: Byte = 0x1E
  val rsp: Byte = (0x81).toByte
  val unsolRsp: Byte = (0x82).toByte

  def funcToString(func: Byte): String = func match {
    case `confirm` => "Confirm"
    case `read` => "Read"
    case `write` => "Write"
    case `select` => "Select"
    case `operate` => "Operate"
    case `directOp` => "DirectOperate"
    case `directOpNoRsp` => "DirectOperateNoResponse"
    case `freeze` => "Freeze"
    case `freezeNoRsp` => "FreezeNoResponse"
    case `freezeClear` => "FreezeClear"
    case `freezeClearNoRsp` => "FreezeClearNoResponse"
    case `freezeAtTime` => "FreezeAtTime"
    case `freezeAtTimeNoRsp` => "FreezeAtTimeNoResponse"
    case `coldRestart` => "ColdRestart"
    case `warmRestart` => "WarmRestart"
    case `initData` => "InitData"
    case `initApp` => "InitApp"
    case `startApp` => "StartApp"
    case `stopApp` => "StopApp"
    case `saveConfig` => "SaveConfig"
    case `enableUnsol` => "EnableUnsol"
    case `disableUnsol` => "DisableUnsol"
    case `assignClass` => "AssignClass"
    case `delayMeas` => "DelayMeas"
    case `recordCurrentTime` => "RecordCurrentTime"
    case `openFile` => "OpenFile"
    case `closeFile` => "CloseFile"
    case `deleteFile` => "DeleteFile"
    case `getFileInfo` => "GetFileInfo"
    case `authFile` => "AuthFile"
    case `abortFile` => "AbortFile"
    case `rsp` => "Response"
    case `unsolRsp` => "UnsolicitedResponse"
    case _ => "Unknown"
  }

  val codes = List(
    confirm,
    read,
    write,
    select,
    operate,
    directOp,
    directOpNoRsp,
    freeze,
    freezeNoRsp,
    freezeClear,
    freezeClearNoRsp,
    freezeAtTime,
    freezeAtTimeNoRsp,
    //coldRestart,
    //warmRestart,
    initData,
    initApp,
    startApp,
    stopApp,
    saveConfig,
    enableUnsol,
    disableUnsol,
    assignClass,
    delayMeas,
    recordCurrentTime,
    openFile,
    closeFile,
    deleteFile,
    getFileInfo,
    authFile,
    abortFile)

}