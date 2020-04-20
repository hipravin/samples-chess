package hipravin.samples.chess.repository;

import hipravin.samples.chess.api.model.GameStateDto;
import hipravin.samples.chess.engine.ChessGame;
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
        GameStateDto gameStateDto = new GameStateDto();
        gameStateDto.setCurrentPlayer(chessGame.getCurrentPlayer().toColorDto());

        gameStateDto.setPieces(chessGame.calculatePieceDtos());
        if(!chessGame.getPreviousMoves().isEmpty()) {
            gameStateDto.setLastOpponentMove(
                    chessGame.getPreviousMoves().get(chessGame.getPreviousMoves().size() - 1).toDto());
        }

//        chessGame.getBoard().pieceMap(chessGame.getCurrentPlayer()).values()

//        gameStateDto.setPieces(chessGame.);

        return gameStateDto;
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
