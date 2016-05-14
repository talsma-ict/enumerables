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
package nl.talsmasoftware.enumerable.descriptions;

import nl.talsmasoftware.enumerable.Enumerable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Registry for {@link DescriptionProvider description providers}.
 * <p>
 * It should normally not be necessary to access this class from your own code.
 * <p>
 * The default implementation of {@link Enumerable#getDescription()} uses this registry to find a suitable description
 * provider for itself.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
@SuppressWarnings("deprecation")
public final class DescriptionProviderRegistry {
    private static final Logger LOGGER = Logger.getLogger(DescriptionProviderRegistry.class.getName());

    /**
     * Constant to indicate in the registry that there is NO provider for a particular type.
     */
    private static final DescriptionProvider NO_PROVIDER = new DescriptionProvider() {
        public String describe(Enumerable enumerable) {
            return null;
        }
    };

    /**
     * Singleton instance of this registry.
     */
    private static final DescriptionProviderRegistry INSTANCE = new DescriptionProviderRegistry();

    /**
     * The description providers that were registered per enumerable type.
     */
    private final ConcurrentMap<Class<? extends Enumerable>, DescriptionProvider> providers =
            new ConcurrentHashMap<Class<? extends Enumerable>, DescriptionProvider>();

    /**
     * Private constructor due to singleton pattern.
     */
    private DescriptionProviderRegistry() {
    }

    /**
     * Singleton implementation.
     *
     * @return The unique description provider registry in this JVM classloader chain.
     */
    public static DescriptionProviderRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Initialization logic for each Enumerable type.
     * This is required to allow the type to 'auto-register' itself upon class reflection.
     *
     * @param enumerableType The enumerable type to initialize.
     * @return The initialized type.
     */
    private <E extends Enumerable> Class<E> _init(final Class<E> enumerableType) {
        if (!providers.containsKey(enumerableType)) {
            providers.putIfAbsent(enumerableType, NO_PROVIDER);
            Enumerable dummy = Enumerable.parse(enumerableType, "dummy");
            // Provider may now have been implicitly registered due to parse().
            LOGGER.log(Level.FINEST, "Tried to register an enumerable type by parsing a dummy value: {0}.", dummy);
        }
        return enumerableType;
    }

    private DescriptionProvider _nullForNoResult(DescriptionProvider provider) {
        return NO_PROVIDER.equals(provider) ? null : provider;
    }

    /**
     * Returns the description provider for the specified {@link Enumerable enumerable type}, if one was registered.
     *
     * @param enumerableType The Enumerable type to find a <code>description provider</code> for, if registered.
     * @return The found <code>DescriptionProvider</code>, or <code>null</code> if there was no provider registered
     * for the specified <code>Enumerable</code> type.
     */
    public DescriptionProvider getDescriptionProviderFor(Class<? extends Enumerable> enumerableType) {
        return _nullForNoResult(providers.get(_init(enumerableType)));
    }

    /**
     * This operation registers (or de-registers) a {@link DescriptionProvider} for an {@link Enumerable} type.
     *
     * @param enumerableType The Enumerable type for which a <code>DescriptionProvider</code> must be registered or
     *                       de-registered (required, non-<code>null</code>).
     * @param provider       The provider that should be used for descriptions of the specified enumerable type,
     *                       or <code>null</code> to de-register a previous description provider.
     * @return A <code>DescriptionProvider</code> that was registered and is now de-registered as a result of this
     * operation, or <code>null</code> if there was no previously registered provider.
     */
    public DescriptionProvider registreerOmschrijvingProvider(
            final Class<? extends Enumerable> enumerableType, final DescriptionProvider provider) {
        if (enumerableType == null)
            throw new IllegalStateException("The enumerable type is required to register a description provider.");
        return _nullForNoResult(providers.put(_init(enumerableType), provider == null ? NO_PROVIDER : provider));
    }

}
