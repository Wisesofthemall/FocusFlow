import styles from './ErrorBanner.module.css';

export function ErrorBanner({ message }: { message: string | null }) {
  if (!message) return null;
  return (
    <div role="alert" className={styles.banner}>
      {message}
    </div>
  );
}
