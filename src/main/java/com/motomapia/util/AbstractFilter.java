/*
 * $Id: AbstractFilter.java 465 2006-05-22 01:30:41Z lhoriman $
 * $URL: https://subetha.googlecode.com/svn/branches/resin/src/org/subethamail/web/util/AbstractFilter.java $
 */

package com.motomapia.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Some really trivial boilerplate code
 */
abstract public class AbstractFilter implements Filter
{
	/**
	 */
	private FilterConfig config = null;

	/**
	 */
	public void destroy()
	{
	}

	/**
	 */
	public void init(FilterConfig cfg) throws ServletException
	{
		this.config = cfg;
	}

	/**
	 */
	public FilterConfig getFilterConfig()
	{
		return this.config;
	}

	/**
	 */
	public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException
	{
		this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
	}

	/**
	 */
	abstract public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws IOException, ServletException;
}
