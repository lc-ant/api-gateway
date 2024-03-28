package net.lecousin.ant.apigateway.tenant.v1;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.lecousin.ant.core.api.PageRequest;
import net.lecousin.ant.core.api.PageResponse;
import net.lecousin.ant.core.expression.Expression;
import net.lecousin.ant.service.tenant.TenantService;
import net.lecousin.ant.service.tenant.dto.Tenant;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tenant/v1")
@RequiredArgsConstructor
public class ApiTenantV1RestController {
	
	private final TenantService service;
	
	@PostMapping("/_search")
	public Mono<PageResponse<Tenant>> search(@RequestBody(required = false) Expression<Boolean> criteria, PageRequest pageRequest) {
		return service.search(criteria, pageRequest);
	}
	
	@GetMapping("/_textSearch")
	public Mono<PageResponse<Tenant>> textSearch(@RequestParam("text") String text, PageRequest pageRequest) {
		return service.textSearch(text, pageRequest);
	}
	
	@GetMapping("/{id}")
	public Mono<Tenant> findById(@PathVariable("id") String id) {
		return service.findById(id);
	}

	@PostMapping
	public Mono<Tenant> create(@RequestBody Tenant tenant) {
		return service.create(tenant);
	}

	@PutMapping
	public Mono<Tenant> update(@RequestBody Tenant tenant) {
		return service.update(tenant);
	}

	@DeleteMapping("/{id}")
	public Mono<Void> delete(@PathVariable("id") String id) {
		return service.delete(id);
	}
	
}
