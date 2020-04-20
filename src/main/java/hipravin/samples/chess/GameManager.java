package hipravin.samples.chess;

import hipravin.samples.chess.api.InvalidTokenException;
import hipravin.samples.chess.api.model.MoveDto;
import hipravin.samples.chess.engine.model.PieceMove;
import hipravin.samples.chess.repository.ChessGameMetadata;
import hipravin.samples.chess.repository.GameNotFoundException;
import hipravin.samples.chess.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

@Service
public class GameManager {
    private final GameRepository gameRepository;
    private final SubmissionPublisher<ChessGameMetadata> gameUpdatesPublisher = new SubmissionPublisher<>();
    private final Map<String, Consumer<ChessGameMetadata>> handlers = new ConcurrentHashMap<>();

    public GameManager(GameRepository gameRepository) {
        this.gameRepository = gameRepository;

        GameUpdatesSubscriber subscriber = new GameUpdatesSubscriber();
        gameUpdatesPublisher.subscribe(subscriber);
    }

    public ChessGameMetadata newGame() {
        return gameRepository.newGame();
    }

    public ChessGameMetadata joinGame(String id) {
        ChessGameMetadata chessGameMetadata = gameRepository.find(id).orElseThrow(() -> new GameNotFoundException("Game not found: " + id));
        gameUpdatesPublisher.submit(chessGameMetadata);

        return chessGameMetadata;
    }

    public ChessGameMetadata applyMove(String id, String token, MoveDto moveDto) {
        ChessGameMetadata chessGameMetadata = gameRepository.find(id).orElseThrow(() -> new GameNotFoundException("Game not found: " + id));
        if(!chessGameMetadata.currentPlayerToken().equals(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        chessGameMetadata.applyMove(PieceMove.of(moveDto));
        gameRepository.save(id, chessGameMetadata);

        gameUpdatesPublisher.submit(chessGameMetadata);
        return chessGameMetadata;
    }

    public void awaitOpponentMove(String gameId, Consumer<ChessGameMetadata> handler) {
        handlers.put(gameId, handler);
    }

    public GameRepository getGameRepository() {
        return gameRepository;
    }

    class GameUpdatesSubscriber implements Flow.Subscriber<ChessGameMetadata> {
        private Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(ChessGameMetadata metadata) {
            if(handlers.containsKey(metadata.getId())) {
                handlers.get(metadata.getId()).accept(metadata);
                handlers.remove(metadata.getId());
            }

            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {

        }
    }
}
