package io.flowinquiry.modules.teams.utils;

import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import io.flowinquiry.utils.Obfuscator;

public class TicketPathUtils {

    public static String buildTicketPath(TicketDTO ticketDTO) {
        String teamId = Obfuscator.obfuscate(ticketDTO.getTeamId());

        if (ticketDTO.getProjectId() != null) {
            return String.format(
                    "/portal/teams/%s/projects/%s/%s",
                    teamId, ticketDTO.getProjectShortName(), ticketDTO.getProjectTicketNumber());
        }

        return String.format(
                "/portal/teams/%s/tickets/%s", teamId, Obfuscator.obfuscate(ticketDTO.getId()));
    }

    private TicketPathUtils() {
        // utility class
    }
}
