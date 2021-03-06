package org.rakam.analysis.query.simple.predicate;

import org.rakam.util.ValidationUtil;
import org.rakam.util.json.JsonArray;
import org.rakam.util.json.JsonObject;

import java.util.function.Predicate;

/**
 * Created by buremba <Burak Emre Kabakcı> on 15/09/14 13:54.
 */
public class LessPredicate extends AbstractRichPredicate {

    protected final long value;

    public LessPredicate(String attribute, Number value) {
        super(attribute);
        this.value = value.longValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LessPredicate) {
            LessPredicate p = (LessPredicate) obj;
            return p.attribute.equals(attribute) && value == p.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (value ^ (value >>> 32));
        return result;
    }

    @Override
    public boolean test(JsonObject entry) {
        final Long entryValue = entry.getLong(attribute);
        if (entryValue == null) {
            return false;
        }
        return entryValue < value;
    }

    @Override
    public boolean isSubSet(Predicate predicate) {
        if (predicate instanceof GreaterEqualPredicate) {
            GreaterEqualPredicate p = (GreaterEqualPredicate) predicate;
            return ValidationUtil.equalOrNull(p.attribute, attribute) && p.value > value;

        }
        return false;
    }

    @Override
    public JsonArray toJson() {
        return new JsonArray().add(attribute).add("$lt").add(value);
    }
}
