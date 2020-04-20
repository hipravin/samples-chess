package hipravin.samples.chess.api;

import hipravin.samples.chess.GameException;

public class InvalidTokenException extends GameException {
    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
