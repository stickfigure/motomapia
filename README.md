# Motomapia - download Wikimapia to your GPS

Motomapia is a website that lets you download Wikimapia placemarks to your GPS as POI files.  It
is an opensource project demonstrating a handful of web technologies.

You can visit this code in operation at http://www.motomapia.com/

## Technology

Motomapia is built on (and demonstrates the use of):

### Frontend
* [Google Maps V3](http://code.google.com/apis/maps/documentation/javascript/)
* [CoffeeScript](http://jashkenas.github.com/coffee-script/)
* [Less](http://lesscss.org/)
* [Twitter Bootstrap](http://twitter.github.com/bootstrap/)
* [RequireJS](http://requirejs.org/)

### Backend

* [Google App Engine](http://code.google.com/appengine/) (for Java)
* [Objectify-Appengine](http://code.google.com/p/objectify-appengine/)
* [Guice](http://code.google.com/p/google-guice/)
* [Jersey](http://jersey.java.net/)
* [Wikimapia's public API](http://wikimapia.org/api/)
* [Java Geomodel](http://code.google.com/p/javageomodel/) (for geohashing)

### Check it out

Some things you may want to look closer at:

* Using Mozilla Persona for authentication
* Building and optimizing javascript with RequireJS' r.js
* Using a Guice interceptor to provide EJB-like transactions with Objectify4

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
exceed the 60s limit for GAE client requests.

* There is no way to detect when Wikimapia places are deleted.  The simple solution (not implemented) is to expire data
in the datastore, although this creates expensive churn.  Otherwise Wikimapia needs to be pressed for an API to fetch deleted items.

## Development

You can build and run your own version of Motomapia.

### To edit the frontend

* Install Node.js, CoffeeScript, and Less
* Run watch-coffee.sh to automatically compile the .coffee files on change

### To edit the backend (and deploy)

* Create a Google App Engine application at the GAE website
* Install Eclipse
* Install [Project Lombok](http://projectlombok.org/) into Eclipse (run "java -jar build-only/lombok-edge.jar")
* Install the Google Plugin for Eclipse
* Install the Google App Engine SDK
* Open the Motomapia project
* Edit war/WEB-INF/appengine-web.xml and replace *motomapia* with your application id
* Click the deploy button in Eclipse

## Help

The most likely place to ask for help is the [Objectify-Appengine group](http://groups.google.com/group/objectify-appengine).