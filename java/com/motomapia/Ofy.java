/*
 */

package com.motomapia;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.TxnWork;
import com.googlecode.objectify.util.cmd.ObjectifyWrapper;
import com.googlecode.objectify.x.ObjectifyService;

/**
 * Our basic data access interface.  Extends the basic Objectify interface to add our custom logic.
 *
 * @author Jeff Schnitzer
 */
public class Ofy extends ObjectifyWrapper<Ofy, OfyFactory>
{
	/**
	 * This is a temporary workaround to handle push() and pop() without impacting the objectify core.
	 */
	abstract public static class Work<R> implements TxnWork<Ofy, R> {
		@Override
		final public R run(Ofy ofy) {
			try {
				ObjectifyService.push(ofy);
				return run();
			} finally {
				ObjectifyService.pop();
			}
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