/*
 */

package com.motomapia;

import static com.motomapia.OfyService.ofy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.motomapia.Ofy.VoidWork;
import com.motomapia.entity.Place;
import com.motomapia.wikimapia.WikiPlace;

/**
 * Synchronizes one place
 * 
 * @author Jeff Schnitzer
 */
@RequiredArgsConstructor
@Slf4j
public class SyncPlaceTask implements DeferredTask
{
	private static final long serialVersionUID = 1L;
	
	final WikiPlace wikiPlace;
	
	@Override
	public void run() {
		
		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				log.debug("Syncing " + wikiPlace);
				
				Place place = ofy().load().type(Place.class).id(wikiPlace.getId()).get();

				if (place == null) {
					ofy().save().entity(new Place(wikiPlace));
				} else {
					if (place.updateFrom(wikiPlace)) {
						if (log.isInfoEnabled())
							log.info("Updating " + place);
						
						ofy().save().entity(place);
					}
				}
			}
		});
	}
}