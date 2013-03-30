/*
 */
package com.motomapia;

import java.io.Serializable;

import lombok.Data;

import com.google.appengine.api.datastore.GeoPt;

/**
 * A mark that represents a place on Wikimapia.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
@Data
public class Placemark implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/** */
	long id;
	
	/** */
	String name;

	/** Calculated centerpoint */
	GeoPt center;
	
	/** Encoded as polyline, doesn't include the first point at the end */
	String polygon;
	
	/** Number of points in the polygon */
	int pointCount;
	
	/** */
	double area;
	
	/** */
	public Placemark() {}
	
	/** */
	public Placemark(long id, String name, GeoPt center, String polygon, int pointCount, double area)
	{
		this.id = id;
		this.name = name;
		this.center = center;
		this.polygon = polygon;
		this.pointCount = pointCount;
		this.area = area;
	}
}
