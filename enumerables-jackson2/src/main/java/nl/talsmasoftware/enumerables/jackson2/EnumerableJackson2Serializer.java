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
package nl.talsmasoftware.enumerables.jackson2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import nl.talsmasoftware.enumerables.Enumerable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static nl.talsmasoftware.enumerables.jackson2.Compatibility.mustIncludeNull;

/**
 * Serializer for {@link Enumerable} objects.
 * <p>
 * Whether it is serialized as a JSON object or a primitive String depends on the specified {@link SerializationMethod}
 * which is {@link SerializationMethod#AS_STRING} by default.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableJackson2Serializer extends StdSerializer<Enumerable> {
    /**
     * Cache for reflected objects based on classname.
     */
    private static final ConcurrentMap<String, List<BeanPropertyDefinition>> CACHE =
            new ConcurrentHashMap<String, List<BeanPropertyDefinition>>();

    private final SerializationMethod serializationMethod;

    public EnumerableJackson2Serializer() {
        this(null);
    }

    public EnumerableJackson2Serializer(SerializationMethod serializationMethod) {
        super(Enumerable.class);
        this.serializationMethod = serializationMethod == null ? SerializationMethod.AS_STRING : serializationMethod;
    }

    @Override
    public void serialize(Enumerable value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (value == null) {
            jgen.writeNull();
        } else if (serializationMethod.serializeAsObject(value.getClass())) {
            serializeObject(value, jgen, provider.getConfig());
        } else {
            jgen.writeString(Enumerable.print(value));
        }
    }

    private void serializeObject(Enumerable value, JsonGenerator jgen, SerializationConfig config) throws IOException {
        jgen.writeStartObject();
        Class<? extends Enumerable> enumerableType = value.getClass();
        for (BeanPropertyDefinition property : serializationPropertiesFor(enumerableType, config)) {
            if (property.couldSerialize()) {
                final Object propertyValue = property.getAccessor().getValue(value);
                if (propertyValue != null || property.isExplicitlyIncluded() || mustIncludeNull(config, enumerableType)) {
                    jgen.writeObjectField(property.getName(), propertyValue);
                }
            }
        }
        jgen.writeEndObject();
    }

    private static List<BeanPropertyDefinition> serializationPropertiesFor(
            Class<? extends Enumerable> enumerableType, SerializationConfig config) {
        final String cacheKey = enumerableType.getName();
        List<BeanPropertyDefinition> properties = CACHE.get(cacheKey);
        if (properties == null) {
            properties = new BasicClassIntrospector()
                    .forSerialization(config, Compatibility.asJavaType(config, enumerableType), null)
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
