package org.apache.pinot.thirdeye.detection.components;

import org.apache.pinot.thirdeye.dataframe.DataFrame;
import org.apache.pinot.thirdeye.dataframe.util.MetricSlice;
import org.apache.pinot.thirdeye.detection.DataProvider;
import org.apache.pinot.thirdeye.detection.DefaultInputDataFetcher;
import org.apache.pinot.thirdeye.detection.DetectionTestUtils;
import org.apache.pinot.thirdeye.detection.MockDataProvider;
import org.apache.pinot.thirdeye.detection.spec.NoPreviousDataRuleAnomalyFilterSpec;
import org.apache.pinot.thirdeye.detection.spi.components.AnomalyFilter;
import org.apache.pinot.thirdeye.rootcause.timeseries.Baseline;
import org.apache.pinot.thirdeye.rootcause.timeseries.BaselineAggregate;
import org.apache.pinot.thirdeye.rootcause.timeseries.BaselineAggregateType;
import org.joda.time.DateTimeZone;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoPreviousDataRuleAnomalyFilterTest {
    private static final String METRIC_URN = "thirdeye:metric:123";
    private static final long CONFIG_ID = 125L;
    private static final double ZERO_NUM = 0.;
    private static final double SMALL_NUM = 0.0001;
    private static final double VERY_SMALL_NUM = 0.00001;

    private DataProvider testDataProvider;
    private Baseline baseline;

    @BeforeMethod
    public void beforeMethod() {
        this.baseline = BaselineAggregate.fromWeekOverWeek(BaselineAggregateType.MEAN, 1, 1, DateTimeZone.forID("UTC"));

        MetricSlice slice1 = MetricSlice.from(123L, 1555570800000L, 1555693200000L);
        MetricSlice baselineSlice1 = this.baseline.scatter(slice1).get(0);
        MetricSlice slice2 = MetricSlice.from(123L, 1554163200000L, 1554249600000L);
        MetricSlice baselineSlice2 = this.baseline.scatter(slice2).get(0);
        MetricSlice slice3 = MetricSlice.from(123L, 1554076800000L, 1554163200000L);
        MetricSlice baselineSlice3 = this.baseline.scatter(slice3).get(0);

        Map<MetricSlice, DataFrame> aggregates = new HashMap<>();
        aggregates.put(slice1,
                new DataFrame().addSeries(DataFrame.COL_TIME, slice1.getStart()).addSeries(
                        DataFrame.COL_VALUE, 100).setIndex(DataFrame.COL_TIME));
        aggregates.put(baselineSlice1,
                new DataFrame().addSeries(DataFrame.COL_TIME, baselineSlice1.getStart()).addSeries(
                        DataFrame.COL_VALUE, ZERO_NUM).setIndex(DataFrame.COL_TIME));
        aggregates.put(slice2,
                new DataFrame().addSeries(DataFrame.COL_VALUE, 100).addSeries(DataFrame.COL_TIME, slice2.getStart()).setIndex(
                        DataFrame.COL_TIME));
        aggregates.put(baselineSlice2,
                new DataFrame().addSeries(DataFrame.COL_VALUE, VERY_SMALL_NUM).addSeries(DataFrame.COL_TIME, baselineSlice2.getStart()).setIndex(
                        DataFrame.COL_TIME));
        aggregates.put(slice3,
                new DataFrame().addSeries(DataFrame.COL_VALUE, 100).addSeries(DataFrame.COL_TIME, slice3.getStart()).setIndex(
                        DataFrame.COL_TIME));
        aggregates.put(baselineSlice3,
                new DataFrame().addSeries(DataFrame.COL_VALUE, SMALL_NUM).addSeries(DataFrame.COL_TIME, baselineSlice3.getStart()).setIndex(
                        DataFrame.COL_TIME));

        this.testDataProvider = new MockDataProvider().setAggregates(aggregates);
    }

    @Test
    public void testNoPreviousDataFilterDefaultConfig() {
        NoPreviousDataRuleAnomalyFilterSpec spec = new NoPreviousDataRuleAnomalyFilterSpec();
        spec.setOffset("mean1w");
        AnomalyFilter filter = new NoPreviousDataRuleAnomalyFilter();
        filter.init(spec, new DefaultInputDataFetcher(this.testDataProvider, CONFIG_ID));
        List<Boolean> results =
                Stream.of(DetectionTestUtils.makeAnomaly(1555570800000L, 1555693200000L, CONFIG_ID, METRIC_URN, 100),
                        DetectionTestUtils.makeAnomaly(1554163200000L, 1554249600000L, CONFIG_ID, METRIC_URN, 100),
                        DetectionTestUtils.makeAnomaly(1554076800000L, 1554163200000L, CONFIG_ID, METRIC_URN, 100))
                        .map(anomaly -> filter.isQualified(anomaly))
                        .collect(Collectors.toList());
        Assert.assertEquals(results, Arrays.asList(false, true, true));
    }

    @Test
    public void testNoPreviousDataFilterWithZeroPrecision() {
        NoPreviousDataRuleAnomalyFilterSpec spec = new NoPreviousDataRuleAnomalyFilterSpec();
        spec.setOffset("mean1w");
        spec.setZeroPrecision(VERY_SMALL_NUM);
        AnomalyFilter filter = new NoPreviousDataRuleAnomalyFilter();
        filter.init(spec, new DefaultInputDataFetcher(this.testDataProvider, CONFIG_ID));
        List<Boolean> results =
                Stream.of(DetectionTestUtils.makeAnomaly(1555570800000L, 1555693200000L, CONFIG_ID, METRIC_URN, 100),
                        DetectionTestUtils.makeAnomaly(1554163200000L, 1554249600000L, CONFIG_ID, METRIC_URN, 100),
                        DetectionTestUtils.makeAnomaly(1554076800000L, 1554163200000L, CONFIG_ID, METRIC_URN, 100))
                        .map(anomaly -> filter.isQualified(anomaly))
                        .collect(Collectors.toList());
        Assert.assertEquals(results, Arrays.asList(false, false, true));
    }
}
