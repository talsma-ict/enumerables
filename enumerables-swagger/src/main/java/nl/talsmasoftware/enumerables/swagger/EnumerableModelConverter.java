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
import io.swagger.models.ModelImpl;
import io.swagger.models.RefModel;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import nl.talsmasoftware.enumerables.Enumerable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * {@link ModelConverter} that provides a {@link Model Swagger Model} for {@link Enumerable} objects.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableModelConverter implements ModelConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnumerableModelConverter.class);

    /**
     * Attempts to resolve an {@link EnumerableModel} for the specified {@code type}.
     * <p>
     * If such a {@link Model} is resolved, a {@link RefProperty} is returned to it.
     * Otherwise, the other model converters in the {@code chain} will be used.
     *
     * @param type        The type of the property to resolve (may or may not be an Enumerable subtype.
     * @param context     The context containing already-resolved models.
     * @param annotations Annotations for the property being resolved.
     * @param chain       The other model converters in the chain to delegate non-Enumerable properties to.
     * @return The resolved property.
     */
    public Property resolveProperty(Type type, ModelConverterContext context, Annotation[] annotations, Iterator<ModelConverter> chain) {
        Property resolved = resolvePropertyFor(type, context);
        if (resolved != null) {
            LOGGER.debug("Resolved enumerable property {} to {}.", type, resolved);
        }
        if (resolved == null && chain.hasNext()) {
            resolved = chain.next().resolveProperty(type, context, annotations, chain);
        }
        return resolved;
    }

    public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        Model resolved = resolveModelFor(type, context);
        if (resolved != null) {
            LOGGER.debug("Resolved enumerable model for {}: {}.", type, resolved);
        }
        if (resolved == null && chain.hasNext()) {
            resolved = chain.next().resolve(type, context, chain);
        }
        return resolved;
    }

    @SuppressWarnings("unchecked") // Checked by isAssignableFrom
    private static Class<? extends Enumerable> enumerableSubtype(Type type) {
        if (type instanceof JavaType) type = ((JavaType) type).getRawClass();
        if (type instanceof Class && Enumerable.class.isAssignableFrom((Class<?>) type)) {
            LOGGER.trace("Encountered Enumerable subtype: {}.", type);
            return (Class<? extends Enumerable>) type;
        }
        return null;
    }

    protected RefProperty resolvePropertyFor(Type type, ModelConverterContext context) {
        RefModel model = resolveModelFor(type, context);
        return model == null ? null : new RefProperty(model.getReference());
    }

    /**
     * Resolves the {@link EnumerableModel} for {@link Enumerable} subtypes.
     * <p>
     * If the specified {@code type} is not a subtype of {@link Enumerable}, this method should return {@code null}.
     * <p>
     * In case the context already contains a defined {@link EnumerableModel} for the same name
     * (Enumerable's {@link Class#getSimpleName() simple name}), it will be looked up and returned.
     * Otherwise, a new {@link EnumerableModel} is populated, registered in the context, and returned.
     *
     * @param type    The type to resolve an EnumerableModel for
     *                (type does not necessarily need to be an actual Enumerable).
     * @param context The context to register populated EnumerableModel instances with
     *                (and to use for existing-model lookup).
     * @return The looked-up model, a newly populated model, or {@code null} if type was not a subtype of Enumerable.
     */
    protected RefModel resolveModelFor(Type type, ModelConverterContext context) {
        Model resolved = null;
        Class<? extends Enumerable> enumerableType = enumerableSubtype(type);
        if (enumerableType != null) {
            String name = enumerableType.getSimpleName();

            if (context instanceof ModelConverterContextImpl) { // Optimization from unnecessary re-defining
                Model current = ((ModelConverterContextImpl) context).getDefinedModels().get(name);
                if (current instanceof EnumerableModel) resolved = current;
            }

            if (resolved == null) {
                resolved = EnumerableModel.of(enumerableType).name(name);
                LOGGER.trace("Populated EnumerableModel for {}.", enumerableType);
                if (!enumerableType.equals(type)) context.defineModel(name, resolved, enumerableType, null);
                context.defineModel(name, resolved, type, null);
                LOGGER.debug("Registered EnumerableModel for {} by name \"{}\" and type {}.", enumerableType, name, type);
            } else {
                LOGGER.debug("Returning existing EnumerableModel named \"{}\" for type {}.", name, type);
            }
        }
        return referenceTo(resolved);
    }

    protected RefModel referenceTo(Model model) {
        if (model == null || model instanceof RefModel) return (RefModel) model;
        String reference = model.getReference();
        if (reference == null && model instanceof ModelImpl) reference = ((ModelImpl) model).getName();
        return reference == null ? null : new RefModel(reference);
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
