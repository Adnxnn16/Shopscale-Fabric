/**
 * @vitest-environment jsdom
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, act } from '@testing-library/react';
import App from './App';

// Mock matchMedia for jsdom
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(), // deprecated
    removeListener: vi.fn(), // deprecated
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});

describe('App Routing Resolution', () => {
  beforeEach(() => {
    // Set a fake token so ProtectedRoutes don't redirect to /login
    sessionStorage.setItem('jwt_token', 'fake-test-token');
  });

  const testRoute = (path: string, expectedHeadingText: string) => {
    it(`resolves the ${path} route correctly`, async () => {
      window.history.pushState({}, 'Test page', path);
      
      await act(async () => {
        render(<App />);
      });

      const heading = await screen.findByRole('heading', { name: new RegExp(expectedHeadingText, 'i') });
      expect(heading).toBeTruthy();
    });
  };

  testRoute('/privacy-policy', 'Privacy Policy');
  testRoute('/terms-of-service', 'Terms of Service');
  testRoute('/shipping', 'Shipping Information');
  testRoute('/contact', 'Contact Us');
});
