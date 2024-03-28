package net.lecousin.ant.apigateway.events.v1;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import net.lecousin.ant.core.springboot.service.provider.websocket.WebSocketServerConfiguration;

@Configuration
@Import({WebSocketServerConfiguration.class})
public class ApiDataEventsV1Configuration {

}
