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
package com.automatak.dnp4s.conformance

import java.io.File
import javax.xml.bind.JAXBContext
import com.automatak.dnp4s.config.{ Option => ConfigOption, Filter, Config }
import scala.collection.JavaConversions._

case class Configuration(options: Map[String, String], filters: Map[String, Boolean])

object Configuration {

  def empty = Configuration(Map.empty[String, String], Map.empty[String, Boolean])

  def read(file: File): Configuration = {
    val unmarshaller = JAXBContext.newInstance(classOf[Config]).createUnmarshaller()
    val config = unmarshaller.unmarshal(file).asInstanceOf[Config]
    val options = config.getOption.map(o => o.getId -> o.getValue).toMap
    val filters = config.getFilter.map(o => o.getId -> o.isValue).toMap
    Configuration(options, filters)
  }

  def write(file: File, cfg: Configuration): Unit = {
    val marshaller = JAXBContext.newInstance(classOf[Config]).createMarshaller()
    val config = new Config()
    cfg.options.foreach {
      case (k, v) =>
        val o = new ConfigOption
        o.setId(k)
        o.setValue(v)
        config.getOption.add(o)
    }
    cfg.filters.foreach {
      case (k, v) =>
        val o = new Filter
        o.setId(k)
        o.setValue(v)
        config.getFilter.add(o)
    }
    marshaller.marshal(config, file)
  }
}
