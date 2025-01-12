/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.pinot.thirdeye.datasource.sql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.pinot.thirdeye.common.time.TimeSpec;
import org.apache.pinot.thirdeye.constant.MetricAggFunction;


public class SqlDataset {

  @JsonProperty
  private String tableName;
  @JsonProperty
  private String timeColumn;
  @JsonProperty
  private List<String> dimensions = new ArrayList<>();
  @JsonProperty
  private Map<String, MetricAggFunction> metrics;
  @JsonProperty
  private String granularity = "1DAYS";
  @JsonProperty
  private String timezone = TimeSpec.DEFAULT_TIMEZONE;
  @JsonProperty
  private String dataFile = "";
  @JsonProperty
  private String timeFormat = "EPOCH";
  @JsonProperty
  private String expectedDelay = "24_HOURS";
  @JsonProperty
  private String customPartition = "";


  public String getTimeColumn() {
    return timeColumn;
  }

  public List<String> getDimensions() {
    return dimensions;
  }

  public Map<String, MetricAggFunction> getMetrics() {
    return metrics;
  }

  public String getGranularity() {
    return granularity;
  }

  public String getTimezone() {
    return timezone;
  }

  public String getTimeFormat() {
    return timeFormat;
  }

  public String getTableName() {
    return tableName;
  }

  public String getDataFile() {
    return dataFile;
  }

  public String getExpectedDelay() {return expectedDelay;}

  public String getCustomPartition() {return customPartition;}

  @Override
  public String toString() {
    return "SqlDataset{" + "tableName='" + tableName + '\'' + ", timeColumn='" + timeColumn + '\'' + ", dimensions="
        + dimensions + ", metrics=" + metrics + ", granularity='" + granularity + '\'' + ", timezone='" + timezone
        + '\'' + ", timeFormat='" + timeFormat + '\'' + ", expectedDelay='" + expectedDelay + '\''
        + ", customPartition='" + customPartition + '\'' + '}'
        + ", dataFile='" + dataFile + '\'';
  }
}
