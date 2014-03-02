define([], function(){
	return function Controller(arg1, arg2) {
		
		var impl = {}, mixins =[];
		if (arg1 && arg2) {
			mixins = arg1;
			impl = arg2;		
		} else {
			impl = arg1;		
		}
		
		function isAttributeChangedMethod(functionName) {
			return functionName.indexOf("on") === 0 && functionName.lastIndexOf("Changed") === functionName.length-"Changed".length;
		}
		
		function getAttributeName(functionName) {
			var x = functionName.substr("on".length, functionName.length - "on".length - "changed".length);
			var head = x.substr(0,1);
			var tail = x.substr(1);
			return head.toLowerCase() + tail;
		}
		 
		return function Constructor(node, document, factory) {
			var parent = null;
			var privateController = {
				bindAttribute: function (attributeName, onChange) {
					
				},
				
				bindReference: function (refName, onAddition, onRemoval) {
					
				},
				
				getControllerForNode: function (nodeOrId) {
					return factory.getControllerForNode(nodeOrId);
				},
				
				getNode: function () {
					return node;
				},
				
				getParentController: function () {
					return parent;
				},
				
				init: function () {
					
				},
				
				getView: function () {
					
				},
				
				execute: function (command) {
					document.applyChange(command.apply, command.getDescription());
				},
				
				deferredCall: function (callback) {
					var that = this;
					return function () {
						callback.apply(that, arguments);
					}
				},
				setParentController: function(controller) {
					parent = controller;
				},
				getParentController: function () {
					return parent;
				},
				destroy: function () {
					document.unbindAttribute(this.getNode());
					this.getView().remove();
				}
			};
			
			function bindAttribute(attribute, onChangedFunction){
				document.bindAttribute(attribute, node).onChanged(function () {
					onChangedFunction.apply(privateController, arguments);
				});
			}
			
			for (var p in impl) {
				if (impl.hasOwnProperty(p)) {
					privateController[p] = impl[p];
					if (isAttributeChangedMethod(p)) {
						bindAttribute(getAttributeName(p), privateController[p])
					}
				}
			}
			privateController.init();
	
			$.each(mixins, function (_, mixin) {
				for (var p in mixin) {
					if (mixin.hasOwnProperty(p) && p !== "init") {
						privateController[p] = mixin[p];						
					}
				}
				mixin.init.apply(privateController);
			});
			
			return privateController;
		};
	};
});