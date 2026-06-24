import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api/apiClient';
import { apiClient } from '../api/apiClient';

type Tab = 'login' | 'signup';

export function LoginPage() {
  const [tab, setTab] = useState<Tab>('login');

  // Login state
  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');

  // Signup state
  const [signupName, setSignupName] = useState('');
  const [signupEmail, setSignupEmail] = useState('');
  const [signupPassword, setSignupPassword] = useState('');
  const [signupConfirm, setSignupConfirm] = useState('');

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const storeToken = (token: string) => {
    sessionStorage.setItem('jwt_token', token);
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const data = await authApi.login(loginEmail, loginPassword);
      storeToken(data.token);
      navigate('/');
    } catch {
      setError('Invalid credentials. Please check your email and password.');
    } finally {
      setLoading(false);
    }
  };

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();
    if (signupPassword !== signupConfirm) {
      setError('Passwords do not match.');
      return;
    }
    if (signupPassword.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }
    setLoading(true);
    setError('');
    try {
      const data = await authApi.signup(signupName, signupEmail, signupPassword);
      storeToken(data.token);
      navigate('/');
    } catch {
      setError('Could not create account. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-root">
      {/* Animated background orbs */}
      <div className="auth-orb auth-orb-1" />
      <div className="auth-orb auth-orb-2" />
      <div className="auth-orb auth-orb-3" />

      {/* Particle grid */}
      <div className="auth-grid" />

      <div className="auth-card-wrap">
        {/* ── Animated Logo ── */}
        <div className="auth-logo-wrap">
          <div className="auth-logo-ring auth-logo-ring-outer" />
          <div className="auth-logo-ring auth-logo-ring-inner" />
          <div className="auth-logo-hex">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg" className="auth-hex-svg">
              <path
                d="M24 4L42 14V34L24 44L6 34V14L24 4Z"
                stroke="url(#hexGrad)"
                strokeWidth="2"
                fill="url(#hexFill)"
                className="auth-hex-path"
              />
              <text x="24" y="28" textAnchor="middle" fontSize="13" fontWeight="700" fill="white" fontFamily="Inter,sans-serif">SS</text>
              <defs>
                <linearGradient id="hexGrad" x1="6" y1="4" x2="42" y2="44" gradientUnits="userSpaceOnUse">
                  <stop stopColor="#ff6b35" />
                  <stop offset="1" stopColor="#ffb59d" />
                </linearGradient>
                <linearGradient id="hexFill" x1="6" y1="4" x2="42" y2="44" gradientUnits="userSpaceOnUse">
                  <stop stopColor="#ff6b35" stopOpacity="0.15" />
                  <stop offset="1" stopColor="#ffb59d" stopOpacity="0.05" />
                </linearGradient>
              </defs>
            </svg>
          </div>
        </div>

        {/* Brand name */}
        <div className="auth-brand-wrap">
          <h1 className="auth-brand-title">
            {'SHOPSCALE'.split('').map((ch, i) => (
              <span key={i} className="auth-brand-char" style={{ animationDelay: `${0.05 * i + 0.4}s` }}>
                {ch}
              </span>
            ))}
          </h1>
          <div className="auth-brand-underline" />
          <p className="auth-brand-sub">Enterprise Microservices Commerce Platform</p>
        </div>

        {/* ── Card ── */}
        <div className="auth-card">
          {/* Tab switcher */}
          <div className="auth-tabs">
            <button
              id="tab-login"
              className={`auth-tab ${tab === 'login' ? 'auth-tab-active' : ''}`}
              onClick={() => { setTab('login'); setError(''); }}
            >
              Sign In
            </button>
            <button
              id="tab-signup"
              className={`auth-tab ${tab === 'signup' ? 'auth-tab-active' : ''}`}
              onClick={() => { setTab('signup'); setError(''); }}
            >
              Create Account
            </button>
            <div className="auth-tab-indicator" style={{ transform: tab === 'login' ? 'translateX(0)' : 'translateX(100%)' }} />
          </div>

          {/* Error banner */}
          {error && (
            <div className="auth-error">
              <span className="auth-error-icon">⚠</span>
              {error}
            </div>
          )}

          {/* ── Login Form ── */}
          {tab === 'login' && (
            <form id="login-form" className="auth-form" onSubmit={handleLogin}>
              <div className="auth-field">
                <input
                  id="login-email"
                  className="auth-input"
                  type="email"
                  placeholder=" "
                  value={loginEmail}
                  onChange={e => setLoginEmail(e.target.value)}
                  required
                  autoComplete="email"
                />
                <label className="auth-label" htmlFor="login-email">Email Address</label>
                <div className="auth-field-line" />
              </div>

              <div className="auth-field">
                <input
                  id="login-password"
                  className="auth-input"
                  type="password"
                  placeholder=" "
                  value={loginPassword}
                  onChange={e => setLoginPassword(e.target.value)}
                  required
                  autoComplete="current-password"
                />
                <label className="auth-label" htmlFor="login-password">Password</label>
                <div className="auth-field-line" />
              </div>

              <button id="login-submit" type="submit" disabled={loading} className="auth-btn">
                {loading
                  ? <span className="auth-spinner" />
                  : <><span className="auth-btn-text">SIGN IN</span><span className="auth-btn-arrow">→</span></>
                }
              </button>

              <p className="auth-switch-hint">
                Don't have an account?{' '}
                <button type="button" className="auth-switch-link" onClick={() => { setTab('signup'); setError(''); }}>
                  Create one
                </button>
              </p>
            </form>
          )}

          {/* ── Signup Form ── */}
          {tab === 'signup' && (
            <form id="signup-form" className="auth-form" onSubmit={handleSignup}>
              <div className="auth-field">
                <input
                  id="signup-name"
                  className="auth-input"
                  type="text"
                  placeholder=" "
                  value={signupName}
                  onChange={e => setSignupName(e.target.value)}
                  required
                  autoComplete="name"
                />
                <label className="auth-label" htmlFor="signup-name">Full Name</label>
                <div className="auth-field-line" />
              </div>

              <div className="auth-field">
                <input
                  id="signup-email"
                  className="auth-input"
                  type="email"
                  placeholder=" "
                  value={signupEmail}
                  onChange={e => setSignupEmail(e.target.value)}
                  required
                  autoComplete="email"
                />
                <label className="auth-label" htmlFor="signup-email">Email Address</label>
                <div className="auth-field-line" />
              </div>

              <div className="auth-field">
                <input
                  id="signup-password"
                  className="auth-input"
                  type="password"
                  placeholder=" "
                  value={signupPassword}
                  onChange={e => setSignupPassword(e.target.value)}
                  required
                  autoComplete="new-password"
                />
                <label className="auth-label" htmlFor="signup-password">Password (min 6 chars)</label>
                <div className="auth-field-line" />
              </div>

              <div className="auth-field">
                <input
                  id="signup-confirm"
                  className="auth-input"
                  type="password"
                  placeholder=" "
                  value={signupConfirm}
                  onChange={e => setSignupConfirm(e.target.value)}
                  required
                  autoComplete="new-password"
                />
                <label className="auth-label" htmlFor="signup-confirm">Confirm Password</label>
                <div className="auth-field-line" />
              </div>

              <button id="signup-submit" type="submit" disabled={loading} className="auth-btn">
                {loading
                  ? <span className="auth-spinner" />
                  : <><span className="auth-btn-text">CREATE ACCOUNT</span><span className="auth-btn-arrow">→</span></>
                }
              </button>

              <p className="auth-switch-hint">
                Already have an account?{' '}
                <button type="button" className="auth-switch-link" onClick={() => { setTab('login'); setError(''); }}>
                  Sign in
                </button>
              </p>
            </form>
          )}
        </div>

        <p className="auth-footer">
          Powered by Spring Boot · Kafka · Redis · React
        </p>
      </div>
    </div>
  );
}
