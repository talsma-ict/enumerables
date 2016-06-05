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
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.io.StringWriter;

import static nl.talsmasoftware.testing.Fixtures.fixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class EnumerableXmlAdapterTest {

    private static String marshalCar(AnnotatedCar car) throws JAXBException {
        StringWriter xml = new StringWriter();
        JAXBContext.newInstance(AnnotatedCar.class).createMarshaller().marshal(car, xml);
        return xml.toString();
    }

    private static AnnotatedCar unmarshalCar(String xml) throws JAXBException {
        return (AnnotatedCar) JAXBContext.newInstance(AnnotatedCar.class)
                .createUnmarshaller().unmarshal(new StringReader(xml));
    }

    @Test
    public void testXmlMarshalling() throws JAXBException {
        String expectedXml = fixture("lamborghini.xml");
        String actualXml = marshalCar(new AnnotatedCar(CarBrand.LAMBORGHINI));
        assertThat(actualXml, isSimilarTo(expectedXml).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testXmlUnmarshalling() throws JAXBException {
        AnnotatedCar unmarshalled = unmarshalCar(fixture("lamborghini.xml"));
        assertThat(unmarshalled, is(not(nullValue())));
        assertThat(unmarshalled.brand, is(sameInstance(CarBrand.LAMBORGHINI)));
    }

}
