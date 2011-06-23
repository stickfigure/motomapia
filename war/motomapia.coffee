
# make this global to our script, it will be set during initialization
map = null

# Fired whenever bounds change
onBoundsChange = ->
	$('#blah').text("center: #{map.getCenter()}")

# Initialization
$ ->
	opts =
		zoom: 8
		center: new google.maps.LatLng(37, -122)
		mapTypeId: google.maps.MapTypeId.ROADMAP
	
	map = new google.maps.Map(document.getElementById("map"), opts)
	
	google.maps.event.addListener map, 'bounds_changed', onBoundsChange
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Spits out a format useful to javascript.
	 * This code is copied from http://facstaff.unca.edu/mcmcclur/GoogleMaps/EncodePolyline/decode.js 
	 */
	public static native JsArray<LatLng> decode(String encoded)
	/*-{
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
	
			array.push(new $wnd.google.maps.LatLng(lat * 1e-5, lng * 1e-5));
		}

		return array;
	}-*/;
	