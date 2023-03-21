

export function escapeCsvField(text: string|undefined): string {
  if (!text) {
    return '';
  }

  /* If the field contains a comma, a double quote, or a newline character,
  enclose it in double quotes and escape any double quotes inside the field. */
  if (text.includes(',') || text.includes('"') || text.includes('\n')) {
    return `"${text.replace(/"/g, '""')}"`;
  }

  return text;
}