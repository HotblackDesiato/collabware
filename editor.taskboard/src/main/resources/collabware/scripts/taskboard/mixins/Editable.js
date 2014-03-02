define([], function () {
	return function Editable(property) {
		return {
			_editable_isEditing: false,
			_editable_size: {width:0, height:0},
			_editable_oldValue: undefined,
			
			init: function () {
				this.input = $("<textarea/>");
				this.input.css("resize", "none");
				this.input.css("font", "inherit");
				this._editable_view = this._editable_getView();
				this._editable_registerEditStartEvents();
			},
			
			_editable_getView: function () {
				if (this.getEditableView) {
					return this.getEditableView();
				} else {
					return this.getView();
				}
			},
			
			_editable_registerEditStartEvents: function() {
				var that = this;
				function startEdit() {
					if (that._editable_isEditing) return;
					that._editable_startEditing.apply(that);
				}
				this._editable_view.bind('vdblclick',startEdit);
				this._editable_view.bind('dblclick',startEdit);
				
			},

			_editable_showTextArea: function (view) {
				var size = {width: view.width(), height: view.height()};
				view.empty();
				view.append(this.input);
				this.input.css("height", size.height);
				this.input.css("width", size.width);
				this.input.val(this._editable_oldValue);
			},
			
			_editable_selectAllText: function () {
				this.input[0].selectionStart=0;
				this.input[0].selectionEnd=this.input.val().length;
				this.input.focus();
			},
			
			_editable_startEditing: function () {
				if (!this._editable_isEditing) {
					this._editable_oldValue = this.getNode().attr(property);
					this._editable_isEditing = true;
					this._editable_showTextArea(this._editable_view);
					this._editable_selectAllText();
					this._editable_registerEditEndEvents();
				}
			},
			
			_editable_registerEditEndEvents: function () {
				this.input.blur(this.deferredCall(this._editable_endEditing));
				var that = this;
				this.input.keypress(function (e) {
					if (e.charCode === 13 /*return*/) {
						that._editable_endEditing();
					}
				});						
			},
			
			_editable_endEditing: function () {
				var newValue = this.input.val();
				this._editable_isEditing = false;
				this._editable_view.empty();
				this._editable_view.text(this._editable_oldValue);
				if (this.endEditing && newValue !== this._editable_oldValue) {
					this.endEditing(this.input.val());				
				}
			}
		};
	};
});