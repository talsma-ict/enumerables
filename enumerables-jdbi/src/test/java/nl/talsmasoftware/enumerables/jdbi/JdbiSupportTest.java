/*
 * Copyright 2016-2025 Talsma ICT
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

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static nl.talsmasoftware.enumerables.jdbi.CarBrand.ASTON_MARTIN;
import static nl.talsmasoftware.enumerables.jdbi.CarBrand.JAGUAR;
import static nl.talsmasoftware.enumerables.jdbi.CarBrand.TESLA;
import static org.assertj.core.api.Assertions.assertThat;

class JdbiSupportTest {

    static DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "user", "pwd");

    @AfterAll
    static void shutdownDataSource() {
        ((JdbcConnectionPool) dataSource).dispose();
    }

    @BeforeEach
    void prepareTestdata() throws SQLException {
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
    void testDummy() {
        DBI dbi = new DBI(dataSource);
        TestDao testDao = dbi.onDemand(TestDao.class);

        assertThat(testDao.findCars(null, null))
                .containsExactlyInAnyOrder(
                        new Car(JAGUAR, "XK", 2006),
                        new Car(TESLA, "Model S", 2015)
                );
        assertThat(testDao.findCars(JAGUAR, null))
                .containsExactly(new Car(JAGUAR, "XK", 2006));
        assertThat(testDao.findCars(null, "Model S"))
                .containsExactly(new Car(TESLA, "Model S", 2015));
        assertThat(testDao.findCars(ASTON_MARTIN, null)).isEmpty();

        // The brand should have passed the 'parse' method and therefore reuse the constant instances!
        assertThat(testDao.findCars(null, "XK").get(0).brand).isSameAs(JAGUAR);
        assertThat(testDao.findCars(TESLA, "Model S").get(0).brand).isSameAs(TESLA);
    }

}
