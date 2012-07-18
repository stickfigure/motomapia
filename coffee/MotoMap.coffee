
define ['app/polylines'], (polylines) ->
	#
	# Set the little ajax busy loading indicator visible or not
	#
	showBusy = (x) ->
		if x then $('#busy').show() else $('#busy').hide()
	showError = (x) ->
		if x then $('#error').show() else $('#error').hide()
		
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
	# Tracks mouse movement in a variable
	#
	mouseX = null
	mouseY = null
	document.onmousemove = (e) ->
		mouseX = e.clientX
		mouseY = e.clientY
	
	#
	# The bulk of our code is this class
	#
	return class MotoMap
		roadmapPolygonOpts:
			strokeWeight: 1
			strokeColor: '#000000'
			fillColor: '#000000'
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
				#mapTypeControlOptions:
				#	position: google.maps.ControlPosition.TOP_LEFT
			
			@currentPolygonOpts = @roadmapPolygonOpts
			
			@map = new google.maps.Map(document.getElementById(domId), opts)
			@markers = {}
			@placeName = $('#placeName')
			
			google.maps.event.addListener @map, 'idle', @onIdle
			google.maps.event.addListener @map, 'maptypeid_changed', @onMapTypeChange
			
			$(document).ajaxError ->
				showBusy(off)
				showError(on)
		
		# After bounds are done changing, redraw all the wikimapia places	
		# Note:  needs fat arrow because this is used as a callback
		onIdle: =>
			showError(off)
			showBusy(on)
			bounds = @map.getBounds()
			sw = bounds.getSouthWest()
			ne = bounds.getNorthEast()
			$.get '/places', { swLat: sw.lat(), swLng: sw.lng(), neLat: ne.lat(), neLng: ne.lng() }, (data) =>
				marker.setMap(null) for id, marker of @markers
				@markers = {}
				@createMarker(placemark) for placemark in data
				showBusy(off)
				
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
			poly.setPath(polylines.decode(placemark.polygon))
			poly.setMap(@map)
			@markers[placemark.id] = poly
			
			google.maps.event.addListener poly, 'mouseover', =>
				poly.setOptions(@hoverPolygonOpts)
				@placeName.text(placemark.name)
				@placeName.show()
				
			google.maps.event.addListener poly, 'mousemove', =>
				@placeName.css('left', mouseX)
				@placeName.css('top', mouseY)
	
			google.maps.event.addListener poly, 'mouseout', =>
				poly.setOptions(@currentPolygonOpts)
				@placeName.hide()
