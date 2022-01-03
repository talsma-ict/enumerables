/*
 * Copyright 2016-2022 Talsma ICT
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

import com.google.gson.GsonBuilder;
import nl.talsmasoftware.enumerables.Enumerable;

/**
 * Utility class to help creating a {@link GsonBuilder} capable of serializing and deserializing {@link Enumerable}
 * objects.
 *
 * @author Sjoerd Talsma
 */
public final class GsonEnumerables {

    private GsonEnumerables() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return A new GSON builder that will serialize any <code>Enumerable</code> value as a plain JSON String value.
     */
    public static GsonBuilder defaultGsonBuilder() {
        return createGsonBuilder(null);
    }

    /**
     * @param serializationMethod Serialization method that determines which <code>Enumerable</code> types will be
     *                            serialized as plain JSON String values and which types will be serialized as reflected
     *                            JSON objects.
     * @return A new GSON builder that will serialize according to the specified serialization method.
     */
    public static GsonBuilder createGsonBuilder(SerializationMethod serializationMethod) {
        return configureGsonBuilder(new GsonBuilder(), serializationMethod);
    }

    /**
     * This method allows an existing GSON builder to be configured to also provide serialization and deserialization
     * for {@link Enumerable} objects.
     *
     * @param gsonBuilder         The GSON builder to be configured.
     * @param serializationMethod Serialization method that determines which <code>Enumerable</code> types will be
     *                            serialized as plain JSON String values and which types will be serialized as reflected
     *                            JSON objects.
     * @return The reference to the configured GSON builder.
     */
    public static GsonBuilder configureGsonBuilder(GsonBuilder gsonBuilder, SerializationMethod serializationMethod) {
        if (gsonBuilder != null) {
            gsonBuilder = gsonBuilder.registerTypeAdapterFactory(new EnumerableTypeAdapterFactory(serializationMethod));
        }
        return gsonBuilder;
    }

}
