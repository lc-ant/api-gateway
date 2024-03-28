package net.lecousin.ant.apigateway.events.v1;

import java.util.List;

public interface ApiDataEventDomainClassProvider {

	List<ApiDataEventDomainClass<?>> getEventDomainClasses();
	
}
