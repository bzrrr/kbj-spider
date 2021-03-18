package com.bzrrr.kbjspider.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: wangziheng
 * @Date: 2020/11/27
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private static final String splitor = ";";

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket spider() {
//        ParameterBuilder ticketPar = new ParameterBuilder();
//        List<Parameter> pars = new ArrayList<Parameter>();
//        ticketPar.name("Authorization").description("登录token")
//                .modelRef(new ModelRef("string")).parameterType("header")
//                .required(false)/*.defaultValue("Bearer ")*/.build();
//        pars.add(ticketPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bzrrr"))
                .paths(Predicates.not(PathSelectors.regex("/error.*")))// 错误路径不监控
                .paths(Predicates.not(PathSelectors.regex("/actuator.*")))
                .paths(Predicates.not(PathSelectors.regex("/oauth.*")))
                .paths(Predicates.not(PathSelectors.regex("/cas.*")))
//				.paths(PathSelectors.none())//如果是线上环境，添加路径过滤，设置为全部都不符合
                .build().groupName("spider");
    }


    protected ApiInfo getApiInfo() {
        return new ApiInfo("监控中心API", "cxhc Rest Web Service " + new Date(), "", "",
                new Contact("cxhc", "", ""), "", "", new ArrayList<VendorExtension>());
    }

    public static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
    }

    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
        return input -> {
            // 循环判断匹配
            for (String strPackage : basePackage.split(splitor)) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }
}
