package net.lecousin.ant.apigateway.events.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.lecousin.ant.core.api.ApiData;

@Data
@AllArgsConstructor
public class ApiDataEventDomainClass<T extends ApiData> {

	private Class<T> clazz;
	private String domain;
	private String name;
	
}
