/*
 * Copyright 2016-2018 Talsma ICT
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
package nl.talsmasoftware.enumerables.swagger;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.converter.ModelConverterContextImpl;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;
import nl.talsmasoftware.enumerables.Enumerable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Sjoerd Talsma
 */
public class EnumerableModelConverter implements ModelConverter {
    private static final Logger LOGGER = Logger.getLogger(EnumerableModelConverter.class.getName());

    public Property resolveProperty(Type type, ModelConverterContext context, Annotation[] annotations, Iterator<ModelConverter> chain) {
        Property resolved = chain.hasNext() ? chain.next().resolveProperty(type, context, annotations, chain) : null;

        Class<? extends Enumerable> enumerableSubtype = enumerableSubtype(type);
        if (enumerableSubtype != null) {

            // TODO magic here :)
            LOGGER.log(Level.FINE, "Resolving a swagger property for an Enumerable type: {0}: {1}.", new Object[]{type, resolved});

        }

        return resolved;
    }

    public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        final Model resolved = resolveModelFor(enumerableSubtype(type), context);
        if (resolved != null) return resolved;
        else if (chain.hasNext()) return chain.next().resolve(type, context, chain);
        else return null;
    }

    private static Class<? extends Enumerable> enumerableSubtype(Type type) {
        if (type instanceof JavaType) type = ((JavaType) type).getRawClass();
        if (type instanceof Class && Enumerable.class.isAssignableFrom((Class<?>) type)) {
            return (Class<? extends Enumerable>) type;
        }
        return null;
    }

    private static Model resolveModelFor(Class<? extends Enumerable> enumerableType, ModelConverterContext context) {
        Model resolved = null;
        if (enumerableType != null) {
            String name = enumerableType.getSimpleName();

            if (context instanceof ModelConverterContextImpl) { // Optimization from unnecessary re-defining
                resolved = ((ModelConverterContextImpl) context).getDefinedModels().get(name);
                if (resolved != null && !(resolved instanceof EnumerableModel)) resolved = null;
            }

            if (resolved == null) {
                resolved = EnumerableModel.of(enumerableType);
                context.defineModel(name, resolved, enumerableType, null);
            }
        }
        return resolved;
    }

    private static String produceExample(Class<? extends Enumerable> enumerableType) {
        for (Enumerable value : Enumerable.values(enumerableType)) {
            return value.getValue();
        }
        return null;
    }

    private static List<String> knownValues(Class<? extends Enumerable> enumerableType) {
        List<String> knownValues = new ArrayList<String>();
        for (Enumerable value : Enumerable.values(enumerableType)) {
            knownValues.add(value.getValue());
        }
        return knownValues;
    }

    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean equals(Object other) {
        return this == other || (other != null && getClass().equals(other.getClass()));
    }

    public String toString() {
        return getClass().getSimpleName();
    }

}
