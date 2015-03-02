<!DOCTYPE html>

<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
		<meta name="description" content="Motomapia lets you download Wikimapia data as Points Of Interest that you can load into your GPS." />
		<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
		
		<title>Motomapia - download Wikimapia to your GPS</title>

		<link rel="stylesheet" type="text/css" href="/bootstrap/css/bootstrap.css" />
		
		<!-- These need to go together to prevent FOUC. In a production app, less would be compiled statically. -->
		<link rel="stylesheet/less" type="text/css" href="/css/motomapia.less" />
		<script type="text/javascript" charset="utf8" src="/js/less-1.3.0.min.js"></script>
	</head>

	<body>
		<div class="navbar navbar-fixed-top">
			<div class="navbar-inner">
				<div class="container">
					<a class="brand" href="#"><span class="moto">Moto</span><span class="mapia">mapia</span></a>

					<span class="navbar-text pull-left subtitle">download <a href="http://www.wikimapia.org/" target="_blank">Wikimapia</a> to your GPS</span>

					<ul class="nav pull-left">
						<li class="divider-vertical"></li>
						<li><a data-toggle="modal" href="#instructions">Instructions</a></li>
						<li><a href="https://github.com/stickfigure/motomapia" target="_blank">Code</a></li>
						<li><button id="download" class="btn btn-primary">Download POIs</button></li>
						<li class="divider-vertical"></li>
						<li>
							<img id="busy" src="/loading.gif" alt="loading"/>
							<img id="error" src="/error.png" alt="error" title="An error occurred communicating with the server. Move the map to restart."/>
						</li>
					</ul>
					<ul class="nav pull-right">
						<li id="loginArea">
							<button id="loginButton" class="btn">Login</button>
							<button id="logoutButton" class="btn">Logout</button>
						</li>
					</ul>
					
					<span id="identity" class="navbar-text pull-right"></span>
				</div>
			</div>
		</div>
		
		<div id="map"></div>
		
		<div id="placeName"></div>
		
		<div id="instructions" class="modal hide">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">�</button>
				<h3>Instructions</h3>
			</div>
			<div class="modal-body">
				<p>
					Motmapia lets you download
					place data from <a href="http://www.wikimapia.org">Wikimapia</a> in a format suitable for most GPSes.
				</p>
				
				<h3>How do I use Motomapia?</h3>
				<ol>
					<li>Browse the map, zooming in to view relevant places in the part of the map that interests you.</li>
					<li>Zoom out and resize your browser to encompass the area for which you want to download.</li>
					<li>Click the "Download POIs" button to download all previously seen POIs in the visible area.</li>
				</ol>
				
				<h3>Does this mean I only download the currently visible places?</h3>
				No.  You download <b>all</b> known POIs in the map area shown in your browser.  This could be a <b>lot</b>.
				
				<h3>Can I just download POIs without browsing?</h3>
				Maybe.  As you scroll, Wikimapia places are synced to Motomapia's database.  When you download,
				you download only what is in Motomapia's database.  Without scrolling, your places might not be available.
				On the other hand, if someone syncs an area today, it will probably still be present (for everyone) tomorrow.
				
				<h3>How do I edit places?</h3>
				Visit <a href="http://www.wikimapia.org/">Wikimapia</a>
	
				<h3>How do I load the downloaded file onto my GPS?</h3>
				It depends on your model of GPS.  You're on your own here.
				
				<h3>Why is there a Login button?</h3>
				At the moment there is no benefit to logging in.  It only serves to demonstrate Persona login and
				some nifty transactional behavior behind the scenes.  Look at the code.

				<h3>Why doesn't Motomapia seem to work?</h3>
				This is running on the free tier of Google App Engine and frequently goes over one or more quota
				limitations. If you want this to work, deploy it yourself - Motomapia is really just a code demo.
			</div>
			<div class="modal-footer">
				<a href="#" class="btn btn-primary" data-dismiss="modal">Close</a>
			</div>
		</div>

		<!-- These are a huge pain to work with when trying to use r.js, so just do it the old-fashioned way -->
		<script type="text/javascript" charset="utf8" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
		<script type="text/javascript" charset="utf8" src="http://maps.google.com/maps/api/js?sensor=false"></script>
		<script type="text/javascript" charset="utf8" src="/bootstrap/js/bootstrap.min.js"></script>
		
		<script type="text/javascript" src="/js/require.js"></script>
		<script type="text/javascript">
			<%@ include file="config.require.js" %>
		</script>
		<script type="text/javascript">
			require(['app/page/index']);
		</script>
		
		<script type="text/javascript">
			var _gaq = _gaq || [];
			_gaq.push(['_setAccount', 'UA-24156120-1']);
			_gaq.push(['_trackPageview']);
		
			(function() {
				var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
				ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
				var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
			})();
		</script>
	</body>
</html>
