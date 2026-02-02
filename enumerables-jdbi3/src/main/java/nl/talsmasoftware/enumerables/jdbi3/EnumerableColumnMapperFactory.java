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
package nl.talsmasoftware.enumerables.jdbi3;

import nl.talsmasoftware.enumerables.Enumerable;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.generic.GenericTypes;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.ColumnMapperFactory;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * {@link ColumnMapperFactory JDBI ColumnMapperFactory} for {@link Enumerable} subtypes.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableColumnMapperFactory implements ColumnMapperFactory {

    @Override
    public Optional<ColumnMapper<?>> build(Type type, ConfigRegistry config) {
        return enumerableType(type)
                .map(enumerableType -> (rs, colNr, ctx) -> Enumerable.parse(enumerableType, rs.getString(colNr)));
    }

    @SuppressWarnings("unchecked") // Actually checked by isAssignableFrom
    private static Optional<Class<? extends Enumerable>> enumerableType(Type type) {
        Optional<Class<?>> erasedType = Optional.ofNullable(type).map(GenericTypes::getErasedType);
        return erasedType.filter(Enumerable.class::isAssignableFrom).map(clz -> (Class<? extends Enumerable>) clz);
    }

}
