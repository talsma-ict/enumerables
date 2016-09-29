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
import nl.talsmasoftware.enumerables.constraints.IsOneOf;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Locale;
import java.util.Set;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.GERMAN;
import static nl.talsmasoftware.enumerables.CarBrand.FERRARI;
import static nl.talsmasoftware.enumerables.CarBrand.LAMBORGHINI;
import static nl.talsmasoftware.enumerables.support.validation.ClientLocaleHolder.DUTCH;
import static nl.talsmasoftware.enumerables.support.validation.ClientLocaleHolder.FRISIAN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * @author Sjoerd Talsma
 */
public class IsOneOfEnumerablesValidatorTest {

    static class ValidatedObject {
        @IsOneOf({"Ferrari", "Aston martin"})
        public CarBrand brand;

        ValidatedObject(CarBrand brand) {
            this.brand = brand;
        }
    }

    Validator validator;
    Set<ConstraintViolation<ValidatedObject>> violations;

    @BeforeClass
    public static void rememberOldDefaultLocale() {
        IsOneOfCharSequenceValidatorTest.rememberOldDefaultLocale();
    }

    @AfterClass
    public static void restoreOldDefaultLocale() {
        IsOneOfCharSequenceValidatorTest.restoreOldDefaultLocale();
    }

    @Before
    public void setUp() {
        Locale.setDefault(GERMAN); // Default to non-dutch or english to test.
        ClientLocaleHolder.set(ENGLISH);
        Configuration config = Validation.byDefaultProvider().configure();
        config = config.messageInterpolator(new ClientLocaleMessageInterpolator(config.getDefaultMessageInterpolator()));
        validator = config.buildValidatorFactory().getValidator();
    }

    @Test
    public void testIsOneOf_enumerable_null() {
        violations = validator.validate(new ValidatedObject(null));
        assertThat(violations, is(empty()));
    }

    @Test
    public void testIsOneOf_validEnumerable() {
        violations = validator.validate(new ValidatedObject(FERRARI));
        assertThat(violations, is(empty()));
    }

    @Test
    public void testIsOneOf_otherEnumerable() {
        violations = validator.validate(new ValidatedObject(LAMBORGHINI));
        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getPropertyPath(), hasToString("brand"));
    }

    @Test
    public void testIsOneOfCharSeq_ValidationMessage_i18n() {
        ClientLocaleHolder.set(ENGLISH);
        ConstraintViolation<ValidatedObject> violation = validator.validate(new ValidatedObject(LAMBORGHINI)).iterator().next();
        assertThat(violation.getMessage(), equalTo("is not one of [Ferrari, Aston martin]"));

        ClientLocaleHolder.set(DUTCH);
        violation = validator.validate(new ValidatedObject(LAMBORGHINI)).iterator().next();
        assertThat(violation.getMessage(), equalTo("is niet een waarde uit [Ferrari, Aston martin]"));

        // Does fallback to default work as expected?
        ClientLocaleHolder.set(FRISIAN);
        violation = validator.validate(new ValidatedObject(LAMBORGHINI)).iterator().next();
        assertThat(violation.getMessage(), equalTo("is not one of [Ferrari, Aston martin]"));
    }

}
