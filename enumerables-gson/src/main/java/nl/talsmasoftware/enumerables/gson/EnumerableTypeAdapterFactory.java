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

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.gson.EnumerableDeserializer.UnknownEnumerable;

import java.io.IOException;

/**
 * @author Sjoerd Talsma
 */
final class EnumerableTypeAdapterFactory implements TypeAdapterFactory {

    private final SerializationMethod serializationMethod;

    EnumerableTypeAdapterFactory(SerializationMethod serializationMethod) {
        this.serializationMethod = serializationMethod == null ? SerializationMethod.AS_STRING : serializationMethod;
    }

    @SuppressWarnings("unchecked") // Beware: Intentional typecasts here.
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        return isEnumerable(type) ?
                (TypeAdapter<T>) new EnumerableTypeAdapter(
                        (TypeAdapter<Enumerable>) gson.getDelegateAdapter(this, type),
                        (Class<? extends Enumerable>) type.getRawType(),
                        serializationMethod)
                : null;
    }

    private static boolean isEnumerable(TypeToken<?> type) {
        Class<?> rawType = type == null ? null : type.getRawType();
        return rawType != null && Enumerable.class.isAssignableFrom(rawType);
    }

    static class EnumerableTypeAdapter extends TypeAdapter<Enumerable> {
        final TypeAdapter<Enumerable> delegate;
        final Class<? extends Enumerable> enumerableType;
        final SerializationMethod serializationMethod;

        EnumerableTypeAdapter(TypeAdapter<Enumerable> delegate, Class<? extends Enumerable> enumerableType, SerializationMethod serializationMethod) {
            this.delegate = delegate;
            this.enumerableType = Enumerable.class.equals(enumerableType) ? UnknownEnumerable.class : enumerableType;
            this.serializationMethod = serializationMethod;
        }

        public void write(JsonWriter out, Enumerable value) throws IOException {
            if (value == null || serializationMethod.serializeAsObject(value.getClass())) {
                delegate.write(out, value);
            } else {
                out.value(Enumerable.print(value));
            }
        }

        public Enumerable read(JsonReader in) throws IOException {
            // TODO: Replace the EnumerableDeserializer by actual parsing in here (similar to Jackson parser)
            return delegate.read(in);
        }
    }

}
