package hipravin.samples.chess.engine.model;

import hipravin.samples.chess.api.model.ColorDto;

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

    public ColorDto toColorDto() {
        return this == WHITE ? ColorDto.WHITE : ColorDto.BLACK;
    }

}
