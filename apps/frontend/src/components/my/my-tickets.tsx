"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { useSession } from "next-auth/react";
import React, { useState } from "react";
import useSWR from "swr";

import DynamicQueryBuilder from "@/components/my/ticket-query-component";
import PaginationExt from "@/components/shared/pagination-ext";
import TicketList from "@/components/teams/ticket-list";
import { Skeleton } from "@/components/ui/skeleton";
import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";
import { searchTickets } from "@/lib/actions/tickets.action";
import { useError } from "@/providers/error-provider";
import { Filter, Operator, Pagination, QueryDTO } from "@/types/query";

const validTicketTypes = ["reported", "assigned"] as const;
type TicketType = (typeof validTicketTypes)[number];

const MyTicketsView = () => {
  const { data: session } = useSession();
  const { setError } = useError();
  const router = useRouter();
  const searchParams = useSearchParams();

  // Read ticket type from URL
  const ticketTypeParam = searchParams.get("ticketType") as TicketType;
  const [ticketType, setTicketType] = useState<TicketType>(
    validTicketTypes.includes(ticketTypeParam) ? ticketTypeParam : "reported",
  );

  // Watch for changes in search params and update state
  React.useEffect(() => {
    if (validTicketTypes.includes(ticketTypeParam)) {
      setTicketType(ticketTypeParam);
    } else {
      setTicketType("reported"); // Default if invalid
    }
  }, [ticketTypeParam]);

  const [query, setQuery] = useState<QueryDTO | null>(null);
  const [pagination, setPagination] = useState<Pagination>({
    page: 1,
    size: 10,
    sort: [{ field: "createdAt", direction: "desc" }],
  });

  // Handles search updates
  const handleSearch = (newQuery: QueryDTO) => {
    setQuery(newQuery);
    setPagination((prev) => ({ ...prev, page: 1 }));
  };

  // Construct query for API call
  const userFilter: Filter = {
    field: ticketType === "reported" ? "requestUser.id" : "assignUser.id",
    operator: "eq" as Operator,
    value: session?.user?.id ?? "",
  };

  const combinedQuery: QueryDTO = {
    groups: [
      {
        logicalOperator: "AND",
        filters: [userFilter], // Always include user filter
        groups: query?.groups || [], // Merge with other filters if available
      },
    ],
  };

  // Fetch team tickets using SWR
  const { data, isLoading } = useSWR(
    session?.user?.id ? [`/api/tickets`, combinedQuery, pagination] : null,
    async () => searchTickets(combinedQuery, pagination, setError),
    { keepPreviousData: true },
  );

  const totalPages = data?.totalPages ?? 1; // Default to 1 to prevent invalid pagination

  const handleTicketTypeChange = (newType: TicketType) => {
    if (newType !== ticketType) {
      setTicketType(newType);
      router.push(`?ticketType=${newType}`, { scroll: false });
    }
  };

  return (
    <>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-semibold">
          {ticketType === "reported"
            ? "My Reported Tickets"
            : "My Assigned Tickets"}
        </h1>
        <ToggleGroup
          type="single"
          value={ticketType}
          onValueChange={(value) => {
            if (value) handleTicketTypeChange(value as TicketType);
          }}
        >
          <ToggleGroupItem value="reported">Reported</ToggleGroupItem>
          <ToggleGroupItem value="assigned">Assigned</ToggleGroupItem>
        </ToggleGroup>
      </div>

      <div className="flex flex-col md:flex-row gap-4">
        <div className="w-[500px] flex-none min-h-[250px] overflow-hidden">
          <DynamicQueryBuilder onSearch={handleSearch} />
        </div>

        {/* Tickets View & Pagination (Expands on Right) */}
        <div className="flex-1 flex flex-col space-y-4">
          {isLoading ? (
            <>
              <Skeleton className="h-8 w-full" />
              <Skeleton className="h-8 w-full" />
              <Skeleton className="h-8 w-full" />
            </>
          ) : (
            <>
              <TicketList tickets={data?.content || []} instantView={false} />
              <PaginationExt
                currentPage={pagination.page}
                totalPages={totalPages}
                onPageChange={(page) =>
                  setPagination((prev) => ({ ...prev, page }))
                }
              />
            </>
          )}
        </div>
      </div>
    </>
  );
};

export default MyTicketsView;
