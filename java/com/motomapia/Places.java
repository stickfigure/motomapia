/*
 */

package com.motomapia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.GeoPt;
import com.motomapia.entity.Place;
import com.motomapia.wikimapia.WikiPlace;
import com.motomapia.wikimapia.Wikimapia;

/**
 * For getting information about places to render on a map
 * 
 * @author Jeff Schnitzer
 */
@Path("/api/places")
public class Places
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(Places.class);
	
	/** How many (max) places to fetch in a single call */
	public static final int PLACES_TO_FETCH = 100;
	
	/** */
	@Inject Ofy ofy;
	
	/**
	 * This method fetches places from Wikimapia, syncs them into our datastore, and returns placemarks to the client.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Placemark> getPlaces(
			@QueryParam("swLat") float swLat,
			@QueryParam("swLng") float swLng,
			@QueryParam("neLat") float neLat,
			@QueryParam("neLng") float neLng)
	{
		if (log.isDebugEnabled())
			log.debug("getPlaces({},{} -- {},{})", new Object[] { swLat, swLng, neLat, neLng });
		
		List<WikiPlace> wikis = Wikimapia.box(new GeoPt(swLat, swLng), new GeoPt(neLat, neLng), PLACES_TO_FETCH);
		
		this.syncPlaces(wikis);

		List<Placemark> marks = new ArrayList<Placemark>(wikis.size());

		for (WikiPlace wiki: wikis)
			marks.add(new Placemark(wiki.getId(), wiki.getName(), wiki.getCenter(), wiki.getPolyline(), wiki.getPolygon().length, wiki.getArea()));
		
		return marks;
	}

	/**
	 * Ensures that we have correct Place objects in our database.
	 */
	private void syncPlaces(List<WikiPlace> wikiPlaces)
	{
		// First get all the relevant Place entities
		List<Long> placeIds = new ArrayList<Long>(wikiPlaces.size());
		for (WikiPlace wikiPlace: wikiPlaces)
			placeIds.add(wikiPlace.getId());
		
		Map<Long, Place> places = ofy.load().type(Place.class).ids(placeIds);

		// Now we can process all the wiki places, looking for any changes or corrections
		List<Place> needUpdating = new ArrayList<Place>();

		for (WikiPlace wikiPlace: wikiPlaces)
		{
			Place place = places.get(wikiPlace.getId());
			
			if (place == null)
			{
				needUpdating.add(new Place(wikiPlace));
			}
			else
			{
				// Check the Place object, make sure it is correct
				if (place.updateFrom(wikiPlace))
					needUpdating.add(place);
			}
		}

		// Save any new places or places that have changed
		if (!needUpdating.isEmpty())
		{
			if (log.isInfoEnabled())
				log.info("Updating: " + needUpdating);
			
			ofy.save().entities(needUpdating);	// async
		}
	}

}