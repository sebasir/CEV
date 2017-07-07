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
    var tooltipText;
    if (typeof params.tooltip === "string") {
        tooltipText = '<h3>' + params.name + '</h3>';
    } else {
        tooltipText = '<div>';
        for (var s in params.tooltip) {
            tooltipText = tooltipText + '<h3>' + params.tooltip[s].scientificName + '</h3>';
            tooltipText = tooltipText + '<br/>';
            tooltipText = tooltipText + '<p>' + params.tooltip[s].commonName + '</p>';
            tooltipText = tooltipText + '<hr />';
        }
        tooltipText = tooltipText + '</div>';
    }

    var infoWindow = new google.maps.InfoWindow({
        content: tooltipText
    });

    if (isNumeric(paramLat) && isNumeric(paramLon)) {
        var latitude = parseFloat(paramLat), longitude = parseFloat(paramLon);
        var theZoom = parseInt(zoomVal);
        if (latitude != 0 || longitude != 0) {
            setMapLocation({lat: latitude, lng: longitude}, theZoom, paramName, infoWindow);
        } else {
            locateName(new google.maps.Geocoder(), paramName, infoWindow, setMapLocation);
        }
    }
}

function setMapLocation(location, zoom, name, infoWindow) {
    var mapOptions = {
        center: location,
        zoom: zoom
    };
    var mapCanvas = document.getElementById('map');
    var marker = new google.maps.Marker({
        position: location,
        icon: '../images/utils/bee_marker.png',
        title: name
    });
    map = new google.maps.Map(mapCanvas, mapOptions);
    marker.setMap(map);
    marker.addListener('click', function () {
        infoWindow.open(map, marker);
    });
}

function isNumeric(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

locateName = function (geocoder, locationName, infoWindow, f) {
    geocoder.geocode({'address': locationName}, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK)
            f(results[0].geometry.location, defaultZoom, locationName, infoWindow);
        else
            f(defaultCenter, defaultZoom, locationName, infoWindow);
    });
}