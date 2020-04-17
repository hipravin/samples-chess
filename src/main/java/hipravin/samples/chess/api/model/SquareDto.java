package hipravin.samples.chess.api.model;

public class SquareDto {
    //e.g a1 e2
    private String pos;

    public SquareDto() {
    }

    public SquareDto(String pos) {
        this.pos = pos;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }
}
