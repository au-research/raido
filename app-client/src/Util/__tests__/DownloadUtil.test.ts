import { escapeCsvField } from "Util/DownloadUtil";

describe('DownloadUtil test', () => {
  it('escapes the values', () => {
    expect(escapeCsvField('test value')).toBe('test value');
    expect(escapeCsvField('test,value')).toBe('"test,value"');
    expect(escapeCsvField('test "value"')).toBe('"test ""value"""');
    expect(escapeCsvField('test\nvalue')).toBe('"test\nvalue"');
    expect(escapeCsvField('"test\nvalue"')).toBe('"""test\nvalue"""');
  });
});