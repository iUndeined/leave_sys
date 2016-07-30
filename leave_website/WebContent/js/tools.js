$.cjd = {
	'open': function(url, data){
		var form = $('<form>', {
	      'action': url 
	      , 'target': '_blank'
	      , 'method': 'get'
	    });
	    
	    // 只接受对像参数
	    if (typeof data == 'object' && !$.isArray(data)) {
	      $.each(data, function(i){
	        var hidden = $('<input>', {'name': i, 'type': 'hidden', 'value': this});
	        form.append(hidden);
	      });
	    }
	    
	    form.appendTo('body');
	    form.submit();
	    form.remove();
	},
	'submit': function(url, data, method, isBlank){
		if (!url) {
			return;
		}
		
		var form = $('<form>', {'action': url});
		
		// post 方式提交
		if (method && /post/gi.test(method)) {
			form.prop('method', 'post');
		}
		
		// 新窗口提交
		if (isBlank) {
			form.prop('target', '_blank');
		}
		
		if (data) {
			$.each(data, function(name){
				hidden = $('<input>', {'type': 'hidden', 'name': name, 'value': this}).appendTo(form);
			});
		}
		
		form.appendTo('body');
		form.submit();
	}
}