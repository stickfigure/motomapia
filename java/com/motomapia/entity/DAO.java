/*
 * $Id$
 */

package com.motomapia.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

/**
 * Our basic data access interface.
 * 
 * @author Jeff Schnitzer
 */
public class DAO extends DAOBase
{
	/** */
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(DAO.class);

	/** Register our entity types*/
	static {
		ObjectifyService.factory().register(Place.class);
	}
}