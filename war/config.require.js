require.config({
	baseUrl: '/js',
	paths: {
		persona: 'https://browserid.org/include'
		
		//jquery: 'https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min',
		
		// This makes no sense; it adds .js to the end and causes google to reject the request.
		// The explanation here makes even less sense: https://github.com/jrburke/requirejs/issues/163
		//gmaps: 'http://maps.google.com/maps/api/js?sensor=false',
		
		//jqueryui: 'http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.19/jquery-ui.min',
		//facebook: 'http://connect.facebook.net/en_US/all',
		//persona: 'http://browserid.org/include'
	}
});

// This a workaround for requirejs cracksmokery.
//requireDependency = {};
//requireDependency.gmaps = 'http://maps.google.com/maps/api/js?sensor=false';