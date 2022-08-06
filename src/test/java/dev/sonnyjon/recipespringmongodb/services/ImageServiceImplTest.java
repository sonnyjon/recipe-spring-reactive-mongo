package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.model.Recipe;
import dev.sonnyjon.recipespringmongodb.repositories.reactifve.RecipeReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Sonny on 7/11/2022.
 */
@ExtendWith(SpringExtension.class)
class ImageServiceImplTest
{
    public static final String RECIPE_ID = "RECIPE-1";

    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    ImageService imageService;
    AutoCloseable mocks;

    @BeforeEach
    void setUp()
    {
        mocks = MockitoAnnotations.openMocks(this);
        imageService = new ImageServiceImpl( recipeReactiveRepository );
    }

    @AfterEach
    void tearDown() throws Exception
    {
        mocks.close();
    }

    // John Thompson's test

    @Test
    void test_saveImageFile() throws Exception
    {
        // given
        MultipartFile multipartFile = new MockMultipartFile("imagefile", "testing.txt", "text/plain",
                "Spring Framework Guru".getBytes());

        Recipe recipe = new Recipe();
        recipe.setId( RECIPE_ID );

        // when
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just( recipe ));
        when(recipeReactiveRepository.save(any(Recipe.class))).thenReturn(Mono.just( recipe ));

        ArgumentCaptor<Recipe> argumentCaptor = ArgumentCaptor.forClass( Recipe.class );
        imageService.saveImageFile( RECIPE_ID, multipartFile );

        // then
        verify(recipeReactiveRepository, times(1)).save( argumentCaptor.capture() );
        Recipe savedRecipe = argumentCaptor.getValue();
        assertEquals( multipartFile.getBytes().length, savedRecipe.getImage().length );
    }
}