package hipravin.samples.chess.api.model;

import java.util.List;

public class GameStateDto {

    private List<PieceDto> pieces;
    private ColorDto currentPlayer;
    private MoveDto lastOpponentMove;

    private boolean myTurn = false;
    private boolean gameFinished = false;
    private boolean check = false;
    private ColorDto winner = null;
    private String gameFinishedReason = null;

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

    public MoveDto getLastOpponentMove() {
        return lastOpponentMove;
    }

    public void setLastOpponentMove(MoveDto lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public String getGameFinishedReason() {
        return gameFinishedReason;
    }

    public void setGameFinishedReason(String gameFinishedReason) {
        this.gameFinishedReason = gameFinishedReason;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public ColorDto getWinner() {
        return winner;
    }

    public void setWinner(ColorDto winner) {
        this.winner = winner;
    }
}
