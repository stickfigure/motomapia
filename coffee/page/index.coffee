
require ['app/MotoMap'], (MotoMap) ->
	$ ->
		motoMap = new MotoMap("map")
		
		$('#download').click ->
			download(motoMap.map.getBounds())
			
		$('#instructionsLink').click ->
			$('#instructions').dialog({ width: 600 })
