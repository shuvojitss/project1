import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { StatusBadge } from '@/components/StatusBadge';

describe('StatusBadge component', () => {
  it('renders status text correctly', () => {
    render(<StatusBadge status="CONFIRMED" />);
    expect(screen.getByText('CONFIRMED')).toBeInTheDocument();
  });

  it('replaces underscores with spaces', () => {
    render(<StatusBadge status="PENDING_APPROVAL" />);
    expect(screen.getByText('PENDING APPROVAL')).toBeInTheDocument();
  });

  it('returns null when status is empty', () => {
    const { container } = render(<StatusBadge status={null} />);
    expect(container.firstChild).toBeNull();
  });
});
