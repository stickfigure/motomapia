/*
 */

package com.motomapia;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.util.cmd.ObjectifyWrapper;

/**
 * Our basic data access interface.  Extends the basic Objectify interface to add our custom logic.
 *
 * @author Jeff Schnitzer
 */
public class Ofy extends ObjectifyWrapper<Ofy, OfyFactory>
{
	/** */
	public Ofy(Objectify base) {
		super(base);
	}

	/** Shortcut */
	public OfyFactory fact() {
		return this.getFactory();
	}

	/** More wrappers, fun */
	@Override
	public OfyLoader load() {
		return new OfyLoader(super.load());
	}
}