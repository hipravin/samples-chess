package hipravin.samples.chess.engine.model;

import java.util.Objects;

public class GamePlayerDesc {

    private final String gameId;
    private final String playerToken;

    public GamePlayerDesc(String gameId, String playerToken) {
        this.gameId = gameId;
        this.playerToken = playerToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlayerDesc that = (GamePlayerDesc) o;
        return Objects.equals(gameId, that.gameId) &&
                Objects.equals(playerToken, that.playerToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, playerToken);
    }

    public String getGameId() {
        return gameId;
    }

    public String getPlayerToken() {
        return playerToken;
    }
}
