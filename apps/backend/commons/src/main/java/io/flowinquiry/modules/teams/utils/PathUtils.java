package io.flowinquiry.modules.teams.utils;

import static io.flowinquiry.utils.Obfuscator.obfuscate;

import io.flowinquiry.modules.teams.service.dto.TicketDTO;

/**
 * Utility class for generating URL paths for tickets. This class provides methods to build
 * standardized paths for accessing tickets in the web portal based on their properties.
 */
public class PathUtils {

    /**
     * Builds a URL path for accessing a ticket in the web portal.
     *
     * @param ticketDTO The ticket data transfer object containing ticket information
     * @return A formatted URL path string for accessing the ticket For project tickets:
     *     /portal/teams/{teamId}/projects/{projectShortName}/{projectTicketNumber} For regular
     *     tickets: /portal/teams/{teamId}/tickets/{ticketId}
     */
    public static String buildTicketPath(TicketDTO ticketDTO) {
        String teamId = obfuscate(ticketDTO.getTeamId());

        if (ticketDTO.getProjectId() != null) {
            return String.format(
                    "/portal/teams/%s/projects/%s/%s",
                    teamId, ticketDTO.getProjectShortName(), ticketDTO.getProjectTicketNumber());
        }

        return String.format("/portal/teams/%s/tickets/%s", teamId, obfuscate(ticketDTO.getId()));
    }

    /**
     * Private constructor to prevent instantiation. This is a utility class that should not be
     * instantiated as it only contains static methods.
     */
    private PathUtils() {
        // utility class
    }
}
