package hipravin.samples.chess.engine.model;

import hipravin.samples.chess.api.model.MoveDto;
import hipravin.samples.chess.api.model.PieceTypeDto;

import java.util.Optional;

public class PieceMove {
    private final Position from;
    private final Position to;
    private final Type promotion;

    public PieceMove(Position from, Position to) {
        this.from = from;
        this.to = to;
        this.promotion = null;
    }

    public PieceMove(Position from, Position to, Type promotion) {
        this.from = from;
        this.to = to;
        this.promotion = promotion;
    }

    public static PieceMove of(MoveDto moveDto) {
        Type promotion = (moveDto.getPromotion() != null && !moveDto.getPromotion().isEmpty())
                ? Type.valueOf(moveDto.getPromotion().toUpperCase())
                : null;
        return  new PieceMove(Position.of(moveDto.getFrom()), Position.of(moveDto.getTo()), promotion);
    }

    public MoveDto toDto() {
        return new MoveDto(from.stringValue(), to.stringValue(),
                Optional.ofNullable(promotion).map(Enum::name).orElse(null));
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Type getPromotion() {
        return promotion;
    }
}
