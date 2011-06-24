(function() {
  var MotoMap, decodePolyline, download, mouseX, mouseY, showBusy, showError;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };
  decodePolyline = function(encoded) {	
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
};
  showBusy = function(x) {
    if (x) {
      return $('#busy').show();
    } else {
      return $('#busy').hide();
    }
  };
  showError = function(x) {
    if (x) {
      return $('#error').show();
    } else {
      return $('#error').hide();
    }
  };
  download = function(bounds) {
    var iframe, ne, sw, url;
    sw = bounds.getSouthWest();
    ne = bounds.getNorthEast();
    url = '/download/poi.csv?swLat=' + sw.lat() + '&swLng=' + sw.lng() + '&neLat=' + ne.lat() + '&neLng=' + ne.lng();
    iframe = document.getElementById('downloader');
    if (iframe === null) {
      iframe = document.createElement('iframe');
      iframe.id = 'downloader';
      iframe.style.display = 'none';
      document.body.appendChild(iframe);
    }
    return iframe.src = url;
  };
  mouseX = null;
  mouseY = null;
  document.onmousemove = function(e) {
    mouseX = e.clientX;
    return mouseY = e.clientY;
  };
  MotoMap = (function() {
    MotoMap.prototype.roadmapPolygonOpts = {
      strokeWeight: 1,
      strokeColor: '#000000',
      fillColor: '#000000',
      fillOpacity: 0.2
    };
    MotoMap.prototype.satellitePolygonOpts = {
      strokeWeight: 1,
      strokeColor: '#ffffff',
      fillColor: '#ffffff',
      fillOpacity: 0.3
    };
    MotoMap.prototype.hoverPolygonOpts = {
      strokeWeight: 0.6,
      fillColor: '#ffd700',
      fillOpacity: 0.5
    };
    function MotoMap(domId) {
      this.onMapTypeChange = __bind(this.onMapTypeChange, this);
      this.onIdle = __bind(this.onIdle, this);      var opts;
      opts = {
        zoom: 8,
        center: new google.maps.LatLng(37, -122),
        mapTypeId: google.maps.MapTypeId.ROADMAP
      };
      this.currentPolygonOpts = this.roadmapPolygonOpts;
      this.map = new google.maps.Map(document.getElementById(domId), opts);
      this.markers = {};
      this.placeName = $('#placeName');
      google.maps.event.addListener(this.map, 'idle', this.onIdle);
      google.maps.event.addListener(this.map, 'maptypeid_changed', this.onMapTypeChange);
      $(document).ajaxError(function() {
        showBusy(false);
        return showError(true);
      });
    }
    MotoMap.prototype.onIdle = function() {
      var bounds, ne, sw;
      showError(false);
      showBusy(true);
      bounds = this.map.getBounds();
      sw = bounds.getSouthWest();
      ne = bounds.getNorthEast();
      return $.get('/api/places', {
        swLat: sw.lat(),
        swLng: sw.lng(),
        neLat: ne.lat(),
        neLng: ne.lng()
      }, __bind(function(data) {
        var id, marker, placemark, _i, _len, _ref;
        _ref = this.markers;
        for (id in _ref) {
          marker = _ref[id];
          marker.setMap(null);
        }
        this.markers = {};
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          placemark = data[_i];
          this.createMarker(placemark);
        }
        return showBusy(false);
      }, this));
    };
    MotoMap.prototype.onMapTypeChange = function() {
      var id, marker, _ref, _results;
      switch (this.map.getMapTypeId()) {
        case google.maps.MapTypeId.ROADMAP:
        case google.maps.MapTypeId.TERRAIN:
          this.currentPolygonOpts = this.roadmapPolygonOpts;
          break;
        default:
          this.currentPolygonOpts = this.satellitePolygonOpts;
      }
      _ref = this.markers;
      _results = [];
      for (id in _ref) {
        marker = _ref[id];
        _results.push(marker.setOptions(this.currentPolygonOpts));
      }
      return _results;
    };
    MotoMap.prototype.createMarker = function(placemark) {
      var poly;
      poly = new google.maps.Polygon(this.currentPolygonOpts);
      poly.setPath(decodePolyline(placemark.polygon));
      poly.setMap(this.map);
      this.markers[placemark.id] = poly;
      google.maps.event.addListener(poly, 'mouseover', __bind(function() {
        poly.setOptions(this.hoverPolygonOpts);
        this.placeName.text(placemark.name);
        return this.placeName.show();
      }, this));
      google.maps.event.addListener(poly, 'mousemove', __bind(function() {
        this.placeName.css('left', mouseX);
        return this.placeName.css('top', mouseY);
      }, this));
      return google.maps.event.addListener(poly, 'mouseout', __bind(function() {
        poly.setOptions(this.currentPolygonOpts);
        return this.placeName.hide();
      }, this));
    };
    return MotoMap;
  })();
  $(function() {
    var motoMap;
    motoMap = new MotoMap("map");
    $('#download').click(function() {
      return download(motoMap.map.getBounds());
    });
    return $('#instructionsLink').click(function() {
      return $('#instructions').dialog({
        width: 600
      });
    });
  });
}).call(this);
