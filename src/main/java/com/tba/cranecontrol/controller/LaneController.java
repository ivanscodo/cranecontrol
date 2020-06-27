package com.tba.cranecontrol.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tba.cranecontrol.controller.converters.LaneConverter;
import com.tba.cranecontrol.controller.dto.LaneDTO;
import com.tba.cranecontrol.controller.request.CraneMoveRequest;
import com.tba.cranecontrol.controller.request.CreateLaneRequest;
import com.tba.cranecontrol.service.LaneService;


@RestController
@RequestMapping(path = LaneController.PATH)
public class LaneController {

    public static final String PATH = "/lane";
    private final ObjectMapper mapper;
    private final LaneService laneService;
    private final LaneConverter laneConverter;

    public LaneController(
            final ObjectMapper mapper, final LaneService laneService,
            final LaneConverter laneConverter
    ) {
        this.mapper = mapper;
        this.laneService = laneService;
        this.laneConverter = laneConverter;
        this.mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    @GetMapping
    public List<LaneDTO> findAll() {
        return laneService.findAll()
                .stream()
                .map(laneConverter::convert)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{laneId}")
    public LaneDTO findById(@PathVariable("laneId") final String laneId) {
        return laneConverter.convert(laneService.findById(laneId));
    }

    @PostMapping
    public List<LaneDTO> createLanes(@RequestBody @Valid final CreateLaneRequest request) {
        return laneService.create(request.getLanes(), request.getPositions())
                .stream()
                .map(laneConverter::convert)
                .collect(Collectors.toList());
    }

    @PatchMapping
    public LaneDTO moveCrane(@RequestBody @Valid final CraneMoveRequest craneMoveRequest) {
        return laneConverter.convert(
                laneService.moveCrane(
                        craneMoveRequest.getLaneId(),
                        craneMoveRequest.getLaneOrder(),
                        craneMoveRequest.getDesiredPosition()
                )
        );
    }
}
