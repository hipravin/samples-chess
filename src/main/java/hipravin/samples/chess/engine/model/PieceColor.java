package hipravin.samples.chess.engine.model;

public enum PieceColor {
    WHITE,
    BLACK;

    public PieceColor negate() {
        switch (this) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            default:
                throw new IllegalStateException();
        }
    }
}
