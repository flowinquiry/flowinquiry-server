/**
 * Utility function to generate data-testid attributes for components.
 *
 * This function takes a props object and an optional testId string,
 * and returns a new object that includes all the original props plus
 * a 'data-testid' property if a testId was provided.
 *
 * Using data-testid attributes helps with selecting elements in automated tests,
 * making tests more reliable by avoiding selection based on CSS classes or DOM structure
 * which might change more frequently.
 *
 * @example
 * // Basic usage with a React component
 * <Button {...withTestId(buttonProps, 'submit-button')}>Submit</Button>
 *
 * @param props - The original props object to be extended
 * @param testId - Optional test ID to be added as 'data-testid'
 * @returns A new object with all original props and optionally the data-testid property
 */
export function withTestId<T extends object>(
  props: T,
  testId?: string,
): T & { "data-testid"?: string } {
  return testId ? { ...props, "data-testid": testId } : props;
}
