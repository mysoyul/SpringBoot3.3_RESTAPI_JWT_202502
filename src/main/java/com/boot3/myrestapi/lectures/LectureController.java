package com.boot3.myrestapi.lectures;

import com.boot3.myrestapi.lectures.dto.LectureReqDto;
import com.boot3.myrestapi.lectures.validator.LectureValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/lectures", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class LectureController {
    private final LectureRepository lectureRepository;
    private final ModelMapper modelMapper;
    private final LectureValidator validator;

    //Constructor Injection
//    public LectureController(LectureRepository lectureRepository) {
//        this.lectureRepository = lectureRepository;
//    }

    /* 
       @Valid 어노테이션 - Data Binding 하고 검증 역할을 하는 Validator 를 호출하는 역할
       Errors - 입력항목 검증 시 Error 정보를 저장하거나 조회 해주는 역할을 담당하는 객체
     */
    @PostMapping
    public ResponseEntity<?> createLecture(@RequestBody @Valid LectureReqDto lectureReqDto,
                                           Errors errors) {
        //입력항목 검증 시 Error 가 발생 했나요?
        if(errors.hasErrors()) {
            return getErrors(errors);
        }

        //Biz 로직 입력항목 검증 - LectureValidator 호출
        validator.validate(lectureReqDto, errors);
        if(errors.hasErrors()) {
            //400 에러 발생시킴
            return getErrors(errors);
        }

        //ReqDTO => Entity 매핑
        Lecture lecture = modelMapper.map(lectureReqDto, Lecture.class);
        //테이블에 Insert
        Lecture addedLecture = this.lectureRepository.save(lecture);
        //Link 생성하는 역할을 담당하는 객체 http://localhost:8080/api/lectures/10
        WebMvcLinkBuilder selfLinkBuilder =
                WebMvcLinkBuilder.linkTo(LectureController.class).slash(addedLecture.getId());
        //생성된 Link를 URL 형식으로 생성해줌
        URI createUri = selfLinkBuilder.toUri();
        //ResponseEntity = body + header + statusCode
        //created() : statusCode를 201로 설정하고, 위에서 생성한 Link를 Response location 헤더로 설정한다.
        return ResponseEntity.created(createUri).body(addedLecture);
    }

    private static ResponseEntity<?> getErrors(Errors errors) {
        //400 에러 발생시킴
        return ResponseEntity.badRequest().body(errors);
    }
}