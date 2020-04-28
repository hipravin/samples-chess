package hipravin.samples.chess.repository;

import hipravin.samples.chess.engine.ChessGame;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {

    private Map<String, ChessGameMetadata> metadataMap = new ConcurrentHashMap<>();

    @Override
    public ChessGameMetadata newGame() {
        ChessGame chessGame = ChessGame.startGame();
        ChessGameMetadata chessGameMetadata = new ChessGameMetadata();
        chessGameMetadata.setChessGame(chessGame);
        chessGameMetadata.setId(generateId());

        metadataMap.put(chessGameMetadata.getId(), chessGameMetadata);
        return chessGameMetadata;
    }

    @Override
    public Optional<ChessGameMetadata> find(String id) {
        return Optional.ofNullable(metadataMap.get(id));
    }

    @Override
    public void save(ChessGameMetadata chessGameMetadata) {
        metadataMap.put(chessGameMetadata.getId(), chessGameMetadata);
    }



    @Override
    public void deleteOlderThan(OffsetDateTime boundary) {
        metadataMap.entrySet().removeIf(e -> e.getValue().getWhenCreated().isBefore(boundary));
    }

    private String generateId() {
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (find(id).isPresent());

        return  id;
    }
}
