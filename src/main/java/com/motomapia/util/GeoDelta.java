package com.motomapia.util;

import java.io.Serializable;

/**
 * Nearly identical to a GeoPt but allows values outside the bounds that GeoPt provides.
 * Also, this gives us a little bit more type safety.
 */
public class GeoDelta implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private float latitude;
	public float getLatitude() { return this.latitude; }
	
	private float longitude;
	public float getLongitude() { return this.longitude; }

	/** For GWT */
	public GeoDelta() {}
	
	public GeoDelta(float latitude, float longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String toString()
	{
		return this.getClass().getName() + "{latitude=" + latitude + ", longitude=" + longitude + "}";
	}
}