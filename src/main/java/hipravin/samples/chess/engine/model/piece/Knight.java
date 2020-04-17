package hipravin.samples.chess.engine.model.piece;

import hipravin.samples.chess.engine.ChessGame;
import hipravin.samples.chess.engine.model.PieceColor;
import hipravin.samples.chess.engine.model.Position;
import hipravin.samples.chess.engine.model.Type;

import java.util.HashSet;
import java.util.Set;

public class Knight extends Piece {
    public Knight(Position position, PieceColor playerColor) {
        super(position, playerColor, Type.KNIGHT);
    }

    @Override
    public Set<Position> validPieceMoves(ChessGame game) {
        return new HashSet<>(position.knight());
    }

    @Override
    public Piece moveTo(Position to) {
        return new Knight(to, getPieceColor());
    }
}
