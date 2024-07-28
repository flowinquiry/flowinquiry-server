package io.flexwork.modules.usermanagement.service;

import io.flexwork.modules.usermanagement.stateMachine.SignupEvents;
import io.flexwork.modules.usermanagement.stateMachine.SignupStates;
import io.flexwork.security.domain.User;
import io.flexwork.security.repository.UserRepository;
import java.util.Optional;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@WithStateMachine
public class SignupService {

    private static final Logger log = LoggerFactory.getLogger(SignupService.class);

    private final UserRepository userRepository;

    private final StateMachineService<SignupStates, SignupEvents> stateMachineService;

    private final StateMachinePersister<SignupStates, SignupEvents, String> stateMachinePersister;

    public SignupService(
            UserRepository userRepository,
            StateMachineService<SignupStates, SignupEvents> stateMachineService,
            StateMachinePersister<SignupStates, SignupEvents, String> stateMachinePersister) {
        this.userRepository = userRepository;
        this.stateMachineService = stateMachineService;
        this.stateMachinePersister = stateMachinePersister;
    }

    @SneakyThrows
    public void signup(User user) {
        log.debug("Start signup workflow {}", user);
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User " + user.getId() + " existed");
        } else {
            userRepository.save(user);
            StateMachine<SignupStates, SignupEvents> stateMachine =
                    stateMachineService.acquireStateMachine("signup-" + user.getId(), true);

            stateMachine.getExtendedState().getVariables().put("user", user);
            stateMachine
                    .sendEvent(
                            Mono.just(MessageBuilder.withPayload(SignupEvents.NEW_SIGNUP).build()))
                    .subscribe(
                            signupStatesSignupEventsStateMachineEventResult ->
                                    log.debug(
                                            "Result {}",
                                            signupStatesSignupEventsStateMachineEventResult
                                                    .getResultType()));
            stateMachinePersister.persist(stateMachine, "signup-" + user.getId());
        }
    }
}