define([], function () {
	return {
		isMoving: false,
		oldX:0,
		oldY:0,
		dx: 0,
		dy: 0,
		margin: 10,
		oldCssPosition:"",
		
		init:function () {
			this.getView().bind("vmousedown",this.deferredCall(this._moveable_startMoving));
			this.getView().bind("vmouseup",this.deferredCall(this._moveable_endMoving));
			$(document).bind("vmousemove",this.deferredCall(this._moveable_moving));
		},
		
		_moveable_startMoving: function(event) {
			if (this._editable_isEditing) return;
			event.preventDefault();
			
			if (typeof this.startMoving === "function") {
				this.startMoving();
			}
			this.isMoving = true;
			var offset = this.getView().offset();
			this.oldX = event.pageX;
			this.oldY = event.pageY;
			this.dx = event.pageX - offset.left + this.margin;
			this.dy = event.pageY - offset.top + this.margin;
			this.oldCssPosition = this.getView().css("position")
		},
		
		_moveable_moving: function(event) {
			if (this.isMoving) {
				event.preventDefault();
				var x = event.pageX - this.dx;
				var y = event.pageY - this.dy;
				this.getView().css("position", "absolute");
				this.getView().css("top", y);
				this.getView().css("left", x);
				if (typeof this.moving === "function") {
					this.moving(event.pageX, event.pageY);
				}
			}
		},
		
		_moveable_endMoving: function (event) {
			if (this.isMoving) {
				this.isMoving = false;
				if (this.endMoving && (this.oldX !== event.pageX || this.oldY !== event.pageY))
					this.endMoving(event.pageX-this.dx, event.pageY-this.dy, event.pageX, event.pageY);
				else 
					this.getView().css("position", this.oldCssPosition)
				event.preventDefault();
			}
		}
	};
});