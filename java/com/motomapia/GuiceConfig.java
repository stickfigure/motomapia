/*
 */

package com.motomapia;

import javax.inject.Singleton;
import javax.servlet.ServletContextEvent;

import lombok.extern.slf4j.Slf4j;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import com.motomapia.util.GuiceResteasyFilterDispatcher;


/**
 * Creates our Guice module
 *
 * @author Jeff Schnitzer
 */
@Slf4j
public class GuiceConfig extends GuiceServletContextListener
{
	/** */
	static class MotomapiaServletModule extends ServletModule
	{
		/* (non-Javadoc)
		 * @see com.google.inject.servlet.ServletModule#configureServlets()
		 */
		@Override
		protected void configureServlets() {
			filter("/*").through(AsyncCacheFilter.class);
			filter("/api/*").through(GuiceResteasyFilterDispatcher.class);
			serve("/download/*").with(DownloadServlet.class);
		}
	}

	/** Public so it can be used by unit tests */
	public static class MotompaiaModule extends AbstractModule
	{
		/* (non-Javadoc)
		 * @see com.google.inject.AbstractModule#configure()
		 */
		@Override
		protected void configure() {
			// External things that don't have Guice annotations
			bind(AsyncCacheFilter.class).in(Singleton.class);

			bind(Places.class);
		}

		/** */
		@Provides @RequestScoped
		Ofy provideOfy(OfyFactory fact) {
			return fact.begin();
		}
	}

	/**
	 * Logs the time required to initialize Guice
	 */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		long time = System.currentTimeMillis();

		super.contextInitialized(servletContextEvent);

		long millis = System.currentTimeMillis() - time;
		log.info("Guice initialization took " + millis + " millis");
	}

	/* (non-Javadoc)
	 * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
	 */
	@Override
	protected Injector getInjector() {
		Injector inj = Guice.createInjector(new MotomapiaServletModule(), new MotompaiaModule());
		return inj;
	}

}