/**
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
package org.apache.pinot.common.metrics;

import org.apache.pinot.common.Utils;


/**
 * Enumeration containing all the meters exposed by the Pinot server.
 */
public enum ServerMeter implements AbstractMetrics.Meter {
  QUERIES("queries", true),
  UNCAUGHT_EXCEPTIONS("exceptions", true),
  REQUEST_DESERIALIZATION_EXCEPTIONS("exceptions", true),
  RESPONSE_SERIALIZATION_EXCEPTIONS("exceptions", true),
  SCHEDULING_TIMEOUT_EXCEPTIONS("exceptions", true),
  QUERY_EXECUTION_EXCEPTIONS("exceptions", false),
  HELIX_ZOOKEEPER_RECONNECTS("reconnects", true),
  DELETED_SEGMENT_COUNT("segments", false),
  REALTIME_ROWS_CONSUMED("rows", true),
  INVALID_REALTIME_ROWS_DROPPED("rows", false),
  REALTIME_CONSUMPTION_EXCEPTIONS("exceptions", true),
  REALTIME_OFFSET_COMMITS("commits", true),
  REALTIME_OFFSET_COMMIT_EXCEPTIONS("exceptions", false),
  ROWS_WITH_ERRORS("rows", false),
  LLC_CONTROLLER_RESPONSE_NOT_SENT("messages", true),
  LLC_CONTROLLER_RESPONSE_COMMIT("messages", true),
  LLC_CONTROLLER_RESPONSE_HOLD("messages", true),
  LLC_CONTROLLER_RESPONSE_CATCH_UP("messages", true),
  LLC_CONTROLLER_RESPONSE_DISCARD("messages", true),
  LLC_CONTROLLER_RESPONSE_KEEP("messages", true),
  LLC_CONTROLLER_RESPONSE_NOT_LEADER("messages", true),
  LLC_CONTROLLER_RESPONSE_FAILED("messages", true),
  LLC_CONTROLLER_RESPONSE_COMMIT_SUCCESS("messages", true),
  LLC_CONTROLLER_RESPONSE_COMMIT_CONTINUE("messages", true),
  LLC_CONTROLLER_RESPONSE_PROCESSED("messages", true),
  LLC_CONTROLLER_RESPONSE_UPLOAD_SUCCESS("messages", true),
  NUM_DOCS_SCANNED("rows", false),
  NUM_ENTRIES_SCANNED_IN_FILTER("entries", false),
  NUM_ENTRIES_SCANNED_POST_FILTER("entries", false),
  NUM_SEGMENTS_QUERIED("numSegmentsQueried", false),
  NUM_SEGMENTS_PROCESSED("numSegmentsProcessed", false),
  NUM_SEGMENTS_MATCHED("numSegmentsMatched", false),
  NUM_MISSING_SEGMENTS("segments", false),
  RELOAD_FAILURES("segments", false),
  REFRESH_FAILURES("segments", false),
  NUM_GROUPS_IGNORED_PRE_COMBINE("numGroupsIgnoredPreCombine", false),
  NUM_GROUPS_IGNORED_IN_COMBINE("numGroupsIgnoredInCombine", false),
  NUM_GROUPS_AGGR_IN_COMBINE("numGroupsAggrInCombine", false),
  NUM_GROUPS_IGNORED_POST_COMBINE("numGroupsIgnoredPostCombine", false);

  private final String meterName;
  private final String unit;
  private final boolean global;

  ServerMeter(String unit, boolean global) {
    this.unit = unit;
    this.global = global;
    this.meterName = Utils.toCamelCase(name().toLowerCase());
  }

  @Override
  public String getMeterName() {
    return meterName;
  }

  @Override
  public String getUnit() {
    return unit;
  }

  /**
   * Returns true if the metric is global (not attached to a particular resource)
   *
   * @return true if the metric is global
   */
  @Override
  public boolean isGlobal() {
    return global;
  }
}
