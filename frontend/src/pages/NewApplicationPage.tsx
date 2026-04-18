import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { createApplication } from '../api/applications';
import { ErrorBanner } from '../components/ErrorBanner';
import { apiErrorMessage } from '../api/client';
import type { ApplicationPriority, ApplicationStatus } from '../types';
import styles from './NewApplicationPage.module.css';

function defaultDateTimeLocal(): string {
  const d = new Date();
  d.setDate(d.getDate() + 7);
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

export function NewApplicationPage() {
  const navigate = useNavigate();
  const [company, setCompany] = useState('');
  const [roleTitle, setRoleTitle] = useState('');
  const [status, setStatus] = useState<ApplicationStatus>('APPLIED');
  const [priority, setPriority] = useState<ApplicationPriority>('MEDIUM');
  const [nextActionDate, setNextActionDate] = useState(defaultDateTimeLocal());
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const iso = nextActionDate.length === 16 ? `${nextActionDate}:00` : nextActionDate;
      await createApplication({
        company: company.trim(),
        roleTitle: roleTitle.trim(),
        status,
        priority,
        nextActionDate: iso,
      });
      navigate('/applications');
    } catch (err) {
      setError(apiErrorMessage(err, 'Could not save application'));
      setSubmitting(false);
    }
  }

  return (
    <>
      <div className="page-header">
        <h1 className="page-title">New application</h1>
      </div>

      <div className={`card ${styles.formCard}`}>
        <ErrorBanner message={error} />
        <form onSubmit={onSubmit} className={styles.form}>
          <div>
            <label htmlFor="company">Company</label>
            <input
              id="company"
              required
              placeholder="e.g. Acme Corp"
              value={company}
              onChange={(e) => setCompany(e.target.value)}
            />
          </div>
          <div>
            <label htmlFor="role">Role title</label>
            <input
              id="role"
              required
              placeholder="e.g. Software Engineer Intern"
              value={roleTitle}
              onChange={(e) => setRoleTitle(e.target.value)}
            />
          </div>
          <div className={styles.row}>
            <div>
              <label htmlFor="status">Status</label>
              <select
                id="status"
                value={status}
                onChange={(e) => setStatus(e.target.value as ApplicationStatus)}
              >
                <option value="APPLIED">Applied</option>
                <option value="INTERVIEWING">Interviewing</option>
                <option value="OFFER">Offer</option>
                <option value="REJECTED">Rejected</option>
                <option value="WITHDRAWN">Withdrawn</option>
              </select>
            </div>
            <div>
              <label htmlFor="priority">Priority</label>
              <select
                id="priority"
                value={priority}
                onChange={(e) => setPriority(e.target.value as ApplicationPriority)}
              >
                <option value="HIGH">High</option>
                <option value="MEDIUM">Medium</option>
                <option value="LOW">Low</option>
              </select>
            </div>
          </div>
          <div>
            <label htmlFor="nextActionDate">Next action date</label>
            <input
              id="nextActionDate"
              type="datetime-local"
              required
              value={nextActionDate}
              onChange={(e) => setNextActionDate(e.target.value)}
            />
          </div>
          <div className={styles.actions}>
            <button
              type="button"
              className="btn btn-ghost"
              onClick={() => navigate('/applications')}
            >
              Cancel
            </button>
            <button type="submit" className="btn" disabled={submitting}>
              {submitting ? 'Saving…' : 'Save application'}
            </button>
          </div>
        </form>
      </div>
    </>
  );
}
