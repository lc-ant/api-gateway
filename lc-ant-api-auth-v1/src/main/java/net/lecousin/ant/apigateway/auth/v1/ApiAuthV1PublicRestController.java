package net.lecousin.ant.apigateway.auth.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.lecousin.ant.core.springboot.security.InternalJwtAuthenticationManager;
import net.lecousin.ant.core.springboot.security.JwtRequest;
import net.lecousin.ant.core.springboot.security.JwtResponse;
import net.lecousin.ant.service.client.user.UserServiceClientV1;
import net.lecousin.ant.service.user.dto.AuthenticatedUserResponse;
import net.lecousin.ant.service.user.dto.User;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public-api/auth/v1")
@RequiredArgsConstructor
public class ApiAuthV1PublicRestController {
	
	private final UserServiceClientV1 userClient;
	private final InternalJwtAuthenticationManager jwtService;

	@PostMapping("/tenant/{tenantId}/user/{username}")
	public Mono<AuthenticatedUserResponse> authenticateUser(
		@PathVariable("tenantId") String tenantId,
		@PathVariable("username") String username,
		@RequestBody String password
	) {
		return userClient.authPublic().authenticateUserByUsernameAndPassword(tenantId, username, password);
	}
	
	@GetMapping("/_searchEmail")
	public Mono<User> searchEmail(@RequestParam("email") String email) {
		return userClient.authPublic().searchEmail(email);
	}
	
	@PostMapping("/{tenantId}/refreshToken")
	public Mono<JwtResponse> refreshToken(
		@PathVariable("tenantId") String tenantId,
		@RequestBody JwtRequest tokens
	) {
		return userClient.authPublic().refreshToken(tenantId, tokens);
	}

	@PostMapping("/{tenantId}/closeToken")
	public Mono<Void> closeToken(
		@PathVariable("tenantId") String tenantId,
		@RequestBody JwtRequest tokens
	) {
		return userClient.authPublic().closeToken(tenantId, tokens);
	}
	
	@PostMapping("/_validate")
	public Mono<Void> validateAccessToken(@RequestBody String accessToken) {
		return jwtService.validate(accessToken).then();
	}
	
}
