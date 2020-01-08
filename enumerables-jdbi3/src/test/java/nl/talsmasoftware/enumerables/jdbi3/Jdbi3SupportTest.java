/*
 * Copyright 2016-2020 Talsma ICT
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
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static nl.talsmasoftware.enumerables.jdbi3.CarBrand.ASTON_MARTIN;
import static nl.talsmasoftware.enumerables.jdbi3.CarBrand.JAGUAR;
import static nl.talsmasoftware.enumerables.jdbi3.CarBrand.TESLA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

public class Jdbi3SupportTest {

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

    @Test
    public void testDummy() {
        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        TestDao testDao = jdbi.onDemand(TestDao.class);

        assertThat(testDao.findCars(null, null), containsInAnyOrder(
                new Car(JAGUAR, "XK", 2006),
                new Car(TESLA, "Model S", 2015)));
        assertThat(testDao.findCars(JAGUAR, null), contains(
                new Car(JAGUAR, "XK", 2006)));
        assertThat(testDao.findCars(null, "Model S"), contains(
                new Car(TESLA, "Model S", 2015)));
        assertThat(testDao.findCars(ASTON_MARTIN, null), hasSize(0));

        // The brand should have passed the 'parse' method and therefore reuse the constant instances!
        assertThat(testDao.findCars(null, "XK").get(0).brand, is(sameInstance(JAGUAR)));
        assertThat(testDao.findCars(TESLA, "Model S").get(0).brand, is(sameInstance(TESLA)));
    }

}
