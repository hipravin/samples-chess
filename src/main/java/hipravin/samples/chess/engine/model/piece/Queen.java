package hipravin.samples.chess.engine.model.piece;

import hipravin.samples.chess.engine.ChessGame;
import hipravin.samples.chess.engine.model.PieceColor;
import hipravin.samples.chess.engine.model.Position;
import hipravin.samples.chess.engine.model.Type;

import java.util.Set;

public class Queen extends Piece {

    public Queen(Position position, PieceColor pieceColor) {
        super(position, pieceColor, Type.QUEEN);
    }

    @Override
    public Set<Position> validPieceMoves(ChessGame game) {
        return position.moveUntilHit(position.queen(), game, pieceColor);
    }

    @Override
    public Piece moveTo(Position to) {
        return new Queen(to, getPieceColor());
    }
}
