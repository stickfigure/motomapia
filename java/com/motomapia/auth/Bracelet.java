/*
 */

package com.motomapia.auth;

import lombok.Getter;

import com.google.inject.servlet.RequestScoped;
import com.googlecode.objectify.Key;
import com.motomapia.entity.Person;


/**
 * This class authoritatively manages the identity of a user through the context of a request.
 * It works in concert with the BraceletFilter to establish an identity.
 */
@RequestScoped
public class Bracelet
{
	/**
	 * If we are logged in, this will be present.
	 */
	@Getter
	Key<Person> meKey;


	/**
	 * @return true if we are logged in
	 */
	public boolean isLoggedIn() {
		return this.meKey != null;
	}

	/**
	 * Log in our true identity.
	 */
	public void login(Key<Person> who) {
		this.meKey = who;
	}
}
