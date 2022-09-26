package com.bcsdlab.biseo.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
            .apiInfo(getApiInfo())                      // Api Info
            .select()                                   // ApiSelectBuilder 생성
            .apis(RequestHandlerSelectors.basePackage("com.bcsdlab.biseo.controller"))  // api 스펙이 작성될 패키지
            .paths(PathSelectors.any())                 // path 조건에 해당하는 api만 불러옴
            .build()
            .securitySchemes(List.of(getApiKey()));
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
