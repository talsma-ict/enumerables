/*
 * Copyright 2016-2017 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.enumerables.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import nl.talsmasoftware.enumerables.Enumerable;

import java.lang.reflect.Type;

/**
 * @author Sjoerd Talsma
 */
public class EnumerableDeserializer implements JsonDeserializer<Enumerable> {

    public Enumerable deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        final Class<? extends Enumerable> enumerableType = enumerableSubTypeOf(type);
        if (json.isJsonNull()) return null;
        else if (json.isJsonPrimitive()) return Enumerable.parse(enumerableType, json.getAsString());
        else if (json instanceof JsonObject) return Enumerable.parse(enumerableType, valueOf((JsonObject) json));
        throw new IllegalStateException("Unable to parse JSON element as Enumerable object: " + json);
    }

    private String valueOf(JsonObject jsonObject) {
        final JsonElement value = jsonObject.get("value");
        if (value == null)
            throw new IllegalStateException("Attribute \"value\" is required to parse an Enumerable JSON object.");
        else if (!value.isJsonPrimitive())
            throw new IllegalStateException("Attribute \"value\" must contain a String value for Enumerable JSON objects.");
        return value.getAsString();
    }

    @SuppressWarnings("unchecked")
    static Class<? extends Enumerable> enumerableSubTypeOf(Object type) {
        if (type instanceof TypeToken) type = ((TypeToken<?>) type).getRawType();
        if (type instanceof Class && Enumerable.class.isAssignableFrom((Class<?>) type)) {
            return Enumerable.class.equals(type) ? UnknownEnumerable.class : (Class<? extends Enumerable>) type;
        }
        return null;
    }

    /**
     * Non-abstract {@link Enumerable} class to serialize to if the concrete type can somehow not be determined.
     */
    static final class UnknownEnumerable extends Enumerable {
        private UnknownEnumerable(String value) {
            super(value);
        }
    }

}