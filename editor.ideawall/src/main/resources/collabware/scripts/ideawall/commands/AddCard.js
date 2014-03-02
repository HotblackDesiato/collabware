define(["ideawall/framework/AbstractCommand"], function (Command) {
	return Command({
		init: function (board, color, x ,y) {
			this.board = board;
			this.color = color;
			this.x = x;
			this.y = y;
		},
		
		apply: function execute(graph) {
			var card = graph.addNode();
			card.attr("name", "New Card");
			card.attr("color", this.color);
			card.attr("x", this.x);
			card.attr("y", this.y);
			this.board.orderedRef("card", card);
		}, 
		
		description: function () {
			return "Adding new card to the board.";
		}
	});
});