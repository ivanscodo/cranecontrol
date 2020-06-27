package com.tba.cranecontrol.controller.dto;

import java.util.List;

public class LaneDTO {

    private final String id;
    private final Integer positions;
    private final List<CraneDTO> cranes;

    public LaneDTO(String id, Integer positions, List<CraneDTO> cranes) {
        this.id = id;
        this.positions = positions;
        this.cranes = cranes;
    }

    public String getId() {
        return id;
    }

    public Integer getPositions() {
        return positions;
    }

    public List<CraneDTO> getCranes() {
        return cranes;
    }
}
