import { useEffect, useReducer } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getDashboard } from '../api/dashboard';
import { getRemoteJobs } from '../api/externalJobs';
import { StatusBadge } from '../components/StatusBadge';
import { apiErrorMessage } from '../api/client';
import type { CalendarEvent, JobApplication, RemoteOkJob } from '../types';
import styles from './DashboardPage.module.css';

type Slice<T> =
  | { state: 'loading' }
  | { state: 'ok'; data: T }
  | { state: 'error'; message: string };

interface DashState {
  applications: Slice<JobApplication[]>;
  events: Slice<CalendarEvent[]>;
  jobs: Slice<RemoteOkJob[]>;
}

type Action =
  | { type: 'DASHBOARD_OK'; apps: JobApplication[]; events: CalendarEvent[] }
  | { type: 'DASHBOARD_ERR'; message: string }
  | { type: 'JOBS_OK'; jobs: RemoteOkJob[] }
  | { type: 'JOBS_ERR'; message: string };

const initial: DashState = {
  applications: { state: 'loading' },
  events: { state: 'loading' },
  jobs: { state: 'loading' },
};

function reducer(state: DashState, action: Action): DashState {
  switch (action.type) {
    case 'DASHBOARD_OK':
      return {
        ...state,
        applications: { state: 'ok', data: action.apps },
        events: { state: 'ok', data: action.events },
      };
    case 'DASHBOARD_ERR':
      return {
        ...state,
        applications: { state: 'error', message: action.message },
        events: { state: 'error', message: action.message },
      };
    case 'JOBS_OK':
      return { ...state, jobs: { state: 'ok', data: action.jobs } };
    case 'JOBS_ERR':
      return { ...state, jobs: { state: 'error', message: action.message } };
  }
}

function formatDateTime(iso: string): string {
  try {
    return new Date(iso).toLocaleString(undefined, {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
    });
  } catch {
    return iso;
  }
}

export function DashboardPage() {
  const { user } = useAuth();
  const [state, dispatch] = useReducer(reducer, initial);

  useEffect(() => {
    let cancelled = false;
    getDashboard()
      .then((s) => {
        if (!cancelled)
          dispatch({
            type: 'DASHBOARD_OK',
            apps: s.applicationsDueInRange,
            events: s.eventsInRange,
          });
      })
      .catch((err) => {
        if (!cancelled)
          dispatch({ type: 'DASHBOARD_ERR', message: apiErrorMessage(err, 'Failed to load') });
      });
    getRemoteJobs('engineer', 6)
      .then((j) => {
        if (!cancelled) dispatch({ type: 'JOBS_OK', jobs: j });
      })
      .catch((err) => {
        if (!cancelled)
          dispatch({ type: 'JOBS_ERR', message: apiErrorMessage(err, 'Job feed unavailable') });
      });
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <>
      <div className={styles.welcome}>
        <h1 className={styles.welcomeHeading}>
          Welcome back{user?.name ? `, ${user.name.split(' ')[0]}` : ''}
        </h1>
        <p className={styles.welcomeSub}>Here&apos;s what&apos;s on your plate this week.</p>
      </div>

      <div className={styles.grid}>
        {/* Applications due */}
        <section className={`card ${styles.cardFill}`}>
          <div className="card-header">
            <h2 className="card-title">Due this week</h2>
            <Link to="/applications" className={styles.cardLink}>
              View all →
            </Link>
          </div>
          <ApplicationsCard slice={state.applications} />
        </section>

        {/* Calendar events */}
        <section className={`card ${styles.cardFill}`}>
          <div className="card-header">
            <h2 className="card-title">Upcoming events</h2>
          </div>
          <EventsCard slice={state.events} />
        </section>

        {/* External API: RemoteOK */}
        <section className={`card ${styles.cardFill}`}>
          <div className="card-header">
            <h2 className="card-title">Trending remote jobs</h2>
            <span className={styles.sourceTag}>via RemoteOK</span>
          </div>
          <RemoteJobsCard slice={state.jobs} />
        </section>
      </div>
    </>
  );
}

function ApplicationsCard({ slice }: { slice: Slice<JobApplication[]> }) {
  if (slice.state === 'loading') return <p className="muted">Loading…</p>;
  if (slice.state === 'error') return <p className="muted">{slice.message}</p>;
  if (slice.data.length === 0)
    return (
      <p className="muted">
        Nothing due in the next 7 days.{' '}
        <Link to="/applications/new">Add an application</Link>
      </p>
    );
  return (
    <ul className={styles.list}>
      {slice.data.slice(0, 5).map((a) => (
        <li key={a.id} className={styles.listItem}>
          <div>
            <div className={styles.itemTitle}>{a.company}</div>
            <div className={styles.itemMeta}>{a.roleTitle}</div>
          </div>
          <div className={styles.itemRight}>
            <StatusBadge status={a.status} />
            <div className={styles.itemDate}>{formatDateTime(a.nextActionDate)}</div>
          </div>
        </li>
      ))}
    </ul>
  );
}

function EventsCard({ slice }: { slice: Slice<CalendarEvent[]> }) {
  if (slice.state === 'loading') return <p className="muted">Loading…</p>;
  if (slice.state === 'error') return <p className="muted">{slice.message}</p>;
  if (slice.data.length === 0)
    return <p className="muted">No upcoming events in the next 7 days.</p>;
  return (
    <ul className={styles.list}>
      {slice.data.slice(0, 5).map((e) => (
        <li key={e.id} className={styles.listItem}>
          <div>
            <div className={styles.itemTitle}>{e.title}</div>
            <div className={styles.itemMeta}>
              {e.source === 'GOOGLE' ? 'Google Calendar' : 'Manual'}
            </div>
          </div>
          <div className={styles.itemRight}>
            <div className={styles.itemDate}>{formatDateTime(e.startsAt)}</div>
          </div>
        </li>
      ))}
    </ul>
  );
}

function RemoteJobsCard({ slice }: { slice: Slice<RemoteOkJob[]> }) {
  if (slice.state === 'loading') return <p className="muted">Loading…</p>;
  if (slice.state === 'error')
    return <p className="muted">Job feed unavailable right now.</p>;
  if (slice.data.length === 0)
    return <p className="muted">No trending roles available right now.</p>;
  return (
    <ul className={styles.list}>
      {slice.data.map((j) => (
        <li key={j.id} className={styles.listItem}>
          <div>
            <div className={styles.itemTitle}>{j.position ?? 'Open role'}</div>
            <div className={styles.itemMeta}>
              {j.company ?? 'Remote'}
              {j.location ? ` · ${j.location}` : ''}
            </div>
          </div>
          <div className={styles.itemRight}>
            {j.url && (
              <a
                className={styles.jobLink}
                href={j.url}
                target="_blank"
                rel="noreferrer noopener"
              >
                View →
              </a>
            )}
          </div>
        </li>
      ))}
    </ul>
  );
}
