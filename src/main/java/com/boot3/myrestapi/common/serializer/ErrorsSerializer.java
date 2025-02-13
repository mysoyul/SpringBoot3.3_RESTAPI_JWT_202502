package com.boot3.myrestapi.common.serializer;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors>{
	@Override
	public void serialize(Errors errors, 
                          JsonGenerator gen, 
                          SerializerProvider serializers) throws IOException {
		//직렬화 시작
        gen.writeStartArray();
        //List<FieldError> FieldError 객체 포함된 정보를 직렬화
        errors.getFieldErrors().forEach(e -> {
            try {
                gen.writeStartObject();
                //입력필드명
                gen.writeStringField("field", e.getField());
                //입력필드가 포함된 DTO 객체명
                gen.writeStringField("objectName", e.getObjectName());
                //검증규칙 (어노테이션)
                gen.writeStringField("code", e.getCode());
                //에러 메시지
                gen.writeStringField("defaultMessage", e.getDefaultMessage());
                //잘못 입력된 값
                Object rejectedValue = e.getRejectedValue();
                if (rejectedValue != null) {
                    gen.writeStringField("rejectedValue", rejectedValue.toString());
                }
                gen.writeEndObject();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        //List<ObjectError> ObjectError(2개 이상의 필드 체크) 객체 포함된 정보를 직렬화
        errors.getGlobalErrors().forEach(e -> {
            try {
                gen.writeStartObject();
                gen.writeStringField("objectName", e.getObjectName());
                gen.writeStringField("code", e.getCode());
                gen.writeStringField("defaultMessage", e.getDefaultMessage());
                gen.writeEndObject();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        //직렬화 종료
        gen.writeEndArray();
		
	}
}