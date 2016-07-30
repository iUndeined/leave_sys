/**
 * name:          jquery-foxibox-0.2.js
 * author:        Stefan Benicke - www.opusonline.at
 * version:       0.2
 * last update:   30.09.2009
 * category:      jQuery plugin
 * copyright:     (c) 2009 Stefan Benicke (www.opusonline.at)
 * license:       GNU GPLv3
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * documentation: http://www.opusonline.at/foxitools/foxibox/
 */
 (function($) {
  $.fn.foxibox = function(settings) {

    settings = jQuery.extend({
      speed: 'normal',
      overlayOpacity: 0.5,
      title: true,
      scale: true,
      border: 10,
      textImage: '',
      textOf: '/',
			callback: function(){}
		}, settings);

    var window_width;
    var window_height;
    var document_width;
    var document_height;
    var scroll_top;
    var scroll_left;
    var open_gallery = false;
		var images_array = [];
    var array_position;
    var set_array;
    var set_position;
    var set_count;
    var is_set;
    var scaled;
    var rescaled;
    var container_width;
    var container_height;
    var container_top;
    var container_left;
    var loader_top;
    var loader_left;
    var scaled_top;
    var scaled_left;
    var scaled_loader_top;
    var scaled_loader_left;
    var old_overlay_width;
    var old_overlay_height;
	
    var foxibox_objects = '<div id="foxibox_overlay"></div><div id="foxibox_loader"></div><div id="foxibox_container"><img id="foxibox_image" src="" /></div><div id="foxibox_details"><div id="foxibox_title"></div><a href="#" id="close"></a><a href="#" id="scale"></a><div id="foxibox_nav"><a href="#" id="next"></a><a href="#" id="prev"></a><div></div></div></div>';

    $('body').append(foxibox_objects);
    $('#foxibox_overlay, #foxibox_loader, #foxibox_container, #foxibox_details').hide();

    getSize();
    getScroll();
		$(window).scroll(function(){ getScroll(); });
    $(window).resize(function(){
      if(open_gallery){
        $('#foxibox_overlay').hide();
        getSize();
        scaled = false;
        rescaled = false;
        $('#foxibox_overlay').css({'width':document_width, 'height':document_height}).show();
        $('#foxibox_image, #foxibox_title, #foxibox_details, #foxibox_details #scale').hide();
        loaderPosition();
        $('#foxibox_loader').show();
        if(is_set) showImage($(set_array[(set_position-1)]));
        else showImage($(images_array[(array_position)]));
      }
    });

		$(document).keydown(function(event){
      switch(event.keyCode){
				// left arrow and 'p'
        case 37: case 80:
          if(open_gallery){
            if(set_position > 1) changeImage('previous');
            return false;
          }
					break;
				// right arrow and 'n'
        case 39: case 78:
					if(open_gallery){
            if(set_position < set_count) changeImage('next');
            return false;
          }
					break;
        // 'esc' and 'c'
				case 27: case 67:
					if(open_gallery){
            close();
            return false;
          }
					break;
			};
    });

		$(this).each(function(){
			images_array[images_array.length] = this;
      $(this).bind('click', function(){
				open(this);
				return false;
			});
		});
	
		function open(element){
      open_gallery = true;
      scaled = false;
      rescaled = false;
      
      $('#foxibox_details, #foxibox_container, #foxibox_loader, #foxibox_overlay').remove();

			// find out if the picture is part of a set
      array_position = 0;
   		set_array = [];
      set_position = 0;
      set_count = 0;
      is_set = false;
      
      var element_rel = $(element).attr('rel');
      var element_href = $(element).attr('href');

			var gallery_reg_exp = /\[(?:.*)\]/;
			var gallery = gallery_reg_exp.exec(element_rel);
		
			for (var i = 0; i < images_array.length; i++){
        var this_rel = $(images_array[i]).attr('rel');
        var this_href = $(images_array[i]).attr('href');
				if(this_rel.indexOf(gallery) != -1 && this_rel == element_rel){
          set_count++;
					set_array[set_array.length] = images_array[i];
					if(this_href == element_href) {
            array_position = i;
            set_position = set_count;
          }
				}
        else if(this_href == element_href) array_position = i;
			}
      if(set_count > 1) is_set = true;
      
      // create objects and locate container and loader
      $('body').append(foxibox_objects);
      
      scaled_top = 0;
      scaled_left = 0;
      container_width = 100;
      container_height = 100;
      container_top = scroll_top+window_height/2-container_height/2-settings.border;
      container_left = scroll_left+window_width/2-container_width/2-settings.border;

      // fix ie6 bug with opacity
			if($.browser.msie && $.browser.version <= 6){}
      else $('#foxibox_overlay').css('opacity', settings.overlayOpacity);

      $('#foxibox_overlay').css({'width':document_width,
        'height':document_height}).hide().bind('click', function() { close(); });
      $('#foxibox_container').css({'top':container_top,
        'left':container_left,
        'width':container_width,
        'height':container_height,
        'padding':settings.border}).hide();
      $('#foxibox_details').css({'top':container_top,
        'left':container_left}).hide();
      loaderPosition();
      $('#foxibox_loader').css({'top':loader_top, 'left':loader_left}).hide();
      $('#foxibox_nav #prev').bind('click', function(){
        if(set_position > 1) changeImage('previous');
        return false;
      });
      $('#foxibox_nav #next').bind('click', function(){
        if(set_position < set_count) changeImage('next');
        return false;
      });
      $('#foxibox_details #close').bind('click', function(){
        close();
        return false;
      });
      $('#foxibox_details #scale').bind('click', function(){
        scaleImage();
        return false;
      }).hide();
      $('#foxibox_nav, #foxibox_title, #foxibox_image').hide();
      
      // show overlay and container
      $('#foxibox_overlay, #foxibox_container, #foxibox_loader').show();
      
      showImage(element);
    }
    
    function showImage(element){
      var element_href = $(element).attr('href');
      var element_title = $(element).attr('title');

      if(settings.title && element_title) $('#foxibox_title').text(element_title).show();
      if(is_set){
        $('#foxibox_nav div').text(settings.textImage+set_position+settings.textOf+set_count);
        $('#foxibox_nav').show();
      }

      // preload
      var preload_width = 0;
      var preload_height = 0;
      var preload_img = new Image();

      preload_img.onload = function(){
        preload_width = preload_img.width;
        preload_height = preload_img.height;
        
        // get the details height and the dimensions
        $('#foxibox_details').css('width', preload_width);
        var details_height = $('#foxibox_details').height();
        container_width = preload_width;
        container_height = preload_height + details_height;
      
        // scale to screen if the image is too large
        var prop;
        if(!scaled && settings.scale){
          // too wide
          if((container_width+settings.border*2) > window_width && !scaled){
            prop = preload_width/(window_width-settings.border*2);
            preload_width = Math.round(window_width-settings.border*2);
            preload_height = Math.round(preload_height/prop)-details_height;
            scaled = true;
          }
          // too high
          if((container_height+settings.border*2) > window_height){
            prop = preload_height/(window_height-settings.border*2-details_height);
            preload_height = Math.round(window_height-settings.border*2-details_height);
            preload_width = Math.round(preload_width/prop);
            scaled = true;
          }
          if(scaled){
            // correct details
            $('#foxibox_details').css('width', preload_width);
            details_height = $('#foxibox_details').height();
            container_height = preload_height + details_height;
            container_width = preload_width;
            $('#foxibox_details #scale').show();
          }
        }
        else scaled = false;
      
        container_top = scroll_top+window_height/2-container_height/2-settings.border;
        container_left = scroll_left+window_width/2-container_width/2-settings.border;
        if(container_top < scroll_top) container_top = scroll_top;
        if(container_left < scroll_left) container_left = scroll_left;

        if(scaled && !rescaled){
          scaled_top = container_top;
          scaled_left = container_left;
          scaled_loader_top = loader_top;
          scaled_loader_left = loader_left;
          old_overlay_width = document_width;
          old_overlay_height = document_height;
        }
        if(rescaled){
          container_top = scaled_top;
          container_left = scaled_left;
          loader_top = scaled_loader_top;
          loader_left = scaled_loader_left;
        }

        var details_top = container_top + preload_height + settings.border;
        var details_left = container_left + settings.border;

        // resize overlay
        getSize();
        var full_width = scroll_left+settings.border*2+container_width;
        var full_height = scroll_top+settings.border*2+container_height;
        if(full_width > document_width) $('#foxibox_overlay').css({'width':full_width});
        else $('#foxibox_overlay').css({'width':document_width});
        if(full_height > document_height) $('#foxibox_overlay').css({'height':full_height});
        else $('#foxibox_overlay').css({'height':document_height});
      
        // show image
        $('#foxibox_loader').animate({'top':loader_top, 'left':loader_left});
        $('#foxibox_container').animate({'width':container_width, 'height':container_height, 'left':container_left, 'top':container_top}, settings.speed, function(){
          $('#foxibox_image').width(preload_width).height(preload_height).attr('src', element_href);
          $('#foxibox_loader').hide();
          $('#foxibox_image').fadeIn(settings.speed);
          $('#foxibox_details').css({'top':details_top, 'left':details_left}).fadeIn(settings.speed);
          if(rescaled){
            $('#foxibox_overlay').css({'width':old_overlay_width, 'height':old_overlay_height});
            rescaled = false;
            getSize();
          }
        });
      }
      preload_img.src = element_href;

      // preload neighbour images if is_set
      if(is_set){
        if(set_position > 1){
          var prev_image = new Image();
          prev_image.src = $(set_array[(set_position-2)]).attr('href');
        }
        if(set_position < set_count){
          var next_image = new Image();
          next_image.src = $(set_array[(set_position)]).attr('href');
        }
      }
    }

    function changeImage(direction){
			if(direction == 'previous') set_position--;
      else set_position++;
      $('#foxibox_image, #foxibox_title, #foxibox_details, #foxibox_details #scale').hide();
      loaderPosition();
      $('#foxibox_loader').show();
      scaled = false;

      showImage($(set_array[(set_position-1)]));
    }
    
    function scaleImage(){
      if(!scaled) rescaled = true;
      $('#foxibox_image, #foxibox_title, #foxibox_details').hide();
      loaderPosition();
      $('#foxibox_loader').show();
      if(is_set) showImage($(set_array[(set_position-1)]));
      else showImage($(images_array[(array_position)]));
    }

    function close(){
      $('#foxibox_details, #foxibox_container').fadeOut(settings.speed);
      $('#foxibox_overlay').fadeOut(settings.speed, function(){
        $('#foxibox_details, #foxibox_container, #foxibox_loader, #foxibox_overlay').remove();
      });
      open_gallery = false;
    }

    function loaderPosition(){
      loader_top = scroll_top+window_height/2-$('#foxibox_loader').height()/2;
      loader_left = scroll_left+window_width/2-$('#foxibox_loader').width()/2;
    }

		function getScroll(){
      scroll_top = $(document).scrollTop();
      scroll_left = $(document).scrollLeft();
		}

    function getSize(){
      window_height = $(window).height();
      window_width = $(window).width();
      document_width = $(document).width();
      document_height = $(document).height();
    }
  }
})(jQuery);
