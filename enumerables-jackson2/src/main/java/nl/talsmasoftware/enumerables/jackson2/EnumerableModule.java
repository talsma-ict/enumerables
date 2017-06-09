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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import nl.talsmasoftware.enumerables.Enumerable;

import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_STRING;

/**
 * Mapping module for converting one or more {@link Enumerable} types to and from JSON using Jackson v2.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableModule extends SimpleModule {

    private final SerializationMethod serializationMethod;
    private final EnumerableDeserializer deserializer = new EnumerableDeserializer();

    /**
     * Constructor that will serialize {@link Enumerable} objects as primitive String values.
     * Both primitive Strings and JSON object representations can be deserialized.
     */
    public EnumerableModule() {
        this(null);
    }

    /**
     * Constructor for custom {@link SerializationMethod} for {@link Enumerable} types.
     *
     * @param serializationMethod The serialization method to use. This paramter may be {@code null} in which case the
     *                            {@code AS_STRING} method will be used by default.
     * @see SerializationMethod#AS_STRING
     * @see SerializationMethod#AS_OBJECT
     */
    public EnumerableModule(SerializationMethod serializationMethod) {
        super("Enumerable mapping module");
        this.serializationMethod = serializationMethod == null ? AS_STRING : serializationMethod;
        super.addSerializer(Enumerable.class, new EnumerableSerializer(this.serializationMethod));
        super.addDeserializer(Enumerable.class, this.deserializer);
    }

    private static boolean isEnumberableSubtype(BeanDescription beanDesc) {
        final Class<?> type = beanDesc == null ? null : beanDesc.getType().getRawClass();
        return type != null && Enumerable.class.isAssignableFrom(type) && !Enumerable.class.equals(type);
    }

    /**
     * Configures this Jackson module.
     * <p>
     * This creates a {@link BeanDeserializerModifier} that returns the configured {@link #deserializer} for all
     * subtypes of {@link Enumerable}.
     *
     * @param setupContext De setup context to initialize.
     *                     A deserializer modifier is added to this context to process all Enumerable subtypes.
     */
    @Override
    public void setupModule(final SetupContext setupContext) {
        if (setupContext != null) {
            setupContext.addBeanDeserializerModifier(new BeanDeserializerModifier() {
                @Override
                public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                    // Jackson wants to use its default bean deserializer for subtypes of Enumerable.
                    // We must prevent that; return the deserializer for Enumerables instead.
                    return isEnumberableSubtype(beanDesc) ? EnumerableModule.this.deserializer
                            : super.modifyDeserializer(config, beanDesc, deserializer);
                }
            });
        }
        super.setupModule(setupContext);
    }

    @Override
    public int hashCode() {
        return serializationMethod.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof EnumerableModule
                && serializationMethod.equals(((EnumerableModule) other).serializationMethod));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{serializationMethod=" + serializationMethod + '}';
    }

}
