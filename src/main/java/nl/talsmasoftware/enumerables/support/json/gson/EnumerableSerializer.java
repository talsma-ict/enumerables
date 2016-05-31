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

import static nl.talsmasoftware.enumerables.support.json.SerializationMethod.PLAIN_STRING;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
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
        return value == null ? JsonNull.INSTANCE
                : serializationMethod.serializeAsJsonObject(value.getClass()) ? serializeAsObject(value)
                : jsonString(Enumerable.print(value));
    }

    private static JsonElement jsonString(String object) {
        return object != null ? new JsonPrimitive(object) : JsonNull.INSTANCE;
    }

    protected JsonElement serializeAsObject(Enumerable value) {
        return new Gson().toJsonTree(value);
//        if (delegateAdapter == null) delegateAdapter = (TypeAdapter<? extends Enumerable>) new Gson().getAdapter((Class<?>)Object.class);
//        return ((TypeAdapter<Enumerable>) delegateAdapter).toJsonTree(value);
//        return context.serialize(value, Object.class); // Unfortunately, overflows the stack!
//        JsonObject jsonObject = new JsonObject();
//        // TODO: Smart bean reflection
//        jsonObject.add("value", jsonString(value.getValue()));
//        jsonObject.add("description", jsonString(value.getDescription()));
//        return jsonObject;
    }

}
