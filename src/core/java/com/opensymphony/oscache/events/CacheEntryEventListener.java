/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.oscache.events;


/**
 * This is the interface to listen to cache entry events. There is a method
 * for each event type. These methods are called via a dispatcher. If you
 * want to be notified when an event occurs on an entry, group or across a
 * pattern, register a listener and implement this interface.
 *
 * @version        $Revision$
 * @author <a href="mailto:fbeauregard@pyxis-tech.com">Francois Beauregard</a>
 */
public interface CacheEntryEventListener extends CacheListener {
    /**
     * Event fired when an entry is added to the cache.
     */
    void cacheEntryAdded(CacheEntryEvent event);

    /**
     * Event fired when an entry is flushed from the cache.
     */
    void cacheEntryFlushed(CacheEntryEvent event);

    /**
     * Event fired when an entry is removed from the cache.
     */
    void cacheEntryRemoved(CacheEntryEvent event);

    /**
     * Event fired when an entry is updated in the cache.
     */
    void cacheEntryUpdated(CacheEntryEvent event);

    /**
     * Event fired when a group is flushed from the cache.
     */
    void cacheGroupFlushed(CacheGroupEvent event);

    /**
     * An event that is fired when an entire cache gets flushed.
     */
    void cacheFlushed(CachewideEvent event);
}
