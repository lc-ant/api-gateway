package net.lecousin.ant.apigateway.tenant.v1;

import java.util.List;

import org.springframework.stereotype.Component;

import net.lecousin.ant.apigateway.events.v1.ApiDataEventDomainClass;
import net.lecousin.ant.apigateway.events.v1.ApiDataEventDomainClassProvider;
import net.lecousin.ant.service.tenant.dto.Tenant;

@Component
public class ApiTenantV1ApiDataEventDomain implements ApiDataEventDomainClassProvider {

	@Override
	public List<ApiDataEventDomainClass<?>> getEventDomainClasses() {
		return List.of(
			new ApiDataEventDomainClass<>(Tenant.class, "tenant", "Tenant")
		);
	}
	
}
