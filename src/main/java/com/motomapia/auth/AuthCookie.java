package com.motomapia.auth;

import lombok.Data;

import com.motomapia.util.CryptoUtils;
import com.motomapia.util.URLUtils;


/**
 * The components of Voost's auth cookie
 *
 * The cookie associated with a local identity looks like "YOURID:EMAIL:EXPIRETIME:SIGNATURE".  Expire time
 * is millis since epoch.  It gets refreshed every time this login is executed (each page load).
 * The cookie is session scoped (maxAge 0).  Note that there is javascript in master.coffee that parses this
 * cookie; be careful changing the format.
 */
@Data
public class AuthCookie {
	/** Keep it valid for two days */
	private static final long EXPIRATION_MILLIS = 1000 * 3600 * 48;

	/** Secret for digital signature */
	private static final byte[] SECRET = "addyourpasswordhere".getBytes();

	/** */
	long id;
	String email;
	long expires;
	String sig;

	/**
	 * Create a new cookie from the basic info, establishing expiration and signature.
	 */
	public AuthCookie(long id, String email) {
		this.id = id;
		this.email = email;

		this.expires = System.currentTimeMillis() + EXPIRATION_MILLIS;
		this.sig = this.generateSig();
	}

	/**
	 * Extract values from the string produced by toCookieString()
	 * @param cookieValue is from toCookieString()
	 */
	public AuthCookie(String cookieValue) {
		String[] parts = cookieValue.split(":");

		this.id = Long.parseLong(parts[0]);
		this.email = URLUtils.urlDecode(parts[1]);
		this.expires = Long.parseLong(parts[2]);
		this.sig = parts[3];
	}

	/** This cookie as a string value which can be used with the String constructor */
	public String toCookieString() {
		return id + ":" + URLUtils.urlEncode(email) + ":" + expires + ":" + sig;
	}

	/** Generate a signature based on internal values */
	private String generateSig() {
		String signIt = id + email + expires;
		return CryptoUtils.macHmacSHA256Hex(signIt, SECRET);
	}

	/** @return true if the signature passes validation */
	public boolean isValid() {
		String correct = this.generateSig();
		return correct.equals(this.sig);
	}

	/** @return true if the cookie has not expired */
	public boolean isFresh() {
		return expires > System.currentTimeMillis();
	}

	/** Gets a cookie with an updated expiration time and proper signature */
	public AuthCookie next() {
		return new AuthCookie(id, email);
	}
}