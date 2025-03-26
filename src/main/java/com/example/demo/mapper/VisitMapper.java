package com.example.demo.mapper;

import com.example.demo.command.visit.InsertVisitCommand;
import com.example.demo.model.visit.Visit;
import com.example.demo.model.visit.VisitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    VisitDTO toDto(Visit visit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    Visit toEntity(InsertVisitCommand insertVisitCommand);
}
