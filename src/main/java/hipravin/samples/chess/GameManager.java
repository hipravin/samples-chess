package hipravin.samples.chess;

import hipravin.samples.chess.api.InvalidTokenException;
import hipravin.samples.chess.api.model.MoveDto;
import hipravin.samples.chess.engine.model.GamePlayerDesc;
import hipravin.samples.chess.engine.model.PieceMove;
import hipravin.samples.chess.repository.ChessGameMetadata;
import hipravin.samples.chess.repository.GameNotFoundException;
import hipravin.samples.chess.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Service
public class GameManager {
    private final Logger log = LoggerFactory.getLogger(GameManager.class);

    private final GameRepository gameRepository;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final SubmissionPublisher<ChessGameMetadata> gameUpdatesPublisher = new SubmissionPublisher<>(executor, Integer.MAX_VALUE);
    private final ConcurrentMap<GamePlayerDesc, Consumer<ChessGameMetadata>> handlers = new ConcurrentHashMap<>();

    public GameManager(GameRepository gameRepository) {
        this.gameRepository = gameRepository;

        GameUpdatesSubscriber subscriber = new GameUpdatesSubscriber();
        gameUpdatesPublisher.subscribe(subscriber);
    }

    public ChessGameMetadata newGame() {
        return gameRepository.newGame();
    }

    public ChessGameMetadata findOrThrow(String id) {
        return gameRepository.find(id).orElseThrow(() -> new GameNotFoundException("Game not found: " + id));
    }

    public ChessGameMetadata joinGame(String id) {
        ChessGameMetadata chessGameMetadata = findOrThrow(id);
        chessGameMetadata.setSecondPlayerJoined(true);
        gameRepository.save(chessGameMetadata);
        gameUpdatesPublisher.submit(chessGameMetadata);
        return chessGameMetadata;
    }

    public ChessGameMetadata applyMove(String id, String token, MoveDto moveDto) {
        ChessGameMetadata chessGameMetadata = findOrThrow(id);
        if (!chessGameMetadata.currentPlayerToken().equals(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        chessGameMetadata.applyMove(PieceMove.of(moveDto));
        gameRepository.save(chessGameMetadata);

        log.debug("Submit called {} {} {}", id, token, moveDto);
        gameUpdatesPublisher.submit(chessGameMetadata);
        return chessGameMetadata;
    }

    public void awaitOpponentMove(GamePlayerDesc gamePlayerDesc, Consumer<ChessGameMetadata> handler) {

        handlers.put(gamePlayerDesc, handler);
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
            log.debug("On next {} {} {}", metadata.getId(), metadata.currentPlayerToken(), metadata.getChessGame().getCurrentPlayer());

            GamePlayerDesc gamePlayerDesc = metadata.currentPlayerDesc();
            Optional.ofNullable(handlers.remove(gamePlayerDesc)).ifPresent(h -> {
                log.debug("Handlers present {} {} {}", metadata.getId(), metadata.currentPlayerToken(), metadata.getChessGame().getCurrentPlayer());
                h.accept(metadata);
            });

            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }

        @Override
        public void onComplete() {

        }
    }
}
