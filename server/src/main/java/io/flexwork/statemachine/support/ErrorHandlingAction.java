package io.flexwork.statemachine.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class ErrorHandlingAction<S, E> implements Action<S, E> {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandlingAction.class);

    @Override
    public void execute(StateContext<S, E> context) {
        LOG.error(
                "State machine {} has a error while executing at state {}",
                context.getStateMachine().getId(),
                context.getStateMachine().getState().getId(),
                context.getException());
    }
}
