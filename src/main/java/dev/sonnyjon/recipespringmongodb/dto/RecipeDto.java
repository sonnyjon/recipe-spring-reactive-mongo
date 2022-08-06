package dev.sonnyjon.recipespringmongodb.dto;

import dev.sonnyjon.recipespringmongodb.model.Difficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sonny on 7/8/2022.
 */
@Getter
@Setter
@NoArgsConstructor
public class RecipeDto
{
    private String id;

    @NotBlank
    @Size(min=3, max=255)
    private String description;

    @Min(1)
    @Max(999)
    private Integer prepTime;

    @Min(1)
    @Max(999)
    private Integer cookTime;

    @Min(1)
    @Max(100)
    private Integer servings;
    private String source;
    private String url;
    @NotBlank
    private String directions;
    private List<IngredientDto> ingredients = new ArrayList<>();
    private Byte[] image;
    private Difficulty difficulty;
    private NotesDto notes;
    private List<CategoryDto> categories = new ArrayList<>();
}
