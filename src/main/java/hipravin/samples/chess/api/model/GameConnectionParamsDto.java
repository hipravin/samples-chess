package hipravin.samples.chess.api.model;

public class GameConnectionParamsDto {
    private String id;
    private String token;
    private ColorDto playerSide;

    private GameStateDto gameState;

    public GameConnectionParamsDto() {
    }

    public GameConnectionParamsDto(String id, String token, ColorDto playerSide, GameStateDto gameState) {
        this.id = id;
        this.token = token;
        this.playerSide = playerSide;
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

    public ColorDto getPlayerSide() {
        return playerSide;
    }

    public void setPlayerSide(ColorDto playerSide) {
        this.playerSide = playerSide;
    }
}
