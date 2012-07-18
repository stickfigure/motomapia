/*
 */

package com.motomapia.entity;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * One human user associated with an email address.
 *
 * @author Jeff Schnitzer
 */
@Entity
@Cache
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class Person
{
	/** */
	public static Key<Person> key(long id) {
		return Key.create(Person.class, id);
	}
	
	/**
	 * Synthetic id
	 */
	@Id
	@Getter
	Long id;
	
	/** Pretty, non-normalized version */
	@Getter
	String email;

	/** Date user first logged in */
	@Index
	@Getter
	Date created;

	/** Date user last logged in */
	@Index
	@Getter
	Date lastLogin;
	
	/**
	 */
	public Person(String email) {
		this.email = email;
		this.created = new Date();
	}

	/** */
	public void loggedIn() {
		this.lastLogin = new Date();
	}

	public Key<Person> getKey() {
		return key(id);
	}
}