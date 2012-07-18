/*
 */

package com.motomapia;

import javax.inject.Singleton;

import com.googlecode.objectify.ObjectifyService;

/**
 * Gives us our custom version rather than the standard Objectify one. 
 * 
 * @author Jeff Schnitzer
 */
@Singleton
public class OfyService
{
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