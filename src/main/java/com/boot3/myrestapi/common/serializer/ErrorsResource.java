package com.boot3.myrestapi.common.serializer;

import com.boot3.myrestapi.common.IndexController;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Getter
public class ErrorsResource extends EntityModel<Errors> {
    private Errors errors;

    public ErrorsResource(Errors content) {
        this.errors = content;
        //IndexController에 index() 메서드에 설정된 Path("/api") 를 Link 객체로 생성함
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }

}