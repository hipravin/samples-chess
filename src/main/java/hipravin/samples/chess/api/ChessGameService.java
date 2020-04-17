package hipravin.samples.chess.api;

import hipravin.samples.chess.api.model.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/game")
public class ChessGameService {
    @GetMapping("/sample")
    public GameStateDto gameStateSample() {
        GameStateDto gameStateDto = new GameStateDto();
        gameStateDto.setCurrentPlayer(ColorDto.WHITE);
        gameStateDto.setMyColor(ColorDto.WHITE);
        PieceDto pawn1 = new PieceDto();
        pawn1.setColor(ColorDto.WHITE);
        pawn1.setPieceType(PieceTypeDto.PAWN);
        pawn1.setSquare(new SquareDto("e2"));
        pawn1.setValidMoves(Arrays.asList(new SquareDto("e3"), new SquareDto("e4")));

        PieceDto pawnBlack = new PieceDto();
        pawnBlack.setColor(ColorDto.BLACK);
        pawnBlack.setPieceType(PieceTypeDto.PAWN);
        pawnBlack.setSquare(new SquareDto("f7"));

        gameStateDto.setPieces(Arrays.asList(pawn1, pawnBlack));

        return gameStateDto;
    }

    @GetMapping("/game/{id}")
    public GameStateDto gameState() {
        return null;
    }

    @PostMapping("/game/{id}")
    public GameStateDto makeMove(Object move) {
        return null;
    }

}
