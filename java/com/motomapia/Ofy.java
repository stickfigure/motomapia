/*
 */

package com.motomapia;

import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.TxnWork;
import com.googlecode.objectify.util.cmd.ObjectifyWrapper;

/**
 * Our basic data access interface.  Extends the basic Objectify interface to add our custom logic.
 *
 * @author Jeff Schnitzer
 */
public class Ofy extends ObjectifyWrapper<Ofy, OfyFactory>
{
	/**
	 * A Work interface you can use with Ofy.
	 */
	public interface Work<R> extends TxnWork<Ofy, R> {}

	/** Work which doesn't require returning a value */
	public static abstract class VoidWork implements Work<Void> {
		@Override
		public Void run(Ofy ofy) {
			this.vrun(ofy);
			return null;
		}

		abstract public void vrun(Ofy ofy);
	}

	/** */
	public Ofy(Objectify base) {
		super(base);
	}

	/** Shortcut */
	public OfyFactory fact() {
		return this.getFactory();
	}

	/** Convenience method */
	public <T> T load(Key<T> key) {
		return load().key(key).get();
	}

	/** Convenience method */
	public <T> T load(Class<T> clazz, long id) {
		return load().type(clazz).id(id).get();
	}

	/** Convenience method */
	public <T> T load(Class<T> clazz, String id) {
		return load().type(clazz).id(id).get();
	}

	/** Convenience method */
	public <T> T loadSafe(Key<T> key) {
		return load().key(key).safe();
	}

	/** Convenience method */
	public <T> T loadSafe(Class<T> clazz, long id) {
		return load().type(clazz).id(id).safe();
	}

	/** Convenience method */
	public <T> T loadSafe(Class<T> clazz, String id) {
		return load().type(clazz).id(id).safe();
	}

	/** Convenience method */
	public <T> Key<T> save(T entity) {
		return save().entity(entity).now();
	}

	/** Convenience method */
	public <K, E extends K> Map<Key<K>, E> save(E... entities) {
		return save().<K, E>entities(entities).now();
	}

	/** Convenience method */
	public <K, E extends K> Map<Key<K>, E> save(Iterable<E> entities) {
		return save().<K, E>entities(entities).now();
	}

	/** Convenience method */
	public void delete(Object... keysOrEntities) {
		delete().keys(keysOrEntities).now();
	}

	/** Convenience method */
	public void delete(Iterable<?> keysOrEntities) {
		delete().keys(keysOrEntities).now();
	}
}