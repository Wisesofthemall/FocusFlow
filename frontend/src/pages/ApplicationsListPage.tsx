import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { listApplications, deleteApplication } from '../api/applications';
import { StatusBadge } from '../components/StatusBadge';
import { ErrorBanner } from '../components/ErrorBanner';
import { apiErrorMessage } from '../api/client';
import type { ApplicationStatus, JobApplication } from '../types';
import styles from './ApplicationsListPage.module.css';

const FILTERS: Array<'ALL' | ApplicationStatus> = [
  'ALL',
  'APPLIED',
  'INTERVIEWING',
  'OFFER',
  'REJECTED',
  'WITHDRAWN',
];

function formatDate(iso: string): string {
  try {
    return new Date(iso).toLocaleDateString(undefined, {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
    });
  } catch {
    return iso;
  }
}

export function ApplicationsListPage() {
  const [apps, setApps] = useState<JobApplication[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<(typeof FILTERS)[number]>('ALL');

  useEffect(() => {
    let cancelled = false;
    listApplications()
      .then((data) => {
        if (!cancelled) setApps(data);
      })
      .catch((err) => {
        if (!cancelled) setError(apiErrorMessage(err, 'Failed to load applications'));
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  const visible = useMemo(() => {
    if (filter === 'ALL') return apps;
    return apps.filter((a) => a.status === filter);
  }, [apps, filter]);

  async function onDelete(id: number) {
    if (!confirm('Delete this application?')) return;
    try {
      await deleteApplication(id);
      setApps((prev) => prev.filter((a) => a.id !== id));
    } catch (err) {
      setError(apiErrorMessage(err, 'Failed to delete'));
    }
  }

  return (
    <>
      <div className="page-header">
        <h1 className="page-title">Applications</h1>
        <Link to="/applications/new" className="btn">
          + New application
        </Link>
      </div>

      <div className={styles.filters}>
        {FILTERS.map((f) => (
          <button
            key={f}
            type="button"
            className={`${styles.filterChip} ${filter === f ? styles.filterActive : ''}`}
            onClick={() => setFilter(f)}
          >
            {f === 'ALL' ? 'All' : f.charAt(0) + f.slice(1).toLowerCase()}
          </button>
        ))}
      </div>

      <ErrorBanner message={error} />

      {loading ? (
        <div className="loading">Loading applications…</div>
      ) : visible.length === 0 ? (
        <div className={`card ${styles.empty}`}>
          <p>
            {apps.length === 0
              ? "You haven't added any applications yet."
              : 'No applications match this filter.'}
          </p>
          {apps.length === 0 && (
            <Link to="/applications/new" className="btn">
              Add your first application
            </Link>
          )}
        </div>
      ) : (
        <div className={`card ${styles.tableWrap}`}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Company</th>
                <th>Role</th>
                <th>Status</th>
                <th>Next action</th>
                <th>Priority</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {visible.map((app) => (
                <tr key={app.id}>
                  <td className={styles.strong}>{app.company}</td>
                  <td>{app.roleTitle}</td>
                  <td>
                    <StatusBadge status={app.status} />
                  </td>
                  <td>{formatDate(app.nextActionDate)}</td>
                  <td className={styles[`priority_${app.priority}`]}>{app.priority}</td>
                  <td>
                    <button
                      type="button"
                      className={styles.deleteBtn}
                      onClick={() => onDelete(app.id)}
                      aria-label="Delete application"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}
