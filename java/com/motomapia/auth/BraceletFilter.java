/*
 */

package com.motomapia.auth;

import java.io.IOException;

import javax.inject.Provider;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.motomapia.entity.Person;
import com.motomapia.util.AbstractFilter;

/**
 * This filter keeps the Bracelet updated from a header sent along with each request.
 *
 * Note that we don't create Person or update login date here; this is strictly to set up the
 * Bracelet.  Those functions are handled by specific login callbacks.
 */
@Singleton
//@Slf4j
public class BraceletFilter extends AbstractFilter
{
	/**
	 * Convenient to hook these together.
	 */
	public static class Worker {
		/** */
		@Inject Bracelet bracelet;
		@Inject Doorman doorman;

		/** */
		public void go() {
			Key<Person> me = doorman.get();

			if (me != null)
				bracelet.login(me);
		}
	}

	/** */
	@Inject Provider<Worker> workerProv;

	/**
	 */
	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		// Only process this for requests, not forwards (which would trigger the filter again)
		if (request.getAttribute("javax.servlet.forward.request_uri") == null) {
			Worker worker = workerProv.get();
			worker.go();
		}

		chain.doFilter(request, response);
	}
}
