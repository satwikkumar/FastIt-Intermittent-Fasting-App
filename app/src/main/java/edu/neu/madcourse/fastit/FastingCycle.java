package edu.neu.madcourse.fastit;

public enum FastingCycle {
    THIRTY_SECOND_CYCLE(0),
    FOURTEEN_HOUR_CYCLE(1),
    SIXTEEN_HOUR_CYCLE(2),
    EIGHTEEN_HOUR_CYCLE(3),
    TWENTY_HOUR_CYCLE(4),
    TWENTY_FOUR_HOUR_CYCLE(5),
    INVALID_CYCLE(-1);

    private final int id;

    FastingCycle(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
