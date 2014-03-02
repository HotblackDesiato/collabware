define([], function () {
	return function DnDTool(domNode) {
		var ghostUi;
		var onDropCallback;
		var moving = false;
		var dx=0,dy=0;
		
		function positionGhostAt(x, y) {
			ghostUi.css("left", x);
			ghostUi.css("top", y);
		}
		
		function mouseDown(event) {
			moving = true;
			ghostUi.css("position","absolute");
			$("body").append(ghostUi);
			var offset = $(event.target).offset();
			dx = event.pageX - offset.left+5;
			dy = event.pageY - offset.top+5;
			positionGhostAt(event.pageX-dx, event.pageY-dy)
			event.preventDefault();

			ghostUi.bind("vmouseup", mouseUp);
		}
		
		function mouseUp(event) {
			onDropCallback(event);
			ghostUi.remove();
			ghostUi.unbind("vmouseup");
			moving = false;
			event.preventDefault();
		}
		
		function mouseMove(event) {
			if (moving) {
				var x = event.pageX - dx;
				var y = event.pageY - dy;
				positionGhostAt(x, y);
				event.preventDefault();
			}
		}
		
		$(document).bind("vmousemove",mouseMove);
		domNode.bind("vmousedown", mouseDown);
		
		return {
			ghost: function (ui) {
				ghostUi = ui;
				return this;
			},
			
			onDrop: function (callback) {
				onDropCallback = callback;
				return this;
			}
		}
	}
});