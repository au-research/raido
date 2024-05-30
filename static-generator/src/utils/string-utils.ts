/**
 * Utility function to convert a camelCase string to a more readable format.
 * @param input - The camelCase string to format.
 * @returns The formatted string.
 */
export function formatCamelCase(input: string): string {
  // Insert a space before each uppercase letter and convert the string to lowercase
  const result = input.replace(/([A-Z])/g, " $1").toLowerCase();

  // Capitalize the first letter of the formatted string
  return result.charAt(0).toUpperCase() + result.slice(1);
}
