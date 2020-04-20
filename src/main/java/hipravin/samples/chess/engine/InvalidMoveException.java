package hipravin.samples.chess.engine;

import hipravin.samples.chess.GameException;

/**
 * Game process is designed to check all moves in advance and UI should never send impossible move
 */
public class InvalidMoveException extends GameException {

    public InvalidMoveException() {
        super();
    }

    public InvalidMoveException(String message) {
        super(message);
    }
}
