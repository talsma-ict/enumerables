/*
 * Copyright 2016-2019 Talsma ICT
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
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * {@link ArgumentFactory JDBI ArgumentFactory} for {@link Enumerable} objects to bind {@code Enumerable} objects
 * as JDBI arguments for {@code String} parameters.
 * <p>
 * This {@link ArgumentFactory} can be registered similar to the following example:
 * <pre><code>
 *     {@literal @}RegisterArgumentFactory(EnumerableArgumentFactory.class)
 *     {@literal @}UseRowMapper(MyValueObjectMapper.class) // defining a custom RowMapper
 *      public interface MyDao {
 *          ...
 *         {@literal @}SqlQuery("select ... as obj from ...  where type = :type")
 *          List&lt;MyValueObject&gt; queryWithType({@literal @}Bind("type") MyTypeEnumerable type);
 *          ...
 *      }
 * </code></pre>
 *
 * @author Sjoerd Talsma
 */
public class EnumerableArgumentFactory implements ArgumentFactory {

    public Optional<Argument> build(Type type, Object value, ConfigRegistry config) {
        return Optional.ofNullable(value)
                .filter(Enumerable.class::isInstance).map(Enumerable.class::cast)
                .map(enumerable -> (paramIdx, stmt, ctx) -> stmt.setString(paramIdx, Enumerable.print(enumerable)));
    }

}
