package com.tba.cranecontrol.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public final class Lane {

    private final String id;
    private final Integer positions;
    private final List<Crane> cranes;

    private Lane(final String id, final Integer positions, final List<Crane> cranes) {
        this.id = id;
        this.positions = positions;
        this.cranes = cranes;
    }

    public static LaneBuilder builder() {
        return new LaneBuilder();
    }

    public String getId() {
        return id;
    }

    public Integer getPositions() {
        return positions;
    }

    public List<Crane> getCranes() {
        return cranes;
    }

    public static final class LaneBuilder {
        private String id;
        private Integer positions;
        private List<Crane> cranes;

        public LaneBuilder withId(final String id) {
            this.id = id;
            return this;
        }

        public LaneBuilder withPositions(final Integer positions) {
            this.positions = positions;
            return this;
        }

        public LaneBuilder withCranes(final List<Crane> cranes) {
            this.cranes = cranes;
            return this;
        }

        public Lane build() {
            return new Lane(id, positions, cranes);
        }
    }
}
