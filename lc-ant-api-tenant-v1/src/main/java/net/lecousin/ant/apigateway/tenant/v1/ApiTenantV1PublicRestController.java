package net.lecousin.ant.apigateway.tenant.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.lecousin.ant.service.tenant.TenantPublicService;
import net.lecousin.ant.service.tenant.dto.Tenant;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public-api/tenant/v1")
@RequiredArgsConstructor
public class ApiTenantV1PublicRestController {

	private final TenantPublicService service;
	
	@GetMapping("/{id}")
	public Mono<Tenant> findById(@PathVariable("id") String id) {
		return service.findById(id);
	}
	
}
