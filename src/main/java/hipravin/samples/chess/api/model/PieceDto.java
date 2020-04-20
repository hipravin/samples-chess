package hipravin.samples.chess.api.model;

import java.util.List;

public class PieceDto {
    private String position;
    private ColorDto color;
    private PieceTypeDto pieceType;

    private List<String> validMoves;
//    List<SquareDto> validAttackMoves;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public List<String> getValidMoves() {
        return validMoves;
    }

    public void setValidMoves(List<String> validMoves) {
        this.validMoves = validMoves;
    }
}
