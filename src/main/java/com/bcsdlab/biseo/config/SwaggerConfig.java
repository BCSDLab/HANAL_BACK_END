package com.bcsdlab.biseo.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)   // 기본적인 응답 메세지를 사용할 것인가
            .consumes(getConsumeContentTypes())         // Request Content-type
            .produces(getProduceContentTypes())         // Response Content-type
            .apiInfo(getApiInfo())                      // Api Info
            .select()                                   // ApiSelectBuilder 생성
            .apis(RequestHandlerSelectors.basePackage("com.bcsdlab.biseo.controller"))  // api 스펙이 작성될 패키지
            .paths(PathSelectors.any())                 // path 조건에 해당하는 api만 불러옴
            .build()
            .securitySchemes(Arrays.asList(getApiKey()));
    }

    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        consumes.add("multipart/from-data");
        return consumes;
    }

    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json;charset=UTF-8");
        return produces;
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
            .title("Biseo API")
            .description("BCSDLab Biseo API Docs")
            .version("1.0")
            .build();
    }

    private ApiKey getApiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }
}
