/*
 */
package com.motomapia;

import java.io.Serializable;

import com.google.appengine.api.datastore.GeoPt;

/**
 * Base class for all placemarks on the map.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public class Mark implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** In degrees */
	GeoPt coords;
	public GeoPt getCoords() { return this.coords; }
	
	/** Name might be null, careful */
	String name;
	public String getName() { return this.name; }
	
	/** */
	public Mark() {}
	
	/** */
	public Mark(GeoPt coords, String name)
	{
		this.coords = coords;
		this.name = name;
	}
	
	/** @return the simple type name of this placemark */
	public String getType()
	{
		// Note that Class.getSimpleName() doesn't work in GWT
		String className = this.getClass().getName();
		return className.substring(className.lastIndexOf('.') + 1);
	}
	
	/** @return an arbitrarily constructed unique id for this placemark */
	public String getUniqueId()
	{
		return this.getType() + "#" + getName();
	}
	
	/** */
	public String toString()
	{
		return this.getClass().getName() + "{name=" + name + ", uniqueId=" + this.getUniqueId() + "}";
	}
	
	/** Compare on unique id */
	public boolean equals(Object other)
	{
		if (other == null)
			return false;
		else if (!(other instanceof Mark))
			return false;
		else
			return this.getUniqueId().equals(((Mark)other).getUniqueId());
	}
}
