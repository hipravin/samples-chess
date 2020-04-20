package hipravin.samples.chess.api.model;

public class MoveDto {
    /**
     * e.g "e2"
     */
    private String from;
    private String to;
    /**
     * Applicable only to pawn promotion when reaching opponent's edge
     */
    private String promotion;

    public MoveDto() {
    }

    public MoveDto(String from, String to, String promotion) {
        this.from = from;
        this.to = to;
        this.promotion = promotion;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }
}
