package org.rakam.constant;

/**
 * Created by buremba on 17/01/14.
 */
public enum AggregationType {
    COUNT(0),
    COUNT_X(1),
    UNIQUE_X(2),
    SUM_X(3),
    MINIMUM_X(4),
    MAXIMUM_X(5),
    AVERAGE_X(6);

    public final int id;

    AggregationType(int id) {
        this.id = id;
    }

    public static AggregationType get(int id) {
        for (AggregationType a : AggregationType.values()) {
            if (a.id == id)
                return a;
        }
        throw new IllegalArgumentException("Invalid id");
    }

    public static AggregationType get(String name) {
        if (name != null)
            name = name.toUpperCase();
        return AggregationType.valueOf(name);
    }
}
