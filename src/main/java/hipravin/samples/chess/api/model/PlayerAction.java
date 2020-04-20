package hipravin.samples.chess.api.model;

public class PlayerAction {
    private final String gameId;
    private final String token;



    public PlayerAction(String gameId, String token) {
        this.gameId = gameId;
        this.token = token;
    }

    public String getGameId() {
        return gameId;
    }

    public String getToken() {
        return token;
    }
}
