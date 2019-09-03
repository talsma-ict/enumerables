/*
 * Copyright 2016-2019 Talsma ICT
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
package nl.talsmasoftware.testing;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Sjoerd Talsma
 */
public class Fixtures {

    public static String fixture(String name) {
        InputStream stream = null;
        try {
            // stacktrace [0] = Thread.getStackTrace
            // stacktrace [1] = Fixtures.fixture
            Class<?> caller = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            stream = caller.getResourceAsStream(name);
            if (stream == null) throw new IllegalArgumentException("Fixture not found: " + name);
            final Reader reader = new InputStreamReader(stream, "UTF-8");
            final Writer writer = new StringWriter();
            final char[] buf = new char[1024];
            for (int read = reader.read(buf); read >= 0; read = reader.read(buf)) {
                writer.write(buf, 0, read);
            }
            return writer.toString();
        } catch (IOException ioe) {
            throw new RuntimeException("I/O exception reading fixture " + name, ioe);
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalStateException("Caller class could not be found: " + cnfe.getMessage(), cnfe);
        } finally {
            if (stream != null) try {
                stream.close();
            } catch (IOException ioe) {
                Logger.getLogger(Fixtures.class.getName()).log(Level.FINEST, "Could not close stream from " + name, ioe);
            }
        }
    }


}
