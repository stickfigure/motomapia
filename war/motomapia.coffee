
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
# Start a download
# bounds is a LatLngBounds
#
download = (bounds) -> 
	sw = bounds.getSouthWest()
	ne = bounds.getNorthEast()
	
	url = '/download/poi.csv?swLat=' + sw.lat() + '&swLng=' + sw.lng() + '&neLat=' + ne.lat() + '&neLng=' + ne.lng()
#	window.location.href = url
	iframe = document.getElementById('downloader');
	if iframe == null
		iframe = document.createElement('iframe');  
		iframe.id = 'downloader';
		iframe.style.display = 'none';
		document.body.appendChild(iframe);

	iframe.src = url;   

#
# The bulk of our code is this class
#
class MotoMap
	roadmapPolygonOpts:
		strokeWeight: 1
		strokeColor: '#646464'
		fillColor: '#646464'
		fillOpacity: 0.2
		
	satellitePolygonOpts:
		strokeWeight: 1
		strokeColor: '#ffffff'
		fillColor: '#ffffff'
		fillOpacity: 0.3
	
	hoverPolygonOpts:
		strokeWeight: 0.6
		fillColor: '#ffd700'
		fillOpacity: 0.5

	#
	constructor: (domId) ->
		opts =
			zoom: 8
			center: new google.maps.LatLng(37, -122)
			mapTypeId: google.maps.MapTypeId.ROADMAP
		
		@currentPolygonOpts = @roadmapPolygonOpts
		
		@map = new google.maps.Map(document.getElementById(domId), opts)
		@markers = {}
		
		google.maps.event.addListener @map, 'idle', @onIdle
		google.maps.event.addListener @map, 'maptypeid_changed', @onMapTypeChange
	
	# After bounds are done changing, redraw all the wikimapia places	
	# Note:  needs fat arrow because this is used as a callback
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
			
	# When map type changes we need to change color of polygons
	# Note:  needs fat arrow because this is used as a callback
	onMapTypeChange: =>
		switch @map.getMapTypeId()
			when google.maps.MapTypeId.ROADMAP, google.maps.MapTypeId.TERRAIN
				@currentPolygonOpts = @roadmapPolygonOpts
			else
				@currentPolygonOpts = @satellitePolygonOpts
		
		marker.setOptions(@currentPolygonOpts) for id, marker of @markers

	# Add a marker to our map
	#
	createMarker: (placemark) ->
		poly = new google.maps.Polygon(@currentPolygonOpts)
		poly.setPath(decodePolyline(placemark.polygon))
		poly.setMap(@map)
		@markers[placemark.id] = poly
		
		google.maps.event.addListener poly, 'mouseover', =>
			poly.setOptions(@hoverPolygonOpts)
			
		google.maps.event.addListener poly, 'mouseout', =>
			poly.setOptions(@currentPolygonOpts)
			

#
# Initialization
#
$ ->
	motoMap = new MotoMap("map")
	
	$('#download').click ->
		download(motoMap.map.getBounds())
