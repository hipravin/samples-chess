package hipravin.samples.chess.api.model;

public class GameConnectionParamsDto {
    private String id;
    private String token;

    private GameStateDto gameState;

    public GameConnectionParamsDto() {
    }

    public GameConnectionParamsDto(String id, String token, GameStateDto gameState) {
        this.id = id;
        this.token = token;
        this.gameState = gameState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public GameStateDto getGameState() {
        return gameState;
    }

    public void setGameState(GameStateDto gameState) {
        this.gameState = gameState;
    }
}
