package hipravin.samples.chess.api.model;

import java.util.List;

public class GameStateDto {

    private List<PieceDto> pieces;
    private ColorDto myColor;
    private ColorDto currentPlayer;

    public List<PieceDto> getPieces() {
        return pieces;
    }

    public void setPieces(List<PieceDto> pieces) {
        this.pieces = pieces;
    }

    public ColorDto getMyColor() {
        return myColor;
    }

    public void setMyColor(ColorDto myColor) {
        this.myColor = myColor;
    }

    public ColorDto getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(ColorDto currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
