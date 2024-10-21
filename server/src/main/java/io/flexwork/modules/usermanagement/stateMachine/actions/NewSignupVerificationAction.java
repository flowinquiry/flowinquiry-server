package io.flexwork.modules.usermanagement.stateMachine.actions;

import io.flexwork.modules.usermanagement.stateMachine.SignupEvents;
import io.flexwork.modules.usermanagement.stateMachine.SignupStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class NewSignupVerificationAction implements Action<SignupStates, SignupEvents> {

    private static final Logger LOG = LoggerFactory.getLogger(NewSignupVerificationAction.class);

    @Override
    public void execute(StateContext<SignupStates, SignupEvents> context) {
        LOG.debug("Verifying the new signup user");
    }
}
