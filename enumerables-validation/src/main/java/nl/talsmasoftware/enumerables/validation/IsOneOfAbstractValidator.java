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
package nl.talsmasoftware.enumerables.validation;

import nl.talsmasoftware.enumerables.constraints.IsOneOf;

import javax.validation.ConstraintValidator;
import java.util.Locale;

import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;

/**
 * @author Sjoerd Talsma
 */
abstract class IsOneOfAbstractValidator<TYPE> implements ConstraintValidator<IsOneOf, TYPE> {

    private String[] acceptedSortedValues = null;
    private boolean caseSensitive = true;

    @IsOneOf
    public void initialize(IsOneOf constraintAnnotation) {
        acceptedSortedValues = constraintAnnotation.value().clone();
        caseSensitive = constraintAnnotation.caseSensitive();
        if (!caseSensitive) for (int i = 0; i < acceptedSortedValues.length; i++) {
            acceptedSortedValues[i] = lowercase(acceptedSortedValues[i]);
        }
        sort(acceptedSortedValues);
    }

    private static String lowercase(String value) {
        return value.toLowerCase(Locale.ENGLISH);
    }

    protected boolean isAccepted(String value) {
        return value == null || binarySearch(acceptedSortedValues, caseSensitive ? value : lowercase(value)) >= 0;
    }

}
