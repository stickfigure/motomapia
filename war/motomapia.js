(function() {
  var MotoMap, decodePolyline;
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
  MotoMap = (function() {
    function MotoMap(domId) {
      this.onIdle = __bind(this.onIdle, this);      var opts;
      opts = {
        zoom: 8,
        center: new google.maps.LatLng(37, -122),
        mapTypeId: google.maps.MapTypeId.ROADMAP
      };
      this.map = new google.maps.Map(document.getElementById(domId), opts);
      this.markers = {};
      google.maps.event.addListener(this.map, 'idle', this.onIdle);
    }
    MotoMap.prototype.onIdle = function() {
      var bounds, ne, sw;
      bounds = this.map.getBounds();
      sw = bounds.getSouthWest();
      ne = bounds.getNorthEast();
      return $.get('/api/places', {
        swLat: sw.lat(),
        swLng: sw.lng(),
        neLat: ne.lat(),
        neLng: ne.lng()
      }, __bind(function(data) {
        var id, marker, placemark, _i, _len, _ref, _results;
        _ref = this.markers;
        for (id in _ref) {
          marker = _ref[id];
          marker.setMap(null);
        }
        this.markers = {};
        _results = [];
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          placemark = data[_i];
          _results.push(this.createMarker(placemark));
        }
        return _results;
      }, this));
    };
    MotoMap.prototype.createMarker = function(placemark) {
      var poly;
      poly = new google.maps.Polygon({
        paths: decodePolyline(placemark.polygon),
        strokeWeight: 1
      });
      poly.setMap(this.map);
      return this.markers[placemark.id] = poly;
    };
    return MotoMap;
  })();
  $(function() {
    return new MotoMap("map");
  });
}).call(this);
