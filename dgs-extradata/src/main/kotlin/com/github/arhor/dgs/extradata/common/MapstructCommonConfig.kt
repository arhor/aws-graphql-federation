package com.github.arhor.dgs.extradata.common;

import org.mapstruct.InjectionStrategy
import org.mapstruct.MapperConfig
import org.mapstruct.MappingConstants
import org.mapstruct.NullValueMappingStrategy

@MapperConfig(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
)
class MapstructCommonConfig
