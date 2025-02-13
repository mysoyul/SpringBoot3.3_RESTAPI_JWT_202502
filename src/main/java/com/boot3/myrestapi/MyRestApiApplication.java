package com.boot3.myrestapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyRestApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(MyRestApiApplication.class, args);
	}

	/*
		ModelMapper 객체를 Spring Bean 으로 설정하면 Singleton 객체로 생성이 되므로
		new ModelMapper() 로 생성하는 방법 보다 Heap 메모리를 절약하는 장점이 있다.
		@Autowired
		private ModelMapper modelMapper
	 */
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper;
	}
}
