/*
 */

package com.motomapia.action;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.extern.slf4j.Slf4j;

import com.googlecode.objectify.TxnType;
import com.motomapia.auth.Doorman;
import com.motomapia.entity.EmailLookup;
import com.motomapia.entity.Person;
import com.motomapia.util.BrowserID;
import com.motomapia.util.BrowserID.Assertion;
import com.motomapia.util.txn.Transact;

import static com.motomapia.OfyService.ofy;

/**
 * Methods related to signing in and registering.
 */
@Path("/")
@Slf4j
public class SignIn
{
	@Inject Doorman doorman;
	@Inject HttpServletRequest request;

	/**
	 * Tries to log a user in via a browserid assertion.
	 * Sets cookies and logs them into the Bracelet.
	 */
	@POST
	@Path("/login/persona")
	@Produces(MediaType.APPLICATION_JSON)
	public Person loginPersona(@FormParam("assertion") String assertion) throws IOException {

		String email = verify(assertion);

		Person who = login(email);
		
		doorman.login(who);

		return who;
	}

	/**
	 * Transactionally log in the Person, creating one if necessary
	 */
	@Transact(TxnType.REQUIRED)
	Person login(final String email) {
		
		// Might be a simple login rather than a new account
		Person result = ofy().load().personByEmail(email);
		if (result != null) {
			result.loggedIn();
			ofy().save().entity(result);
		} else {
			result = new Person(email);
			result.loggedIn();

			ofy().save().entity(result).now();
			ofy().save().entity(new EmailLookup(email, result));
		}

		return result;
	}

	/**
	 * Verify the assertion and return the email component.
	 * Normally we should hardcode the audience, but since GAE only allows specific virtual hosts, this should be secure.
	 */
	private String verify(String assertion) throws IOException {
		String audience = request.getScheme() + "://" + request.getServerName();
		if (request.getServerPort() != 80)
			audience += ":" + request.getServerPort();

		log.debug("Audience is: " + audience);
		
		Assertion ass = BrowserID.verify(assertion, audience);
		log.debug("Asserting " + ass);

		return ass.getEmail();
	}
}
