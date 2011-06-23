/*
 */

package com.motomapia.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import com.motomapia.wikimapia.WikiPlace;

/**
 * Our version of a Wikimapia place - we cache their data and add
 * a bit of our own.
 * 
 * @author Jeff Schnitzer
 */
@Cached
@Unindexed
@ToString
@Entity(name="P")
public class Place implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** */
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(Place.class);
	
	/** Wikimapia place id */
	@Id
	@Getter long id;

	/** When this record was created; the first time a user touched the data. */
	@Getter Date created;

	/** When the wikimapia data was last modified */
	@Indexed
	@Getter Date modified;
	
	/** Textual name in Wikimapia */
	@Getter @Setter String name;
	
	/** Calculated centerpoint */
	@Getter @Setter GeoPt center;
	
	/** Properly cleaned and converted.  Does *not* contain duplicate points */
	@Getter @Setter String polygon;
	
	/** Number of points in the polygon */
	@Getter @Setter int pointCount;
	
	/** Calculated area */
	@Getter @Setter double area;
	
	/** GAE & Objectify want this */
	public Place() {}
	
	/** */
	public Place(WikiPlace wikiPlace)
	{
		this.id = wikiPlace.getId();
		this.name = wikiPlace.getName();
		this.center = wikiPlace.getCenter();
		this.polygon = wikiPlace.getPolyline();
		this.pointCount = wikiPlace.getPolygon().length;
		this.area = wikiPlace.getArea();
		
		this.created = new Date();
		this.modified = this.created;
	}
	
	/**
	 * Update any wikimapia data in this place from the specified WikiPlace.
	 * @return true if the Place changed, false if it remains unchanged.
	 */
	public boolean updateFrom(WikiPlace wikiPlace)
	{
		if (wikiPlace.getId() != this.id)
			throw new IllegalArgumentException("Tried to update from mismatched WikiPlace");

		boolean changed = false;
		
		if (!this.name.equals(wikiPlace.getName()))
		{
			this.name = wikiPlace.getName();
			changed = true;
		}
		
		if (!this.center.equals(wikiPlace.getCenter()))
		{
			this.center = wikiPlace.getCenter();
			changed = true;
		}

		String wikiPolyline = wikiPlace.getPolyline();
		if (!wikiPolyline.equals(this.polygon))
		{
			this.polygon = wikiPolyline;
			this.pointCount = wikiPlace.getPolygon().length;
			changed = true;
		}
		
		if (this.area != wikiPlace.getArea())
		{
			this.area = wikiPlace.getArea();
			changed = true;
		}

		if (changed)
			this.modified = new Date();
		
		return changed;
	}
}