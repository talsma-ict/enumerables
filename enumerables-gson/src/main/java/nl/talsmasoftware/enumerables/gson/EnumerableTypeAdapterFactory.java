/*
 * Copyright 2016-2026 Talsma ICT
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

import java.io.IOException;

import static java.lang.reflect.Modifier.isAbstract;

/**
 * Gson type adapter factory for {@link Enumerable} subclasses.
 * A new {@link TypeAdapter} is returned for each concrete {@link Enumerable} sub-type.
 *
 * @author Sjoerd Talsma
 */
final class EnumerableTypeAdapterFactory implements TypeAdapterFactory {

    private final SerializationMethod serializationMethod;

    EnumerableTypeAdapterFactory(SerializationMethod serializationMethod) {
        this.serializationMethod = serializationMethod == null ? SerializationMethod.AS_STRING : serializationMethod;
    }

    @SuppressWarnings("unchecked") // Intentional typecasts here.
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

    @Override
    public int hashCode() {
        return serializationMethod.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof EnumerableTypeAdapterFactory
                && serializationMethod.equals(((EnumerableTypeAdapterFactory) other).serializationMethod)
        );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '{' + serializationMethod + '}';
    }

    static class EnumerableTypeAdapter extends TypeAdapter<Enumerable> {
        final TypeAdapter<Enumerable> delegate;
        final Class<? extends Enumerable> enumerableType;
        final SerializationMethod serializationMethod;

        EnumerableTypeAdapter(TypeAdapter<Enumerable> delegate, Class<? extends Enumerable> enumerableType, SerializationMethod serializationMethod) {
            this.delegate = delegate;
            this.enumerableType = isAbstract(enumerableType.getModifiers()) ? UnknownEnumerable.class : enumerableType;
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
            switch (in.peek()) {
                case NULL:
                    in.nextNull();
                    return null;
                case BOOLEAN:
                    return Enumerable.parse(enumerableType, Boolean.toString(in.nextBoolean()));
                case NUMBER:
                case STRING:
                    return Enumerable.parse(enumerableType, in.nextString());
                case BEGIN_OBJECT:
                    return readObject(in);
                default:
                    // fall-through to delegate reader; a custom deserializer may have been registered
            }
            return delegate.read(in);
        }

        private Enumerable readObject(JsonReader in) throws IOException {
            boolean valueFound = false;
            Enumerable enumerable = null;

            in.beginObject();
            while (in.hasNext()) {
                if ("value".equals(in.nextName())) {
                    valueFound = true;
                    switch (in.peek()) {
                        case NULL:
                            in.nextNull();
                            enumerable = null;
                            break;
                        case BOOLEAN:
                            enumerable = Enumerable.parse(enumerableType, Boolean.toString(in.nextBoolean()));
                            break;
                        default: // Assume value is either NUMBER or STRING:
                            enumerable = Enumerable.parse(enumerableType, in.nextString());
                    }
                } else {
                    in.skipValue();
                }
            }
            in.endObject();

            if (!valueFound) {
                throw new IllegalStateException("Attribute \"value\" is required to parse an Enumerable JSON object.");
            }
            return enumerable;
        }
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
