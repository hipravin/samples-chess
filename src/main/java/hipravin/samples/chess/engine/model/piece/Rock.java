package hipravin.samples.chess.engine.model.piece;

import hipravin.samples.chess.engine.ChessGame;
import hipravin.samples.chess.engine.model.PieceColor;
import hipravin.samples.chess.engine.model.Position;
import hipravin.samples.chess.engine.model.Type;

import java.util.Set;

public class Rock extends Piece {

    public Rock(Position position, PieceColor pieceColor) {
        super(position, pieceColor, Type.ROCK);
    }

    @Override
    public Set<Position> validPieceMoves(ChessGame game) {
        return position.moveUntilHit(position.rock(), game, pieceColor);
    }

    @Override
    public Piece moveTo(Position to) {
        return new Rock(to, getPieceColor());
    }
}
