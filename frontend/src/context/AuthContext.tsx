import {
  createContext,
  useContext,
  useEffect,
  useReducer,
  type ReactNode,
} from 'react';
import { TOKEN_KEY, USER_KEY } from '../api/client';
import * as authApi from '../api/auth';
import type { AuthUser } from '../types';

type AuthState = {
  token: string | null;
  user: AuthUser | null;
  loading: boolean;
};

type AuthAction =
  | { type: 'HYDRATE'; token: string | null; user: AuthUser | null }
  | { type: 'LOGIN_SUCCESS'; token: string; user: AuthUser }
  | { type: 'LOGOUT' };

const initialState: AuthState = { token: null, user: null, loading: true };

function reducer(_state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case 'HYDRATE':
      return { token: action.token, user: action.user, loading: false };
    case 'LOGIN_SUCCESS':
      return { token: action.token, user: action.user, loading: false };
    case 'LOGOUT':
      return { token: null, user: null, loading: false };
  }
}

interface AuthContextValue {
  token: string | null;
  user: AuthUser | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    const token = localStorage.getItem(TOKEN_KEY);
    const rawUser = localStorage.getItem(USER_KEY);
    const user = rawUser ? (JSON.parse(rawUser) as AuthUser) : null;
    dispatch({ type: 'HYDRATE', token, user });
  }, []);

  async function login(email: string, password: string) {
    const res = await authApi.login(email, password);
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, JSON.stringify(res.user));
    dispatch({ type: 'LOGIN_SUCCESS', token: res.token, user: res.user });
  }

  async function register(name: string, email: string, password: string) {
    const res = await authApi.register(name, email, password);
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, JSON.stringify(res.user));
    dispatch({ type: 'LOGIN_SUCCESS', token: res.token, user: res.user });
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    dispatch({ type: 'LOGOUT' });
  }

  return (
    <AuthContext.Provider
      value={{
        token: state.token,
        user: state.user,
        loading: state.loading,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
