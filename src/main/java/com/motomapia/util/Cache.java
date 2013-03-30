/*
 * $Id$
 */

package com.motomapia.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * Creates type-safe, namespaced interfaces to the memcache.  For any exception
 * that goes wrong when fetching from memcache, just returns null (or empty).
 * 
 * @author Jeff Schnitzer
 */
public class Cache<K, V>
{
	/** */
	private static final Logger log = LoggerFactory.getLogger(Cache.class);
	
	/** */
	MemcacheService memCache;
	
	/** If non-null, expire all entries after this number of seconds */
	Integer expireSeconds;

	/** Create cache interface with default namespace */
	public Cache(String namespace)
	{
		this.memCache = MemcacheServiceFactory.getMemcacheService(namespace);
	}
	
	/** Create cache with explicit namespace and an expiration period */
	public Cache(String namespace, int expireSeconds)
	{
		this(namespace);
		this.expireSeconds = expireSeconds;
	}
	
	/** Create cache interface with namespace defined by a class */
	public Cache(Class<?> clazzNamespace)
	{
		this(clazzNamespace.getSimpleName());
	}
	
	/** */
	@SuppressWarnings("unchecked")
	public V get(K key)
	{
		try
		{
			return (V)this.memCache.get(key);
		}
		catch (Exception e)
		{
			log.warn("Exception from memCache: " + e);
			return null;
		}
	}
	
	/** */
	public void put(K key, V value)
	{
		if (this.expireSeconds != null)
			this.memCache.put(key, value, Expiration.byDeltaSeconds(this.expireSeconds));
		else
			this.memCache.put(key, value);
	}
	
	/** */
	@SuppressWarnings("unchecked")
	public void putAll(Map<K, V> values)
	{
		if (this.expireSeconds != null)
			this.memCache.putAll((Map<Object, Object>)values, Expiration.byDeltaSeconds(this.expireSeconds));
		else
			this.memCache.putAll((Map<Object, Object>)values);
	}
	
	/** */
	@SuppressWarnings("unchecked")
	public Map<K, V> getAll(Collection<K> keys)
	{
		try
		{
			Map<K, V> all = (Map<K, V>)this.memCache.getAll((Collection<Object>) keys);
			return all == null ? Collections.EMPTY_MAP : all;
		}
		catch (Exception e)
		{
			log.warn("Exception from memCache: " + e);
			return Collections.EMPTY_MAP;
		}
	}

	/** */
	public boolean remove(K key)
	{
		return this.memCache.delete(key);
	}
	
	/** */
	@SuppressWarnings("unchecked")
	public Set<K> removeAll(Collection<K> keys)
	{
		return (Set<K>)this.memCache.deleteAll((Collection<Object>)keys);
	}
}