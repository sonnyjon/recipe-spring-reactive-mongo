package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.dto.UnitOfMeasureDto;
import reactor.core.publisher.Flux;

/**
 * Created by Sonny on 7/9/2022.
 */
public interface UnitOfMeasureService
{
    Flux<UnitOfMeasureDto> listAllUoms();
}
