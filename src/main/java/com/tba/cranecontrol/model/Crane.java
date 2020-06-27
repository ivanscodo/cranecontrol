package com.tba.cranecontrol.model;

public class Crane {

    private final Integer currentPosition;
    private final LaneOrder laneOrder;

    public Crane(final Integer currentPosition, final LaneOrder laneOrder) {
        this.currentPosition = currentPosition;
        this.laneOrder = laneOrder;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public LaneOrder getLaneOrder() {
        return laneOrder;
    }
}
