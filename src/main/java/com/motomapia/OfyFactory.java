/*
 */

package com.motomapia;

import com.google.inject.Injector;
import com.googlecode.objectify.ObjectifyFactory;
import com.motomapia.entity.EmailLookup;
import com.motomapia.entity.Person;
import com.motomapia.entity.Place;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Our version of ObjectifyFactory which integrates with Guice.  You could add convenience methods here too.
 *
 * @author Jeff Schnitzer
 */
@Singleton
@Slf4j
public class OfyFactory extends ObjectifyFactory
{
	/** */
	private Injector injector;

	/** Register our entity types */
	@Inject
	public OfyFactory(Injector injector) {
		this.injector = injector;

		long time = System.currentTimeMillis();

		this.register(Place.class);
		this.register(Person.class);
		this.register(EmailLookup.class);

		long millis = System.currentTimeMillis() - time;
		log.info("Registration took " + millis + " millis");
	}

	/** Use guice to make instances instead! */
	@Override
	public <T> T construct(Class<T> type) {
		return injector.getInstance(type);
	}

	@Override
	public Ofy begin() {
		return new Ofy(this);
	}
}
