/*
 */

package com.motomapia.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import lombok.Data;

import org.codehaus.jackson.map.ObjectMapper;

import com.googlecode.batchfb.util.RequestBuilder;
import com.googlecode.batchfb.util.RequestBuilder.HttpMethod;
import com.googlecode.batchfb.util.RequestBuilder.HttpResponse;

/**
 * Some static methods to help with BrowserID
 */
public class BrowserID
{
	/** Number of times we should try */
	private static final int RETRIES = 2;

	/** */
	private static final String PROD_DOMAIN = "https://browserid.org";

	@SuppressWarnings("unused")
	private static final String DEV_DOMAIN = "https://diresworb.org";

	private static final String ACTIVE_DOMAIN = PROD_DOMAIN;
	private static final String VERIFY_LINK = ACTIVE_DOMAIN + "/verify";
	
	/** */
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Data
	public static class Assertion {
		String status;
		String email;
		String audience;
		long expires;
		String issuer;
		String reason;
	}

	/**
	 * Actually verify the assertion (at the browserid.org website) and return the parsed results.
	 * Retry this a few times just in case.
	 */
	public static Assertion verify(String assertion, String audience) throws IOException {
		int tries = 0;
		IOException last = null;

		while (tries++ <= RETRIES) {
			try {
				return verifyOnce(assertion, audience);
			} catch (IOException ex) {
				last = ex;
			}
		}

		throw last;
	}

	/**
	 * Actually verify the assertion (at the browserid.org website) and return the parsed results.
	 */
	private static Assertion verifyOnce(String assertion, String audience) throws IOException {

		RequestBuilder request = new RequestBuilder(VERIFY_LINK, HttpMethod.POST);
		request.addParam("assertion", assertion);
		request.addParam("audience", audience);

		HttpResponse response = request.execute();
		if (response.getResponseCode() != HttpServletResponse.SC_OK)
			throw new IllegalStateException("Bad response code: " + response.getResponseCode());

		Assertion result = MAPPER.readValue(response.getContentStream(), Assertion.class);

		if (!"okay".equals(result.getStatus()))
			throw new IllegalStateException("Bad assertion content: " + result);

		if (result.getExpires() < System.currentTimeMillis())
			throw new IllegalStateException("Expired: " + result);

		return result;
	}
}
