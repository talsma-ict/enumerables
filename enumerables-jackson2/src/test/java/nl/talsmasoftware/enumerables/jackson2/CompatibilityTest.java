/*
 * Copyright 2016-2017 Talsma ICT
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
package nl.talsmasoftware.enumerables.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import nl.talsmasoftware.enumerables.jackson2.PlainTestObject.BigCo;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Sjoerd Talsma
 */
public class CompatibilityTest {

    @After
    public void restoreCompatibilityState() {
        try {
            for (String fieldName : asList("supportsContextualType", "supportsTypeId")) {
                Field field = Compatibility.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(null, true);
                field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Not allowed to restore field: " + e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Field to be restored not found: " + e.getMessage(), e);
        }
    }

    @Test
    public void testGetContextualType() {
        DeserializationContext ctx = mock(DeserializationContext.class);
        JavaType javaType = mock(JavaType.class);
        when(ctx.getContextualType()).thenReturn(javaType);

        assertThat(Compatibility.getContextualType(ctx), is(equalTo(javaType)));

        verify(ctx).getContextualType();
        verifyNoMoreInteractions(ctx, javaType);
    }

    @Test
    public void testGetContextualType_NoSuchMethodError() {
        DeserializationContext ctx = mock(DeserializationContext.class);
        when(ctx.getContextualType()).thenThrow(new NoSuchMethodError("DeserializationContext.getContextualType()"));

        assertThat(Compatibility.getContextualType(ctx), is(nullValue())); // No error, but <null> result.

        verify(ctx).getContextualType();
        verifyNoMoreInteractions(ctx);
    }

    @Test
    public void testGetTypeId() throws IOException {
        JsonParser parser = mock(JsonParser.class);
        when(parser.getTypeId()).thenReturn(BigCo.class);

        assertThat(Compatibility.getTypeId(parser), is(equalTo((Object) BigCo.class)));

        verify(parser).getTypeId();
        verifyNoMoreInteractions(parser);
    }

    @Test
    public void testGetTypeId_NoSuchMethodError() throws IOException {
        JsonParser parser = mock(JsonParser.class);
        when(parser.getTypeId()).thenThrow(NoSuchMethodError.class);

        assertThat(Compatibility.getTypeId(parser), is(nullValue()));

        verify(parser).getTypeId();
        verifyNoMoreInteractions(parser);
    }

}
