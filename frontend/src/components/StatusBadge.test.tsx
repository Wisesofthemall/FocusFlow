import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { StatusBadge } from './StatusBadge';
import type { ApplicationStatus } from '../types';

describe('StatusBadge', () => {
  const cases: Array<{ status: ApplicationStatus; label: string }> = [
    { status: 'APPLIED', label: 'Applied' },
    { status: 'INTERVIEWING', label: 'Interviewing' },
    { status: 'OFFER', label: 'Offer' },
    { status: 'REJECTED', label: 'Rejected' },
    { status: 'WITHDRAWN', label: 'Withdrawn' },
  ];

  it.each(cases)('renders the human label for $status', ({ status, label }) => {
    render(<StatusBadge status={status} />);
    expect(screen.getByText(label)).toBeInTheDocument();
  });

  it('applies the badge base class plus a status-specific class', () => {
    const { container } = render(<StatusBadge status="OFFER" />);
    const span = container.querySelector('span');
    expect(span).not.toBeNull();
    expect(span!.className.split(' ').length).toBeGreaterThanOrEqual(2);
  });
});
