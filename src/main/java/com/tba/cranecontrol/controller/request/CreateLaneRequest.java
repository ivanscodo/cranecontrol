package com.tba.cranecontrol.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public final class CreateLaneRequest {

    public CreateLaneRequest(){

    }

    @NotNull
    @Min(value = 1, message = "Minimum value is 1")
    @Max(value = 100, message = "You can't create more than 100 lanes.")
    private Integer lanes;

    @NotNull
    @Min(value = 1, message = "Minimum value is 1")
    private Integer positions;

    public CreateLaneRequest(final Integer lanes, final Integer positions) {
        this.lanes = lanes;
        this.positions = positions;
    }

    public Integer getLanes() {
        return lanes;
    }

    public Integer getPositions() {
        return positions;
    }
}
