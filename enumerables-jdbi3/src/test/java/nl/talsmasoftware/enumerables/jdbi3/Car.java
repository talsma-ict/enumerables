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

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.mapper.reflect.FieldMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Car {

    public CarBrand brand;
    public String type;
    public Integer productionYear;

    public Car() {
        this(null, null, null);
    }

    public Car(CarBrand brand, String type, Integer productionYear) {
        this.brand = brand;
        this.type = type;
        this.productionYear = productionYear;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Car
                && Objects.equals(this.brand, ((Car) other).brand)
                && Objects.equals(this.type, ((Car) other).type)
                && Objects.equals(this.productionYear, ((Car) other).productionYear)
        );
    }

    public static class Mapper implements RowMapper<Car> {
        private final RowMapper<Car> delegate = FieldMapper.of(Car.class);

        public Car map(ResultSet rs, StatementContext ctx) throws SQLException {
            return delegate.map(rs, ctx);
        }
    }

}
