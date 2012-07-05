package com.motomapia.wikimapia;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.codehaus.jackson.JsonNode;

import com.google.appengine.api.datastore.GeoPt;
import com.motomapia.util.GeoUtils;
import com.motomapia.util.Polylines;

/**
 * The data methods on Wikimapia return WikiPlaces.  Naturally sorts by area descending.
 * Note that center and area are calculated; the official coordinate values are
 * absolutely worthless.
 */
@Data
@EqualsAndHashCode(of="id")
@ToString(of={"id", "name"})
public class WikiPlace implements Serializable, Comparable<WikiPlace>
{
	private static final long serialVersionUID = 1L;
	
	final long id;
	final String name;
	final GeoPt center;
	final GeoPt[] polygon;
	final double area;

	/** 
	 * Construct based on data from wikimapia, assumes node is valid (not error).
	 * @throws WikimapiaException if something is wrong with the data
	 */
	public WikiPlace(JsonNode placeNode, boolean fromBox) throws WikimapiaException
	{
		if (fromBox)
		{
			// Annoyingly, the id comes back as a String
			String idStr = placeNode.get("id").getTextValue();
			this.id = Long.parseLong(idStr);
			
			String nameStr = placeNode.path("name").getTextValue();
			if (nameStr != null)
				nameStr = nameStr.trim();
			
			this.name = nameStr;
		}
		else	// from an object (place) query
		{
			this.id = placeNode.get("id").getLongValue();
			
			String nameStr = placeNode.path("title").getTextValue();
			if (nameStr != null)
				nameStr = nameStr.trim();
			
			this.name = nameStr;
		}
		
//			JsonNode locationNode = placeNode.get("location");
//			double lat = locationNode.get("lat").getDoubleValue();
//			double lon = locationNode.get("lon").getDoubleValue();
//			GeoPt coords = new GeoPt((float)lat, (float)lon);

		JsonNode polygonNode = placeNode.path("polygon");
		
		// The wikimapia data has some issues - middling polygon points are sometimes duplicated, and the last
		// point seems to be the first point.  Let's just get rid of all duplicate points and assume a closed
		// polygon.
		Set<GeoPt> visited = new LinkedHashSet<GeoPt>(polygonNode.size(), 1);
		
		for (int pointIndex=0; pointIndex<polygonNode.size(); pointIndex++)
		{
			JsonNode pointNode = polygonNode.get(pointIndex);
			double polyLat = pointNode.get("y").getDoubleValue();
			double polyLon = pointNode.get("x").getDoubleValue();
			
			GeoPt thisPoint = new GeoPt((float)polyLat, (float)polyLon);
			if (!visited.contains(thisPoint))
				visited.add(thisPoint);
		}
		
		if (visited.size() < 3)
			throw new WikimapiaException("Not a real polygon");
		
		this.polygon = new GeoPt[visited.size()];
		int i = 0;
		for (GeoPt pt: visited)
			polygon[i++] = pt;

		this.area = GeoUtils.area(polygon);
		this.center = GeoUtils.centroid(polygon);
	}
	
	/** This is actually tricky to get right, equality must mean id equality */
	@Override
	public int compareTo(WikiPlace o)
	{
		if (this.id == o.id)
			return 0;
		else if (this.area == o.area)
			return (this.id < o.id) ? -1 : 1;
		else
			return (this.area > o.area) ? -1 : 1;
	}
	
	/** */
	public String getPolyline()
	{
		return Polylines.encode(this.polygon);
	}
	
	/** Produce a list of the place names */
	public static String namesOf(Collection<WikiPlace> places)
	{
		StringBuilder bld = new StringBuilder(places.size() * 26);
		
		boolean afterFirst = false;
		for (WikiPlace place: places)
		{
			if (afterFirst)
				bld.append(", ");
			else
				afterFirst = true;
			
			bld.append(place.getName());
			bld.append('-');
			bld.append(place.getId());
		}
		
		return bld.toString();
	}
}