/*
 */

package com.motomapia.util;

import java.lang.reflect.Type;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.guice.GuiceResourceFactory;
import org.jboss.resteasy.plugins.server.servlet.FilterDispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;


/**
 * Use this as the Resteasy FilterDispatcher and it will be properly integrated with Guice.
 * The normal Guice integration via servlet context listener seems to be incompatible with
 * the "guice way".
 *
 * Note that you must define this filter in Guice so that it gets proper injection.
 *
 * @author Jeff Schnitzer
 */
@Singleton
//@Slf4j
public class GuiceResteasyFilterDispatcher extends FilterDispatcher
{
	@Inject Injector injector;

	/**
	 * Also initializes all Guice bindings for
	 */
	@Override
	public void init(FilterConfig cfg) throws ServletException {
		super.init(cfg);

		ServletContext context = cfg.getServletContext();
		Registry registry = (Registry)context.getAttribute(Registry.class.getName());
		ResteasyProviderFactory providerFactory = (ResteasyProviderFactory)context.getAttribute(ResteasyProviderFactory.class.getName());

		// This code is copied wholesale as-is from (private) ModuleProcessor.processInjector()
		for (final Binding<?> binding : injector.getBindings().values())
		{
			final Type type = binding.getKey().getTypeLiteral().getType();
			if (type instanceof Class)
			{
				@SuppressWarnings("rawtypes")
				final Class<?> beanClass = (Class) type;
				if (GetRestful.isRootResource(beanClass))
				{
					final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
					//log.info("registering factory for {}", beanClass.getName());
					registry.addResourceFactory(resourceFactory);
				}
				if (beanClass.isAnnotationPresent(Provider.class))
				{
					//log.info("registering provider instance for {}", beanClass.getName());
					providerFactory.registerProviderInstance(binding.getProvider().get());
				}
			}
		}
	}
}