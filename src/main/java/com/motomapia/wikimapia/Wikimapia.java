/*
 */

package com.motomapia.wikimapia;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.GeoPt;
import com.motomapia.util.GeoDelta;

/**
 * Provides a friendly version of the Wikimapia API.
 *
 * @author Jeff Schnitzer
 */
public class Wikimapia
{
	/** */
	private static final Logger log = LoggerFactory.getLogger(Wikimapia.class);

	/** Generated for www.motomapia.com */
	public static final String API_KEY = "B702F8F8-2FAC2906-21A0B2E8-C75D01F6-DF257CD2-18D01244-A3DB4632-CAFDB16C";

	/**
	 * Gets description of a place
	 * @throws Exception if something goes wrong
	 */
	public static WikiPlaceDetail place(long placeId)
	{
		try
		{
			WikiPlaceDetail result = placeUncached(placeId);

			if (log.isDebugEnabled())
				log.debug("For place " + placeId + ", found: " + result);

			return result;
		}
		catch (IOException ex) { throw new WikimapiaException(ex); }

		// The old code with caching
//		try
//		{
//			WikiPlaceDetail result = Caches.place().get(placeId);
//			if (result == null)
//			{
//				// Get the value, put it in the cache
//				result = placeUncached(placeId);
//				Caches.place().put(placeId, result);
//			}
//
//			if (log.isDebugEnabled())
//				log.debug("For place " + placeId + ", found: " + result);
//
//			return result;
//		}
//		catch (IOException ex) { throw new WikimapiaException(ex); }
	}

	/**
	 * Does the work, throwing any exceptions.
	 */
	public static WikiPlaceDetail placeUncached(long placeId) throws IOException
	{
		String urlStr =
			"http://api.wikimapia.org/?function=object" +
				"&id=" + placeId +
				"&language=en" +	// otherwise we get data for other languages, it seems
				"&format=json" +
				"&key=" + API_KEY;

		if (log.isDebugEnabled())
			log.debug("Querying wikimapia: " + urlStr);

		URL url = new URL(urlStr);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonResult = mapper.readValue(url.openStream(), JsonNode.class);

		// This seems the best way to test for error.
		if (jsonResult.get("debug") != null)
		{
			log.error("Error result from " + urlStr + " ::::: " + jsonResult);
			throw new IOException(jsonResult.toString());
		}

		return new WikiPlaceDetail(jsonResult);
	}

	/**
	 * Uses the box API call to fetch a relevant set of places.  Logs but does
	 * not expose errors.  This version uses a bounding rectangle.
	 *
	 * @param count is a limit; you might get less than this
	 * @return an unsorted list, or an empty list if something went wrong.
	 */
	public static List<WikiPlace> box(GeoPt sw, GeoPt ne, int count)
	{
		GeoDelta delta = new GeoDelta(ne.getLatitude() - sw.getLatitude(), ne.getLongitude() - sw.getLongitude());
		GeoPt center = new GeoPt(sw.getLatitude() + delta.getLatitude() / 2, sw.getLongitude() + delta.getLongitude() / 2);

		return box(center, delta, count);
	}

	/**
	 * Uses the box API call to fetch a relevant set of places.  Logs but does
	 * not expose errors.  This version uses a centerpoint and delta.
	 *
	 * @param count is a limit; you might get less than this
	 * @return an unsorted list, or an empty list if something went wrong.
	 */
	public static List<WikiPlace> box(GeoPt center, GeoDelta delta, int count)
	{
		delta = boundDelta(delta);

		float lat_min = center.getLatitude() - (delta.getLatitude() / 2);
		float lat_max = center.getLatitude() + (delta.getLatitude() / 2);
		float lon_min = center.getLongitude() - (delta.getLongitude() / 2);
		float lon_max = center.getLongitude() + (delta.getLongitude() / 2);

		String urlStr =
			"http://api.wikimapia.org/?function=box" +
				"&lat_min=" + lat_min +
				"&lat_max=" + lat_max +
				"&lon_min=" + lon_min +
				"&lon_max=" + lon_max +
				"&count=" + count +
				"&language=en" +	// otherwise we get data for other languages, it seems
				//"&category=category" +
				"&format=json" +
				"&key=" + API_KEY;

		try
		{
			return boxFetch(urlStr);
		}
		catch (RuntimeException e) { throw e; }
		catch (Exception e) { throw new WikimapiaException(e); }
	}

	/**
	 * Does the work, throwing any exceptions.  Results are unsorted!
	 */
	public static List<WikiPlace> boxFetch(String urlStr) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("Querying wikimapia: " + urlStr);

		URL url = new URL(urlStr);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonResult = mapper.readValue(url.openStream(), JsonNode.class);
		JsonNode folder = jsonResult.get("folder");

		if (folder == null)
			throw new WikimapiaException(jsonResult.toString());

		// Now we convert every item into a placemark
		List<WikiPlace> result = new ArrayList<WikiPlace>(folder.size());
		for (int placeIndex=0; placeIndex<folder.size(); placeIndex++)
		{
			JsonNode placeNode = folder.get(placeIndex);

			try
			{
				WikiPlace place = new WikiPlace(placeNode, true);
				result.add(place);
			}
			catch (WikimapiaException ex)
			{
				// Just ignore this place, the data is bogus.
				log.debug("Bad wikimapia node: {} -- {}", placeNode, ex.getMessage());
			}
		}

		if (log.isDebugEnabled())
			log.debug("Found: " + WikiPlace.namesOf(result));

		return result;
	}

	/**
	 * Wikimapia has min and max bounding boxes of:
	 * (lon_max - lon_min) * 10000000 must be greater than 12100 (thus 0.00121)
	 * |(lon_max - lon_min) * (lat_max - lat_min)| * 10000000 must be less than 14641000000 (thus 1464.1)
	 */
	public static GeoDelta boundDelta(GeoDelta delta)
	{
		//final float minDelta = 0.00121f;	// still has problems
		final float minDelta = 0.00122f;
		final float maxCombo = 1464.1f;
		final float sqrtMaxCombo = 38.26355968803739f;

		if (delta.getLatitude() < minDelta || delta.getLongitude() < minDelta)
			return new GeoDelta(minDelta, minDelta);

		float combo = delta.getLatitude() * delta.getLongitude() * 10000000;
		if (combo > maxCombo)
		{
			float lat = delta.getLatitude();
			float lng = delta.getLongitude();

			if (lat > sqrtMaxCombo)
				lat = sqrtMaxCombo;

			if (lng > sqrtMaxCombo)
				lng = sqrtMaxCombo;

			return new GeoDelta(lat, lng);
		}

		return delta;
	}

}