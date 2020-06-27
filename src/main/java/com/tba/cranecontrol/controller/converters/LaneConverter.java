package com.tba.cranecontrol.controller.converters;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.tba.cranecontrol.controller.dto.CraneDTO;
import com.tba.cranecontrol.controller.dto.LaneDTO;
import com.tba.cranecontrol.model.Crane;
import com.tba.cranecontrol.model.Lane;

@Component
public class LaneConverter implements Converter<Lane, LaneDTO> {

    @Override
    public LaneDTO convert(final Lane lane) {
        final List<CraneDTO> craneDTOS = lane.getCranes().stream().map(this::convertCrane).collect(Collectors.toList());
        return new LaneDTO(lane.getId(), lane.getPositions(), craneDTOS);
    }

    private CraneDTO convertCrane(final Crane crane){
        return new CraneDTO(crane.getCurrentPosition(), crane.getLaneOrder());
    }

}
