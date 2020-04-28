package hipravin.samples.chess.engine.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void testKnight1() {
        Position p = Position.of(1, 1);

        List<Position> kn = p.knight();
        assertEquals(2, kn.size());
        assertTrue(kn.contains(Position.of(3, 2)));
        assertTrue(kn.contains(Position.of(2, 3)));
    }

    @Test
    void testKnight2() {
        Position p = Position.of(4, 3);

        Set<Position> kn = new HashSet<>(p.knight());
        assertEquals(8, kn.size());
    }

    @Test
    void testRock() {
        Position p = Position.of(5, 5);

        Set<Position> rock = p.rock().stream().flatMap(s -> s).collect(Collectors.toSet());
        assertEquals(14, rock.size());
    }

    @Test
    void testRock2() {
        Position p = Position.of(1, 1);

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


    @Test
    void testTricky() {
        assertEquals(8, tricky(5,5).size());
    }

    private List<Position> tricky(int x1, int y1) {
        List<Position> result = new LinkedList<>();

        for (int x2 = 1; x2 <=8 ; x2++) {
            for (int y2 = 1; y2 <= 8; y2++) {
                int mx = Math.abs(x1 - x2);
                int my = Math.abs(y1 - y2);
                if( (mx + my == 3) && (mx * my == 2)
                        && Math.abs(x2 - 4.5) < 4.5
                        && Math.abs(y2 - 4.5) < 4.5) {
                    result.add(Position.of(x2,y2));
                }
            }
        }

        return result;
    }
}