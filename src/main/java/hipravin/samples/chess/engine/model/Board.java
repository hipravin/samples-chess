package hipravin.samples.chess.engine.model;

import hipravin.samples.chess.engine.model.piece.Pawn;
import hipravin.samples.chess.engine.model.piece.Piece;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Board implements Cloneable {
    final Map<Position, Piece> whitePieces = new HashMap<>();
    final Map<Position, Piece> blackPieces = new HashMap<>();

    public Board() {
    }

    public Board(Collection<Piece> pieces) {
        whitePieces.putAll(pieces.stream().filter(p -> p.getPieceColor() == PieceColor.WHITE)
                .collect(Collectors.toMap(Piece::getPosition, Function.identity())));
        blackPieces.putAll(pieces.stream().filter(p -> p.getPieceColor() == PieceColor.BLACK)
                .collect(Collectors.toMap(Piece::getPosition, Function.identity())));
    }

    public Position king(PieceColor side) {
        return pieces(side).stream()
                .filter(p -> p.getPieceType() == Type.KING)
                .findAny()
                .map(Piece::getPosition)
                .orElseThrow();
    }

    public Collection<Piece> pieces(PieceColor side) {
        return side == PieceColor.WHITE ? whitePieces.values() : blackPieces.values();
    }

    public Map<Position, Piece> pieceMap(PieceColor side) {
        return side == PieceColor.WHITE ? whitePieces : blackPieces;
    }

    public Board applyMoveNoValidate(PieceMove pieceMove) {
        Board cloned = this.clone();
        if (isCastling(pieceMove)) {
            cloned.applyCastling(pieceMove);
        } else if (isEnPassant(pieceMove)) {
            cloned.applyEnPassant(pieceMove);
        } else {
            cloned.applyStandardMove(pieceMove);
        }
        return cloned;
    }

    private boolean isCastling(PieceMove pieceMove) {
        return at(pieceMove.getFrom()).orElseThrow().getPieceType() == Type.KING
                && Math.abs(pieceMove.getFrom().getX() - pieceMove.getTo().getX()) > 1;
    }

    private void applyCastling(PieceMove pieceMove) {
        Position from = pieceMove.getFrom();
        Position to = pieceMove.getTo();
        Position rockFrom;
        Position rockTo;

        if(to.getX() < from.getX()) {
            rockFrom = to.left1().left1();
            rockTo = to.right1();
        } else {
            rockFrom = to.right1();
            rockTo = to.left1();
        }

        PieceColor color = at(from).orElseThrow().getPieceColor();
        Map<Position, Piece> playerPieces = pieceMap(color);

        moveTo(playerPieces, from, to);
        moveTo(playerPieces, rockFrom, rockTo);
    }

    private boolean isEnPassant(PieceMove pieceMove) {
        return at(pieceMove.getFrom()).orElseThrow().getPieceType() == Type.PAWN
                && pieceMove.getFrom().getX() != pieceMove.getTo().getX()
                && at(pieceMove.getTo()).isEmpty();
    }

    private void applyEnPassant(PieceMove pieceMove) {
        PieceColor pieceColor = at(pieceMove.getFrom()).orElseThrow().getPieceColor();

        UnaryOperator<Position> moveBackward = Pawn.MOVE_BACKWARD.get(pieceColor);
        Position near = moveBackward.apply(pieceMove.getTo());

        moveTo(pieceMap(pieceColor), pieceMove.getFrom(), pieceMove.getTo());
        pieceMap(pieceColor.negate()).remove(near);
    }

    private void applyStandardMove(PieceMove pieceMove) {
        Position from = pieceMove.getFrom();
        Position to = pieceMove.getTo();
        PieceColor color = at(from).orElseThrow().getPieceColor();

        Map<Position, Piece> playerPieces = pieceMap(color);
        Map<Position, Piece> opponentPieces = pieceMap(color.negate());

        opponentPieces.remove(to);
        moveTo(playerPieces, from, to);
    }

    private void moveTo(Map<Position, Piece> playerPieces, Position from, Position to) {
        playerPieces.put(to, playerPieces.get(from).moveTo(to));
        playerPieces.remove(from);
    }

    public Optional<Piece> at(Position position) {
        return whitePieces.containsKey(position)
                ? Optional.of(whitePieces.get(position))
                : Optional.ofNullable(blackPieces.get(position));
    }

    @Override
    public Board clone() {
        Board cloned = new Board();
        cloned.whitePieces.putAll(whitePieces);
        cloned.blackPieces.putAll(blackPieces);

        return cloned;
    }
}
