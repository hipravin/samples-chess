package hipravin.samples.chess.engine.model.piece;

import hipravin.samples.chess.engine.ChessGame;
import hipravin.samples.chess.engine.model.PieceColor;
import hipravin.samples.chess.engine.model.Position;
import hipravin.samples.chess.engine.model.Type;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class King extends Piece {
    final static Map<PieceColor, Position> START_POSITION = Map.of(
            PieceColor.WHITE, Position.of(5,1),
            PieceColor.BLACK, Position.of(5,8)
    );

    public King(Position position, PieceColor playerColor) {
        super(position, playerColor, Type.KING);
    }

    @Override
    public Set<Position> validPieceMoves(ChessGame game) {
        Set<Position> moves = new HashSet<>();
        moves.addAll(position.king());
        //check castling only for current player to prevent infinite recursion
        if(game.getCurrentPlayer() == pieceColor) {
            moves.addAll(castling(game).collect(Collectors.toList()));
        }

        return moves;
    }
    public Stream<Position> castling(ChessGame game) {
        if(position.equals(START_POSITION.get(pieceColor)) && neverMoved(position, game)) {
            return Stream.of(castlingLeft(game), castlingRight(game))
                    .filter(Optional::isPresent)
                    .map(Optional::get);
        } else {
            return Stream.empty();
        }
    }

    private Optional<Position> castlingLeft(ChessGame game) {

        Position l1 = position.left1();
        Position l2 = l1.left1();
        Position l3 = l2.left1();
        Position rock = l3.left1();

        if(game.empty(l1)
             && game.empty(l2)
             && game.empty(l3)
             && neverMoved(rock, game)
             && !game.isUnderAttack(position)
             && !game.isUnderAttack(l1)
             && !game.isUnderAttack(l2)) {

            return Optional.of(l2);
        }

        return Optional.empty();
    }

    private Optional<Position> castlingRight(ChessGame game) {
        Position r1 = position.right1();
        Position r2 = r1.right1();
        Position rock = r2.right1();

        if(game.empty(r1)
                && game.empty(r2)
                && neverMoved(rock, game)
                && !game.isUnderAttack(position)
                && !game.isUnderAttack(r1)
                && !game.isUnderAttack(r2)) {

            return Optional.of(r2);
        }

        return Optional.empty();
    }

    private boolean neverMoved(Position p, ChessGame game) {
        return game.getPreviousMoves().stream()
                .noneMatch(m -> m.getFrom().equals(p));
    }


    @Override
    public Piece moveTo(Position to) {
        return new King(to, getPieceColor());
    }
}
