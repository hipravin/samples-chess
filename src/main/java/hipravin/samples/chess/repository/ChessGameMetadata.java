package hipravin.samples.chess.repository;

import hipravin.samples.chess.api.model.GameStateDto;
import hipravin.samples.chess.engine.ChessGame;
import hipravin.samples.chess.engine.model.GamePlayerDesc;
import hipravin.samples.chess.engine.model.PieceColor;
import hipravin.samples.chess.engine.model.PieceMove;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class ChessGameMetadata {
    private String id;
    private Map<PieceColor, String> playerTokens = generateTokens();
    private ChessGame chessGame;
    private OffsetDateTime whenCreated = OffsetDateTime.now();
    private boolean secondPlayerJoined = false;

    public GameStateDto getGameStateDtoForPlayer(String token) {
        boolean forCurrentPlayer = currentPlayerToken().equals(token);

        GameStateDto gameStateDto = new GameStateDto();
        gameStateDto.setMyTurn(forCurrentPlayer);
        gameStateDto.setCurrentPlayer(chessGame.getCurrentPlayer().toColorDto());

        gameStateDto.setPieces(chessGame.calculatePieceDtos(forCurrentPlayer));
        if(!chessGame.getPreviousMoves().isEmpty()) {
            gameStateDto.setLastOpponentMove(
                    chessGame.getPreviousMoves().get(chessGame.getPreviousMoves().size() - 1).toDto());
        }

        switch(chessGame.getStatus()) {
            case CHECK:
                gameStateDto.setCheck(true);
                break;
            case CHECKMATE:
                gameStateDto.setGameFinished(true);
                gameStateDto.setGameFinishedReason("Checkmate! " + chessGame.getCurrentPlayer().negate() + " wins!");
                break;
            case DRAW_STALEMATE:
                gameStateDto.setGameFinished(true);
                gameStateDto.setGameFinishedReason("Game finished with a stalemate deaw!");
                break;
            case NORMAL:
                break;
        }

        return gameStateDto;
    }

    public GamePlayerDesc currentPlayerDesc() {
        return new GamePlayerDesc(id, currentPlayerToken());
    }
    public GamePlayerDesc waintingPlayerDesc() {
        return new GamePlayerDesc(id, waitingPlayerToken());
    }

    public void applyMove(PieceMove pieceMove) {
        chessGame = chessGame.applyMove(pieceMove);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<PieceColor, String> getPlayerTokens() {
        return playerTokens;
    }

    public void setPlayerTokens(Map<PieceColor, String> playerTokens) {
        this.playerTokens = playerTokens;
    }

    public ChessGame getChessGame() {
        return chessGame;
    }

    public void setChessGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    private Map<PieceColor, String>  generateTokens() {
        return Map.of(
                PieceColor.WHITE, UUID.randomUUID().toString(),
                PieceColor.BLACK, UUID.randomUUID().toString());
    }

    public String currentPlayerToken() {
        return playerTokens.get(chessGame.getCurrentPlayer());
    }

    public String waitingPlayerToken() {
        return playerTokens.get(chessGame.getCurrentPlayer().negate());
    }

    public void setWhenCreated(OffsetDateTime whenCreated) {
        this.whenCreated = whenCreated;
    }

    public OffsetDateTime getWhenCreated() {
        return whenCreated;
    }

    public boolean isSecondPlayerJoined() {
        return secondPlayerJoined;
    }

    public void setSecondPlayerJoined(boolean secondPlayerJoined) {
        this.secondPlayerJoined = secondPlayerJoined;
    }
}
