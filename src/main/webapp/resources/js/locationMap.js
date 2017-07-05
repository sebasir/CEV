var map;
var geocoder;
var defaultZoom = 9;
var defaultCenter = {lat: 4.583333, lng: -74.066667};

$(window).ready(function () {
    var $tree = $('#formLocation\\:data_content');
    $tree.addClass('flexContainer');
    $tree.append('<div id="map" class="locationMap treeHeight"><p>aqui va el mapa</p></div>');
    $tree.find('.ui-tree-container').each(function () {
        $(this).addClass('treeHeight');
    });
    google.maps.event.addDomListener(window, 'load', initialize);
});

function initialize() {
    var mapCanvas = document.getElementById('map');
    var mapOptions = {
        center: defaultCenter,
        zoom: defaultZoom
    };
    map = new google.maps.Map(mapCanvas, mapOptions);
}

function setMapCenter(params) {
    var paramLat = params.latitude;
    var paramLon = params.longitude;
    var paramName = params.name;
    var zoomVal = params.zoom;
    if (isNumeric(paramLat) && isNumeric(paramLon)) {
        var latitude = parseFloat(paramLat), longitude = parseFloat(paramLon);
        var theZoom = parseInt(zoomVal);
        if (latitude != 0 || longitude != 0) {
            setMapLocation({lat: latitude, lng: longitude}, theZoom, paramName);
        } else {
            locateName(new google.maps.Geocoder(), paramName, setMapLocation);
        }
    }
}

function setMapLocation(location, zoom, name) {
    var mapOptions = {
        center: location,
        zoom: zoom
    };
    var mapCanvas = document.getElementById('map');
    var marker = new google.maps.Marker({
        position: location,
        title: name
    });
    map = new google.maps.Map(mapCanvas, mapOptions);
    marker.setMap(map);
}

function isNumeric(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

locateName = function (geocoder, locationName, f) {
    geocoder.geocode({'address': locationName}, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK)
            f(results[0].geometry.location, defaultZoom, locationName);
        else
            f(defaultCenter, defaultZoom, locationName);
    });
}