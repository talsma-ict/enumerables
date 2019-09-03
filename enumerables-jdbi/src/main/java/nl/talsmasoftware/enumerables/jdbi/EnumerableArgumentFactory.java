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
package nl.talsmasoftware.enumerables.jdbi;

import nl.talsmasoftware.enumerables.Enumerable;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link ArgumentFactory JDBI ArgumentFactory} for {@link Enumerable} objects to bind {@code Enumerable} objects
 * as JDBI arguments for {@code String} parameters.
 * <p>
 * This {@link ArgumentFactory} can be registered similar to the following example:
 * <pre><code>
 *     {@literal @}RegisterArgumentFactory(EnumerableArgumentFactory.class)
 *     {@literal @}RegisterMapper(MyValueObjectMapper.class) // defining a custom ResultSetMapper
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
public class EnumerableArgumentFactory implements ArgumentFactory<Enumerable> {
    private static final Logger LOGGER = Logger.getLogger(EnumerableArgumentFactory.class.getName());

    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx) {
        boolean accepted = value instanceof Enumerable;
        LOGGER.log(Level.FINEST, "Expected type {0} {2} accepted with value: {1}.",
                new Object[]{expectedType, value, accepted ? "was" : "was not"});
        return accepted;
    }

    public Argument build(final Class<?> expectedType, final Enumerable value, StatementContext ctx) {
        return new Argument() {
            public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException {
                String printedValue = Enumerable.print(value);
                LOGGER.log(Level.FINEST, "Setting {0} position {1} of statement to SQL String with value: {2}.",
                        new Object[]{expectedType, position, printedValue});
                statement.setString(position, printedValue);
            }
        };
    }

}
