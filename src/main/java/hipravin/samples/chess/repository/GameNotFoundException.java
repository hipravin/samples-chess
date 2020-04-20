package hipravin.samples.chess.repository;

import hipravin.samples.chess.GameException;

public class GameNotFoundException extends GameException {
    public GameNotFoundException() {
        super();
    }

    public GameNotFoundException(String message) {
        super(message);
    }
}
