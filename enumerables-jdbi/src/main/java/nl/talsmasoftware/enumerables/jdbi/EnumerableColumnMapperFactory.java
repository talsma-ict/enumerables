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
package nl.talsmasoftware.enumerables.jdbi;

import nl.talsmasoftware.enumerables.Enumerable;
import org.skife.jdbi.v2.ResultColumnMapperFactory;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultColumnMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link ResultColumnMapperFactory JDBI ResultColumnMapperFactory} for {@link Enumerable} subtypes.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableColumnMapperFactory implements ResultColumnMapperFactory {
    private static final Logger LOGGER = Logger.getLogger(EnumerableColumnMapperFactory.class.getName());

    public boolean accepts(Class type, StatementContext ctx) {
        boolean accepted = type != null && Enumerable.class.isAssignableFrom(type);
        LOGGER.log(Level.FINEST, "Type {0} {1} accepted as Enumerable.", new Object[]{type, accepted ? "was" : "was not"});
        return accepted;
    }

    public ResultColumnMapper columnMapperFor(final Class type, StatementContext ctx) {
        return new ResultColumnMapper() {
            @SuppressWarnings("unchecked")
            public Object mapColumn(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
                return Enumerable.parse((Class<? extends Enumerable>) type, r.getString(columnNumber));
            }

            @SuppressWarnings("unchecked")
            public Object mapColumn(ResultSet r, String columnLabel, StatementContext ctx) throws SQLException {
                return Enumerable.parse((Class<? extends Enumerable>) type, r.getString(columnLabel));
            }
        };
    }
}
