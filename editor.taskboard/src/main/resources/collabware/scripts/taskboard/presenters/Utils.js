define([], function ( ) {
	return {
		loadCss: function (css) {
		    var link = document.createElement("link");
		    link.type = "text/css";
		    link.rel = "stylesheet";
		    link.href = require.toUrl(css);
		    document.getElementsByTagName("head")[0].appendChild(link);
		}
	};
});