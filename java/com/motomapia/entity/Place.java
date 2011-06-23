/*
 */

package com.motomapia.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beoui.geocell.GeocellManager;
import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import com.motomapia.util.GeoUtils;
import com.motomapia.util.Utils;
import com.motomapia.wikimapia.WikiPlace;

/**
 * Cached version of a Wikimapia place.  Also includes geohash indexing so that
 * we can fetch large numbers of points in a bounding box.
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

	/** When this record was created or updated */
	@Indexed
	@Getter Date updated;

	/** Textual name in Wikimapia */
	@Getter @Setter String name;
	
	/** Calculated centerpoint */
	@Getter @Setter GeoPt center;
	
	/** Properly cleaned and converted polyline.  Does *not* contain duplicate points */
	@Getter @Setter String polygon;
	
	/** Number of points in the polygon */
	@Getter @Setter int pointCount;
	
	/** Calculated area */
	@Getter @Setter double area;
	
	/** A list of geohashed cells, suitable for queries */
	@Indexed
	@Getter List<String> cells;
	
	/** GAE & Objectify want this */
	public Place() {}
	
	/** */
	public Place(WikiPlace wikiPlace)
	{
		this.id = wikiPlace.getId();
		this.updateFrom(wikiPlace);
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
		
		if (!Utils.safeEquals(this.name, wikiPlace.getName()))
		{
			this.name = wikiPlace.getName();
			changed = true;
		}
		
		String wikiPolyline = wikiPlace.getPolyline();
		if (!wikiPolyline.equals(this.polygon))
		{
			this.polygon = wikiPolyline;
			this.pointCount = wikiPlace.getPolygon().length;
			this.center = wikiPlace.getCenter();
			this.area = wikiPlace.getArea();
			this.cells = GeocellManager.generateGeoCell(GeoUtils.toPoint(this.center));
			changed = true;
		}
		
		if (changed)
			this.updated = new Date();
		
		return changed;
	}
}