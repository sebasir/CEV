$(function() {
    var collection = $("#collection");
    var sliderContainer = $("#slider_feature");
    var sliderLeft = $("#slider_left");
    var sliderRight = $("#slider_right");
    var prevButton = $("#slide_prev");
    var nextButton = $("#slide_next");
    var specimenCollection, slideWidth, darkSlideWidth, specimenSize;

    $.getJSON('specimenDetail.xhtml', function(data) {
	   specimenCollection = data.collection;
	   slideWidth = data.settings.slide_width;
	   darkSlideWidth = data.settings.darkslide_width;
	   specimenSize = data.settings.specimenSize;
	   specimenCollection = fisherYates(specimenCollection);
	   populateCollection();
	   populateSlides();
    });

    var allSpecs = [];
    var specCount = 1;
    function fisherYates(myArray) {
	   var i = myArray.length;
	   if (i == 0)
		  return false;
	   while (--i) {
		  var j = Math.floor(Math.random() * (i + 1));
		  var tempi = myArray[i];
		  var tempj = myArray[j];
		  myArray[i] = tempj;
		  myArray[j] = tempi;
	   }
	   return myArray;
    }

    function populateCollection() {
	   $.each(specimenCollection, function(j, spec) {
		  j++;
		  var item = renderCapsule(j, spec);
		  item.appendTo(collection);
		  allSpecs.push(spec);
	   });
    }

    function renderCapsule(j, spec) {
	   var image = spec.thumb;
	   var title = spec.cname;
	   var item = $("<div class='item' />");
	   item.attr("data-num", specCount);
	   specCount++;
	   item.append("<a href='#' title='" + title + "'><img src='images/thumbs/" + spec.id + ".jpg' height='80px' width='160px' alt='" + title + "' title='" + title + "' /></a>");
	   return item;
    }

    function populateSlides() {
	   var item2right;
	   $.each(allSpecs, function(k, spec) {
		  var item = renderSliderItem(k, spec);
		  var itemleft = renderDarkSliderItem(k, spec);
		  var itemright = renderDarkSliderItem(k, spec);
		  sliderContainer.append(item);
		  sliderLeft.append(itemleft);
		  sliderRight.append(itemright);
		  if (k == 0) {
			 item2right = renderDarkSliderItem(k, spec);
		  }
		  if (k == allSpecs.length - 1) {
			 var item2 = renderSliderItem(k, spec);
			 var item2left = renderDarkSliderItem(k, spec);
			 sliderContainer.prepend(item2);
			 sliderLeft.prepend(item2left);
			 sliderRight.append(item2right);
		  }
	   });

	   $(".deal_nav .item[data-num=1]").addClass("active");
    }

    function renderSliderItem(k, spec) {
	   k++;
	   var item = $("<div class='item' />");
	   item.attr("data-num", k);
	   item.append("<a href='#' title='" + spec.cname + "'><img src='images/thumbs/" + spec.id + ".jpg' alt='" + spec.cname + "' title='" + spec.cname + "' /></a>");
	   return item;
    }

    function renderDarkSliderItem(k, spec) {
	   k++;
	   var item = $("<div class='item' />");
	   item.attr("data-num", k);
	   item.append("<img src='images/thumbs/" + spec.id + ".jpg' alt='" + spec.cname + "' title='" + spec.cname + "' />");
	   return item;
    }

    $(".deal_nav .item").hoverIntent({
	   sensativity: 7,
	   interval: 400,
	   over: function() {
		  var dataNum = $(this).attr("data-num");
		  setActiveThumbnail(dataNum);
		  slideTo(dataNum);
	   },
	   out: function() {
	   },
	   timeout: 0
    });

    prevButton.click(function() {
	   slideLeft();
    });

    nextButton.click(function() {
	   slideRight();
    });

    sliderLeft.click(function() {
	   slideLeft();
    });

    sliderRight.click(function() {
	   slideRight();
    });

    function setActiveThumbnail(dataNum) {
	   $(".deal_nav .item").removeClass("active");
	   $(".deal_nav .item[data-num=" + dataNum + "]").addClass("active");
    }

    function slideLeft() {
	   if (sliderContainer.is(":animated")) {
		  return false;
	   }
	   var activeSlide = getActiveSlide();
	   if (activeSlide == 1) {
		  slideOffsetLeft();
	   } else {
		  slideTo(parseInt(activeSlide) - 1);
	   }
    }

    function slideRight() {
	   if (sliderContainer.is(":animated")) {
		  return false;
	   }
	   var activeSlide = getActiveSlide();
	   if (activeSlide == specimenSize) {
		  slideOffsetRight();
	   } else {
		  slideTo(parseInt(activeSlide) + 1);
	   }
    }

    function getActiveSlide() {
	   var activeSlide = $(".deal_nav .item.active").attr("data-num");
	   return activeSlide;
    }

    function slideTo(slideNum) {
	   var offset = parseInt(slideNum) * slideWidth;
	   sliderContainer.stop().animate({"left": "-" + offset + "px"}, 500);
	   var leftOffset = (parseInt(slideNum) * darkSlideWidth) - darkSlideWidth;
	   sliderLeft.stop().animate({"left": "-" + leftOffset + "px"}, 500);
	   var rightOffset = (parseInt(slideNum) * darkSlideWidth);
	   sliderRight.stop().animate({"left": "-" + rightOffset + "px"}, 500);
	   setActiveThumbnail(slideNum);
    }

    function slideOffsetLeft() {
	   var sliderLeftInitial = darkSlideWidth * allSpecs.length;
	   sliderLeft.css("left", "-" + sliderLeftInitial + "px");
	   var leftOffset = darkSlideWidth * 11;
	   sliderLeft.stop().animate({"left": "-" + leftOffset + "px"}, 500);
	   sliderRight.stop().animate({"left": "0px"}, 500, function() {
		  var sliderRightEnd = darkSlideWidth * allSpecs.length;
		  sliderRight.css({"left": "-" + sliderRightEnd + "px"});
	   });
	   sliderContainer.stop().animate({"left": "0px"}, 500, function() {
		  var sliderEnd = slideWidth * allSpecs.length;
		  sliderContainer.css({"left": "-" + sliderEnd + "px"});
	   });
	   setActiveThumbnail(allSpecs.length);
    }

    function slideOffsetRight() {
	   sliderRight.css("left", "0");
	   sliderRight.animate({"left": "-" + darkSlideWidth + "px"}, 500);
	   var leftOffset = allSpecs.length * darkSlideWidth;
	   sliderLeft.stop().animate({"left": "-" + leftOffset + "px"}, 500, function() {
		  sliderLeft.css({"left": "0"});
	   });
	   sliderContainer.stop().css({"left": "0px"}).animate({"left": "-" + slideWidth + "px"}, 500);
	   setActiveThumbnail(1);
    }
});