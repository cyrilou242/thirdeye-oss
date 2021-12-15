/**
 * Copyright (C) 2014-2018 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.pinot.thirdeye.datasource.sql;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.pinot.thirdeye.common.time.TimeGranularity;
import org.apache.pinot.thirdeye.common.time.TimeSpec;
import org.apache.pinot.thirdeye.constant.MetricAggFunction;
import org.apache.pinot.thirdeye.datalayer.bao.DAOTestBase;
import org.apache.pinot.thirdeye.datalayer.dto.DatasetConfigDTO;
import org.apache.pinot.thirdeye.datalayer.dto.MetricConfigDTO;
import org.apache.pinot.thirdeye.datasource.DAORegistry;
import org.apache.pinot.thirdeye.datasource.MetricFunction;
import org.apache.pinot.thirdeye.datasource.ThirdEyeCacheRegistry;
import org.apache.pinot.thirdeye.datasource.ThirdEyeRequest;
import org.apache.pinot.thirdeye.datasource.cache.MetricDataset;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class TestSqlUtils {

  private final String dataset = "mysql.db.table";
  private final String metric = "metric";

  private MetricDataset metricDataset;
  private MetricFunction metricFunction;
  private DAOTestBase daoTestBase;

  @BeforeMethod
  public void beforeMethod() throws Exception {
    this.daoTestBase = DAOTestBase.getInstance();
    this.metricDataset = new MetricDataset(metric, dataset);

    LoadingCache<String, DatasetConfigDTO> mockDatasetConfigCache = Mockito.mock(LoadingCache.class);
    Mockito.when(mockDatasetConfigCache.get(this.dataset)).thenReturn(new DatasetConfigDTO());

    LoadingCache<MetricDataset, MetricConfigDTO> mockMetricConfigCache = Mockito.mock(LoadingCache.class);
    Mockito.when(mockMetricConfigCache.get(this.metricDataset)).thenReturn(new MetricConfigDTO());

    ThirdEyeCacheRegistry.getInstance().registerDatasetConfigCache(mockDatasetConfigCache);
    ThirdEyeCacheRegistry.getInstance().registerMetricConfigCache(mockMetricConfigCache);

    MetricConfigDTO metricConfigDTO = new MetricConfigDTO();
    metricConfigDTO.setDataset(this.dataset);
    metricConfigDTO.setName(this.metricDataset.getMetricName());
    metricConfigDTO.setAlias(this.metricDataset.getDataset() + "::" + this.metricDataset.getMetricName());

    metricFunction = new MetricFunction();
    metricFunction.setDataset(dataset);
    metricFunction.setMetricId(1L);
    metricFunction.setMetricName(metric);
    metricFunction.setFunctionName(MetricAggFunction.SUM);

    DAORegistry.getInstance().getMetricConfigDAO().save(metricConfigDTO);
  }

  @AfterMethod
  public void afterMethod() {
    try {
      this.daoTestBase.cleanup();
    } catch (Exception ignore) {}
    DateTimeUtils.setCurrentMillisSystem();
  }

  @Test
  public void testSqlWithExplicitLimit() {
    TimeGranularity timeGranularity = new TimeGranularity(1, TimeUnit.DAYS);
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC);
    ThirdEyeRequest request = ThirdEyeRequest.newBuilder()
        .setDataSource(this.dataset)
        .setLimit(100)
        .setGroupBy("country")
        .setStartTimeInclusive(DateTime.parse("2020-05-01", formatter))
        .setEndTimeExclusive(DateTime.parse("2020-05-01", formatter))
        .setGroupByTimeGranularity(timeGranularity)
        .build("");

    String timeFormat = TimeSpec.SINCE_EPOCH_FORMAT;
    TimeSpec timeSpec = new TimeSpec("date", timeGranularity, timeFormat);
    String actualSql = SqlUtils.getSql(request, this.metricFunction, HashMultimap.create(), timeSpec, this.dataset, new HashMap<>());
    String expected = "SELECT date, country, SUM(metric) FROM table WHERE  date = 18383 GROUP BY date, country ORDER BY SUM(metric) DESC LIMIT 100";
    Assert.assertEquals(actualSql, expected);
  }

  @Test
  public void testSqlWithoutExplicitLimit() {
    TimeGranularity timeGranularity = new TimeGranularity(1, TimeUnit.DAYS);
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC);
    ThirdEyeRequest request = ThirdEyeRequest.newBuilder()
        .setDataSource(this.dataset)
        .setGroupBy("country")
        .setStartTimeInclusive(DateTime.parse("2020-05-01", formatter))
        .setEndTimeExclusive(DateTime.parse("2020-05-01", formatter))
        .setGroupByTimeGranularity(timeGranularity)
        .build("");

    String timeFormat = TimeSpec.SINCE_EPOCH_FORMAT;
    TimeSpec timeSpec = new TimeSpec("date", timeGranularity, timeFormat);
    String actual = SqlUtils.getSql(request, this.metricFunction, HashMultimap.create(), timeSpec, this.dataset, new HashMap<>());
    String expected = "SELECT date, country, SUM(metric) FROM table WHERE  date = 18383 GROUP BY date, country LIMIT 100000";
    Assert.assertEquals(actual, expected);
  }

  @Test
  public void testGetDatePartitionClauseForBigQuery() {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    DateTime startTime = formatter.parseDateTime("2020-01-05 12:00:05");
    DateTime endTime = formatter.parseDateTime("2020-01-15 18:00:05");
    String actual = SqlUtils.getDatePartitionClause(startTime, endTime, "BigQuery", null);

    String expected = "_PARTITIONTIME >= '2020-01-04 00:00:00' AND _PARTITIONTIME <= '2020-01-17 00:00:00'";
    Assert.assertEquals(actual, expected);
  }

  @Test
  public void testGetMaxDataTimeSQLForBigQuery() {
    String timeColumn = "date";
    String tableName = "table_name";

    DateTimeUtils.setCurrentMillisFixed(1612137600000L); // 2021-02-01 00:00:00
    String actual = SqlUtils.getMaxDataTimeSQL(timeColumn, tableName, "BigQuery", new HashMap<>());

    String expected = "SELECT MAX(" + timeColumn + ") FROM " + tableName + " WHERE " + "_PARTITIONTIME >= '2020-11-30 00:00:00' AND _PARTITIONTIME <= '2021-02-10 00:00:00'";
    Assert.assertEquals(actual, expected);

  }

  @Test
  public void testGetDimensionFiltersSQLForBigQuery() {
    String dimension = "myDimension";
    String tableName = "table_name";

    DateTimeUtils.setCurrentMillisFixed(1612137600000L); // 2021-02-01 00:00:00
    String actual = SqlUtils.getDimensionFiltersSQL(dimension, tableName, "BigQuery", new HashMap<>());

    String expected = "SELECT DISTINCT(" + dimension + ") FROM " + tableName + " WHERE " + "_PARTITIONTIME >= '2020-11-30 00:00:00' AND _PARTITIONTIME <= '2021-03-03 00:00:00'";
    Assert.assertEquals(actual, expected);

  }

  @Test
  public void testGetMaxDataTimeSQLForBigQueryCustomPartition() {
    String timeColumn = "date";
    String tableName = "table_name";

    DateTimeUtils.setCurrentMillisFixed(1612137600000L); // 2021-02-01 00:00:00
    Map<String, String> properties = new HashMap<>();
    properties.put("customPartition", "DATE(processingTime)");  //customPartition is the key name for custom properties
    String actual = SqlUtils.getMaxDataTimeSQL(timeColumn, tableName, "BigQuery", properties);

    String expected = "SELECT MAX(" + timeColumn + ") FROM " + tableName + " WHERE " + "DATE(processingTime) >= '2020-11-30 00:00:00' AND DATE(processingTime) <= '2021-02-10 00:00:00'";
    Assert.assertEquals(actual, expected);

  }

  @Test
  public void testGetDimensionFiltersSQLForBigQueryCustomPartition() {
    String dimension = "myDimension";
    String tableName = "table_name";

    DateTimeUtils.setCurrentMillisFixed(1612137600000L); // 2021-02-01 00:00:00
    Map<String, String> properties = new HashMap<>();
    properties.put("customPartition", "DATE(processingTime)");  //customPartition is the key name for custom properties
    String actual = SqlUtils.getDimensionFiltersSQL(dimension, tableName, "BigQuery", properties);

    String expected = "SELECT DISTINCT(" + dimension + ") FROM " + tableName + " WHERE " + "DATE(processingTime) >= '2020-11-30 00:00:00' AND DATE(processingTime) <= '2021-03-03 00:00:00'";
    Assert.assertEquals(actual, expected);

  }
}
