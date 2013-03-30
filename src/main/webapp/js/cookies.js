
define([], function() {
	return {
		get: function(key, options) {
	    	options = options || {};
	    	var decode = options.decode ? decodeURIComponent : function(s) { return s; };
	
	    	var pairs = document.cookie.split('; ');
	    	for (var i = 0, pair; pair = pairs[i] && pairs[i].split('='); i++) {
	    		if (decode(pair[0]) === key) {
	    			var value = decode(pair[1] || ''); // IE saves cookies with empty string as "c; ", e.g. without "=" as opposed to EOMB, thus pair[1] may be undefined
	
	    			// Strip quotes if they were added
					if (value.length > 0 && value[0] == '"' && value[value.length-1] == '"')
						value = value.substring(1, value.length-1);
	
	    			return value;
	    		}
	    	}
	    	return null;
	    },
	    set: function(key, value, options) {
	        options = options || { path: '/' };	// JMS: changed to default to root path
	        if (value === null || value === undefined) {
	            options.expires = -1;
	        }
	
	        if (typeof options.expires === 'number') {
	            var days = options.expires, t = options.expires = new Date();
	            t.setDate(t.getDate() + days);
	        }
	
	        value = String(value);
	
	        return (document.cookie = [
	            encodeURIComponent(key), '=',
	            options.encode ? encodeURIComponent(value) : value,
	            options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
	            options.path ? '; path=' + options.path : '',
	            options.domain ? '; domain=' + options.domain : '',
	            options.secure ? '; secure' : ''
	        ].join(''));
	    },
	    'delete': function(key) {
	        document.cookie = encodeURIComponent(key) + '=0; path=/; expires=Wed, 31 Dec 1969 23:59:59 GMT';
	    }
	};
});
