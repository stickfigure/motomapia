/*
 */

package com.motomapia;

import javax.inject.Singleton;

import com.googlecode.objectify.x.ObjectifyService;

/**
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
	public static OfyFactory fact() {
		return (OfyFactory)ObjectifyService.fact();
	}
}