package hipravin.samples.chess.engine.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void testKnight1() {
        Position p = Position.of(1,1);

        List<Position> kn = p.knight();
        assertEquals(2, kn.size());
        assertTrue(kn.contains(Position.of(3,2)));
        assertTrue(kn.contains(Position.of(2,3)));
    }

    @Test
    void testKnight2() {
        Position p = Position.of(4,3);

        Set<Position> kn = new HashSet<>(p.knight());
        assertEquals(8, kn.size());
    }

    @Test
    void testRock() {
        Position p = Position.of(5,5);

        Set<Position> rock = p.rock().stream().flatMap(s -> s).collect(Collectors.toSet());
        assertEquals(14, rock.size());
    }
    @Test
    void testRock2() {
        Position p = Position.of(1,1);

        Set<Position> rock = p.rock().stream().flatMap(s -> s).collect(Collectors.toSet());
        assertEquals(14, rock.size());
    }

    @Test
    void testConvert() {
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                 assertEquals(Position.of(x, y), Position.of(Position.of(x, y).stringValue()));
            }
        }
    }
}