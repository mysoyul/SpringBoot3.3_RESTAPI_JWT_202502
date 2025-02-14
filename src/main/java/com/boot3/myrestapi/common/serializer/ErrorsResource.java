package com.boot3.myrestapi.common.serializer;

import com.boot3.myrestapi.common.IndexController;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


public class ErrorsResource extends EntityModel<Errors> {
    private Errors errors;

    public ErrorsResource(Errors content) {
        this.errors = content;
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }

}