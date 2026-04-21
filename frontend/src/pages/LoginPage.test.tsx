import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { LoginPage } from './LoginPage';
import { AuthProvider } from '../context/AuthContext';
import { TOKEN_KEY } from '../api/client';
import * as authApi from '../api/auth';

vi.mock('../api/auth');

function renderLogin() {
  return render(
    <AuthProvider>
      <MemoryRouter initialEntries={['/login']}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<div>Dashboard Home</div>} />
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );
}

describe('LoginPage', () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it('logs the user in, stores the token, and navigates to /', async () => {
    vi.mocked(authApi.login).mockResolvedValue({
      token: 'jwt-from-server',
      user: { id: 42, name: 'Demo User', email: 'demo@propath.local' },
    });

    const user = userEvent.setup();
    renderLogin();

    await waitFor(() => {
      expect(screen.getByRole('heading', { name: /welcome back/i })).toBeInTheDocument();
    });

    await user.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() => {
      expect(screen.getByText('Dashboard Home')).toBeInTheDocument();
    });

    expect(authApi.login).toHaveBeenCalledWith('demo@propath.local', 'Passw0rd!');
    expect(localStorage.getItem(TOKEN_KEY)).toBe('jwt-from-server');
  });

  it('shows an error banner when the login API rejects', async () => {
    vi.mocked(authApi.login).mockRejectedValue(new Error('Invalid credentials'));

    const user = userEvent.setup();
    renderLogin();

    await waitFor(() => {
      expect(screen.getByRole('heading', { name: /welcome back/i })).toBeInTheDocument();
    });

    await user.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() => {
      expect(screen.getByText(/invalid credentials|login failed/i)).toBeInTheDocument();
    });

    expect(localStorage.getItem(TOKEN_KEY)).toBeNull();
  });
});
