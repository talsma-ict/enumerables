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

package nl.talsmasoftware.enumerables.support.maven;

import nl.talsmasoftware.enumerables.maven.MavenVersion;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class MavenVersionTest {

    @Test
    public void parse_noValue() {
        assertThat(MavenVersion.parse(null), is(nullValue()));
        assertThat(MavenVersion.parse(""), is(nullValue()));
        assertThat(MavenVersion.parse("twelve"), is(nullValue()));
    }

    @Test
    public void parse_1() {
        MavenVersion parsed = MavenVersion.parse("1");
        assertThat(parsed, notNullValue());
        assertThat(parsed.getMajor(), is(1));
        assertThat(parsed.getMinor(), is(0));
        assertThat(parsed.getIncrement(), is(0));
        assertThat(parsed.getSuffix(), is(nullValue()));
        assertThat(parsed, hasToString("1"));
    }

    @Test
    public void parse_1_2() {
        MavenVersion parsed = MavenVersion.parse("1.2");
        assertThat(parsed, notNullValue());
        assertThat(parsed.getMajor(), is(1));
        assertThat(parsed.getMinor(), is(2));
        assertThat(parsed.getIncrement(), is(0));
        assertThat(parsed.getSuffix(), is(nullValue()));
        assertThat(parsed, hasToString("1.2"));
    }

    @Test
    public void parse_1_2_3() {
        MavenVersion parsed = MavenVersion.parse("1.2.3");
        assertThat(parsed, notNullValue());
        assertThat(parsed.getMajor(), is(1));
        assertThat(parsed.getMinor(), is(2));
        assertThat(parsed.getIncrement(), is(3));
        assertThat(parsed.getSuffix(), is(nullValue()));
        assertThat(parsed, hasToString("1.2.3"));
    }

    @Test
    public void parse_1_snapshot() {
        MavenVersion parsed = MavenVersion.parse("1-SNAPSHOT");
        assertThat(parsed, notNullValue());
        assertThat(parsed.getMajor(), is(1));
        assertThat(parsed.getMinor(), is(0));
        assertThat(parsed.getIncrement(), is(0));
        assertThat(parsed.getSuffix(), is("SNAPSHOT"));
        assertThat(parsed, hasToString("1-SNAPSHOT"));
    }

    @Test
    public void parse_1_2_snapshot() {
        MavenVersion parsed = MavenVersion.parse("1.2-SNAPSHOT");
        assertThat(parsed, notNullValue());
        assertThat(parsed.getMajor(), is(1));
        assertThat(parsed.getMinor(), is(2));
        assertThat(parsed.getIncrement(), is(0));
        assertThat(parsed.getSuffix(), is("SNAPSHOT"));
        assertThat(parsed, hasToString("1.2-SNAPSHOT"));
    }

    @Test
    public void parse_1_0_0_snapshot() {
        MavenVersion parsed = MavenVersion.parse("1.0.0-SNAPSHOT");
        assertThat(parsed, notNullValue());
        assertThat(parsed.getMajor(), is(1));
        assertThat(parsed.getMinor(), is(0));
        assertThat(parsed.getIncrement(), is(0));
        assertThat(parsed.getSuffix(), is("SNAPSHOT"));
        assertThat(parsed, hasToString("1.0.0-SNAPSHOT"));
    }

    @Test
    public void parse_1_2_3_snapshot() {
        MavenVersion parsed = MavenVersion.parse("1.2.3-SNAPSHOT");
        assertThat(parsed, notNullValue());
        assertThat(parsed.getMajor(), is(1));
        assertThat(parsed.getMinor(), is(2));
        assertThat(parsed.getIncrement(), is(3));
        assertThat(parsed.getSuffix(), is("SNAPSHOT"));
        assertThat(parsed, hasToString("1.2.3-SNAPSHOT"));
    }

    @Test
    public void testEquals() {
        assertThat(MavenVersion.parse("1.0.0"), is(equalTo(MavenVersion.parse("1.0"))));
        assertThat(MavenVersion.parse("1.0.1"), is(not(equalTo(MavenVersion.parse("1.0")))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void compareTo_null() {
        MavenVersion.parse("1.2").compareTo(null);
    }

    @Test
    public void compareTo_equalVersions() {
        MavenVersion version = MavenVersion.parse("1.2.3");
        assertThat(version.compareTo(MavenVersion.parse("1.2.3")), is(0));
        version = MavenVersion.parse("1.2");
        assertThat(version.compareTo(MavenVersion.parse("1.2.0")), is(0));
        version = MavenVersion.parse("1");
        assertThat(version.compareTo(MavenVersion.parse("1.0.0")), is(0));
        version = MavenVersion.parse("1.0-SNAPSHOT");
        assertThat(version.compareTo(MavenVersion.parse("1.0.0-SNAPSHOT")), is(0));
    }

    @Test
    public void compareTo_sameVersionSnapshot() {
        MavenVersion version = MavenVersion.parse("1.2.3");
        assertThat(version.compareTo(MavenVersion.parse("1.2.3-SNAPSHOT")), is(greaterThan(0)));
        version = MavenVersion.parse("1.0.0");
        assertThat(version.compareTo(MavenVersion.parse("1.0-SNAPSHOT")), is(greaterThan(0)));

    }

}
