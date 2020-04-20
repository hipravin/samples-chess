package hipravin.samples.chess.engine.model;

import hipravin.samples.chess.engine.ChessGame;
import hipravin.samples.chess.engine.model.piece.Piece;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class Position {
    //from 1 to 8
    private final int x;
    private final int y;

    private Position(int x, int y) {
        if (x < 1 || x > 8 || y < 1 || y > 8) {
            throw new IllegalArgumentException("Invalid position: " + x + " " + y);
        }

        this.x = x;
        this.y = y;
    }

    public static Position of(String s) {
        if(!s.matches("(?i)[A-H][1-8]")) {
            throw new IllegalArgumentException("Position invalid: " + s);
        }

        //JDK 12 switch would be nice there
        int x = 0;
        switch (s.toLowerCase().charAt(0)) {
            case 'a':
                x = 1;
                break;
            case 'b':
                x = 2;
                break;
            case 'c':
                x = 3;
                break;
            case 'd':
                x = 4;
                break;
            case 'e':
                x = 5;
                break;
            case 'f':
                x = 6;
                break;
            case 'g':
                x = 7;
                break;
            case 'h':
                x = 8;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + s.toLowerCase().charAt(0));
        }
        int y = Integer.parseInt(s.substring(1));

        return new Position(x, y);
    }

    /**
     * @return "e2", "a1", ...
     */
    public String stringValue() {
        switch (x) {
            case 1:
                return "a" + y;
            case 2:
                return "b" + y;
            case 3:
                return "c" + y;
            case 4:
                return "d" + y;
            case 5:
                return "e" + y;
            case 6:
                return "f" + y;
            case 7:
                return "g" + y;
            case 8:
                return "h" + y;
            default:
                throw new IllegalStateException("Unexpected value: " + x);
        }
    }

    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    public Position up1() {
        return (y < 8) ? of(x, y + 1) : null;
    }

    public Position down1() {
        return (y > 1) ? of(x, y - 1) : null;
    }

    public Position left1() {
        return (x > 1) ? of(x - 1, y) : null;
    }

    public Position right1() {
        return (x < 8) ? of(x + 1, y) : null;
    }

    public Position diagUr1() {
        return move(Position::up1, Position::right1);
    }

    public Position diagUl1() {
        return move(Position::up1, Position::left1);
    }

    public Position diagDr1() {
        return move(Position::down1, Position::right1);
    }

    public Position diagDl1() {
        return move(Position::down1, Position::left1);
    }

    public List<Position> knight() {
        List<Position> moves = new LinkedList<>();
        moves.add(move(Position::up1, Position::up1, Position::right1));
        moves.add(move(Position::up1, Position::up1, Position::left1));
        moves.add(move(Position::down1, Position::down1, Position::right1));
        moves.add(move(Position::down1, Position::down1, Position::left1));
        moves.add(move(Position::right1, Position::right1, Position::up1));
        moves.add(move(Position::right1, Position::right1, Position::down1));
        moves.add(move(Position::left1, Position::left1, Position::up1));
        moves.add(move(Position::left1, Position::left1, Position::down1));

        moves.removeIf(Objects::isNull);
        return moves;
    }

    public List<Position> king() {
        List<Position> moves = new LinkedList<>();
        moves.add(up1());
        moves.add(down1());
        moves.add(left1());
        moves.add(right1());
        moves.add(diagUl1());
        moves.add(diagUr1());
        moves.add(diagDl1());
        moves.add(diagDr1());

        moves.removeIf(Objects::isNull);
        return moves;
    }

    public Set<Position> moveUntilHit(List<Stream<Position>> moveLines, ChessGame game, PieceColor pieceColor) {
        Set<Position> validMoves = new HashSet<>();
        moveLines.forEach(ml -> {
            for (Position to : (Iterable<Position>) ml::iterator) {
                Optional<Piece> pieceAtPosition = game.at(to);

                if(pieceAtPosition.isEmpty() || pieceAtPosition.get().getPieceColor() != pieceColor) {
                    validMoves.add(to);
                }
                if(pieceAtPosition.isPresent()) {
                    break;
                }
            }
        });

        return validMoves;
    }

    public List<Stream<Position>> rock() {
        return Arrays.asList(up(), down(), left(), right());
    }

    public List<Stream<Position>> bishop() {
        return Arrays.asList(diagDl(), diagDr(), diagUl(), diagUr());
    }

    public List<Stream<Position>> queen() {
        List<Stream<Position>> moveLines = new ArrayList<>();

        moveLines.addAll(rock());
        moveLines.addAll(bishop());

        return moveLines;
    }

    public Stream<Position> up() {
        return Stream.iterate(this, Objects::nonNull, Position::up1).skip(1);
    }

    public Stream<Position> down() {
        return Stream.iterate(this, Objects::nonNull, Position::down1).skip(1);
    }

    public Stream<Position> right() {
        return Stream.iterate(this, Objects::nonNull, Position::right1).skip(1);
    }

    public Stream<Position> left() {
        return Stream.iterate(this, Objects::nonNull, Position::left1).skip(1);
    }


    public Stream<Position> diagUr() {
        return Stream.iterate(this, Objects::nonNull, Position::diagUr1).skip(1);
    }

    public Stream<Position> diagUl() {
        return Stream.iterate(this, Objects::nonNull, Position::diagUl1).skip(1);
    }

    public Stream<Position> diagDl() {
        return Stream.iterate(this, Objects::nonNull, Position::diagDl1).skip(1);
    }

    public Stream<Position> diagDr() {
        return Stream.iterate(this, Objects::nonNull, Position::diagDr1).skip(1);
    }



    @SafeVarargs
    private Position move(UnaryOperator<Position>... moves) {
        Position finalPosition = this;
        for (int i = 0; i < moves.length && (finalPosition != null); i++) {
            UnaryOperator<Position> move = moves[i];
            finalPosition = move.apply(finalPosition);
        }
        return finalPosition;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x &&
                y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return stringValue();
    }
}
