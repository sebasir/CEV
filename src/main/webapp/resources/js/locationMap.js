var map;

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
        center: {lat: 4.583333, lng: -74.066667},
        zoom: 5
    };
    map = new google.maps.Map(mapCanvas, mapOptions);
}