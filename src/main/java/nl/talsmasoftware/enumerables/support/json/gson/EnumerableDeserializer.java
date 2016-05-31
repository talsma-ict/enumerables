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
import nl.talsmasoftware.enumerables.support.json.UnknownEnumerable;

import java.lang.reflect.Type;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
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
    private static Class<? extends Enumerable> enumerableSubTypeOf(Type type) {
        return type instanceof Class && Enumerable.class.isAssignableFrom((Class<?>) type) && !Enumerable.class.equals(type)
                ? (Class<? extends Enumerable>) type : UnknownEnumerable.class;
    }

}
