/*
 */

package com.motomapia.util;

import com.beoui.geocell.model.Point;
import com.google.appengine.api.datastore.GeoPt;

/**
 * Some simple static methods useful for dealing with geolocation
 *  
 * @author Jeff Schnitzer
 */
public class GeoUtils 
{
	/**
	 * Convert to the geocell version
	 */
	public static Point toPoint(GeoPt geoPt)
	{
		return new Point(geoPt.getLatitude(), geoPt.getLongitude());
	}
	
	/**
	 * Convert to the google version
	 */
	public static GeoPt toGeoPt(Point point)
	{
		return new GeoPt((float)point.getLat(), (float)point.getLon());
	}
	
	/**
	 * Get the normal (unsigned) area of a polygon.
	 * 
	 * @param polygon is a simple closed polygon defined by a set of points. A line is drawn from
	 *  the last point to the first one; the array should not contain the first/last point twice. 
	 */
	public static double area(GeoPt[] polygon)
	{
		return Math.abs(areaSigned(polygon));
	}
	
	/**
	 * Get the signed area of a polygon, indicating if the points are left or right clockwise.
	 * 
	 * @param polygon is a simple closed polygon defined by a set of points. A line is drawn from
	 *  the last point to the first one; the array should not contain the first/last point twice. 
	 */
	public static double areaSigned(GeoPt[] polygon)
	{
		if (polygon.length < 2)
			return 0;
		
		double sum = 0.0;
		for (int i=0; i<polygon.length; i++)
		{
			int next = i + 1;
			if (next == polygon.length) next = 0;
			
			sum += ((double)polygon[i].getLongitude() * (double)polygon[next].getLatitude());
			sum -= ((double)polygon[i].getLatitude() * (double)polygon[next].getLongitude());
		}
		
		return sum / 2;
	}

	/**
	 * Gets the approximate center by averaging the points.  Really simple.
	 *  
	 * @param polygon is a simple closed polygon defined by a set of points. A line is drawn from
	 *  the last point to the first one; the array should not contain the first/last point twice. 
	 */
	public static GeoPt centroidByAverage(GeoPt[] polygon)
	{
		float cLat = 0;
		float cLon = 0;
		
		for (int i=0; i<polygon.length; i++)
		{
			cLat += polygon[i].getLatitude();
			cLon += polygon[i].getLongitude();
		}
		
		cLat /= polygon.length;
		cLon /= polygon.length;
		
		return new GeoPt(cLat, cLon);
	}
	
	/**
	 * Get the center of gravity of the polygon.
	 * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
	 * 
	 * @param polygon is a simple closed polygon defined by a set of points. A line is drawn from
	 *  the last point to the first one; the array should not contain the first/last point twice. 
	 */
	public static GeoPt centroid(GeoPt[] polygon)
	{
		if (polygon.length == 0)
			return null;
		else if (polygon.length == 1)
			return polygon[0];
			
		double cLat = 0.0f;
		double cLon = 0.0f;
		
		for (int i=0; i<polygon.length; i++)
		{
			double p1Lat = polygon[i].getLatitude();
			double p1Lon = polygon[i].getLongitude();
			
			int j = (i+1) == polygon.length ? 0 : i+1;
			
			double p2Lat = polygon[j].getLatitude();
			double p2Lon = polygon[j].getLongitude();

			double factor = p1Lon * p2Lat - p2Lon * p1Lat;
			cLat += (p1Lat + p2Lat) * factor;			
			cLon += (p1Lon + p2Lon) * factor;
		}

		double area = areaSigned(polygon);
		area *= 6.0;

		double factor = 1 / area;
		cLon *= factor;
		cLat *= factor;

		//log.debug("Centroid is " + cLat + ", " + cLon);
		return new GeoPt((float)cLat, (float)cLon);
	}
	
	/**
	 * Determine if a polygon contains a point.
	 * 
	 * @param polygon is a simple closed polygon defined by a set of points. A line is drawn from
	 *  the last point to the first one; the array should not contain the first/last point twice.
	 * @param pt is a point which is tested for enclosure in the polygon 
	 */
	public static boolean contains(GeoPt[] polygon, GeoPt pt)
	{
		int crossings = 0;
		for (int i=0; i<polygon.length; i++)
		{
			GeoPt p1 = polygon[i];
			GeoPt p2 = polygon[(i+1) == polygon.length ? 0 : i+1];
			
			double slope = (p2.getLongitude() - p1.getLongitude()) / (p2.getLatitude() - p1.getLatitude());
			boolean cond1 = (p1.getLatitude() <= pt.getLatitude()) && (pt.getLatitude() < p2.getLatitude());
			boolean cond2 = (p2.getLatitude() <= pt.getLatitude()) && (pt.getLatitude() < p1.getLatitude());
			boolean cond3 = pt.getLongitude() < slope * (pt.getLatitude() - p1.getLatitude()) + p1.getLongitude();
			if ((cond1 || cond2) && cond3)
				crossings++;
		}
		
		return (crossings % 2 != 0);
	}
}
