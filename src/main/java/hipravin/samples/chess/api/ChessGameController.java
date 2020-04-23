package hipravin.samples.chess.api;

import hipravin.samples.chess.GameManager;
import hipravin.samples.chess.api.model.*;
import hipravin.samples.chess.engine.InvalidMoveException;
import hipravin.samples.chess.engine.model.GamePlayerDesc;
import hipravin.samples.chess.engine.model.PieceColor;
import hipravin.samples.chess.repository.ChessGameMetadata;
import hipravin.samples.chess.repository.GameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/game")
public class ChessGameController {
    private final Logger log = LoggerFactory.getLogger(ChessGameController.class);
    private final GameManager gameManager;

    public ChessGameController(GameManager gameManager) {
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
        log.debug("move {} {} {}", id, token, moveDto);

        ChessGameMetadata chessGameMetadata = gameManager.applyMove(id, token, moveDto);
        if(chessGameMetadata.getChessGame().isFinished()) {
            log.info("game finished {}", chessGameMetadata.getChessGame().getStatus());
        }
        return chessGameMetadata.getGameStateDtoForPlayer(token);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> join(@PathVariable("id") String id) {
        log.debug("join {} ", id);

        ChessGameMetadata metadata = gameManager.joinGame(id);
        String blackPlayerToken = metadata.getPlayerTokens().get(PieceColor.BLACK);
        return ResponseEntity.ok(new GameConnectionParamsDto(id, blackPlayerToken,
                ColorDto.BLACK, metadata.getGameStateDtoForPlayer(blackPlayerToken)));
    }

    @PostMapping("/host")
    public ResponseEntity<?> host() {
        ChessGameMetadata metadata = gameManager.newGame();
        String whitePlayerToken = metadata.getPlayerTokens().get(PieceColor.WHITE);

        log.debug("host {} {}", metadata.getId(), whitePlayerToken);
        return ResponseEntity.ok(new GameConnectionParamsDto(metadata.getId(), whitePlayerToken,
                ColorDto.WHITE, metadata.getGameStateDtoForPlayer(whitePlayerToken)));
    }

    //supposed to be long polling
    @PostMapping("{id}/wait-for-my-move")
    public DeferredResult<?> waitForMove(@RequestHeader("ptoken") String playerToken,
                                                    @PathVariable("id") String id) {
        log.debug("await move {} {}", id, playerToken);

        DeferredResult<Object> deferredResult = new DeferredResult<>(TimeUnit.MINUTES.toMillis(60));
        ChessGameMetadata chessGameMetadata = gameManager.findOrThrow(id);

        deferredResult.onTimeout(() -> {
            deferredResult.setResult(new ResponseEntity<String>(HttpStatus.REQUEST_TIMEOUT));
            log.warn("await move timeout {} {}", id, playerToken);
        });

        if (chessGameMetadata.currentPlayerToken().equals(playerToken)
                && chessGameMetadata.isSecondPlayerJoined()) {
            deferredResult.setResult(chessGameMetadata.getGameStateDtoForPlayer(playerToken));
            log.debug("await move immediate response {} {}", id, playerToken);
        } else {
            gameManager.awaitOpponentMove(new GamePlayerDesc(id, playerToken), m -> {
                deferredResult.setResult(m.getGameStateDtoForPlayer(playerToken));
                log.debug("await move completed {} {}", id, playerToken);
            });
        }

        return deferredResult;
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<?> handleNotFoind() {
        return new ResponseEntity<Object>("game not found", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidMoveException.class)
    public ResponseEntity<?> handleInvalidMove() {
        return new ResponseEntity<Object>("bad move", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken() {
        return new ResponseEntity<Object>("bad token", new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

}
