/*
 * $Id$
 */

package com.motomapia.util;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Some basic utilities for manipulating cookies.
 * 
 * @author Jeff Schnitzer
 */
public class CookieUtils
{
	/**
	 * @return null if cookie is not present
	 */
	public static Cookie getCookie(HttpServletRequest request, String name)
	{
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null)
		{
			for (int i=0; i<cookies.length; i++)
			{
				if (cookies[i].getName().equals(name))
					return cookies[i];
			}
		}
		
		return null;
	}
	
	/**
	 * @return null if cookie is not present
	 */
	public static String getCookieValue(HttpServletRequest request, String name)
	{
		Cookie cook = getCookie(request, name);
		if (cook == null)
			return null;
		else
			return cook.getValue();
	}

	/**
	 * For systems like Facebook that encode a url-type map of values within a cookie.
	 * @return null if cookie is not present
	 */
	public static Map<String, String> getSubCookies(HttpServletRequest request, String name)
	{
		Cookie cook = getCookie(request, name);
		if (cook == null)
		{
			return null;
		}
		else
		{
			Map<String, String> cookMap = new TreeMap<String, String>();
			String[] kvpairs = cook.getValue().split("&");
			for (String kvPair: kvpairs)
			{
				String[] valPair = kvPair.split("=");
				cookMap.put(URLUtils.urlDecode(valPair[0]), URLUtils.urlDecode(valPair[1]));
			}
			
			return cookMap;
		}
	}
	
	/**
	 * Sets a root cookie which expires when the browser shuts down
	 */
	public static void setCookie(HttpServletResponse response, String name, String value)
	{
		setCookie(response, name, value, -1);
	}
	
	/**
	 * Sets a root cookie value with a specific maxAge in seconds (0 is delete, -1 is "nonpersistent")
	 */
	public static void setCookie(HttpServletResponse response, String name, String value, int maxAge)
	{
		Cookie cook = new Cookie(name, value);
		cook.setMaxAge(maxAge);
		cook.setPath("/");
		
		response.addCookie(cook);
	}
	
	/**
	 * Deletes the named cookie no matter what it's path or domain
	 */
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name)
	{
		Cookie cook = getCookie(request, name);
		if (cook != null)
		{
			cook.setMaxAge(0);
			response.addCookie(cook);
		}
	}
	
	/**
	 * Useful for debugging.
	 */
	public static String toString(Cookie cook)
	{
		if (cook == null)
			return "null";
		else
			return String.format("Cookie{name=%s, value=%s, domain=%s, maxAge=%s, path=%s, secure=%s, version=%s, comment=%s}",
					cook.getName(),
					cook.getValue(),
					cook.getDomain(),
					cook.getMaxAge(),
					cook.getPath(),
					cook.getSecure(),
					cook.getVersion(),
					cook.getComment()
					);
	}
}