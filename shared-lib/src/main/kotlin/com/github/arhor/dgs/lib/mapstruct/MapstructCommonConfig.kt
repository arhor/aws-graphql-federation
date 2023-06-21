package com.github.arhor.dgs.lib.mapstruct;

import org.mapstruct.InjectionStrategy
import org.mapstruct.MapperConfig
import org.mapstruct.MappingConstants
import org.mapstruct.NullValueMappingStrategy

@MapperConfig(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    implementationPackage = "<PACKAGE_NAME>.generated",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
)
class MapstructCommonConfig
