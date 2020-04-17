package hipravin.samples.chess.api.model;

import java.util.List;

public class PieceDto {
    private SquareDto square;
    private ColorDto color;
    private PieceTypeDto pieceType;

    private List<SquareDto> validMoves;
//    List<SquareDto> validAttackMoves;

    public SquareDto getSquare() {
        return square;
    }

    public void setSquare(SquareDto square) {
        this.square = square;
    }

    public ColorDto getColor() {
        return color;
    }

    public void setColor(ColorDto color) {
        this.color = color;
    }

    public PieceTypeDto getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceTypeDto pieceType) {
        this.pieceType = pieceType;
    }

    public List<SquareDto> getValidMoves() {
        return validMoves;
    }

    public void setValidMoves(List<SquareDto> validMoves) {
        this.validMoves = validMoves;
    }
}
