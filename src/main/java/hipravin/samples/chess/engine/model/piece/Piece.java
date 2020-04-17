package hipravin.samples.chess.engine.model.piece;

import hipravin.samples.chess.engine.ChessGame;
import hipravin.samples.chess.engine.model.PieceColor;
import hipravin.samples.chess.engine.model.PieceMove;
import hipravin.samples.chess.engine.model.Position;
import hipravin.samples.chess.engine.model.Type;

import java.util.Set;

public abstract class Piece {

    protected final Position position;
    protected final PieceColor pieceColor;
    protected final Type pieceType;

    public Piece(Position position, PieceColor pieceColor, Type pieceType) {
        this.position = position;
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
    }

    public abstract Piece moveTo(Position to);

    public abstract Set<Position> validPieceMoves(ChessGame game);

    public final Set<Position> finallyValidMoves(ChessGame game) {
        Set<Position> moves = validPieceMoves(game);

        //remove target positions where same team pieces are present
        moves.removeIf(pos ->
                game.at(pos).map(piece -> piece.pieceColor == pieceColor).orElse(false));

        //remove target positions where king will be in trouble after move
        moves.removeIf(m -> game.applyMove(new PieceMove(position, m)).kingUnderAttackAfterMove());

        return moves;
    }

    public Position getPosition() {
        return position;
    }

    public PieceColor getPieceColor() {
        return pieceColor;
    }

    public Type getPieceType() {
        return pieceType;
    }
}
