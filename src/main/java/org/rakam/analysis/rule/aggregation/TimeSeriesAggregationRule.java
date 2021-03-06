package org.rakam.analysis.rule.aggregation;

import org.rakam.analysis.query.FieldScript;
import org.rakam.analysis.query.FilterScript;
import org.rakam.constant.AggregationType;
import org.rakam.constant.Analysis;
import org.rakam.util.Interval;
import org.rakam.util.json.JsonObject;

import java.util.Objects;

/**
 * Created by buremba on 16/01/14.
 */
public class TimeSeriesAggregationRule extends AggregationRule {
    public static final Analysis TYPE = Analysis.ANALYSIS_TIMESERIES;
    public Interval interval;

    public TimeSeriesAggregationRule(String projectId, AggregationType type, Interval interval) {
        super(projectId, type);
        this.interval = interval;
    }

    public TimeSeriesAggregationRule(String projectId, AggregationType type, Interval interval, FieldScript select) {
        super(projectId, type, select);
        this.interval = interval;
    }

    public TimeSeriesAggregationRule(String projectId, AggregationType type, Interval interval, FieldScript select, FilterScript filters) {
        super(projectId, type, select, filters);
        this.interval = interval;
    }

    public TimeSeriesAggregationRule(String projectId, AggregationType type, Interval interval, FieldScript select, FilterScript filters, FieldScript groupBy) {
        super(projectId, type, select, filters, groupBy);
        this.interval = interval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSeriesAggregationRule)) return false;
        if (!super.equals(o)) return false;

        TimeSeriesAggregationRule that = (TimeSeriesAggregationRule) o;

        if (!interval.equals(that.interval)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + interval.hashCode();
        return result;
    }

    @Override
    public Analysis analysisType() {
        return TYPE;
    }

    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.put("interval", interval.toJson());
        return json;
    }

    public boolean isMultipleInterval(TimeSeriesAggregationRule rule) {
        return rule.project.equals(project) &&
                rule.type.equals(type) && Objects.equals(rule.select, select) &&
                Objects.equals(rule.filters, filters) && Objects.equals(rule.groupBy, groupBy)
                && interval.isDivisible(rule.interval);
    }
}
