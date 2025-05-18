"use client";

import { Plus, Search, Trash } from "lucide-react";
import React, { useState } from "react";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Filter, Operator, QueryDTO } from "@/types/query";
import { ticketChannels } from "@/types/tickets";

const fieldDefinitions = [
  { name: "requestTitle", label: "Title", type: "text" },
  { name: "requestDescription", label: "Description", type: "text" },
  { name: "isCompleted", label: "Completed", type: "boolean" },
  {
    name: "channel",
    label: "Channel",
    type: "select",
    options: [...ticketChannels],
  },
];

const operatorMappings: Record<string, Operator[]> = {
  text: ["eq", "lk"],
  number: ["eq", "gt", "lt"],
  date: ["eq", "gt", "lt"],
  boolean: ["eq"],
  select: ["eq", "in"],
};

const DynamicQueryBuilder = ({
  onSearch,
}: {
  onSearch: (query: QueryDTO) => void;
}) => {
  const [filters, setFilters] = useState<Filter[]>([]);
  const [error, setError] = useState<string | null>(null);

  const addFilter = () => {
    setFilters([...filters, { field: "", operator: "eq", value: "" }]);
  };

  const removeFilter = (index: number) => {
    setFilters(filters.filter((_, i) => i !== index));
  };

  const handleFieldChange = (index: number, value: string) => {
    setFilters((prevFilters) => {
      const updatedFilters = [...prevFilters];
      const fieldDef = fieldDefinitions.find((f) => f.name === value);
      updatedFilters[index] = {
        field: value,
        operator: fieldDef ? operatorMappings[fieldDef.type][0] : "eq",
        value: fieldDef?.type === "boolean" ? false : "",
      };
      return updatedFilters;
    });
  };

  const handleOperatorChange = (index: number, value: string) => {
    setFilters((prevFilters) => {
      const updatedFilters = [...prevFilters];
      updatedFilters[index].operator = value as Operator;
      return updatedFilters;
    });
  };

  const handleValueChange = (index: number, value: string | boolean) => {
    setFilters((prevFilters) => {
      const updatedFilters = [...prevFilters];
      updatedFilters[index].value = value;
      return updatedFilters;
    });
  };

  const validateQuery = () => {
    for (const filter of filters) {
      if (!filter.field) {
        setError("Each filter must have a selected field.");
        return false;
      }
      if (!filter.operator) {
        setError("Each filter must have a selected operator.");
        return false;
      }
      if (filter.value === "" || filter.value === undefined) {
        setError("Each filter must have a valid value.");
        return false;
      }
    }
    setError(null);
    return true;
  };

  const handleSearch = () => {
    if (!validateQuery()) return;

    const query: QueryDTO = {
      groups: [
        {
          logicalOperator: "AND",
          filters,
        },
      ],
    };

    onSearch(query);
  };

  const renderInputField = (filter: Filter, index: number) => {
    const fieldDefinition = fieldDefinitions.find(
      (f) => f.name === filter.field,
    );
    if (!fieldDefinition) return null;

    switch (fieldDefinition.type) {
      case "text":
      case "number":
      case "date":
        return (
          <Input
            type={fieldDefinition.type}
            className="w-[180px]"
            placeholder="Value"
            value={filter.value as string}
            onChange={(e) => handleValueChange(index, e.target.value)}
          />
        );
      case "boolean":
        return (
          <div className="w-[180px] flex justify-center">
            <Checkbox
              checked={filter.value === "true"}
              onCheckedChange={(checked) =>
                handleValueChange(index, checked ? "true" : "false")
              }
            />
          </div>
        );
      case "select":
        return (
          <Select onValueChange={(value) => handleValueChange(index, value)}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Select" />
            </SelectTrigger>
            <SelectContent>
              {fieldDefinition.options?.map((option) => (
                <SelectItem key={option} value={option}>
                  {option}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        );
      default:
        return null;
    }
  };

  return (
    <Card className="w-[500px] flex flex-col h-full">
      <CardHeader>
        <h2 className="text-lg font-semibold">Build Your Search Query</h2>
        <p className="text-sm text-gray-500">
          Select fields, choose operators, and enter values to refine your
          search.
        </p>
      </CardHeader>
      <CardContent className="p-4 flex flex-col space-y-4 h-full">
        <div className="flex flex-col space-y-2 overflow-auto">
          {filters.map((filter, index) => {
            const currentField = fieldDefinitions.find(
              (f) => f.name === filter.field,
            );
            const operators = currentField
              ? operatorMappings[currentField.type]
              : [];

            return (
              <div key={index} className="flex items-center space-x-2">
                {/* Field Selection */}
                <Select
                  onValueChange={(value) => handleFieldChange(index, value)}
                >
                  <SelectTrigger className="w-[180px]">
                    <SelectValue placeholder="Field" />
                  </SelectTrigger>
                  <SelectContent>
                    {fieldDefinitions.map((field) => (
                      <SelectItem key={field.name} value={field.name}>
                        {field.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>

                <Select
                  onValueChange={(value) => handleOperatorChange(index, value)}
                  disabled={!filter.field}
                >
                  <SelectTrigger className="w-[100px]">
                    <SelectValue placeholder="Op" />
                  </SelectTrigger>
                  <SelectContent>
                    {operators.map((op) => (
                      <SelectItem key={op} value={op}>
                        {op}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>

                {renderInputField(filter, index)}

                <Button
                  variant="destructive"
                  size="icon"
                  onClick={() => removeFilter(index)}
                >
                  <Trash size={16} />
                </Button>
              </div>
            );
          })}
        </div>

        <Button
          variant="outline"
          size="icon"
          onClick={addFilter}
          className="self-start"
        >
          <Plus size={16} />
        </Button>

        {error && <p className="text-red-500 text-sm">{error}</p>}

        <Button onClick={handleSearch} className="w-full mt-auto">
          <Search size={16} className="mr-2" /> Search
        </Button>
      </CardContent>
    </Card>
  );
};

export default DynamicQueryBuilder;
