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

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

/**
 * DAO that uses automatically installed JDBI plugin instead of registered factories
 */
public interface CarDao {

    @SqlQuery("select brand, type, productionYear " +
            "from   cars " +
            "where  (:brand is null or brand = :brand) " +
            "and    (:type is null or type = :type)")
    @UseRowMapper(Car.Mapper.class)
    List<Car> findCars(@Bind("brand") CarBrand brand, @Bind("type") String type);

}
