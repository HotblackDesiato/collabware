define([], function(Logger) {
	//"collabware/utils/Logger"
//	var LOGGER = new Logger("ControllerFactory");
	
	return function ControllerFactory(document, initFunction) {
		var graph = document.getGraph();
		var mappings = {};
		var nodeToControllerMapping = {};
		
		function createControllerForNode(nodeId, controllerConstructor) {
			var node = graph.node(nodeId);
			var controller;
			if (!nodeToControllerMapping[nodeId]) {
				controller = controllerConstructor(node, document, factory);
				nodeToControllerMapping[nodeId] = controller;
			} else {
				controller = nodeToControllerMapping[nodeId];
			}
			return controller;
		} 
		
		function asAddMethodCall(ref) {
			var head = ref.substr(0,1);
			var tail = ref.substr(1);
			return "add" + head.toUpperCase() + tail;
		}
		
		function asRemoveMethodCall(ref) {
			var head = ref.substr(0,1);
			var tail = ref.substr(1);
			return "remove" + head.toUpperCase() + tail;
		}
		
		var deferredControllerBindings = [];
		
		document.addDocumentChangeListener({
			nodeAdded: function (nodeId) {
				if (nodeId === "---ROOT_NODE"){
					$.each(mappings, function (key, constructor) {
						if (key === "/")
							createControllerForNode(nodeId, constructor);
					});
				}
			},
			nodeRemoved: function (nodeId) {
				if (nodeId !== "---ROOT_NODE"){
					var controller = nodeToControllerMapping[nodeId];
					if (controller) controller.destroy();
					delete nodeToControllerMapping[nodeId];
				}
			},
			referenceAdded: function (nodeId, ref, index, targetId) {
				$.each(mappings, function (key, constructor) {
					var pathParts = key.split("/");
					
					if (pathParts[pathParts.length-1] === ref) {
						var controller = createControllerForNode(targetId, constructor);
						deferredControllerBindings.push({parentId: nodeId, childController: controller, index:index, ref:ref});
					}
				});					
			},
			referenceRemoved: function (nodeId, ref, index, targetId) {
				$.each(mappings, function (key, constructor) {
					var pathParts = key.split("/");
					
					if (pathParts[pathParts.length-1] === ref) {
						var controller = nodeToControllerMapping[targetId];
						var parent = nodeToControllerMapping[nodeId];
						var removeMethodCall = parent[asRemoveMethodCall(ref)]
						if (removeMethodCall) {
							removeMethodCall(controller, index);
						}
						controller.destroy();
						delete nodeToControllerMapping[targetId];
					}
				});					
			},
			
			complexChangeEnded: function (description) {
				$.each(deferredControllerBindings, function (_, binding) {
					var parent = nodeToControllerMapping[binding.parentId];
					if (parent) {
						parent[asAddMethodCall(binding.ref)](binding.childController, binding.index);						
					} else {
//						LOGGER.error("No Controller registered for node '"+ binding.parentId+"'.")
					}
				});
				deferredControllerBindings=[];
			}
		})
		
		var factory = {
			create: function (controllerConstructor) {
				return {
					forPath: function (path) {
						if (path === "/")
							createControllerForNode("---ROOT_NODE", controllerConstructor);
						else 
							mappings[path] = controllerConstructor;	
					}
				}
			},
			getControllerForNode: function (node) {
				if (node.getId) {
					return nodeToControllerMapping[node.getId()];					
				} else {
					return nodeToControllerMapping[node];
				}
			}
		};
		
		return factory;
	}
});