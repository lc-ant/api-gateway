package net.lecousin.ant.apigateway.auth.v1;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import net.lecousin.ant.apigateway.ApiGatewayConfiguration;

@Configuration
@Import({ApiGatewayConfiguration.class})
public class ApiAuthV1Configuration {

}
