/*
 */

package com.motomapia;

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
	 * Get rid of the extra parameter
	 */
	abstract public static class Work<R> implements TxnWork<Ofy, R> {
		@Override
		final public R run(Ofy ofy) {
			return run();
		}

		abstract public R run();
	}

	/** Work which doesn't require returning a value */
	abstract public static class VoidWork extends Work<Void> {
		@Override
		final public Void run() {
			this.vrun();
			return null;
		}

		abstract public void vrun();
	}

	/** */
	public Ofy(Objectify base) {
		super(base);
	}

	/** Shortcut */
	public OfyFactory fact() {
		return this.getFactory();
	}
}