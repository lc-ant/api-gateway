package net.lecousin.ant.apigateway.events.v1;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lecousin.ant.apigateway.ApiGateway;
import net.lecousin.ant.apigateway.events.v1.dto.ListenRequest;
import net.lecousin.ant.apigateway.events.v1.dto.UnlistenRequest;
import net.lecousin.ant.core.springboot.messaging.ApiDataChangeEvent;
import net.lecousin.ant.core.springboot.messaging.LcAntAmpqListenerContainer;
import net.lecousin.ant.core.springboot.messaging.LcAntAmpqListenerContainerFactory;
import net.lecousin.ant.core.springboot.service.provider.websocket.WebSocketController;
import net.lecousin.ant.core.springboot.service.provider.websocket.WebSocketServerHandler.WebSocketServerSession;
import net.lecousin.commons.collections.LcCollectionUtils;
import reactor.core.publisher.Mono;

@WebSocketController(path = "/api/api-data-events/v1/socket", serviceName = ApiGateway.SERVICE_NAME)
@RequiredArgsConstructor
@Slf4j
public class ApiDataEventsWebSocketControllerV1 implements InitializingBean {

	private final AmqpAdmin admin;
	private final LcAntAmpqListenerContainerFactory containerFactory;
	private final List<ApiDataEventDomainClassProvider> domainClassProviders;
	
	private final Map<String, ApiDataEventDomainClass<?>> domainClassByClassName = new HashMap<>();
	private final Map<String, ApiDataEventDomainClass<?>> domainClassByDataType = new HashMap<>();
	
	private static final String KEY_LISTENING = "lc-ant.api-data-events.listening";
	private static final String KEY_LISTENER = "lc-ant.api-data-events.listener";
	
	private String getQueueName(WebSocketServerSession session) {
		return "lc-ant.api-data-events.v1.listener.session-" + session.getId();
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		domainClassProviders.stream()
		.flatMap(domainProvider -> domainProvider.getEventDomainClasses().stream())
		.forEach(domainClass -> {
			domainClassByClassName.put(domainClass.getClazz().getName(), domainClass);
			domainClassByDataType.put(domainClass.getDomain() + ':' + domainClass.getName(), domainClass);
		});
	}
	
	@WebSocketController.OnSessionStarted
	public void onSessionStarted(WebSocketServerSession session) {
		session.getAttributes().put(KEY_LISTENING, new LinkedList<>());
		String queueName = getQueueName(session);
		Queue queue = new Queue(queueName, false, true, true);
		admin.declareQueue(queue);
		
		var container = containerFactory.createDirectMessageListenerContainer(queue, msg -> {
			ApiDataChangeEvent event;
			try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(msg.getBody()))) {
				event = (ApiDataChangeEvent) input.readObject();
			} catch (Exception e) {
				log.error("Unable to decode ApiDataChangeEvent", e);
				return;
			}
			if (!event.getRequiredPermissions().isAllowed(session.getGrants()))
				return;
			var domainType = Optional.ofNullable(domainClassByClassName.get(event.getDataType()))
			.map(domainClass -> domainClass.getDomain() + ':' + domainClass.getName());
			if (domainType.isEmpty())
				return;
			event.setDataType(domainType.get());
			event.setRequiredPermissions(null);
			session.send(event).subscribe();
		});
		session.getAttributes().put(KEY_LISTENER, container);
	}
	
	@WebSocketController.OnSessionClosed
	public void onSessionClosed(WebSocketServerSession session) {
		LcAntAmpqListenerContainer container = (LcAntAmpqListenerContainer) session.getAttributes().get(KEY_LISTENER);
		container.stop();
		admin.deleteQueue(getQueueName(session), false, false);
	}
	
	
	
	public Mono<Void> onListenRequest(WebSocketServerSession session, @RequestBody ListenRequest request) {
		return Mono.justOrEmpty(domainClassByDataType.get(request.getDataType()))
		.flatMap(domainClass -> {
			String routingKey = ApiDataChangeEvent.getRoutingKeyForDataType(domainClass.getClazz().getName());
			@SuppressWarnings("unchecked")
			List<Binding> listening = (List<Binding>) session.getAttributes().get(KEY_LISTENING);
			if (listening.stream().anyMatch(b -> b.getRoutingKey().equals(routingKey)))
				return Mono.empty();
			String queueName = getQueueName(session);
			Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, ApiDataChangeEvent.TOPIC_EXCHANGE, routingKey, null);
			admin.declareBinding(binding);
			listening.add(binding);
			return session.send(request);
		});
	}
	
	public Mono<Void> onUnlistenRequest(WebSocketServerSession session, @RequestBody UnlistenRequest request) {
		return Mono.justOrEmpty(domainClassByDataType.get(request.getDataType()))
		.flatMap(domainClass -> {
			String routingKey = ApiDataChangeEvent.getRoutingKeyForDataType(domainClass.getClazz().getName());
			@SuppressWarnings("unchecked")
			List<Binding> listening = (List<Binding>) session.getAttributes().get(KEY_LISTENING);
			Optional<Binding> opt = LcCollectionUtils.removeFirstMatching(listening, b -> b.getRoutingKey().equals(routingKey));
			if (opt.isEmpty())
				return Mono.empty();
			admin.removeBinding(opt.get());
			return session.send(request);
		});
	}
	
}
