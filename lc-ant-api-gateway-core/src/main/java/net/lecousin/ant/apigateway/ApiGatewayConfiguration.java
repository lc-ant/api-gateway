package net.lecousin.ant.apigateway;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import net.lecousin.ant.core.springboot.service.client.LcAntServiceClientConfiguration;
import net.lecousin.ant.core.springboot.service.provider.LcAntServiceProviderConfiguration;

@Configuration
@EnableAutoConfiguration
@Import({
	LcAntServiceProviderConfiguration.class,
	LcAntServiceClientConfiguration.class
})
@ComponentScan
public class ApiGatewayConfiguration {

}
