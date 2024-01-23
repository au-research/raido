export function parseJwtDate(date: string | number | undefined): Date | undefined {
  if (!date || isNaN(Number(date))) {
    return undefined;
  }
  return new Date(Number(date) * 1000);
}

export function addDays(d: Date | undefined, days: number): Date {
  const date = d ? new Date(d) : new Date();
  date.setDate(date.getDate() + days);
  return date;
}