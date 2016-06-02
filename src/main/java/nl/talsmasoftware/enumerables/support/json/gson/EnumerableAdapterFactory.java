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

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.support.json.SerializationMethod;

import java.io.IOException;

import static nl.talsmasoftware.enumerables.support.json.SerializationMethod.PLAIN_STRING;
import static nl.talsmasoftware.enumerables.support.json.gson.EnumerableDeserializer.enumerableSubTypeOf;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class EnumerableAdapterFactory implements TypeAdapterFactory {

    private final SerializationMethod serializationMethod;

    public EnumerableAdapterFactory() {
        this(null);
    }

    public EnumerableAdapterFactory(SerializationMethod serializationMethod) {
        this.serializationMethod = serializationMethod != null ? serializationMethod : PLAIN_STRING;
    }

    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        final Class<? extends Enumerable> enumerableType = enumerableSubTypeOf(type);
        if (enumerableType == null) return null; // ...or null if this factory doesn't support type
        return (TypeAdapter<T>) new TypeAdapter<Enumerable>() {
            @Override
            public void write(JsonWriter out, Enumerable value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else if (gson != null && serializationMethod.serializeAsJsonObject(value.getClass())) {
                    gson.getDelegateAdapter(EnumerableAdapterFactory.this, type).write(out, (T) value);
                } else {
                    out.value(Enumerable.print(value));
                }
            }

            @Override
            public Enumerable read(JsonReader in) throws IOException {
                return Enumerable.parse(enumerableType, readValue(in));
            }
        };
    }

    // TODO: Parse the value from the json reader.
    private static String readValue(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NULL:
                return null;
            case STRING:
                return in.nextString();
            case NAME:
                in.nextName();
                break;
            default:
                throw new IllegalStateException();
        }
        return null;
    }

}
