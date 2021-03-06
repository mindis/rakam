package org.rakam.util.json;

/**
 * Created by buremba <Burak Emre Kabakcı> on 25/10/14 12:51.
 */

import org.rakam.util.json.impl.Json;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class JsonArray extends JsonElement implements Iterable<Object> {
    @Override
    public boolean isArray() {
        return true;
    }

    private List<Object> list;

    public JsonArray(String json) {
        fromJson(json);
    }

    public JsonArray() {
        list = new ArrayList<>();
    }

    public JsonArray(List list) {
        this.list = list;
    }

    public JsonArray(Collection collection) {
        this.list = new ArrayList(collection);
    }

    public String getString(int pos) {
        CharSequence cs = (CharSequence)list.get(pos);
        return cs == null ? null : cs.toString();
    }

    public Integer getInteger(int pos) {
        Number number = (Number)list.get(pos);
        if (number == null) {
            return null;
        } else if (number instanceof Integer) {
            return (Integer)number; // Avoids unnecessary unbox/box
        } else {
            return number.intValue();
        }
    }

    public Long getLong(int pos) {
        Number number = (Number)list.get(pos);
        if (number == null) {
            return null;
        } else if (number instanceof Long) {
            return (Long)number; // Avoids unnecessary unbox/box
        } else {
            return number.longValue();
        }
    }

    public Double getDouble(int pos) {
        Number number = (Number)list.get(pos);
        if (number == null) {
            return null;
        } else if (number instanceof Double) {
            return (Double)number; // Avoids unnecessary unbox/box
        } else {
            return number.doubleValue();
        }
    }

    public Float getFloat(int pos) {
        Number number = (Number)list.get(pos);
        if (number == null) {
            return null;
        } else if (number instanceof Float) {
            return (Float)number; // Avoids unnecessary unbox/box
        } else {
            return number.floatValue();
        }
    }

    public Boolean getBoolean(int pos) {
        return (Boolean)list.get(pos);
    }

    public JsonObject getJsonObject(int pos) {
        Object val = list.get(pos);
        if (val instanceof Map) {
            val = new JsonObject((Map)val);
        }
        return (JsonObject)val;
    }

    public JsonArray getJsonArray(int pos) {
        Object val = list.get(pos);
        if (val instanceof List) {
            val = new JsonArray((List)val);
        }
        return (JsonArray)val;
    }

    public byte[] getBinary(int pos) {
        String val = (String)list.get(pos);
        if (val == null) {
            return null;
        } else {
            return Base64.getDecoder().decode(val);
        }
    }

    public Object getValue(int pos) {
        Object val = list.get(pos);
        if (val instanceof Map) {
            val = new JsonObject((Map)val);
        } else if (val instanceof List) {
            val = new JsonArray((List)val);
        }
        return val;
    }

    public boolean hasNull(int pos) {
        return list.get(pos) == null;
    }

    public JsonArray add(Enum value) {
        Objects.requireNonNull(value);
        list.add(value.name());
        return this;
    }

    public JsonArray add(CharSequence value) {
        Objects.requireNonNull(value);
        list.add(value.toString());
        return this;
    }

    public JsonArray add(String value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(Integer value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(Long value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(Double value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(Float value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(Boolean value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray addNull() {
        list.add(null);
        return this;
    }

    public JsonArray add(JsonObject value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(JsonArray value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(byte[] value) {
        Objects.requireNonNull(value);
        list.add(Base64.getEncoder().encodeToString(value));
        return this;
    }

    public JsonArray add(Object value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public boolean contains(Object value) {
        return list.contains(value);
    }

    public boolean remove(Object value) {
        return list.remove(value);
    }

    public Object remove(int pos) {
        return list.remove(pos);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public List getList() {
        return list;
    }

    public JsonArray clear() {
        list.clear();
        return this;
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iter(list.iterator());
    }

    public String encode() {
        return Json.encode(list);
    }

    public String encodePrettily() {
        return Json.encodePrettily(list);
    }

    @Override
    public String toString() {
        return encode();
    }

    public JsonArray copy() {
        List<Object> copiedList = new ArrayList<>(list.size());
        for (Object val: list) {
            copiedList.add(val);
        }
        return new JsonArray(copiedList);
    }

    public Stream<Object> stream() {
        return list.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return arrayEquals(list, o);
    }

    static boolean arrayEquals(List<?> l1, Object o2) {
        List<?> l2;
        if (o2 instanceof JsonArray) {
            l2 = ((JsonArray) o2).list;
        } else if (o2 instanceof List<?>) {
            l2 = (List<?>) o2;
        } else {
            return false;
        }
        if (l1.size() != l2.size())
            return false;
        Iterator<?> iter = l2.iterator();
        for (Object entry : l1) {
            Object other = iter.next();
            if (entry == null) {
                if (other != null) {
                    return false;
                }
            } else if (!JsonObject.equals(entry, other)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    private void fromJson(String json) {
        list = Json.decodeValue(json, List.class);
    }

    private class Iter implements Iterator<Object> {

        final Iterator<Object> listIter;

        Iter(Iterator<Object> listIter) {
            this.listIter = listIter;
        }

        @Override
        public boolean hasNext() {
            return listIter.hasNext();
        }

        @Override
        public Object next() {
            Object val = listIter.next();
            if (val instanceof Map) {
                val = new JsonObject((Map)val);
            } else if (val instanceof List) {
                val = new JsonArray((List)val);
            }
            return val;
        }

        @Override
        public void remove() {
            listIter.remove();
        }
    }


}