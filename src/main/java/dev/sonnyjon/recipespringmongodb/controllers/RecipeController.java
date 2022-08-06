package dev.sonnyjon.recipespringmongodb.controllers;

import dev.sonnyjon.recipespringmongodb.dto.RecipeDto;
import dev.sonnyjon.recipespringmongodb.services.RecipeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by Sonny on 7/15/2022.
 */
@Slf4j
@Controller
public class RecipeController
{
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService)
    {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable String id, Model model)
    {
        model.addAttribute("recipe", recipeService.findDtoById(id).block());
        return "recipe/show";
    }

    @GetMapping("/recipe/new")
    public String newRecipe(Model model)
    {
        model.addAttribute("recipe", new RecipeDto());
        return "recipe/recipeform";
    }

    @GetMapping("/recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model)
    {
        model.addAttribute("recipe", recipeService.findDtoById( id ).block());
        return "recipe/recipeform";
    }

    @PostMapping("/recipe")
    public String saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeDto recipeDto, BindingResult bindingResult)
    {
        if (bindingResult.hasErrors())
        {
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            return "recipe/recipeform";
        }

        RecipeDto savedRecipe = recipeService.saveRecipe( recipeDto ).block();

        return String.format("redirect:/recipe/%s/show", savedRecipe.getId());
    }

    @GetMapping("/recipe/{id}/delete")
    public String deleteById(@PathVariable String id)
    {
        log.debug("Deleting id: " + id);
        recipeService.deleteById(id).block();

        return "redirect:/";
    }
}