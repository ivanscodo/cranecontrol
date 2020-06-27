package com.tba.cranecontrol.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tba.cranecontrol.exception.MovementNotAllowedException;
import com.tba.cranecontrol.exception.NotFoundException;
import com.tba.cranecontrol.model.Crane;
import com.tba.cranecontrol.model.Lane;
import com.tba.cranecontrol.model.LaneOrder;
import com.tba.cranecontrol.repository.LaneRepository;

@ExtendWith(MockitoExtension.class)
class LaneServiceTest {

    @InjectMocks
    private LaneService laneService;

    @Mock
    private LaneRepository laneRepository;

    @Test
    void shouldMoveTheFirstCraneForAValidPosition() {
        //given
        final String laneId = "ABCD1234";
        when(laneRepository.findById(laneId)).thenReturn(Optional.of(Lane.builder()
                .withPositions(20)
                .withCranes(List.of(
                        new Crane(0, LaneOrder.FIRST),
                        new Crane(20, LaneOrder.SECOND)
                ))
                .withId(laneId)
                .build()));

        //when
        laneService.moveCrane(laneId, LaneOrder.FIRST, 11);

        //then
        final ArgumentCaptor<Lane> laneArgumentCaptor = ArgumentCaptor.forClass(Lane.class);
        verify(laneRepository).save(laneArgumentCaptor.capture());
        final Lane value = laneArgumentCaptor.getValue();
        assertThat(value.getId()).isEqualTo(laneId);
    }

    @Test
    void shouldMoveTheSecondCraneWhenItIsBlockingTheFirstCrane() {
        //given
        final String laneId = "ABCD1234";
        final int desiredPosition = 12;
        when(laneRepository.findById(laneId)).thenReturn(Optional.of(Lane.builder()
                .withPositions(20)
                .withCranes(List.of(
                        new Crane(0, LaneOrder.FIRST),
                        new Crane(10, LaneOrder.SECOND)
                ))
                .withId(laneId)
                .build()));
        //when
        laneService.moveCrane(laneId, LaneOrder.FIRST, desiredPosition);

        //then
        final ArgumentCaptor<Lane> laneArgumentCaptor = ArgumentCaptor.forClass(Lane.class);
        verify(laneRepository).save(laneArgumentCaptor.capture());
        final Lane lane = laneArgumentCaptor.getValue();
        assertThat(lane.getId()).isEqualTo(laneId);
        assertThat(lane.getCranes().stream()
                .filter(c -> c.getLaneOrder() == LaneOrder.SECOND)
                .findFirst()
                .get()
                .getCurrentPosition())
                .isEqualTo(desiredPosition + 1);
    }

    @Test
    void shouldThrowAnExceptionWhenTheFirstContainerIsBlockingTheSecond() {
        final String laneId = "ABCD1234";
        final int desiredPosition = 4;
        when(laneRepository.findById(laneId)).thenReturn(Optional.of(Lane.builder()
                .withPositions(20)
                .withCranes(List.of(
                        new Crane(5, LaneOrder.FIRST),
                        new Crane(10, LaneOrder.SECOND)
                ))
                .withId(laneId)
                .build()));
        //when
        assertThrows(
                MovementNotAllowedException.class,
                () -> laneService.moveCrane(laneId, LaneOrder.SECOND, desiredPosition)
        );
    }

    @Test
    void shouldThrowANotFoundExceptionWhenALaneIsNotFound() {
        //given
        final String laneId = "ABCD1234";
        final int desiredPosition = 4;
        when(laneRepository.findById(any())).thenReturn(Optional.empty());

        //then
        assertThrows(NotFoundException.class, () -> laneService.moveCrane(laneId, LaneOrder.SECOND, desiredPosition));
    }

    @Test
    void shouldCreateLaneForAValidRequest() {
        //given
        when(laneRepository.save(any())).thenReturn(Lane.builder()
                .withPositions(10)
                .withCranes(List.of(
                        new Crane(0, LaneOrder.FIRST),
                        new Crane(10, LaneOrder.SECOND)
                ))
                .withId("laneId")
                .build());
        //when
        final List<Lane> result = laneService.create(1, 10);

        //then
        final Lane lane = result.get(0);
        assertThat(result.size()).isEqualTo(1);
        assertThat(lane.getPositions()).isEqualTo(10);
        assertThat(lane.getCranes().size()).isEqualTo(2);
    }

    @Test
    void shouldCreateMultipleLanesForAValidRequest() {
        //given
        final Integer lanes = 5;
        final Integer positions = 10;
        when(laneRepository.save(any())).thenReturn(Lane.builder()
                .withPositions(positions)
                .withCranes(List.of(new Crane(0, LaneOrder.FIRST), new Crane(10, LaneOrder.SECOND))).build());

        //when
        final List<Lane> result = laneService.create(lanes, positions);

        //then
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.stream().allMatch(l -> l.getPositions() == 10)).isEqualTo(true);
    }

    @Test
    void shouldThrowAMovementNotAllowedExceptionWhenTheDesiredPositionIsNotValid() {
        final String laneId = "ABCD1234";
        final int desiredPosition = 40;
        when(laneRepository.findById(laneId)).thenReturn(Optional.of(Lane.builder()
                .withPositions(20)
                .withCranes(List.of(
                        new Crane(5, LaneOrder.FIRST),
                        new Crane(10, LaneOrder.SECOND)
                ))
                .withId(laneId)
                .build()));
        //when
        assertThrows(
                MovementNotAllowedException.class,
                () -> laneService.moveCrane(laneId, LaneOrder.SECOND, desiredPosition)
        );
    }

    private List<Lane> mockLanes(final int positions) {
        return List.of(
                Lane.builder()
                        .withPositions(positions)
                        .withCranes(List.of(
                                new Crane(0, LaneOrder.FIRST),
                                new Crane(10, LaneOrder.SECOND)
                        ))
                        .withId("laneId1")
                        .build(),
                Lane.builder()
                        .withPositions(positions)
                        .withCranes(List.of(
                                new Crane(0, LaneOrder.FIRST),
                                new Crane(10, LaneOrder.SECOND)
                        ))
                        .withId("laneId2")
                        .build(),
                Lane.builder()
                        .withPositions(positions)
                        .withCranes(List.of(
                                new Crane(0, LaneOrder.FIRST),
                                new Crane(10, LaneOrder.SECOND)
                        ))
                        .withId("laneId3")
                        .build(),
                Lane.builder()
                        .withPositions(positions)
                        .withCranes(List.of(
                                new Crane(0, LaneOrder.FIRST),
                                new Crane(10, LaneOrder.SECOND)
                        ))
                        .withId("laneId4")
                        .build(),
                Lane.builder()
                        .withPositions(positions)
                        .withCranes(List.of(
                                new Crane(0, LaneOrder.FIRST),
                                new Crane(10, LaneOrder.SECOND)
                        ))
                        .withId("laneId5")
                        .build()
        );
    }
}
