package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.exceptions.NotFoundException;
import dev.sonnyjon.recipespringmongodb.model.Recipe;
import dev.sonnyjon.recipespringmongodb.repositories.reactifve.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by Sonny on 7/9/2022.
 */
@Slf4j
@Service
public class ImageServiceImpl implements ImageService
{
    private final RecipeReactiveRepository recipeReactiveRepository;

    public ImageServiceImpl(RecipeReactiveRepository recipeReactiveRepository)
    {
        this.recipeReactiveRepository = recipeReactiveRepository;
    }

    @Override
    @Transactional
    public void saveImageFile(String recipeId, MultipartFile file)
    {
        try {
            Recipe recipe = recipeReactiveRepository
                                    .findById( recipeId )
                                    .doOnError(err -> {
                                        throw new NotFoundException("Recipe not found: " + recipeId);
                                    })
                                    .block();

            Byte[] byteObjects = new Byte[file.getBytes().length];
            int i = 0;

            for (byte b : file.getBytes())
            {
                byteObjects[i++] = b;
            }

            recipe.setImage(byteObjects);
            recipeReactiveRepository.save( recipe ).block();
        }
        catch (IOException e)
        {
            log.error("Error occurred", e);
            e.printStackTrace();
        }
    }
}
