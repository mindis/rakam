package org.rakam.analysis.query.simple.predicate;

import org.rakam.model.Entry;
import org.rakam.util.json.JsonObject;

import java.util.function.Predicate;

/**
 * Created by buremba <Burak Emre Kabakcı> on 15/09/14 13:54.
 */
public class NegatePredicate<T extends Entry> implements RichPredicate {

    protected Predicate predicate;

    public NegatePredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NegatePredicate && ((NegatePredicate) obj).predicate.equals(predicate);
    }

    @Override
    public int hashCode() {
        return predicate.hashCode();
    }

    @Override
    public boolean test(JsonObject entry) {
        return !predicate.test(entry);
    }

    @Override
    public boolean isSubSet(Predicate predicate) {
        if (predicate instanceof NegatePredicate && this.predicate instanceof RichPredicate) {
            return ((RichPredicate) this.predicate).isSubSet(predicate);

        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        Object value = predicate instanceof RichPredicate ? ((RichPredicate) predicate).toJson() : predicate.toString();
        return jsonObject.put("NOT", value);
    }
}
