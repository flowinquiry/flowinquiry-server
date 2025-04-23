import { format, formatDistanceToNow, parseISO } from "date-fns";
import { toZonedTime } from "date-fns-tz";

export const formatDateTimeDistanceToNow = (date?: Date | null) => {
  if (!date) {
    return "Invalid date";
  }

  return formatDistanceToNow(date, {
    addSuffix: true,
  });
};

export const formatDateTime = (date?: Date | null) => {
  if (!date) {
    return "Invalid date";
  }

  return `${date.toDateString()} ${date.toLocaleTimeString()}`;
};

/**
 * Converts a date range object into URL search parameters.
 * Ensures all dates are properly formatted in ISO 8601 format.
 */
export const formatDateParams = (dateParams: {
  range?: string;
  from?: Date;
  to?: Date;
}) => {
  const params = new URLSearchParams();

  if (dateParams.range) params.append("range", dateParams.range);
  if (dateParams.from) params.append("fromDate", dateParams.from.toISOString());
  if (dateParams.to) params.append("toDate", dateParams.to.toISOString());

  return params.toString();
};

export const calculateDuration = (
  startDate: string | Date,
  endDate: string | Date,
): string => {
  const start = new Date(startDate);
  const end = new Date(endDate);

  // Calculate difference in days
  const diffTime = Math.abs(end.getTime() - start.getTime());
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  // Format as weeks + days or just days
  if (diffDays >= 7) {
    const weeks = Math.floor(diffDays / 7);
    const days = diffDays % 7;
    return `${weeks} week${weeks !== 1 ? "s" : ""}${days > 0 ? `, ${days} day${days !== 1 ? "s" : ""}` : ""}`;
  } else {
    return `${diffDays} day${diffDays !== 1 ? "s" : ""}`;
  }
};

/**
 * Converts a date from UTC to local timezone for display
 * @param date The UTC date (from server/database)
 * @returns Date in local timezone
 */
export const utcToLocalDate = (date: Date | string | null | undefined) => {
  if (!date) return undefined;

  const dateObj = typeof date === "string" ? parseISO(date) : date;
  const userTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
  return toZonedTime(dateObj, userTimeZone);
};

/**
 * Converts a date from local timezone to UTC for storage
 * @param date The local date (from user input)
 * @returns Date in UTC
 */
export const localToUtcDate = (date: Date | string | null | undefined) => {
  if (!date) return undefined;

  const dateObj = typeof date === "string" ? parseISO(date) : date;
  const userTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
  // Convert to UTC by creating a zoned time and then extracting its value
  const zonedDate = toZonedTime(dateObj, "UTC");
  return new Date(zonedDate);
};

/**
 * Formats a date for display
 * @param date The date to format
 * @param formatStr Format string (defaults to 'PPP' - e.g., 'April 29, 2023')
 * @returns Formatted date string
 */
export const formatDisplayDate = (
  date: Date | string | null | undefined,
  formatStr = "PPP",
) => {
  if (!date) return "";

  const localDate = utcToLocalDate(date);
  return localDate ? format(localDate, formatStr) : "";
};
