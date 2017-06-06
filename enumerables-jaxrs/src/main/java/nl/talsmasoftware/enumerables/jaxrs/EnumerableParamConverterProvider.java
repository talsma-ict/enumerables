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
package nl.talsmasoftware.enumerables.jaxrs;

import nl.talsmasoftware.enumerables.Enumerable;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Provides {@link ParamConverter} implementations to JAX-RS for {@link Enumerable} values.
 *
 * @author Sjoerd Talsma
 */
@Provider
public class EnumerableParamConverterProvider implements ParamConverterProvider {

    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType == null || !(Enumerable.class.isAssignableFrom(rawType))) return null;
        Class<? extends Enumerable> enumerableType = (Class<? extends Enumerable>) rawType;
        return (ParamConverter<T>) new EnumerableParamConverter(enumerableType);
    }

}
