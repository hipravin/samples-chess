package hipravin.samples.chess.engine;

import hipravin.samples.chess.engine.model.Board;
import hipravin.samples.chess.engine.model.PieceMove;
import hipravin.samples.chess.engine.model.piece.Piece;
import hipravin.samples.chess.engine.model.PieceColor;
import hipravin.samples.chess.engine.model.Position;

import java.util.*;
import java.util.stream.Collectors;

public class ChessGame {
    private final PieceColor currentPlayer;
    private final List<Board> previousStates;
    private final List<PieceMove> previousMoves;
    private final Board board;

    public ChessGame(PieceColor currentPlayer, List<Board> previousStates, List<PieceMove> previousMoves, Board board) {
        this.currentPlayer = currentPlayer;
        this.previousStates = previousStates;
        this.previousMoves = previousMoves;
        this.board = board;
    }

    public Optional<Piece> at(Position p) {
        return board.at(p);
    }

    public boolean empty(Position p) {
        return board.at(p).isEmpty();
    }

    public Collection<Position> validMoves(Position piecePosition) {
        Piece piece = board.at(piecePosition).orElseThrow();

        return piece.finallyValidMoves(this);
    }

    public ChessGame applyMove(PieceMove pieceMove) {
        Board boardAfterMove = board.applyMoveNoValidate(pieceMove);
        List<Board> states = new ArrayList<>(previousStates);
        states.add(board);

        List<PieceMove> moves = new ArrayList<>(previousMoves);
        moves.add(pieceMove);

        return new ChessGame(currentPlayer.negate(), states, moves, boardAfterMove);
    }

    public boolean isUnderAttack(Position position) {
        Set<Position> attackPositions =
                board.pieces(currentPlayer.negate()).stream()
                        .flatMap(p -> p.validPieceMoves(this).stream())
                        .collect(Collectors.toSet());
        return attackPositions.contains(position);
    }

    public boolean kingUnderAttackAfterMove() {

        Set<Position> attackPositions =
                board.pieces(currentPlayer).stream()
                        .flatMap(p -> p.validPieceMoves(this).stream())
                        .collect(Collectors.toSet());

        return attackPositions.contains(board.king(currentPlayer.negate()));
    }

    public PieceColor getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Board> getPreviousStates() {
        return previousStates;
    }

    public List<PieceMove> getPreviousMoves() {
        return previousMoves;
    }

    public Board getBoard() {
        return board;
    }
}
