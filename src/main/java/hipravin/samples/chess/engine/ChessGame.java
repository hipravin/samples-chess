package hipravin.samples.chess.engine;

import hipravin.samples.chess.api.model.PieceDto;
import hipravin.samples.chess.api.model.PieceTypeDto;
import hipravin.samples.chess.engine.model.*;
import hipravin.samples.chess.engine.model.piece.Piece;

import java.util.*;
import java.util.stream.Collectors;

public class ChessGame {
    private final PieceColor currentPlayer;
    private final List<Board> previousStates;
    private final List<PieceMove> previousMoves;
    private final Board board;

    private boolean finished = false;
    private PieceColor winner = null;
    private GameStatus status = GameStatus.NORMAL;
    private Map<Position, Set<Position>> validMovesForCurrentPlayer;

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

    public static ChessGame startGame() {
        ChessGame chessGame = new ChessGame(PieceColor.WHITE, Collections.emptyList(), Collections.emptyList(), Board.startBoard());
        chessGame.validMovesForCurrentPlayer = chessGame.validMovesForCurrentPlayer();

        return chessGame;
    }

    public List<PieceDto> calculatePieceDtos() {
        return board.pieces()
                .stream()
                .map(p -> {
                    PieceDto pd = new PieceDto();
                    pd.setPieceType(PieceTypeDto.valueOf(p.getPieceType().name()));
                    pd.setPosition(p.getPosition().stringValue());
                    pd.setColor(p.getPieceColor().toColorDto());
                    if(validMovesForCurrentPlayer.containsKey(p.getPosition())) {
                        pd.setValidMoves(validMovesForCurrentPlayer.get(p.getPosition()).stream()
                                .map(Position::stringValue).collect(Collectors.toList()));
                    }
                    return pd;
                }).collect(Collectors.toList());
    }

    public ChessGame applyMove(PieceMove pieceMove) {
        ensureMoveValid(pieceMove);
        ChessGame gameAfterMove = applyMoveNoValidate(pieceMove);
        gameAfterMove.updateGameStatus();
        return gameAfterMove;
    }

    public void updateGameStatus() {
        this.validMovesForCurrentPlayer = validMovesForCurrentPlayer();

        boolean kingAttacked = kingUnderAttack(currentPlayer);
        boolean canMove = validMovesForCurrentPlayer.values().stream().anyMatch(s -> !s.isEmpty());

        if (canMove && kingAttacked) {
            status = GameStatus.CHECK;
        }
        if (!canMove && kingAttacked) {
            status = GameStatus.CHECKMATE;
            finished = true;
        }
        if (!canMove && !kingAttacked) {
            status = GameStatus.DRAW_STALEMATE;
            finished = true;
        }
    }

    private Map<Position, Set<Position>> validMovesForCurrentPlayer() {
        return board.pieceMap(currentPlayer).values().stream()
                .collect(Collectors.toMap(Piece::getPosition, p -> p.finallyValidMoves(this)));
    }

    private void ensureMoveValid(PieceMove pieceMove) throws InvalidMoveException {
        Piece piece = at(pieceMove.getFrom()).orElseThrow(() -> new InvalidMoveException("No piece at position " + pieceMove.getFrom().toString()));
        if (pieceMove.getPromotion() != null
                && (piece.getPieceType() != Type.PAWN || pieceMove.getTo().getY() != 1 && pieceMove.getTo().getY() != 8)) {
            throw new InvalidMoveException("Promotion is only applicable to piece at finish line");
        }

        Set<Position> validMoves = piece.finallyValidMoves(this);
        if (!validMoves.contains(pieceMove.getTo())) {
            throw new InvalidMoveException("Piece is not allowed to be moved to: " + pieceMove.getTo().toString());
        }
    }

    public ChessGame applyMoveNoValidate(PieceMove pieceMove) {
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

    public boolean kingUnderAttack(PieceColor player) {

        Set<Position> attackPositions =
                board.pieces(player.negate()).stream()
                        .flatMap(p -> p.validPieceMoves(this).stream())
                        .collect(Collectors.toSet());

        return attackPositions.contains(board.king(player));
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public PieceColor getWinner() {
        return winner;
    }

    public void setWinner(PieceColor winner) {
        this.winner = winner;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Map<Position, Set<Position>> getValidMovesForCurrentPlayer() {
        return validMovesForCurrentPlayer;
    }

    public void setValidMovesForCurrentPlayer(Map<Position, Set<Position>> validMovesForCurrentPlayer) {
        this.validMovesForCurrentPlayer = validMovesForCurrentPlayer;
    }
}
