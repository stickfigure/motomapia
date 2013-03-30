/*
 */

package com.motomapia.action;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.extern.slf4j.Slf4j;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.TxnType;
import com.motomapia.entity.Person;
import com.motomapia.util.txn.Transact;

import static com.motomapia.OfyService.ofy;

/**
 * Test out some odd issues with the @Transact code
 */
@Path("/txntest")
@Slf4j
public class TxnTest
{
	/**
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Person update(@PathParam("id") long id) {
		return update(Key.create(Person.class, id));
	}

	/**
	 * Transactionally update arbitrary field in person
	 */
	@Transact(TxnType.REQUIRED)
	Person update(Key<Person> key) {
		Person pers = ofy().load().key(key).get();
		pers.loggedIn();
		ofy().save().entity(pers);

		log.debug("Last login date is now " + pers.getLastLogin());
		return pers;
	}
}
