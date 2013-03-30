/*
 */

package com.motomapia;

import com.googlecode.objectify.ObjectifyService;

/**
 * Gives us our custom version rather than the standard Objectify one.  Also responsible for setting up the static
 * OfyFactory instead of the standard ObjectifyFactory.
 * 
 * @author Jeff Schnitzer
 */
public class OfyService
{
	static {
		ObjectifyService.setFactory(new OfyFactory());
	}
	
	/**
	 * @return our extension to Objectify
	 */
	public static Ofy ofy() {
		return (Ofy)ObjectifyService.ofy();
	}

	/**
	 * @return our extension to ObjectifyFactory
	 */
	public static OfyFactory factory() {
		return (OfyFactory)ObjectifyService.factory();
	}
}