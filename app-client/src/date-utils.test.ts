
import { expect, test } from 'vitest'
import { dateDisplayFormatter } from './date-utils'

test('should return a full year', () => {
    const input = "2023";
    expect(dateDisplayFormatter(input)).toBe("2023")
})

test('should return a month and year in format MMM-YYYY', () => {
    const input = "2023-05";
    expect(dateDisplayFormatter(input)).toBe("May-2023")
})

test('should return a month and year in format DD-MMM-YYYY', () => {
    const input = "2023-05-23";
    expect(dateDisplayFormatter(input)).toBe("23-May-2023")
})