"use client";

import { DatePickerWithRange } from "@/components/shared/date-range-picker";
import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { TimeRange, useTimeRange } from "@/providers/time-range-provider";

const TimeRangeSelector = () => {
  const t = useAppClientTranslations();
  const { timeRange, setTimeRange, customDates } = useTimeRange();

  return (
    <div className="flex items-center gap-4">
      <ToggleGroup
        type="single"
        value={timeRange}
        onValueChange={(value) => setTimeRange(value as TimeRange)}
      >
        <ToggleGroupItem value="7d">
          {t.teams.dashboard("last_7_days")}
        </ToggleGroupItem>
        <ToggleGroupItem value="30d">
          {t.teams.dashboard("last_30_days")}
        </ToggleGroupItem>
        <ToggleGroupItem value="90d">
          {t.teams.dashboard("last_90_days")}
        </ToggleGroupItem>
        <ToggleGroupItem value="custom">
          {t.teams.dashboard("custom_range")}
        </ToggleGroupItem>
      </ToggleGroup>

      {timeRange === "custom" && (
        <DatePickerWithRange
          value={customDates}
          onChangeAction={(dates) => setTimeRange("custom", dates)}
        />
      )}
    </div>
  );
};

export default TimeRangeSelector;
