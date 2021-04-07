package edu.neu.madcourse.fastit;

public enum FastingCycle {
    THIRTY_SECOND_CYCLE(11),
    TWELVE_HOUR_CYCLE(1),
    FOURTEEN_HOUR_CYCLE(2),
    SIXTEEN_HOUR_CYCLE(3),
    EIGHTEEN_HOUR_CYCLE(4),
    TWENTY_HOUR_CYCLE(5),
    TWENTY_TWO_HOUR_CYCLE(6),
    TWENTY_FOUR_HOUR_CYCLE(7),
    INVALID_CYCLE(-1);

    private final int id;

    FastingCycle(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
