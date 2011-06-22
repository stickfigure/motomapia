(function() {
  var map, onBoundsChange;
  map = null;
  onBoundsChange = function() {
    return $('#blah').text("center: " + (map.getCenter()));
  };
  $(function() {
    var opts;
    opts = {
      zoom: 8,
      center: new google.maps.LatLng(37, -122),
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("map"), opts);
    return google.maps.event.addListener(map, 'bounds_changed', onBoundsChange);
  });
}).call(this);
