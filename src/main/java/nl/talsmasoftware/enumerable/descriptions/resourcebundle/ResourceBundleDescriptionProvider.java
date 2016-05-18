/*
 * Copyright (C) 2016 Talsma ICT
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
package nl.talsmasoftware.enumerable.descriptions.resourcebundle;

import nl.talsmasoftware.enumerable.Enumerable;
import nl.talsmasoftware.enumerable.descriptions.DescriptionProvider;
import nl.talsmasoftware.enumerable.descriptions.Descriptions;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.ResourceBundle.getBundle;

/**
 * Description provider based on a Java {@link ResourceBundle}.
 * <p>
 * In case there is no {@link Builder#bundleName(String) resource bundle name} specified in the {@link Builder},
 * the {@link Class#getName() name} of the {@link Enumerable} type will also be used as the resource bundle name.
 * <p>
 * In case there is no {@link Builder#locale(Locale) locale} specified in the {@link Builder},
 * the system {@link Locale#getDefault() default locale} will be used to obtain an appropriate resource bundle.
 * This makes the description lookup dependent on the JVM platform configuration and may result in different and
 * unexpected description values.
 * <p>
 * In case there is no {@link Builder#prefix(String) prefix} or {@link Builder#suffix(String) suffix} specified for the
 * constant name in the {@link Builder}, a default <code>prefix</code> value of <code>"description."</code> will be
 * used and no default <code>suffix</code>.
 * <p>
 * The default builder values described above leads to a fully functional {@link ResourceBundleDescriptionProvider}
 * that will look up a resource bundle named after the {@link Enumerable} type and uses the prefix
 * <code>"description."</code> + {@link Enumerable#getValue() value}.<br>
 * This 'default' provider is also available as a constant: {@link #DEFAULT}.
 * <p>
 * If there is no resource bundle message defined for the given {@link Enumerable} value, the description will be
 * returned as <code>null</code>. This will trigger the {@link Descriptions#defaultProvider() default provider} to
 * provide a description instead.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class ResourceBundleDescriptionProvider implements DescriptionProvider {
    // TODO: Also use a ResourceBundle for the logged messages from this library!
    private static final Logger LOGGER = Logger.getLogger(ResourceBundleDescriptionProvider.class.getName());

    /**
     * The default {@link ResourceBundleDescriptionProvider}.
     * <p>
     * This provider will use the {@link Locale#getDefault() default locale} and the {@link Enumerable}
     * {@link Class#getName() class name} to obtain the appropriate {@link ResourceBundle} for the descriptions.
     * <p>
     * The String <code>"description."</code> is used together with the {@link Enumerable#name() name}
     * or {@link Enumerable#getValue() value} of the {@link Enumerable enumerable object} to lookup the description
     * within that {@link ResourceBundle}.
     */
    public static final ResourceBundleDescriptionProvider DEFAULT =
            new ResourceBundleDescriptionProvider(null, null, null, null);

    /**
     * The default message-key prefix to use within the resource bundles.
     */
    private static final String DEFAULT_PREFIX = "description.";

    /**
     * The default message-key suffix to use within the resource bundles.
     */
    private static final String DEFAULT_SUFFIX = "";

    /**
     * The name of the {@link ResourceBundle} to use.
     * This value is optional and will be deduced from the {@link Enumerable} type if <code>null</code>.
     */
    private final String bundleName;

    /**
     * The {@link Locale} to lookup the {@link ResourceBundle} with (non-<code>null</code>).
     */
    private final LocaleProvider localeProvider;

    /**
     * The message-key prefix to use within the resource bundle (non-<code>null</code>).
     */
    private final String prefix;

    /**
     * The message-key suffix to use within the resource bundle (non-<code>null</code>).
     */
    private final String suffix;

    /**
     * Private constructor for use from the {@link Builder}.
     *
     * @param resourceBundleName The name of the ResourceBundle to use (optional).
     * @param localeProvider     The provider that returns the Locale to use (optional).
     * @param keyPrefix          The message-key prefix to use (optional).
     * @param keySuffix          The message-key suffix to use (optional).
     */
    private ResourceBundleDescriptionProvider(
            String resourceBundleName, LocaleProvider localeProvider, String keyPrefix, String keySuffix) {
        this.bundleName = resourceBundleName;
        this.localeProvider = localeProvider == null ? LocaleProvider.DEFAULT : localeProvider;
        this.prefix = keyPrefix == null ? DEFAULT_PREFIX : keyPrefix;
        this.suffix = keySuffix == null ? DEFAULT_SUFFIX : keySuffix;
    }

    /**
     * Creates a {@link Builder} for a {@link DescriptionProvider} based on Java {@link ResourceBundle} implementation.
     * <p>
     * The builder can be used to configure the various parameters for this provider.
     *
     * @return The builder to create a new <code>ResourceBundleDescriptionProvider</code> with.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * This operation provides the description from the {@link ResourceBundle resource bundle} for the specified
     * <code>enumerable</code> object value.
     *
     * @param enumerable The enumerable object value to provide a description for.
     * @return The found description or <code>null</code> if no description can be obtained from the
     * <code>ResourceBundle</code>.
     * @see Descriptions#defaultProvider()
     */
    public String describe(final Enumerable enumerable) {
        String description = null;
        if (enumerable != null) {
            final String key = determineKeyFor(enumerable);
            try {
                description = getResourceBundle(enumerable.getClass()).getString(key);
                LOGGER.log(Level.FINEST, "Description for enumerable value \"{0}\" from resource bundle: \"{1}\".",
                        new Object[]{enumerable, description});
            } catch (RuntimeException rte) {
                LOGGER.log(Level.FINE, "Cannot find value \"{0}\" in resource bundle for {1} due to: {2}",
                        new Object[]{key, enumerable.getClass().getSimpleName(), rte.getMessage(), rte});
            }
        }
        return description;
    }

    /**
     * Obtains the appropriate {@link ResourceBundle} for the specified {@link Enumerable} type.
     *
     * @param type The enumerable type to lookup descriptions for.
     * @return The approprate <code>ResourceBundle</code> to use.
     * @throws MissingResourceException In case the configured <code>ResourceBundle</code> could not be located.
     * @see #determineResourceBundleName(Class)
     * @see #determineLocale()
     */
    protected ResourceBundle getResourceBundle(Class<? extends Enumerable> type) throws MissingResourceException {
        return getBundle(determineResourceBundleName(type), determineLocale());
    }

    /**
     * Returns the fixed {@link ResourceBundle} name if specified in the builder, or the {@link Enumerable} type
     * {@link Class#getName() class name} otherwise.
     *
     * @param enumerableType The enumerable type to determine a <code>ResourceBundle</code> name for.
     * @return The name of the <code>ResourceBundle</code> to use for the specified enumerable type.
     */
    protected String determineResourceBundleName(Class<? extends Enumerable> enumerableType) {
        String resourceBundleName = bundleName == null ? enumerableType.getName() : bundleName;
        LOGGER.log(Level.FINEST, "Resource bundle name determined voor Enumerable {0}: \"{1}\".",
                new Object[]{enumerableType, resourceBundleName});
        return resourceBundleName;
    }

    /**
     * Returns the {@link Locale} that was configured in the {@link Builder} or the
     * {@link Locale#getDefault() default system Locale} otherwise.
     *
     * @return The configured <code>Locale</code> or the default locale otherwise.
     */
    protected Locale determineLocale() {
        Locale locale = localeProvider.provideLocale();
        if (locale == null) {
            locale = LocaleProvider.DEFAULT.provideLocale();
        }
        LOGGER.log(Level.FINEST, "Locale to lookup resource bundle with: \"{0}\".", locale);
        return locale;
    }

    /**
     * Operation to generate a {@link ResourceBundle} <code>key</code> with, based on an
     * {@link Enumerable} object instance.
     * <p>
     * This <code>key</code> will be used to lookup the description message in the resource bundle for an appropriate
     * translation.
     *
     * @param enumerable The enumerable object value to create a message <code>key</code> for.
     * @return The key in the <code>ResourceBundle</code> for the specified <code>enumerable</code> value.
     */
    protected String determineKeyFor(Enumerable enumerable) {
        String key = new StringBuilder(40).append(prefix)
                .append(_nameOrValue(enumerable).replaceAll(" ", "_"))
                .append(suffix).toString();
        LOGGER.log(Level.FINEST, "Resource bundle key determined for enumerable \"{0}\": \"{1}\".",
                new Object[]{enumerable, key});
        return key;
    }

    private static String _nameOrValue(Enumerable enumerable) {
        if (enumerable == null) throw new IllegalArgumentException("Enumerable was null.");
        final String name = enumerable.name();
        return name == null ? enumerable.getValue() : name;
    }

    /**
     * Builder to create a {@link DescriptionProvider} with based on {@link ResourceBundle resource bundles} and
     * providing this provider with optional configuration parameters.
     * <p>
     * If no explicit {@link #bundleName(String) bundle name} is provided, the {@link ResourceBundleDescriptionProvider}
     * will choose the full {@link Class#getName() enumerable class name} (including package) for the actual
     * {@link Enumerable} type as bundle name.
     * <p>
     * If no explicit {@link #locale(Locale) locale} is provided, the {@link Locale#getDefault() default locale} will
     * be chosen.
     * <p>
     * If no explicit {@link #prefix(String) prefix} or {@link #suffix(String) suffix} is provided, the prefix value
     * <code>"description."</code> is chosen and no suffix.
     */
    public static class Builder {
        private String bundleName, prefix, suffix;
        private LocaleProvider localeProvider;

        /**
         * Operation to use a specific resource bundle name to look up descriptions with.
         * <p>
         * This is not required. The {@link ResourceBundleDescriptionProvider} will use the full class name of the
         * {@link Enumerable} type as resource bundle name.
         *
         * @param bundleName The name of the resource bundle to look up descriptions with.
         * @return Reference to this builder to specify other configuration parameters for,
         * in a 'method chaining' fashion.
         */
        public Builder bundleName(String bundleName) {
            this.bundleName = bundleName;
            return this;
        }

        /**
         * Operation to use a specific fixed {@link Locale} for looking up descriptions.
         * This may be useful in case multiple translations are available, but only a single language should be
         * chosen for a particular use-case.
         * <p>
         * This is not required. The {@link ResourceBundleDescriptionProvider} will use the
         * {@link Locale#getDefault() default locale} otherwise.
         * <p>
         * To provide a variable {@link Locale} (for instance per-thread), please use the
         * {@link #localeProvider(LocaleProvider) locale provider} configuration parameter.
         *
         * @param locale The (fixed) locale to obtain resource bundles for.
         * @return Reference to this builder to specify other configuration parameters for,
         * in a 'method chaining' fashion.
         * @see #localeProvider(LocaleProvider)
         */
        public Builder locale(final Locale locale) {
            return localeProvider(locale == null ? null : new LocaleProvider() {
                public Locale provideLocale() {
                    return locale;
                }
            });
        }

        /**
         * Operation to specify a {@link LocaleProvider} for looking up descriptions.
         * This may be useful in case multiple translations are available, but the current locale is managed separately
         * from the {@link Locale#getDefault() default locale}, for instance by a per-thread managed value.
         * <p>
         * This is not required. The {@link ResourceBundleDescriptionProvider} will use the
         * {@link Locale#getDefault() default locale} otherwise.
         *
         * @param localeProvider The provider for the locale to obtain the resource bundles for.
         * @return Reference to this builder to specify other configuration parameters for,
         * in a 'method chaining' fashion.
         * @see #locale(Locale)
         */
        public Builder localeProvider(LocaleProvider localeProvider) {
            this.localeProvider = localeProvider;
            return this;
        }

        /**
         * Operation to specify a prefix for the key to use for looking up descriptions within a particular
         * ResourceBundle.
         * <p>
         * This is not required. The {@link ResourceBundleDescriptionProvider} will use the default prefix
         * <code>"description."</code> by default.
         *
         * @param prefix The prefix to use in the message key before the enumerable constant name (or value).
         * @return Reference to this builder to specify other configuration parameters for,
         * in a 'method chaining' fashion.
         * @see #suffix(String)
         */
        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * Operation to specify a suffix for the key to use for looking up descriptions within a particular
         * ResourceBundle.
         * <p>
         * This is not required. The {@link ResourceBundleDescriptionProvider} will use no suffix by default.
         *
         * @param suffix The suffix to use in the message key after the enumerable constant name (or value).
         * @return Reference to this builder to specify other configuration parameters for,
         * in a 'method chaining' fashion.
         * @see #prefix(String)
         */
        public Builder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        private boolean matchesDefault() {
            return bundleName == null
                    && (localeProvider == null || LocaleProvider.DEFAULT.equals(localeProvider))
                    && (prefix == null || prefix.equals(DEFAULT_PREFIX))
                    && (suffix == null || suffix.equals(DEFAULT_SUFFIX));
        }

        /**
         * The 'build' operation. Creates a new {@link ResourceBundleDescriptionProvider} based on the specified
         * configuration parameters, or returns a reference to the {@link #DEFAULT} implementation if all parameters
         * are either <code>null</code> or equal to the default values.
         *
         * @return The description provider with the specified configuration parameters.
         */
        public ResourceBundleDescriptionProvider build() {
            return matchesDefault() ? DEFAULT
                    : new ResourceBundleDescriptionProvider(bundleName, localeProvider, prefix, suffix);
        }
    }

}
