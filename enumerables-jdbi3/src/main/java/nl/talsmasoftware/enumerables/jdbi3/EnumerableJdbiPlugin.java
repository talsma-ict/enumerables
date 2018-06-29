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
package nl.talsmasoftware.enumerables.jdbi3;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.Arguments;
import org.jdbi.v3.core.mapper.ColumnMappers;
import org.jdbi.v3.core.spi.JdbiPlugin;

/**
 * {@link JdbiPlugin JDBI plugin} that registers mappers for {@code Enumerable} subclasses.
 * <p>
 * This plugin registers itself when {@link Jdbi#installPlugins()} gets called,
 * bug can also be explicitly installed using {@link Jdbi#installPlugin(JdbiPlugin)}.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableJdbiPlugin implements JdbiPlugin {

    /**
     * Registers the {@link EnumerableArgumentFactory} and {@link EnumerableColumnMapperFactory} with the provided
     * {@link Jdbi} instance.
     *
     * @param jdbi The JDBI instance to register the {@code Enumerable} mappings for.
     */
    @Override
    public void customizeJdbi(Jdbi jdbi) {
        jdbi.getConfig(Arguments.class).register(new EnumerableArgumentFactory());
        jdbi.getConfig(ColumnMappers.class).register(new EnumerableColumnMapperFactory());
    }

}
