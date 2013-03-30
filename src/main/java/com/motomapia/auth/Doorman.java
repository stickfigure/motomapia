/*
 */

package com.motomapia.auth;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import com.googlecode.objectify.Key;
import com.motomapia.entity.Person;
import com.motomapia.util.CookieUtils;


/**
 * Manages local identity with a login cookie.
 */
@Slf4j
public class Doorman
{
	/** Name of the cookie that holds login info for the session.  IF CHANGED, also change master.coffee logout mechanism. */
	public static final String SESSION_COOKIE = "login";

	/** Login state is based off of cookies */
	@Inject HttpServletRequest request;
	@Inject HttpServletResponse response;

	/**
	 * Get the current person based on cookie
	 * @return null if there is no valid cookie
	 */
	public Key<Person> get() {
		try {
			return this.getImpl();
		} catch (Exception ex) {
			log.error("Error getting login cookie", ex);
			CookieUtils.deleteCookie(request, response, SESSION_COOKIE);
			return null;
		}
	}

	/**
	 * Impl that can throw exceptions
	 */
	private Key<Person> getImpl() throws Exception {

		AuthCookie cook = this.getAuthCookie(SESSION_COOKIE);
		if (cook == null)
			return null;

		log.debug("Verified and logging in " + cook.getId() + ":" + cook.getEmail());

		// Reset the session cookie with a new expiration
		AuthCookie nextCookie = cook.next();
		CookieUtils.setCookie(response, SESSION_COOKIE, nextCookie.toCookieString());

		return Person.key(cook.getId());
	}

	/**
	 * Tries to get the valid id from a cookie.  If the cookie does not exist or is expired,
	 * this will return null.  Also cleans up expired cookies.
	 */
	private AuthCookie getAuthCookie(String cookieName) {
		String value = CookieUtils.getCookieValue(request, cookieName);

		if (value == null)
			return null;

		AuthCookie cook = new AuthCookie(value);

		if (!cook.isFresh()) {
			log.info("Cookie {} expired", cookieName);
			CookieUtils.deleteCookie(request, response, cookieName);
			return null;
		}

		if (!cook.isValid()) {
			log.warn("Cookie signature for {} failed: {}", cookieName, value);
			CookieUtils.deleteCookie(request, response, cookieName);
			return null;
		}

		return cook;
	}

	/**
	 * Sets the signed identity cookie(s)
	 */
	public void login(Person pers) {
		AuthCookie cook = new AuthCookie(pers.getId(), pers.getEmail());
		CookieUtils.setCookie(response, SESSION_COOKIE, cook.toCookieString());

		log.debug("New login for " + pers.getId() + ":" + pers.getEmail());
	}
}
