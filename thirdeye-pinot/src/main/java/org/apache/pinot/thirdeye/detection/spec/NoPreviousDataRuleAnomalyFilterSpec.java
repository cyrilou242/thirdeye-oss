package org.apache.pinot.thirdeye.detection.spec;

public class NoPreviousDataRuleAnomalyFilterSpec extends AbstractSpec {
    private String timezone = DEFAULT_TIMEZONE;
    private String offset;
    private double zeroPrecision = 0.;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public double getZeroPrecision() {
        return zeroPrecision;
    }

    public void setZeroPrecision(double zeroPrecision) {
        this.zeroPrecision = zeroPrecision;
    }
}
