/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *
 */

package nl.talsmasoftware.enumerables.support.xml;

import nl.talsmasoftware.enumerables.CarBrand;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Object to test serialization with.
 *
 * @author Sjoerd Talsma
 */
@XmlRootElement(name = "car", namespace = "http://talsmasoftware.nl/enumerables/testing")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnnotatedCar {

    public static class CarBrandAdapter extends EnumerableXmlAdapter<CarBrand> {
    }

    @XmlJavaTypeAdapter(CarBrandAdapter.class)
    public CarBrand brand;

    public AnnotatedCar() {
        this(null);
    }

    public AnnotatedCar(CarBrand brand) {
        this.brand = brand;
    }


    @Override
    public int hashCode() {
        return brand.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof AnnotatedCar
                && brand == null ? ((AnnotatedCar) other).brand == null : brand.equals(((AnnotatedCar) other).brand));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{brand=" + brand + '}';
    }
}
