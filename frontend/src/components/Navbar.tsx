import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import styles from './Navbar.module.css';

export function Navbar() {
  const { user, token, logout } = useAuth();
  const navigate = useNavigate();

  function onLogout() {
    logout();
    navigate('/login');
  }

  return (
    <nav className={styles.nav}>
      <Link to="/" className={styles.brand}>
        <span className={styles.brandMark}>P</span>
        ProPath
      </Link>
      {token && (
        <div className={styles.links}>
          <NavLink to="/" end className={({ isActive }) => (isActive ? styles.active : '')}>
            Dashboard
          </NavLink>
          <NavLink
            to="/applications"
            className={({ isActive }) => (isActive ? styles.active : '')}
          >
            Applications
          </NavLink>
          <NavLink
            to="/applications/new"
            className={({ isActive }) => (isActive ? styles.active : '')}
          >
            + New
          </NavLink>
        </div>
      )}
      <div className={styles.right}>
        {user ? (
          <>
            <span className={styles.userName}>{user.name}</span>
            <button type="button" onClick={onLogout} className={styles.logout}>
              Sign out
            </button>
          </>
        ) : (
          <Link to="/login" className={styles.loginLink}>
            Sign in
          </Link>
        )}
      </div>
    </nav>
  );
}
