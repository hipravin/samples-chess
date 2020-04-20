package hipravin.samples.chess.engine.model;

import hipravin.samples.chess.engine.model.piece.*;

public enum Type {
    BISHOP {
        @Override
        public Bishop newPiece(Position position, PieceColor pieceColor) {
            return new Bishop(position, pieceColor);
        }
    },
    KING {
        @Override
        public King newPiece(Position position, PieceColor pieceColor) {
            return new King(position, pieceColor);
        }
    },
    KNIGHT {
        @Override
        public Knight newPiece(Position position, PieceColor pieceColor) {
            return new Knight(position, pieceColor);
        }
    },
    PAWN {
        @Override
        public Pawn newPiece(Position position, PieceColor pieceColor) {
            return new Pawn(position, pieceColor);
        }
    },
    QUEEN {
        @Override
        public Queen newPiece(Position position, PieceColor pieceColor) {
            return new Queen(position, pieceColor);
        }
    },
    ROCK {
        @Override
        public Rock newPiece(Position position, PieceColor pieceColor) {
            return new Rock(position, pieceColor);
        }
    };

    public Piece newPiece(Position position, PieceColor pieceColor) {
        throw new UnsupportedOperationException();
    }
}
