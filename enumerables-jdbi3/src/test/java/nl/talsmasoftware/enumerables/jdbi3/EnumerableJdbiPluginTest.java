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

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.AfterClass;
import org.junit.Before;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Sjoerd Talsma
 */
public class EnumerableJdbiPluginTest {

    static DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "user", "pwd");

    @AfterClass
    public static void shutdownDataSource() {
        ((JdbcConnectionPool) dataSource).dispose();
    }

    @Before
    public void prepareTestdata() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.execute("create table if not exists cars (brand varchar(255), type varchar(255), productionYear int)");
            statement.execute("delete from cars");
            statement.execute("insert into cars (brand, type, productionYear) values ('Jaguar', 'XK', 2006)");
            statement.execute("insert into cars (brand, type, productionYear) values ('Tesla', 'Model S', 2015)");
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }

    // TODO Add test with Jdbi.installPlugins() to verify automatic registration.

}
