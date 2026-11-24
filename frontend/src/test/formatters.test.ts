import { describe, it, expect } from 'vitest';
import { formatCurrency, formatDate, formatQuantity, formatPercent } from '@/utils/formatters';

describe('formatters utility', () => {
  it('formats currency with default USD symbol', () => {
    expect(formatCurrency(1250.5)).toBe('$1,250.50');
    expect(formatCurrency(0)).toBe('$0.00');
    expect(formatCurrency(null)).toBe('$0.00');
  });

  it('formats quantities with unit of measure', () => {
    expect(formatQuantity(500, 'PCS')).toBe('500 PCS');
    expect(formatQuantity(1200)).toBe('1,200');
    expect(formatQuantity(null)).toBe('0');
  });

  it('formats percentages correctly', () => {
    expect(formatPercent(0.952)).toBe('95.2%');
    expect(formatPercent(1.0)).toBe('100.0%');
    expect(formatPercent(null)).toBe('0%');
  });

  it('handles date formatting gracefully', () => {
    expect(formatDate(null)).toBe('-');
    expect(formatDate('')).toBe('-');
    expect(formatDate('2026-11-23T10:00:00Z')).toContain('2026');
  });
});
