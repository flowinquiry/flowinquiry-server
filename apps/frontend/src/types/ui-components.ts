export interface UiAttributes {
  required?: boolean;
  className?: string;
  testId?: string;
}

export type FilterOption = {
  label: string;
  value: string;
  icon?: React.ComponentType<{ className?: string }>;
};
