package com.tba.cranecontrol.controller.request;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.tba.cranecontrol.model.LaneOrder;

public final class CraneMoveRequest {

    @NotEmpty(message = "Lane id can't be null.")
    private String laneId;

    @NotNull(message = "Lane order can't be null. It must have one of the following values: FIRST or SECOND")
    private LaneOrder laneOrder;

    @NotNull(message = "Desired position can't be null.")
    private Integer desiredPosition;

    public CraneMoveRequest(){}

    public CraneMoveRequest(final String laneId, final LaneOrder laneOrder, final Integer desiredPosition) {
        this.laneId = laneId;
        this.laneOrder = laneOrder;
        this.desiredPosition = desiredPosition;
    }

    public String getLaneId() {
        return laneId;
    }

    public LaneOrder getLaneOrder() {
        return laneOrder;
    }

    public Integer getDesiredPosition() {
        return desiredPosition;
    }
}
