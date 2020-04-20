package hipravin.samples.chess.engine;

import hipravin.samples.chess.engine.model.Board;
import hipravin.samples.chess.engine.model.PieceColor;
import hipravin.samples.chess.engine.model.PieceMove;
import hipravin.samples.chess.engine.model.Position;
import hipravin.samples.chess.engine.model.piece.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameTest {

    @Test
    void testStartGame() {
        ChessGame game = ChessGame.startGame();

        assertEquals(16, game.getBoard().pieces(PieceColor.WHITE).size());
        assertEquals(16, game.getBoard().pieces(PieceColor.BLACK).size());
    }

    @Test
    void testMovesInvalid() {
        ChessGame game = ChessGame.startGame();

        Assertions.assertThrows(InvalidMoveException.class, ()-> {
            movesFromString("e2e7").forEach(game::applyMove);
        });
    }

    @Test
    void testMoves() {
        ChessGame game = ChessGame.startGame();
        //so colled "detsky mat"
        List<PieceMove> moves = movesFromString("e2e4 e7e5 d1h5 b8c6 f1c4 g8f6 h5f7").collect(Collectors.toList());
        for (PieceMove move : moves) {
            game = game.applyMove(move);
        }

        assertTrue(game.isFinished());
        assertEquals(GameStatus.CHECKMATE, game.getStatus());
    }

    @Test
    void testValidMovesKnight1() {
        Piece whiteKing = new King(Position.of(2,2), PieceColor.WHITE);
        Piece blackKnight = new Knight(Position.of(4,3), PieceColor.BLACK);
        Piece whiteKnight = new Knight(Position.of(5,5), PieceColor.WHITE);

        Board board = new Board(Arrays.asList(whiteKing, whiteKnight, blackKnight));

        ChessGame chessGame = new ChessGame(PieceColor.WHITE, Collections.emptyList(), Collections.emptyList(), board);
        Set<Position> validMoves = whiteKnight.finallyValidMoves(chessGame);

        assertEquals(Sets.newSet(blackKnight.getPosition()), validMoves);
    }

    @Test
    void testValidMovesRock1() {
        Piece rock = new Rock(Position.of(5, 5), PieceColor.WHITE);
        Piece k1 = new Knight(Position.of(5, 6), PieceColor.WHITE);
        Piece k2 = new Knight(Position.of(6, 5), PieceColor.WHITE);
        Piece ki = new King(Position.of(2, 5), PieceColor.WHITE);
        Piece k4 = new Knight(Position.of(5, 4), PieceColor.BLACK);

        Board board = new Board(Arrays.asList(rock, k1, k2, ki, k4));

        ChessGame chessGame = new ChessGame(PieceColor.WHITE, Collections.emptyList(), Collections.emptyList(), board);
        Set<Position> validMoves = rock.finallyValidMoves(chessGame);

        //attack and two towards king
        assertEquals(Sets.newSet(k4.getPosition(), ki.getPosition().right1(), ki.getPosition().right1().right1()), validMoves);
    }

    @Test
    void testValidMovesPawn1() {
        Piece ki = new King(Position.of(5, 1), PieceColor.WHITE);
        Piece kib = new King(Position.of(5, 8), PieceColor.BLACK);
        Piece p1 = new Pawn(Position.of(5,2), PieceColor.WHITE);
        Piece p2 = new Pawn(Position.of(5,4), PieceColor.BLACK);
        Piece p3 = new Pawn(Position.of(6,3), PieceColor.BLACK);
        Piece pe = new Pawn(Position.of(6,4), PieceColor.BLACK);

        Board board = new Board(Arrays.asList(ki, p1));

        ChessGame chessGame = new ChessGame(PieceColor.WHITE, Collections.emptyList(), Collections.emptyList(), board);
        Set<Position> validMoves = p1.finallyValidMoves(chessGame);

        assertEquals(Sets.newSet(p1.getPosition().up1(), p1.getPosition().up1().up1()), validMoves);

        board = new Board(Arrays.asList(ki, p1, p2, p3));
        chessGame = new ChessGame(PieceColor.WHITE, Collections.emptyList(), Collections.emptyList(), board);
        validMoves = p1.finallyValidMoves(chessGame);

        //up2 blocked, but can attack p3
        assertEquals(Sets.newSet(p1.getPosition().up1(), p3.getPosition()), validMoves);

        //black en-passant
        board = new Board(Arrays.asList(ki, kib, p1, pe));
        PieceMove e2e4 = new PieceMove(Position.of(5, 2), Position.of(5,4));
        board = board.applyMoveNoValidate(e2e4);
        chessGame = new ChessGame(PieceColor.BLACK, Collections.emptyList(), Collections.singletonList(e2e4), board);
        validMoves = pe.validPieceMoves(chessGame);
        assertEquals(Sets.newSet(pe.getPosition().diagDl1(), pe.getPosition().down1()), validMoves);

        board = board.applyMoveNoValidate(new PieceMove(pe.getPosition(), Position.of(5,3)));
        assertNull(board.pieceMap(PieceColor.WHITE).get(Position.of(5,3)));
    }

    @Test
    void testValidMovesKing1() {
        Piece r1 = new Rock(Position.of(1, 1), PieceColor.WHITE);
        Piece r2 = new Rock(Position.of(8, 1), PieceColor.WHITE);
        Piece k1 = new King(Position.of(5, 1), PieceColor.WHITE);
        Piece k2 = new Knight(Position.of(5, 8), PieceColor.BLACK);
        Piece r = new Rock(Position.of(1, 2), PieceColor.BLACK);

        Piece q = new Queen(Position.of(4, 8), PieceColor.BLACK);

        Board board = new Board(Arrays.asList(r1, r2, k1, k2, r));

        ChessGame chessGame = new ChessGame(PieceColor.WHITE, Collections.emptyList(), Collections.emptyList(), board);
        Set<Position> validMoves = k1.finallyValidMoves(chessGame);


        Position kp = k1.getPosition();
        //castling both sides, but not up because of rock
        assertEquals(Sets.newSet(kp.left1(), kp.left1().left1(), kp.right1(), kp.right1().right1()), validMoves);

        //add queen
        board = new Board(Arrays.asList(r1, r2, k1, k2, r, q));

        chessGame = new ChessGame(PieceColor.WHITE, Collections.emptyList(), Collections.emptyList(), board);
        validMoves = k1.finallyValidMoves(chessGame);

        //castling only right bevause of queen attacking passing square
        assertEquals(Sets.newSet(kp.right1(), kp.right1().right1()), validMoves);
    }

    private static Stream<PieceMove> movesFromString(String moves) {
        return Arrays.stream(moves.split("\\s+"))
                .map(s -> {
                    Position from = Position.of(s.substring(0,2));
                    Position to = Position.of(s.substring(2));
                    return  new PieceMove(from, to);
                });
    }
}