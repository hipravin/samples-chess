package hipravin.samples.chess.api.model;

import java.util.List;

public class GameStateDto {

    private List<PieceDto> pieces;
    private ColorDto currentPlayer;
    private MoveDto lastOpponentMove;
    private int moveNumber = 1;

    public List<PieceDto> getPieces() {
        return pieces;
    }

    public void setPieces(List<PieceDto> pieces) {
        this.pieces = pieces;
    }

    public ColorDto getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(ColorDto currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public MoveDto getLastOpponentMove() {
        return lastOpponentMove;
    }

    public void setLastOpponentMove(MoveDto lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
    }
}
