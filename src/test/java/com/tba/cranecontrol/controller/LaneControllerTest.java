package com.tba.cranecontrol.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tba.cranecontrol.controller.dto.LaneDTO;
import com.tba.cranecontrol.controller.request.CraneMoveRequest;
import com.tba.cranecontrol.controller.request.CreateLaneRequest;
import com.tba.cranecontrol.exception.NotFoundException;
import com.tba.cranecontrol.model.Crane;
import com.tba.cranecontrol.model.Lane;
import com.tba.cranecontrol.model.LaneOrder;
import com.tba.cranecontrol.service.LaneService;

import io.restassured.http.Header;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = LaneController.class)
class LaneControllerTest {

    @MockBean
    private LaneService laneService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());
        RestAssuredMockMvc.basePath = LaneController.PATH;
    }

    @Test
    void shouldReturnALaneWhenGetIsCalled() {
        when(laneService.findAll()).thenReturn(List.of(Lane.builder()
                .withPositions(10)
                .withId("1")
                .withCranes(List.of(
                        new Crane(0, LaneOrder.FIRST),
                        new Crane(10, LaneOrder.SECOND)
                ))
                .build()
        ));
        final List<LaneDTO> lanes = RestAssuredMockMvc.given()
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("$", LaneDTO.class);

        final LaneDTO laneDTO = lanes.get(0);
        assertThat(lanes.size()).isEqualTo(1);
        assertThat(laneDTO.getPositions()).isEqualTo(10);
        assertThat(laneDTO.getId()).isEqualTo("1");
        assertThat(laneDTO.getCranes().size()).isEqualTo(2);
        assertThat(laneDTO.getCranes().get(0).getLaneOrder()).isEqualTo(LaneOrder.FIRST);
        assertThat(laneDTO.getCranes().get(1).getLaneOrder()).isEqualTo(LaneOrder.SECOND);
    }

    @Test
    void shouldReturnAnEmptyArrayWhenGetIsCalledButThereAreNoLanes() {
        when(laneService.findAll()).thenReturn(List.of());
        final List<LaneDTO> lanes = RestAssuredMockMvc.given()
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("$", LaneDTO.class);
        assertThat(lanes.size()).isEqualTo(0);
    }

    @Test
    void shouldReturnALaneWhenFindByIdIsCalled() {
        final String laneId = "1";
        when(laneService.findById(laneId)).thenReturn(Lane.builder()
                .withPositions(10)
                .withId(laneId)
                .withCranes(List.of(
                        new Crane(0, LaneOrder.FIRST),
                        new Crane(10, LaneOrder.SECOND)
                ))
                .build()
        );
        final LaneDTO laneDTO = RestAssuredMockMvc.given()
                .when()
                .get("/{laneId}", laneId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response().as(LaneDTO.class);
        assertThat(laneDTO.getPositions()).isEqualTo(10);
        assertThat(laneDTO.getId()).isEqualTo(laneId);
        assertThat(laneDTO.getCranes().size()).isEqualTo(2);
        assertThat(laneDTO.getCranes().get(0).getLaneOrder()).isEqualTo(LaneOrder.FIRST);
        assertThat(laneDTO.getCranes().get(1).getLaneOrder()).isEqualTo(LaneOrder.SECOND);
    }

    @Test
    void shouldThrowANotFoundExceptionWhenThereIsNoLane() {
        final String laneId = "1";
        when(laneService.findById(laneId)).thenThrow(NotFoundException.class);
        RestAssuredMockMvc.given()
                .when()
                .get("/{laneId}", laneId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldCreateLaneForAValidRequest() {
        final String laneId = "1";
        when(laneService.create(2, 10)).thenReturn(
                List.of(Lane.builder()
                                .withPositions(10)
                                .withId(laneId)
                                .withCranes(List.of(
                                        new Crane(0, LaneOrder.FIRST),
                                        new Crane(10, LaneOrder.SECOND)
                                ))
                                .build()
                        ,
                        Lane.builder()
                                .withPositions(10)
                                .withId("2")
                                .withCranes(List.of(
                                        new Crane(0, LaneOrder.FIRST),
                                        new Crane(10, LaneOrder.SECOND)
                                ))
                                .build()
                ));
        final List<LaneDTO> lanesDTO = RestAssuredMockMvc.given()
                .header(new Header("Content-Type", "application/json; charset=utf-8"))
                .body(new CreateLaneRequest(2, 10))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("$", LaneDTO.class);

        final LaneDTO laneDTO = lanesDTO.get(0);
        assertThat(lanesDTO.size()).isEqualTo(2);
        assertThat(laneDTO.getPositions()).isEqualTo(10);
        assertThat(laneDTO.getId()).isEqualTo("1");
        assertThat(laneDTO.getCranes().size()).isEqualTo(2);
        assertThat(laneDTO.getCranes().get(0).getLaneOrder()).isEqualTo(LaneOrder.FIRST);
        assertThat(laneDTO.getCranes().get(1).getLaneOrder()).isEqualTo(LaneOrder.SECOND);
    }

    @Test
    void shouldMoveCraneToAValidPosition() {
        final String laneId = "1";
        when(laneService.moveCrane(any(), any(), any())).thenReturn(Lane.builder()
                .withPositions(10)
                .withId(laneId)
                .withCranes(List.of(
                        new Crane(5, LaneOrder.FIRST),
                        new Crane(6, LaneOrder.SECOND)
                ))
                .build()
        );
        final LaneDTO laneDTO = RestAssuredMockMvc.given()
                .header(new Header("Content-Type", "application/json; charset=utf-8"))
                .body(new CraneMoveRequest("1", LaneOrder.FIRST, 5))
                .when()
                .patch()
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response().as(LaneDTO.class);

        assertThat(laneDTO.getCranes().size()).isEqualTo(2);
        assertThat(laneDTO.getCranes().get(0).getCurrentPosition()).isEqualTo(5);
        assertThat(laneDTO.getCranes().get(1).getCurrentPosition()).isEqualTo(6);
    }

    @Test
    void shouldFailToCreateLaneForAnInvalidNumberOfLanes() {
        RestAssuredMockMvc.given()
                .header(new Header("Content-Type", "application/json; charset=utf-8"))
                .body(new CreateLaneRequest(-5, 5))
                .when()
                .patch()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldFailForAnInvalidPosition() {
        RestAssuredMockMvc.given()
                .header(new Header("Content-Type", "application/json; charset=utf-8"))
                .body(new CreateLaneRequest(5, -5))
                .when()
                .patch()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

}