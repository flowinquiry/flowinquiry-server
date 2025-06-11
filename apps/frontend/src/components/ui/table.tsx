"use client";

import * as React from "react";

import { withTestId } from "@/lib/testIds";
import { cn } from "@/lib/utils";

function Table({
  className,
  testId,
  ...props
}: React.ComponentProps<"table"> & { testId?: string }) {
  return (
    <div
      data-slot="table-container"
      className="relative w-full overflow-x-auto"
    >
      <table
        data-slot="table"
        className={cn("w-full caption-bottom text-sm", className)}
        {...withTestId(props, testId)}
      />
    </div>
  );
}

function TableHeader({
  className,
  testId,
  ...props
}: React.ComponentProps<"thead"> & { testId?: string }) {
  return (
    <thead
      data-slot="table-header"
      className={cn("[&_tr]:border-b", className)}
      {...withTestId(props, testId)}
    />
  );
}

function TableBody({
  className,
  testId,
  ...props
}: React.ComponentProps<"tbody"> & { testId?: string }) {
  return (
    <tbody
      data-slot="table-body"
      className={cn("[&_tr:last-child]:border-0", className)}
      {...withTestId(props, testId)}
    />
  );
}

function TableFooter({
  className,
  testId,
  ...props
}: React.ComponentProps<"tfoot"> & { testId?: string }) {
  return (
    <tfoot
      data-slot="table-footer"
      className={cn(
        "bg-muted/50 border-t font-medium [&>tr]:last:border-b-0",
        className,
      )}
      {...withTestId(props, testId)}
    />
  );
}

function TableRow({
  className,
  testId,
  ...props
}: React.ComponentProps<"tr"> & { testId?: string }) {
  return (
    <tr
      data-slot="table-row"
      className={cn(
        "hover:bg-muted/50 data-[state=selected]:bg-muted border-b transition-colors",
        className,
      )}
      {...withTestId(props, testId)}
    />
  );
}

function TableHead({
  className,
  testId,
  ...props
}: React.ComponentProps<"th"> & { testId?: string }) {
  return (
    <th
      data-slot="table-head"
      className={cn(
        "text-foreground h-10 px-2 text-left align-middle font-medium whitespace-nowrap [&:has([role=checkbox])]:pr-0 [&>[role=checkbox]]:translate-y-[2px]",
        className,
      )}
      {...withTestId(props, testId)}
    />
  );
}

function TableCell({
  className,
  testId,
  ...props
}: React.ComponentProps<"td"> & { testId?: string }) {
  return (
    <td
      data-slot="table-cell"
      className={cn(
        "p-2 align-middle whitespace-nowrap [&:has([role=checkbox])]:pr-0 [&>[role=checkbox]]:translate-y-[2px]",
        className,
      )}
      {...withTestId(props, testId)}
    />
  );
}

function TableCaption({
  className,
  testId,
  ...props
}: React.ComponentProps<"caption"> & { testId?: string }) {
  return (
    <caption
      data-slot="table-caption"
      className={cn("text-muted-foreground mt-4 text-sm", className)}
      {...withTestId(props, testId)}
    />
  );
}

export {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableFooter,
  TableHead,
  TableHeader,
  TableRow,
};
