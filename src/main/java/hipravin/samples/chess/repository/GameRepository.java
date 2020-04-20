package hipravin.samples.chess.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface GameRepository {
    ChessGameMetadata newGame();
    Optional<ChessGameMetadata> find(String id);
    void save(String id, ChessGameMetadata chessGameMetadata);

    /**
     * for housekeeping
     */
    void deleteOlderThan(OffsetDateTime boundary);
}
