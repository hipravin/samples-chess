package hipravin.samples.chess.api;

import hipravin.samples.chess.GameManager;
import hipravin.samples.chess.api.model.*;
import hipravin.samples.chess.engine.model.PieceColor;
import hipravin.samples.chess.repository.ChessGameMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api/game")
public class ChessGameService {
    private final GameManager gameManager;

    private final Map<String, DeferredResult<GameStateDto>> waitRequests = new ConcurrentHashMap<>();
    private SubmissionPublisher<PlayerAction> playerActionPublisher = new SubmissionPublisher<>();

    public ChessGameService(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @GetMapping("/sample")
    public GameStateDto gameStateSample() {
        GameStateDto gameStateDto = new GameStateDto();
        gameStateDto.setCurrentPlayer(ColorDto.WHITE);
        PieceDto pawn1 = new PieceDto();
        pawn1.setColor(ColorDto.WHITE);
        pawn1.setPieceType(PieceTypeDto.PAWN);
        pawn1.setPosition("e2");
        pawn1.setValidMoves(Arrays.asList("e3", "e4"));

        PieceDto pawnBlack = new PieceDto();
        pawnBlack.setColor(ColorDto.BLACK);
        pawnBlack.setPieceType(PieceTypeDto.PAWN);
        pawnBlack.setPosition("f7");

        gameStateDto.setPieces(Arrays.asList(pawn1, pawnBlack));

        return gameStateDto;
    }

    @PostMapping("/{id}/move")
    public GameStateDto makeMove(@RequestHeader("ptoken") String token,
                                 @PathVariable("id") String id,
                                 @RequestBody MoveDto moveDto) {
        ChessGameMetadata chessGameMetadata = gameManager.applyMove(id,  token, moveDto);
        return chessGameMetadata.getGameStateDtoForPlayer(token);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> join(@PathVariable("id") String id) {
        ChessGameMetadata metadata = gameManager.joinGame(id);
        String blackPlayerToken = metadata.getPlayerTokens().get(PieceColor.BLACK);
        return  ResponseEntity.ok(new GameConnectionParams(id, blackPlayerToken, metadata.getGameStateDtoForPlayer(blackPlayerToken)));
    }

    @PostMapping("/host")
    public ResponseEntity<?> host() {
        ChessGameMetadata metadata = gameManager.newGame();
        String whitePlayerToken = metadata.getPlayerTokens().get(PieceColor.WHITE);
        return ResponseEntity.ok(new GameConnectionParams(metadata.getId(), whitePlayerToken,
                metadata.getGameStateDtoForPlayer(whitePlayerToken)));
    }

    //supposed to be long polling
    @PostMapping("{id}/wait-for-my-move/")
    public DeferredResult<GameStateDto> waitForMove(@RequestHeader("ptoken") String playerToken,
                                                    @PathVariable("id") String id) {
        DeferredResult<GameStateDto> deferredResult = new DeferredResult<>();
        Optional<ChessGameMetadata> chessGameMetadata = gameManager.getGameRepository().find(id);
        if(chessGameMetadata.isPresent()
                && chessGameMetadata.get().currentPlayerToken().equals(playerToken)
                && chessGameMetadata.get().isSecondPlayerJoined()) {
            deferredResult.setResult(chessGameMetadata.get().getGameStateDtoForPlayer(playerToken));
        } else {
            gameManager.awaitOpponentMove(id, m -> {
                deferredResult.setResult(m.getGameStateDtoForPlayer(playerToken));
            });
        }

        return deferredResult;
    }

    /**
     * Returns current state of game for specific player.
     *
     */
    @GetMapping("/{id}")
    public GameStateDto gameState() {
        return null;
    }
}
