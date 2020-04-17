package hipravin.samples.chess.engine.model;

public class PieceMove {
    private final Position from;
    private final Position to;

    public PieceMove(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }
}
