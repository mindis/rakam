package org.rakam.analysis.query.simple;

import org.rakam.analysis.query.FieldScript;
import org.rakam.util.json.JsonElement;
import org.rakam.util.json.JsonObject;

/**
 * Created by buremba on 04/05/14.
 */
public class SimpleFieldScript<T> implements FieldScript<T> {
    private final String fieldKey;
    final transient String userData;

    public SimpleFieldScript(String fieldKey) {
        this.fieldKey = fieldKey;
        userData = fieldKey.startsWith("_user.") ? fieldKey.substring(6) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleFieldScript)) return false;

        SimpleFieldScript that = (SimpleFieldScript) o;

        if (!fieldKey.equals(that.fieldKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fieldKey.hashCode();
    }

    @Override
    public boolean requiresUser() {
        return userData != null;
    }

    @Override
    public JsonElement toJson() {
        return JsonElement.valueOf(fieldKey);
    }

    @Override
    public T extract(JsonObject event, JsonObject user) {
        if (userData != null)
            return user == null ? null : (T) user.getValue(userData);
        return (T) event.getValue(fieldKey);
    }

    @Override
    public boolean contains(JsonObject event, JsonObject user) {
        return extract(event, user) != null;
    }

}
