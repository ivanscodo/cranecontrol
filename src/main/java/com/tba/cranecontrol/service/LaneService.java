package com.tba.cranecontrol.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.tba.cranecontrol.exception.LaneCreationException;
import com.tba.cranecontrol.exception.MovementNotAllowedException;
import com.tba.cranecontrol.exception.NotFoundException;
import com.tba.cranecontrol.model.Crane;
import com.tba.cranecontrol.model.Lane;
import com.tba.cranecontrol.model.LaneOrder;
import com.tba.cranecontrol.repository.LaneRepository;

@Service
public class LaneService {

    private final LaneRepository laneRepository;

    public LaneService(final LaneRepository laneRepository) {
        this.laneRepository = laneRepository;
    }

    public List<Lane> findAll() {
        try {
            return new ArrayList<>(CompletableFuture.supplyAsync(laneRepository::findAll).get());
        } catch (InterruptedException | ExecutionException e) {
            throw new LaneCreationException("An error happened while fetching lanes.");
        }
    }

    public Lane findById(final String laneId) {
        return laneRepository.findById(laneId)
                .orElseThrow(() -> new NotFoundException("Lane not found for id: " + laneId));
    }

    public Lane moveCrane(final String laneId, final LaneOrder laneOrder, final Integer desiredPosition) {
        final Lane lane = laneRepository.findById(laneId).orElseThrow(() -> new NotFoundException(
                "Lane not found for id: " + laneId));
        validateDesiredPosition(desiredPosition, lane);
        if (LaneOrder.FIRST == laneOrder) {
            return moveFirstCrane(desiredPosition, lane);
        }
        return moveSecondCrane(lane, desiredPosition);
    }

    public List<Lane> create(final Integer lanes, final Integer positions) {
        return IntStream.range(0, lanes)
                .mapToObj(pos -> CompletableFuture.supplyAsync(() -> createLane(positions)))
                .peek(CompletableFuture::join).map(
                        future -> {
                            try {
                                return future.get();
                            } catch (InterruptedException | ExecutionException e) {
                                throw new LaneCreationException("An error happened while creating lanes.");
                            }
                        })
                .collect(Collectors.toList());
    }

    private Lane moveSecondCrane(final Lane lane, final Integer desiredPosition) {
        final Crane firstCrane = getCrane(LaneOrder.FIRST, lane);
        if (isCollidingWithFirstCrane(firstCrane, desiredPosition)) {
            throw new MovementNotAllowedException(
                    "Movement not allowed for the second crane. The first crane is blocking this movement.");
        }
        return laneRepository.save(Lane.builder()
                .withId(lane.getId())
                .withPositions(lane.getPositions())
                .withCranes(List.of(firstCrane, new Crane(desiredPosition, LaneOrder.SECOND)))
                .build());
    }

    private Lane moveFirstCrane(final Integer desiredPosition, final Lane lane) {
        final Crane secondCrane = getCrane(LaneOrder.SECOND, lane);
        if (isCollidingWithSecondCrane(secondCrane, desiredPosition)) {
            return laneRepository.save(moveSecondCraneToSafePosition(desiredPosition, lane));
        }
        final Crane firstCraneNewPosition = new Crane(desiredPosition, LaneOrder.FIRST);
        return laneRepository.save(Lane.builder()
                .withId(lane.getId())
                .withPositions(lane.getPositions())
                .withCranes(List.of(firstCraneNewPosition, secondCrane))
                .build()
        );
    }

    private Lane createLane(final Integer positions) {
        final Crane firstCrane = new Crane(0, LaneOrder.FIRST);
        final Crane secondCrane = new Crane(positions + 1, LaneOrder.SECOND);
        return laneRepository.save(Lane.builder()
                .withPositions(positions)
                .withCranes(List.of(firstCrane, secondCrane))
                .build());
    }

    private boolean isCollidingWithFirstCrane(final Crane firstCrane, final Integer desiredPosition) {
        return desiredPosition <= firstCrane.getCurrentPosition();
    }

    private boolean isCollidingWithSecondCrane(final Crane secondCrane, final Integer desiredPosition) {
        return desiredPosition >= secondCrane.getCurrentPosition();
    }

    private Crane getCrane(final LaneOrder laneOrder, final Lane lane) {
        return lane.getCranes()
                .stream()
                .filter(c -> Objects.equals(c.getLaneOrder(), laneOrder))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Crane not found for the lane id: " + lane.getId() + " and order " +
                        laneOrder.name() +
                        " informed."));
    }

    private void validateDesiredPosition(final Integer desiredPosition, final Lane lane) {
        if (desiredPosition > lane.getPositions()) {
            throw new MovementNotAllowedException("The desired position given is higher than the allowed for this lane.");
        }
    }

    private Lane moveSecondCraneToSafePosition(final Integer desiredPosition, final Lane lane) {
        final Crane secondCraneNewPosition = new Crane(desiredPosition + 1, LaneOrder.SECOND);
        final Crane firstCraneNewPosition = new Crane(desiredPosition, LaneOrder.FIRST);
        return Lane.builder()
                .withCranes(List.of(firstCraneNewPosition, secondCraneNewPosition))
                .withPositions(lane.getPositions())
                .withId(lane.getId())
                .build();
    }
}