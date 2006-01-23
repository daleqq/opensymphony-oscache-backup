/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.oscache.general;

import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.oscache.core.AbstractCacheAdministrator;
import com.opensymphony.oscache.core.Cache;
import com.opensymphony.oscache.core.CacheEntry;
import com.opensymphony.oscache.core.EntryRefreshPolicy;
import com.opensymphony.oscache.core.MemoryCache;

/**
 * A GeneralCacheAdministrator creates, flushes and administers the cache.
 *
 * EXAMPLES :
 * <pre><code>
 * // ---------------------------------------------------------------
 * // Typical use with fail over
 * // ---------------------------------------------------------------
 * String myKey = "myKey";
 * String myValue;
 * int myRefreshPeriod = 1000;
 * try {
 *     // Get from the cache
 *     myValue = (String) admin.getFromCache(myKey, myRefreshPeriod);
 * } catch (NeedsRefreshException nre) {
 *     try {
 *         // Get the value (probably by calling an EJB)
 *         myValue = "This is the content retrieved.";
 *         // Store in the cache
 *         admin.putInCache(myKey, myValue);
 *     } catch (Exception ex) {
 *         // We have the current content if we want fail-over.
 *         myValue = (String) nre.getCacheContent();
 *         // It is essential that cancelUpdate is called if the
 *         // cached content is not rebuilt
 *         admin.cancelUpdate(myKey);
 *     }
 * }
 *
 *
 *
 * // ---------------------------------------------------------------
 * // Typical use without fail over
 * // ---------------------------------------------------------------
 * String myKey = "myKey";
 * String myValue;
 * int myRefreshPeriod = 1000;
 * try {
 *     // Get from the cache
 *     myValue = (String) admin.getFromCache(myKey, myRefreshPeriod);
 * } catch (NeedsRefreshException nre) {
 *     try {
 *         // Get the value (probably by calling an EJB)
 *         myValue = "This is the content retrieved.";
 *         // Store in the cache
 *         admin.putInCache(myKey, myValue);
 *         updated = true;
 *     } finally {
 *         if (!updated) {
 *             // It is essential that cancelUpdate is called if the
 *             // cached content could not be rebuilt
 *             admin.cancelUpdate(myKey);
 *         }
 *     }
 * }
 * // ---------------------------------------------------------------
 * // ---------------------------------------------------------------
 * </code></pre>
 *
 * @version        $Revision$
 * @author <a href="mailto:fbeauregard@pyxis-tech.com">Francois Beauregard</a>
 * @author <a href="mailto:abergevin@pyxis-tech.com">Alain Bergevin</a>
 */
public class GeneralCacheAdministrator extends AbstractCacheAdministrator {
    private static transient final Log log = LogFactory.getLog(GeneralCacheAdministrator.class);

    /**
     * Create the cache administrator.
     */
    public GeneralCacheAdministrator() {
        this(null);
    }

    /**
     * Create the cache administrator with the specified properties
     */
    public GeneralCacheAdministrator(Properties p) {
        super(p);
        log.info("Constructed GeneralCacheAdministrator()");
        createCache();
    }

    /**
     * Remove an object from the cache
     *
     * @param key             The key entered by the user.
     */
    public void removeEntry(String key) {
        getCache().removeEntry(key);
    }

    /**
     * Get an object from the cache
     *
     * @param key             The key entered by the user.
     * @return   The object from cache
     * @throws NeedsRefreshException when no cache entry could be found with the
     * supplied key, or when an entry was found but is considered out of date. If
     * the cache entry is a new entry that is currently being constructed this method
     * will block until the new entry becomes available. Similarly, it will block if
     * a stale entry is currently being rebuilt by another thread and cache blocking is
     * enabled (<code>cache.blocking=true</code>).
     */
    public Object getFromCache(String key)  {
        return getCache().get(key);
    }

    /**
     * Get an object from the cache
     *
     * @param key             The key entered by the user.
     * @param refreshPeriod   How long the object can stay in cache in seconds. To
     * allow the entry to stay in the cache indefinitely, supply a value of
     * {@link CacheEntry#INDEFINITE_EXPIRY}
     * @return   The object from cache
     * @throws NeedsRefreshException when no cache entry could be found with the
     * supplied key, or when an entry was found but is considered out of date. If
     * the cache entry is a new entry that is currently being constructed this method
     * will block until the new entry becomes available. Similarly, it will block if
     * a stale entry is currently being rebuilt by another thread and cache blocking is
     * enabled (<code>cache.blocking=true</code>).
     */
    public Object getFromCache(String key, int refreshPeriod) {
        return getCache().get(key, refreshPeriod);
    }

    /**
     * Get an object from the cache
     *
     * @param key             The key entered by the user.
     * @param refreshPeriod   How long the object can stay in cache in seconds. To
     * allow the entry to stay in the cache indefinitely, supply a value of
     * {@link CacheEntry#INDEFINITE_EXPIRY}
     * @param cronExpression  A cron expression that the age of the cache entry
     * will be compared to. If the entry is older than the most recent match for the
     * cron expression, the entry will be considered stale.
     * @return   The object from cache
     * @throws NeedsRefreshException when no cache entry could be found with the
     * supplied key, or when an entry was found but is considered out of date. If
     * the cache entry is a new entry that is currently being constructed this method
     * will block until the new entry becomes available. Similarly, it will block if
     * a stale entry is currently being rebuilt by another thread and cache blocking is
     * enabled (<code>cache.blocking=true</code>).
     */
    public Object getFromCache(String key, int refreshPeriod, String cronExpression) {
        return getCache().get(key, refreshPeriod, cronExpression);
    }

    /**
     * Cancels a pending cache update. This should only be called by a thread
     * that received a {@link NeedsRefreshException} and was unable to generate
     * some new cache content.
     *
     * @param key The cache entry key to cancel the update of.
     */
    public void cancelUpdate(String key) {
        getCache().cancelUpdate(key);
    }

    /**
     * Shuts down the cache administrator.
     */
    public void destroy() {
        finalizeListeners(applicationCache);
    }

    // METHODS THAT DELEGATES TO THE CACHE ---------------------

    /**
     * Flush the entire cache immediately.
     */
    public void flushAll() {
        getCache().flushAll(new Date());
    }

    /**
     * Flush the entire cache at the given date.
     *
     * @param date The time to flush
     */
    public void flushAll(Date date) {
        getCache().flushAll(date);
    }

    /**
     * Flushes a single cache entry.
     */
    public void flushEntry(String key) {
        getCache().flushEntry(key);
    }

    /**
     * Sets the cache capacity (number of items). If the cache contains
     * more than <code>capacity</code> items then items will be removed
     * to bring the cache back down to the new size.
     *
     * @param capacity The new capacity of the cache
     */
    public void setCacheCapacity(int capacity) {
        super.setCacheCapacity(capacity);
        getCache().setCapacity(capacity);
    }

    /**
     * Creates a cache in this admin
     */
    private void createCache() {
        log.info("Creating new cache");

        applicationCache = new MemoryCache(cacheCapacity);

        configureStandardListeners(applicationCache);
    }
}