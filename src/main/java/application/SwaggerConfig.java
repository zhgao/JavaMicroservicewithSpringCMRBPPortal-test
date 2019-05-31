package application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket api() {
		// Adding Header
		ParameterBuilder pb = new ParameterBuilder();
		pb.name("X-CSRF-TOKEN") // header name
				.modelRef(new ModelRef("string")).parameterType("header").defaultValue("${_csrf.token}").required(true)
				.build();
		List<Parameter> pl = new ArrayList<>();
		pl.add(pb.build()); // add parameter
		return new Docket(DocumentationType.SWAGGER_2).useDefaultResponseMessages(false).select()
				.apis(RequestHandlerSelectors.basePackage("application.rest.v1")).paths(PathSelectors.any()).build()
				.globalOperationParameters(pl).apiInfo(metadata());
	}

	private ApiInfo metadata() {
		return new ApiInfoBuilder().title("Example RESTful APIs").version("1.0")
				.contact(new Contact("Zeng Li", "https://www.ibm.com", "zzlili@cn.ibm.com")).build();
	}
}