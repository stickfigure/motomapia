
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