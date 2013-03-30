/*
 */

package com.motomapia.action;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.GeoPt;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.motomapia.Placemark;
import com.motomapia.Queues;
import com.motomapia.SyncPlaceTask;
import com.motomapia.wikimapia.WikiPlace;
import com.motomapia.wikimapia.Wikimapia;

/**
 * For getting information about places to render on a map
 *
 * @author Jeff Schnitzer
 */
@Path("/places")
public class Places
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(Places.class);

	/** How many (max) places to fetch in a single call */
	public static final int PLACES_TO_FETCH = 100;

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
	 * Ensures that we have correct Place objects in our database.  Just enqueues a bunch of sync tasks so we
	 * don't have to slow down the UX.
	 */
	private void syncPlaces(List<WikiPlace> wikiPlaces)
	{
		Queues.sync().add(Lists.transform(wikiPlaces, new Function<WikiPlace, SyncPlaceTask>() {
			@Override
			public SyncPlaceTask apply(WikiPlace wikiPlace) {
				return new SyncPlaceTask(wikiPlace);
			}
		}));
	}

}