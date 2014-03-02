sap.ui.controller( "views.Replay" ,{

	onInit: function(){
		 this.oPopup = this.byId("ReplayPopup");
		 this.oBeginning = this.byId("beginning");
		 this.oBack = this.byId("back");
		 this.oPlay = this.byId("play");
		 this.oStop = this.byId("stop");
		 this.oForward = this.byId("forward");
		 this.oEnd = this.byId("end");
		 this.oSlider = this.byId("Slider");
	},

	onBeforeRendering: function(){
	
	},

	onAfterRendering: function() {
	
	},
	
	beginning: function() {
		if (this.replayControl) {
			this.replayControl.beginning();
		}
	},
	
	back: function() {
		if (this.replayControl && this.replayControl.canPrevious()) {
			this.replayControl.previous();
		}		
	},
	
	stop: function() {
		this.oStop.setEnabled(false);
		this.oStop.rerender();
		this.oPlay.setEnabled(true);
		this.oPlay.rerender();
		clearTimeout(this.timeout);
	},
	
	play: function() {
		this.oPlay.setEnabled(false);
		this.oPlay.rerender();
		this.oStop.setEnabled(true);
		this.oStop.rerender();
		
		var that = this;
		this.replayControl.next();
		if (this.replayControl.canNext()) {
			this.timeout = setTimeout(function() {that.play();}, 700);
		} else {
			this.oStop.setEnabled(false);
			this.oStop.rerender();
			this.oPlay.setEnabled(true);
			this.oPlay.rerender();
		}
	},
	
	forward: function() {
		if (this.replayControl && this.replayControl.canNext()) {
			this.replayControl.next();
		}
	},
	
	end: function() {
		if (this.replayControl) {
			this.replayControl.end();
		}
	},
	
	close: function(){
		this.oPopup.close();
	},
	
	startReplay: function () {
		var that = this;
		this.replay = collabwareSession.replay();
		this.replay.addListener({
			started: function () {
				that.replayControl = that.replay.getReplayControl();
				that.oSlider.setMax(that.replayControl.length());
			},
			
			replayed: function (op) {
				that.oSlider.setValue(that.replayControl.getPosition());
			},
			
			ended: function () {
				that.oPopup.close();
			}
		});
		this.replay.enter();
	},
	
	endReplay: function () {
		this.replay.exit();
	},
	
	seek: function (oEvent) {
		this.replayControl.seek(oEvent.getParameters().value);
	}
});