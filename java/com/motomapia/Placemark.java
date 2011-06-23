/*
 */
package com.motomapia;

import com.google.appengine.api.datastore.GeoPt;

/**
 * A mark that represents a place on Wikimapia.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public class Placemark extends Mark
{
	private static final long serialVersionUID = 1L;
	
	/** */
	long placeId;
	public long getPlaceId() { return this.placeId; }
	
	/** Encoded as polyline, doesn't include the first point at the end */
	String polygon;
	public String getPolygon() { return this.polygon; }
	
	/** Number of points in the polygon */
	int pointCount;
	public int getPointCount() { return this.pointCount; }
	
	/** */
	double area;
	public double getArea() { return this.area; }
	
	/** */
	public Placemark() {}
	
	/** */
	public Placemark(GeoPt coords, String name, long id, String polygon, int pointCount, double area)
	{
		super(coords, name);

		this.placeId = id;
		this.polygon = polygon;
		this.pointCount = pointCount;
		this.area = area;
	}

	/** */
	@Override
	public String getUniqueId()
	{
		return this.getType() + "#" + this.getPlaceId();
	}
	
	/** */
	public String toString()
	{
		return super.toString() + "{polygon=" + this.polygon + "}";
	}
}
