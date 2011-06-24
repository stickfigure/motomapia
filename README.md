# Motomapia - download Wikimapia to your GPS

Motomapia is a website that lets you download Wikimapia placemarks to your GPS as POI files.  It
is also an opensource project demonstrating a handful of web technologies.

You can visit this code in operation at http://www.motomapia.org/

## Technology

Motomapia is built on:

### Frontend
* [Google Maps V3](http://code.google.com/apis/maps/documentation/javascript/)
* [CoffeeScript](http://jashkenas.github.com/coffee-script/)
* [Sass](http://sass-lang.com/)  

### Backend

* [Google App Engine](http://code.google.com/appengine/) (for Java)
* [Objectify-Appengine](http://code.google.com/p/objectify-appengine/)
* [RESTeasy](http://www.jboss.org/resteasy) 
* [Wikimapia's public API](http://wikimapia.org/api/)
* [Java Geomodel](http://code.google.com/p/javageomodel/) (for geohashing)

## How Motomapia works

* When the user moves the map, the frontend makes a call to /api/places, passing the current lat/lng bounds of the viewport.
	* The backend makes a request to Wikimapia with this bound, getting back 100 places.
	* The backend ensures that all of these places are present in the local datastore.
		* The places will be fetched from memcache thanks to Objectify's @Cached feature.
		* Places are stored with an indexed list of geocells in which the centerpoint resides.
	* The backend returns the place data to the client as JSON.
		* Polygons are encoded as polylines; some places have hundreds of points and straight-up JSON impacts performance.
		
* When the user clicks download, the frotnend makes a call to /download/poi.csv, passing the current lat/lng bounds of the viewport.
	* The backend calculates a relevant set of geocells for the bounding rectangle.
	* The backend queries the datastore for all Places which are indexed for the relevant geocells.
	* The backend writes all places back to the client in POI CSV format.
	
## Issues

* Nothing stops users from requesting download of all POIs worldwide.  This might be expensive ($$) and might even
exceed the 30s limit for GAE client requests.

* There is no way to detect when Wikimapia places are deleted.  The simple solution (not implemented) is to expire data
in the datastore, although this creates expensive churn.  Otherwise Wikimapia needs to be pressed for an API to fetch deleted items.

## Development

You can build and run your own version of Motomapia.

### To edit the frontend

* Install Node.js, CoffeeScript, and Sass
* Run the scripts that automatically compile the .sass and .coffee files on change:
	* watch-coffee.sh
	* watch-sass.sh 

### To edit the backend (and deploy)

* Create a Google App Engine application at the GAE website
* Install Eclipse
* Install the Google Plugin for Eclipse
* Install the Google App Engine SDK
* Open the Motomapia project
* Edit war/WEB-INF/appengine-web.xml and replace *motomapia* with your application id
* Click the deploy button in Eclipse