/*
 */

package com.motomapia;

import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.ServletContextEvent;

import lombok.extern.slf4j.Slf4j;

import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.motomapia.auth.BraceletFilter;
import com.motomapia.util.GuiceResteasyFilterDispatcher;
import com.motomapia.util.txn.Transact;
import com.motomapia.util.txn.TransactInterceptor;


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
			Map<String, String> appstatsParams = Maps.newHashMap();
			appstatsParams.put("logMessage", "Appstats: /admin/appstats/details?time={ID}");
			appstatsParams.put("calculateRpcCosts", "true");
			filter("/*").through(AppstatsFilter.class, appstatsParams);
			serve("/appstats/*").with(AppstatsServlet.class);

			filter("/*").through(ObjectifyFilter.class);
			filter("/*").through(BraceletFilter.class);
			filter("/*").through(GuiceResteasyFilterDispatcher.class);
			
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
			requestStaticInjection(OfyFactory.class);
			
			// Lets us use @Transact
			bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transact.class), new TransactInterceptor());

			// External things that don't have Guice annotations
			bind(AppstatsFilter.class).in(Singleton.class);
			bind(AppstatsServlet.class).in(Singleton.class);
			bind(ObjectifyFilter.class).in(Singleton.class);

			bind(Places.class);
			bind(SignIn.class);
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
		return Guice.createInjector(new MotomapiaServletModule(), new MotompaiaModule());
	}

}