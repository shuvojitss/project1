// Formatters for currency, date, and numbers per item 41 & 53

export function formatCurrency(amount: number | null | undefined, currency: string = 'USD'): string {
  if (amount == null) return '$0.00';
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(amount);
}

export function formatDate(dateString: string | null | undefined): string {
  if (!dateString) return '-';
  try {
    const d = new Date(dateString);
    if (isNaN(d.getTime())) return dateString;
    return d.toLocaleDateString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  } catch {
    return dateString;
  }
}

export function formatDateTime(dateString: string | null | undefined): string {
  if (!dateString) return '-';
  try {
    const d = new Date(dateString);
    if (isNaN(d.getTime())) return dateString;
    return d.toLocaleString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return dateString;
  }
}

export function formatQuantity(qty: number | null | undefined, uom: string = ''): string {
  if (qty == null) return '0';
  const formatted = new Intl.NumberFormat('en-US', {
    maximumFractionDigits: 2,
  }).format(qty);
  return uom ? `${formatted} ${uom}` : formatted;
}

export function formatPercent(rate: number | null | undefined): string {
  if (rate == null) return '0%';
  return `${(rate * 100).toFixed(1)}%`;
}
