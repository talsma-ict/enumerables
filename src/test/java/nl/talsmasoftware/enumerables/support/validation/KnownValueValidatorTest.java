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

package nl.talsmasoftware.enumerables.support.validation;

import nl.talsmasoftware.enumerables.CarBrand;
import nl.talsmasoftware.enumerables.constraints.KnownValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.Set;

import static nl.talsmasoftware.enumerables.CarBrand.FERRARI;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class KnownValueValidatorTest {
    private static final Locale DUTCH = new Locale("nl", "NL");

    static class ValidatedObject {
        @KnownValue
        CarBrand brand;

        ValidatedObject(CarBrand brand) {
            this.brand = brand;
        }
    }

    static class StandardMessagesObject {
        @NotNull
        String testedProperty;
    }

    Locale oldDefault;
    Validator validator;
    Set<ConstraintViolation<ValidatedObject>> violations;

    @Before
    public void setUp() {
        oldDefault = Locale.getDefault();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @After
    public void tearDown() {
        Locale.setDefault(oldDefault);
    }

    @Test
    public void testKnownValue_enumerable_null() {
        violations = validator.validate(new ValidatedObject(null));
        assertThat(violations, is(empty()));
    }

    @Test
    public void testKnownValue_validEnumerable() {
        violations = validator.validate(new ValidatedObject(FERRARI));
        assertThat(violations, is(empty()));
    }

    @Test
    public void testKnownValue_otherEnumerable() {
//        Locale.setDefault(ENGLISH);
        violations = validator.validate(new ValidatedObject(CarBrand.parseLeniently("Unknown brand")));
        assertThat(violations, hasSize(1));
        ConstraintViolation<ValidatedObject> violation = violations.iterator().next();
        // TODO: Work out the I18N issues.
//        assertThat(violation.getMessage(), equalTo("Value \"Unknown brand\" is not a known value for CarBrand."));

//        Locale.setDefault(DUTCH);
//        violation = validator.validate(new ValidatedObject(CarBrand.parseLeniently("Onbekend merk"))).iterator().next();
//        assertThat(violation.getMessage(), equalTo("Waarde \"Unknown brand\" is geen bekende waarde voor CarBrand."));
    }

    @Test
    public void testStandardMessages() {
        Set<ConstraintViolation<StandardMessagesObject>> violations = validator.validate(new StandardMessagesObject());
        assertThat(violations, hasSize(1));
        ConstraintViolation<StandardMessagesObject> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath(), hasToString("testedProperty"));
        assertThat(violation.getMessage(), equalTo("may not be null"));
    }

}
