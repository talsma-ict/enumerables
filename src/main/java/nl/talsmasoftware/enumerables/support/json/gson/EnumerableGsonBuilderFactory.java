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

import com.google.gson.GsonBuilder;
import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.support.json.SerializationMethod;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class EnumerableGsonBuilderFactory {

    public static GsonBuilder defaultGsonBuilder() {
        return createGsonBuilder(null);
    }

    public static GsonBuilder createGsonBuilder(SerializationMethod serializationMethod) {
        return configureGsonBuilder(new GsonBuilder(), serializationMethod);
    }

    public static GsonBuilder configureGsonBuilder(GsonBuilder gsonBuilder, SerializationMethod serializationMethod) {
        if (gsonBuilder != null) {
            gsonBuilder = gsonBuilder
                    .registerTypeHierarchyAdapter(Enumerable.class, new EnumerableSerializer(serializationMethod))
                    .registerTypeHierarchyAdapter(Enumerable.class, new EnumerableDeserializer());
        }
        return gsonBuilder;
    }

}
