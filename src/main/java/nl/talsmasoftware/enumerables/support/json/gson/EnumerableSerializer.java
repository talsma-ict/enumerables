/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package nl.talsmasoftware.enumerables.support.json.gson;

import com.google.gson.*;
import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.support.json.SerializationMethod;

import java.lang.reflect.Type;
import java.util.Map;

import static nl.talsmasoftware.enumerables.support.json.SerializationMethod.PLAIN_STRING;
import static nl.talsmasoftware.reflection.beans.BeanReflectionSupport.getPropertyValues;

/**
 * @author Sjoerd Talsma
 */
public class EnumerableSerializer implements JsonSerializer<Enumerable> {

    private final SerializationMethod serializationMethod;
    private TypeAdapter<? extends Enumerable> delegateAdapter;

    public EnumerableSerializer() {
        this(null);
    }

    public EnumerableSerializer(SerializationMethod serializationMethod) {
        this.serializationMethod = serializationMethod != null ? serializationMethod : PLAIN_STRING;
    }

    public JsonElement serialize(Enumerable value, Type type, JsonSerializationContext context) {
        return serialize(value == null || serializationMethod.serializeAsJsonObject(value.getClass())
                ? value : Enumerable.print(value));
    }

    private static JsonElement jsonString(String object) {
        return object != null ? new JsonPrimitive(object) : JsonNull.INSTANCE;
    }

    protected JsonElement serialize(Object value) {
        if (value == null) return JsonNull.INSTANCE;
        else if (value instanceof CharSequence) return new JsonPrimitive(value.toString());
        else if (value instanceof Number) return new JsonPrimitive((Number) value);
        else if (value instanceof Character) return new JsonPrimitive((Character) value);
        else if (value instanceof Boolean) return new JsonPrimitive((Boolean) value);
        // Otherwise, reflect the value
        JsonObject result = new JsonObject();
        for (Map.Entry<String, Object> property : getPropertyValues(value).entrySet()) {
            if (!"class".equals(property.getKey())) result.add(property.getKey(), serialize(property.getValue()));
        }
        return result;
    }

}
