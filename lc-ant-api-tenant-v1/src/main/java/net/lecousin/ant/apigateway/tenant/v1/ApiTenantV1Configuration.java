package net.lecousin.ant.apigateway.tenant.v1;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import net.lecousin.ant.apigateway.ApiGatewayConfiguration;

@Configuration
@Import({ApiGatewayConfiguration.class})
public class ApiTenantV1Configuration {

}
