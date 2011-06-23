
#
# See Polyline.java for documentation of this utility method.  Note this is embedded javascript.
#
decodePolyline = `function(encoded) {	
	var len = encoded.length;
	var index = 0;
	var array = [];
	var lat = 0;
	var lng = 0;

	while (index < len) {
		var b;
		var shift = 0;
		var result = 0;
		do {
		  b = encoded.charCodeAt(index++) - 63;
		  result |= (b & 0x1f) << shift;
		  shift += 5;
		} while (b >= 0x20);
		var dlat = ((result & 1) ? ~(result >> 1) : (result >> 1));
		lat += dlat;

		shift = 0;
		result = 0;
		do {
		  b = encoded.charCodeAt(index++) - 63;
		  result |= (b & 0x1f) << shift;
		  shift += 5;
		} while (b >= 0x20);
		var dlng = ((result & 1) ? ~(result >> 1) : (result >> 1));
		lng += dlng;

		array.push(new google.maps.LatLng(lat * 1e-5, lng * 1e-5));
	}

	return array;
}`

#
# Set the little ajax busy loading indicator visible or not
#
busy = (x) ->
	$('#busy').css('visibility', if x then 'visible' else 'hidden') 

#
# The bulk of our code is this class
#
class MotoMap
	#
	constructor: (domId) ->
		opts =
			zoom: 8
			center: new google.maps.LatLng(37, -122)
			mapTypeId: google.maps.MapTypeId.ROADMAP
		
		@map = new google.maps.Map(document.getElementById(domId), opts)
		@markers = {}
		
		google.maps.event.addListener @map, 'idle', @onIdle
	
	# After bounds are done changing, redraw all the wikimapia places	
	#
	onIdle: =>
		busy(on)
		bounds = @map.getBounds()
		sw = bounds.getSouthWest()
		ne = bounds.getNorthEast()
		$.get '/api/places', { swLat: sw.lat(), swLng: sw.lng(), neLat: ne.lat(), neLng: ne.lng() }, (data) =>
			marker.setMap(null) for id, marker of @markers
			@markers = {}
			@createMarker(placemark) for placemark in data
			busy(off)

	# Add a marker to our map
	#
	createMarker: (placemark) ->
		poly = new google.maps.Polygon
			paths: decodePolyline(placemark.polygon)
			strokeWeight: 1
			
		poly.setMap(@map)
		@markers[placemark.id] = poly

#
# Initialization
#
$ ->
	new MotoMap("map")
