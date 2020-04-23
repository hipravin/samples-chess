package hipravin.samples.chess.api;

import hipravin.samples.chess.api.model.GameConnectionParamsDto;
import hipravin.samples.chess.api.model.GameStateDto;
import hipravin.samples.chess.api.model.MoveDto;
import hipravin.samples.chess.engine.ChessGameTest;
import hipravin.samples.chess.engine.model.PieceMove;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChessGameControllerTest {
    public static final int TIMEOUT = 500;

    ExecutorService cfPool = Executors.newFixedThreadPool(12, r -> new Thread(r, "completable future pool"));

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Disabled
    void dksyMultith() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(4, r -> new Thread(r, "run pool #" + counter.getAndIncrement()));

        for (int i = 0; i < 100; i++) {
            pool.submit(() -> {
                try {
                    testDetskyMat();
                } catch (ExecutionException | InterruptedException | TimeoutException e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(100_000);
    }

    @RepeatedTest(10)
    void testDetskyMat() throws ExecutionException, InterruptedException, TimeoutException {
        List<MoveDto> whiteMoves = ChessGameTest.movesFromString("e2e4 d1h5 f1c4 h5f7")
                .map(PieceMove::toDto).collect(Collectors.toList());
        List<MoveDto> blackMoves = ChessGameTest.movesFromString("e7e5 b8c6 g8f6")
                .map(PieceMove::toDto).collect(Collectors.toList());

        GameConnectionParamsDto white = host();
        getQuietly(awaitMove(white.getId(), white.getToken()), 1);

        join(host().getId());
        join(host().getId());//couple other players

        //we can do this request one more time, no issue
        GameConnectionParamsDto black = join(white.getId());

        CompletableFuture<GameStateDto> whiteMoveseq = CompletableFuture.supplyAsync(() ->
                moveSequence(white.getId(), white.getToken(), whiteMoves), cfPool);
        CompletableFuture<GameStateDto> blackMoveseq = CompletableFuture.supplyAsync(() ->
                moveSequence(black.getId(), black.getToken(), blackMoves), cfPool);

        GameStateDto whiteFinal = whiteMoveseq.get(TIMEOUT * 2, TimeUnit.SECONDS);
        GameStateDto blackFinal = blackMoveseq.get(TIMEOUT * 2, TimeUnit.SECONDS);

        assertTrue(whiteFinal.isGameFinished());
        assertTrue(blackFinal.isGameFinished());

        assertEquals("Checkmate! WHITE wins!", whiteFinal.getGameFinishedReason());
        assertEquals("Checkmate! WHITE wins!", blackFinal.getGameFinishedReason());
    }

    private GameStateDto moveSequence(String id, String token, List<MoveDto> moves) {
        GameStateDto state = null;
        for (MoveDto move : moves) {
            try {
                state = awaitMove(id, token).get(TIMEOUT, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                throw new AssertionFailedError(e.getMessage());
            }
            assertTrue(state.isMyTurn());
            state = move(id, token, move);
            assertFalse(state.isMyTurn());
        }

        if(state != null && !state.isGameFinished()) {
            try {
                state = awaitMove(id, token).get(TIMEOUT, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new AssertionFailedError(e.getMessage());
            }
        }

        return state;
    }

    @Test
    public void testGameNotFoundJoin() {
        ResponseEntity<String> badJoin =
                restTemplate.postForEntity("http://localhost:" + port + "/api/game/broken-id/join", "", String.class);

        assertEquals(HttpStatus.NOT_FOUND, badJoin.getStatusCode());
        assertNotNull(badJoin.getBody());
        assertTrue(badJoin.getBody().contains("not found"));
    }

    @Test
    public void testGameNotFoundWait() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("ptoken", "broken-token");
        HttpEntity<String> request = new HttpEntity<>("", httpHeaders);

        ResponseEntity<String> badJoin =
                restTemplate.postForEntity("http://localhost:" + port + "/api/game/broken-id/wait-for-my-move", request, String.class);

        assertEquals(HttpStatus.NOT_FOUND, badJoin.getStatusCode());
        assertNotNull(badJoin.getBody());
        assertTrue(badJoin.getBody().contains("not found"));
    }

    private GameConnectionParamsDto host() {
        return post("/host", Optional.empty(), "", GameConnectionParamsDto.class);
    }

    private GameConnectionParamsDto join(String id) {
        return post(id + "/join", Optional.empty(), "", GameConnectionParamsDto.class);
    }

    private CompletableFuture<GameStateDto> awaitMove(String id, String pheader) {
        return CompletableFuture.supplyAsync(() ->
                post(id + "/wait-for-my-move", Optional.of(pheader), "", GameStateDto.class), cfPool);
    }

    private GameStateDto move(String id, String pheader, MoveDto moveDto) {
        return post(id + "/move", Optional.of(pheader), moveDto, GameStateDto.class);
    }

    private <T, R> T post(String subUrl, Optional<String> pheader, R body, Class<T> clazz) {
        HttpHeaders httpHeaders = new HttpHeaders();
        pheader.ifPresent(h -> {
            httpHeaders.add("ptoken", h);
        });

        HttpEntity<R> request = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<T> responseEntity =
                restTemplate.postForEntity("http://localhost:" + port + "/api/game/" + subUrl, request, clazz);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        return responseEntity.getBody();
    }

    private <T> T getQuietly(CompletableFuture<T> completableFuture, long sec) {
        try {
            return completableFuture.get(sec, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
             return null;
        }
    }
}