import type { ApplicationStatus } from '../types';
import styles from './StatusBadge.module.css';

const classByStatus: Record<ApplicationStatus, string> = {
  APPLIED: styles.applied,
  INTERVIEWING: styles.interviewing,
  OFFER: styles.offer,
  REJECTED: styles.rejected,
  WITHDRAWN: styles.withdrawn,
};

const labelByStatus: Record<ApplicationStatus, string> = {
  APPLIED: 'Applied',
  INTERVIEWING: 'Interviewing',
  OFFER: 'Offer',
  REJECTED: 'Rejected',
  WITHDRAWN: 'Withdrawn',
};

export function StatusBadge({ status }: { status: ApplicationStatus }) {
  return (
    <span className={`${styles.badge} ${classByStatus[status]}`}>{labelByStatus[status]}</span>
  );
}
