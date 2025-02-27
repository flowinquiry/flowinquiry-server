package io.flowinquiry.modules.teams.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import io.flowinquiry.IntegrationTest;
import io.flowinquiry.exceptions.ResourceNotFoundException;
import io.flowinquiry.modules.teams.domain.WorkflowVisibility;
import io.flowinquiry.modules.teams.service.dto.WorkflowDTO;
import io.flowinquiry.modules.teams.service.dto.WorkflowDetailedDTO;
import io.flowinquiry.modules.teams.service.dto.WorkflowStateDTO;
import io.flowinquiry.modules.teams.service.dto.WorkflowTransitionDTO;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class WorkflowServiceIT {

    @Autowired private WorkflowService workflowService;

    @Test
    public void shouldUpdateWorkflowSuccessfully() {
        WorkflowDTO workflowDTO = workflowService.getWorkflowById(1L).get();
        workflowDTO.setName("New workflow");
        workflowDTO.setRequestName("New request name");
        workflowService.updateWorkflow(1L, workflowDTO);
        workflowDTO = workflowService.getWorkflowById(1L).get();

        assertThat(workflowDTO)
                .isNotNull()
                .extracting(WorkflowDTO::getName, WorkflowDTO::getRequestName)
                .containsExactly("New workflow", "New request name");
    }

    @Test
    public void shouldUpdateWorkflowFailedForNotFoundWorkflow() {
        WorkflowDTO workflowDTO = WorkflowDTO.builder().build();

        assertThatThrownBy(() -> workflowService.updateWorkflow(1000L, workflowDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Workflow not found with id: 1000");
    }

    @Test
    public void shouldGetWorkflowForTeamSuccessfully() {
        List<WorkflowDTO> workflows = workflowService.getWorkflowsForTeam(1L, false);
        assertThat(workflows.size()).isEqualTo(2);

        List<Tuple> expectedWorkflows =
                List.of(
                        Tuple.tuple(2L, "Refund Process Workflow"),
                        Tuple.tuple(4L, "New Hardware Request"));

        assertThat(workflows)
                .extracting(WorkflowDTO::getId, WorkflowDTO::getName)
                .containsExactlyInAnyOrderElementsOf(expectedWorkflows);
    }

    @Test
    public void shouldGetEmptyWorkflowNotNonExistentTeam() {
        List<WorkflowDTO> workflows = workflowService.getWorkflowsForTeam(1000L, false);
        assertThat(workflows.size()).isEqualTo(0);
    }

    @Test
    public void shouldGetWorkflowDetailSuccessfully() {
        WorkflowDetailedDTO workflowDetail = workflowService.getWorkflowDetail(2L).get();
        assertThat(workflowDetail)
                .extracting(WorkflowDetailedDTO::getId, WorkflowDetailedDTO::getName)
                .containsExactly(2L, "Refund Process Workflow");

        // Expected workflow states
        assertThat(workflowDetail.getStates()).hasSize(6);
        assertThat(workflowDetail.getStates())
                .extracting(
                        WorkflowStateDTO::getId,
                        WorkflowStateDTO::getStateName,
                        WorkflowStateDTO::getIsInitial,
                        WorkflowStateDTO::getIsFinal)
                .containsExactlyInAnyOrder(
                        tuple(6L, "New", true, false),
                        tuple(7L, "Request Evidence", false, false),
                        tuple(8L, "Evidence Provided", false, false),
                        tuple(9L, "Refund Approved", false, false),
                        tuple(10L, "Refund Denied", false, true),
                        tuple(11L, "Refund Completed", false, true));

        assertThat(workflowDetail.getTransitions()).hasSize(5);

        // Expected workflow transitions
        assertThat(workflowDetail.getTransitions())
                .extracting(
                        WorkflowTransitionDTO::getId,
                        WorkflowTransitionDTO::getWorkflowId,
                        WorkflowTransitionDTO::getEventName,
                        WorkflowTransitionDTO::getSourceStateId,
                        WorkflowTransitionDTO::getTargetStateId)
                .containsExactlyInAnyOrder(
                        tuple(11L, 2L, "Request Evidence", 6L, 7L),
                        tuple(12L, 2L, "Provide Evidence", 7L, 8L),
                        tuple(13L, 2L, "Approve Refund", 8L, 9L),
                        tuple(14L, 2L, "Deny Refund", 8L, 10L),
                        tuple(15L, 2L, "Complete Refund", 9L, 11L));
    }

    @Test
    public void shouldSaveWorkflowSuccessfully() {
        WorkflowDetailedDTO workflowDetail = workflowService.getWorkflowDetail(1L).get();
        workflowDetail.setId(null);
        WorkflowDetailedDTO savedWorkflowDTO = workflowService.saveWorkflow(workflowDetail);
        assertThat(savedWorkflowDTO.getId()).isGreaterThan(1L);
    }

    @Test
    public void shouldGetWorkflowNotLinkWithTeamsSuccessfully() {
        List<WorkflowDTO> workflows = workflowService.listGlobalWorkflowsNotLinkedToTeam(1L);
        assertThat(workflows.size()).isEqualTo(3);
        List<Tuple> expectedWorkflows =
                List.of(
                        Tuple.tuple(1L, "Project Workflow"),
                        Tuple.tuple(3L, "Bug Fix Workflow"),
                        Tuple.tuple(5L, "Software Approval Workflow"));

        assertThat(workflows)
                .extracting(WorkflowDTO::getId, WorkflowDTO::getName)
                .containsExactlyInAnyOrderElementsOf(expectedWorkflows);
    }

    @Test
    public void shouldCreateWorkflowFromReferenceSuccessfully() {
        WorkflowDTO workflowDTO =
                WorkflowDTO.builder()
                        .name("Workflow Example")
                        .requestName("request")
                        .description("description")
                        .build();
        WorkflowDetailedDTO workflowDetailedDTO =
                workflowService.createWorkflowByReference(1L, 1L, workflowDTO);

        // Then: Verify the workflow is created successfully
        assertThat(workflowDetailedDTO)
                .isNotNull()
                .extracting(
                        WorkflowDetailedDTO::getName,
                        WorkflowDetailedDTO::getRequestName,
                        WorkflowDetailedDTO::getDescription)
                .containsExactly("Workflow Example", "request", "description");

        // Ensure ID is assigned
        assertThat(workflowDetailedDTO.getId()).isNotNull().isPositive();
        assertThat(workflowDetailedDTO.getVisibility()).isEqualTo(WorkflowVisibility.PRIVATE);

        // Verify the reference workflow details
        assertThat(workflowDetailedDTO.getOwnerId()).isEqualTo(1L);

        // Ensure workflow states are cloned properly
        assertThat(workflowDetailedDTO.getStates()).isNotEmpty();
        assertThat(workflowDetailedDTO.getStates())
                .allSatisfy(
                        state -> {
                            assertThat(state.getWorkflowId())
                                    .isNotEqualTo(workflowDetailedDTO.getId());
                        });

        // Ensure workflow transitions are cloned properly
        assertThat(workflowDetailedDTO.getTransitions()).isNotEmpty();
        assertThat(workflowDetailedDTO.getTransitions())
                .allSatisfy(
                        transition -> {
                            assertThat(transition.getWorkflowId())
                                    .isNotEqualTo(workflowDetailedDTO.getId());
                        });

        // Verify auditing fields
        assertThat(workflowDetailedDTO.getCreatedAt()).isNotNull();
        assertThat(workflowDetailedDTO.getModifiedAt()).isNotNull();
    }

    @Test
    public void shouldDeleteWorkflowFromTeamSuccessfully() {
        assertThat(workflowService.getWorkflowsForTeam(1L, false).size()).isEqualTo(2);
        workflowService.deleteWorkflowByTeam(1L, 2L);
        assertThat(workflowService.getWorkflowsForTeam(1L, false).size()).isEqualTo(1);
    }
}
