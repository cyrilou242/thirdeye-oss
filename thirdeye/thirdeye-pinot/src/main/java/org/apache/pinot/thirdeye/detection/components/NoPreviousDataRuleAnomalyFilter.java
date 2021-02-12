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

package org.apache.pinot.thirdeye.detection.components;

import org.apache.pinot.thirdeye.dashboard.resources.v2.BaselineParsingUtils;
import org.apache.pinot.thirdeye.dataframe.DataFrame;
import org.apache.pinot.thirdeye.dataframe.util.MetricSlice;
import org.apache.pinot.thirdeye.datalayer.dto.MergedAnomalyResultDTO;
import org.apache.pinot.thirdeye.detection.InputDataFetcher;
import org.apache.pinot.thirdeye.detection.annotation.Components;
import org.apache.pinot.thirdeye.detection.annotation.DetectionTag;
import org.apache.pinot.thirdeye.detection.spec.NoPreviousDataRuleAnomalyFilterSpec;
import org.apache.pinot.thirdeye.detection.spi.components.AnomalyFilter;
import org.apache.pinot.thirdeye.detection.spi.model.InputDataSpec;
import org.apache.pinot.thirdeye.rootcause.impl.MetricEntity;
import org.apache.pinot.thirdeye.rootcause.timeseries.Baseline;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * No previous data anomaly filter. This rule filters the anomalies
 * if there is no data (0 or null) in the given baseline.
 */
@Components(type = "NO_PREVIOUS_DATA_FILTER", tags = {DetectionTag.RULE_FILTER})
public class NoPreviousDataRuleAnomalyFilter implements AnomalyFilter<NoPreviousDataRuleAnomalyFilterSpec> {
    private static final Logger LOG = LoggerFactory.getLogger(NoPreviousDataRuleAnomalyFilter.class);
    private InputDataFetcher dataFetcher;
    private Baseline baseline;
    private double zeroPrecision;

    @Override
    public boolean isQualified(MergedAnomalyResultDTO anomaly) {
        MetricEntity me = MetricEntity.fromURN(anomaly.getMetricUrn());
        List<MetricSlice> slices = new ArrayList<>();
        MetricSlice currentSlice = MetricSlice.from(me.getId(), anomaly.getStartTime(), anomaly.getEndTime(), me.getFilters());
        // customize baseline offset
        if (baseline != null) {
            slices.addAll(this.baseline.scatter(currentSlice));
        }

        Map<MetricSlice, DataFrame> aggregates =
                this.dataFetcher.fetchData(new InputDataSpec().withAggregateSlices(slices)).getAggregates();

        double baselineValue;
        if (baseline == null) {
            baselineValue = anomaly.getAvgBaselineVal();
        } else {
            try {
                baselineValue = this.baseline.gather(currentSlice, aggregates).getDouble(DataFrame.COL_VALUE, 0);
            } catch (Exception e) {
                baselineValue = anomaly.getAvgBaselineVal();
                LOG.warn("Unable to fetch baseline for anomaly {}. start = {} end = {} filters = {}. Using anomaly"
                        + " baseline ", anomaly.getId(), anomaly.getStartTime(), anomaly.getEndTime(), me.getFilters(), e);
            }
        }

        return isNotZero(baselineValue);
    }

    private boolean isNotZero(double value) {
            return (value > zeroPrecision) || (value < -zeroPrecision);
    }

    @Override
    public void init(NoPreviousDataRuleAnomalyFilterSpec spec, InputDataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
        // customize baseline offset
        if (StringUtils.isNotBlank(spec.getOffset())) {
            this.baseline = BaselineParsingUtils.parseOffset(spec.getOffset(), spec.getTimezone());
        }
        this.zeroPrecision = spec.getZeroPrecision();
    }
}
