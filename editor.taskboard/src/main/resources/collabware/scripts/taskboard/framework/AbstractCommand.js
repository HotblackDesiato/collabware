define([], function () {
	return function Command(impl) {
		return function () {
			if (typeof impl.init ==='function') {
				impl.init.apply(impl, arguments);
			} 
			return {
				apply: function (graph) {
					impl.apply.call(impl, graph);
				},
				getDescription: function () {
					if (typeof impl.description	=== 'function'){
						return impl.description.call(impl);
					} else {
						return impl.description;
					}
				}
			};
		}
	}
});