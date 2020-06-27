package com.tba.cranecontrol.controller.dto;

import com.tba.cranecontrol.model.LaneOrder;

public class CraneDTO {

    private final Integer currentPosition;
    private final LaneOrder laneOrder;

    public CraneDTO(Integer currentPosition, LaneOrder laneOrder) {
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
