/*
 * Copyright (C) 2016 Talsma ICT
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
package nl.talsmasoftware.enumerables.support.json.jackson2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.support.json.SerializationMethod;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;
import static nl.talsmasoftware.enumerables.support.json.SerializationMethod.PLAIN_STRING;

/**
 * Serializer for Jackson-2 to serialize Enumerable Objects with.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableSerializer extends StdSerializer<Enumerable> {

    /**
     * Static cache for reflected objects based on their class names.
     */
    private static final ConcurrentMap<String, List<BeanPropertyDefinition>> CACHE = new ConcurrentHashMap<String, List<BeanPropertyDefinition>>();

    /**
     * Hardcoded names of <code>Inclusion</code> values that should skip <code>null</code> property values.
     */
    private static final Collection<String> SKIP_NULL_INCLUSIONS = unmodifiableCollection(asList(
            "NON_NULL", "NON_ABSENT", "NON_EMPTY"
    ));

    /**
     * The serialization method to determine whether we need to serialize a plain String or a JSON Object.
     */
    private final SerializationMethod serializationMethod;

    protected EnumerableSerializer() {
        this(null);
    }

    protected EnumerableSerializer(SerializationMethod serializationMethod) {
        super(Enumerable.class);
        this.serializationMethod = serializationMethod != null ? serializationMethod : PLAIN_STRING;
    }

    @Override
    public void serialize(Enumerable value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (value == null) {
            jgen.writeNull();
        } else if (serializationMethod.serializeAsJsonObject(value.getClass())) {
            serializeObject(value, jgen, provider.getConfig());
        } else {
            jgen.writeString(Enumerable.print(value));
        }
    }

    private void serializeObject(Enumerable value, JsonGenerator jgen, SerializationConfig config) throws IOException {
        jgen.writeStartObject();
        for (BeanPropertyDefinition property : serializationPropertiesFor(value.getClass(), config)) {
            if (property.couldSerialize()) {
                final Object propertyValue = property.getAccessor().getValue(value);
                if (propertyValue != null || property.isExplicitlyIncluded() || mustIncludeNull(config)) {
                    jgen.writeObjectField(property.getName(), propertyValue);
                }
            }
        }
        jgen.writeEndObject();
    }

    // TODO Use non-deprecated methods first and switch to deprecated variants using reflection.
    // Maybe move this method to the Compatibility class?
    private boolean mustIncludeNull(SerializationConfig config) {
        final JsonInclude.Include inclusion = config.getSerializationInclusion();
        return inclusion == null || !SKIP_NULL_INCLUSIONS.contains(inclusion.name());
    }

    private static List<BeanPropertyDefinition> serializationPropertiesFor(Class<?> simpleType, SerializationConfig config) {
        final String cacheKey = simpleType.getName();
        List<BeanPropertyDefinition> properties = CACHE.get(cacheKey);
        if (properties == null) {
            properties = new BasicClassIntrospector()
                    .forSerialization(config, SimpleType.construct(simpleType), null)
                    .findProperties();
            CACHE.putIfAbsent(cacheKey, properties);
        }
        return properties;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '{' + serializationMethod + '}';
    }

}
