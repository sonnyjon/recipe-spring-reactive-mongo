package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.converters.UnitOfMeasureConverter;
import dev.sonnyjon.recipespringmongodb.dto.UnitOfMeasureDto;
import dev.sonnyjon.recipespringmongodb.repositories.reactifve.UnitOfMeasureReactiveRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Created by Sonny on 7/12/2022.
 */
@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService
{
    private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
    private final UnitOfMeasureConverter converter = new UnitOfMeasureConverter();

    public UnitOfMeasureServiceImpl(UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository)
    {
        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
    }

    @Override
    public Flux<UnitOfMeasureDto> listAllUoms()
    {
        return unitOfMeasureReactiveRepository
                .findAll()
                .map(converter::convertEntity);
    }
}
