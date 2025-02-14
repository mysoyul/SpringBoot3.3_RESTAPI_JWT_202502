package com.boot3.myrestapi.lectures;

import com.boot3.myrestapi.common.serializer.ErrorsResource;
import com.boot3.myrestapi.lectures.dto.LectureReqDto;
import com.boot3.myrestapi.lectures.dto.LectureResDto;
import com.boot3.myrestapi.lectures.dto.LectureResource;
import com.boot3.myrestapi.lectures.validator.LectureValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
        //free, offline 값 업데이트
        lecture.update();
        //테이블에 Insert
        Lecture addedLecture = this.lectureRepository.save(lecture);
        //Entity => ResDto 로 매핑
        LectureResDto lectureResDto = modelMapper.map(addedLecture, LectureResDto.class);

        //Link 생성하는 역할을 담당하는 객체 http://localhost:8080/api/lectures/10
        WebMvcLinkBuilder selfLinkBuilder =
                linkTo(LectureController.class).slash(lectureResDto.getId());
        //생성된 Link를 URL 형식으로 생성해줌
        URI createUri = selfLinkBuilder.toUri();

        LectureResource lectureResource = new LectureResource(lectureResDto);
        //query-lectures 라는 이름을 갖는 링크 생성
        lectureResource.add(linkTo(LectureController.class).withRel("query-lectures"));
        lectureResource.add(selfLinkBuilder.withRel("update-lecture"));

        //ResponseEntity = body + header + statusCode
        //created() : statusCode를 201로 설정하고, 위에서 생성한 Link를 Response location 헤더로 설정한다.
        return ResponseEntity.created(createUri).body(lectureResource);
    }

    /*
        HATEOAS PagedResourcesAssembler 는 Page(paging data) 를 PagedModel(paging data + Link) 로 변환
        public PagedModel<EntityModel<T>> toModel(Page<T> entity)
     */
    @GetMapping
    public ResponseEntity queryLectures(Pageable pageable,
                                        PagedResourcesAssembler<LectureResDto> assembler) {
        Page<Lecture> lecturePage = this.lectureRepository.findAll(pageable);
        // Page<Lecture> => Page<LectureResDto> 변환
        Page<LectureResDto> lectureResDtoPage =
                //Page<U> map(Function<? super T,? extends U> converter)
                lecturePage.map(lecture -> modelMapper.map(lecture, LectureResDto.class));
        // Page<LectureResDto> => HATEOAS PagedModel 로 변환
        //PagedModel<EntityModel<LectureResDto>> pagedResources = assembler.toModel(lectureResDtoPage);

        //2번째 인자 => RepresentationModelAssembler 의 D toModel(T entity)
        // T = LectureResDto, D = LectureResource
        //assembler.toModel(lectureResDtoPage,resDto -> new LectureResource(resDto));
        PagedModel<LectureResource> pagedModel =
                assembler.toModel(lectureResDtoPage, LectureResource::new);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity getLecture(@PathVariable Integer id) {
        Optional<Lecture> optionalLecture = this.lectureRepository.findById(id);
        if(optionalLecture.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Lecture lecture = optionalLecture.get();
        LectureResDto lectureResDto = modelMapper.map(lecture, LectureResDto.class);
        LectureResource lectureResource = new LectureResource(lectureResDto);
        return ResponseEntity.ok(lectureResource);
    }

    private static ResponseEntity<?> getErrors(Errors errors) {
        //400 에러 발생시킴
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}