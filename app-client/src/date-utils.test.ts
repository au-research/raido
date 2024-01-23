
import { expect, test } from 'vitest'
import { dateDisplayFormatter } from './date-utils'

test('should return a full year', () => {
    const input = "2020";
    expect(dateDisplayFormatter(input)).toBe("2020")
})

test('should return a month and year in format YYYY-MMM', () => {
    const input = "2020-06";
    expect(dateDisplayFormatter(input)).toBe("Jun-2020")
})

test('should return a month and year in format YYYY-MMM', () => {
    const input = "2020-12-01";
    expect(dateDisplayFormatter(input)).toBe("01-Dec-2020")
})