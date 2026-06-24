/**
 * @vitest-environment jsdom
 */
import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';
import { render, act } from '@testing-library/react';
import { ProductListPage } from './ProductListPage';
import { BrowserRouter } from 'react-router-dom';

// Mock productApi to avoid real network calls
vi.mock('../api/apiClient', () => ({
  productApi: {
    getAll: vi.fn().mockResolvedValue([]),
    getFeatured: vi.fn().mockResolvedValue([]),
  },
  cartApi: {
    addItem: vi.fn(),
  },
  getUserId: vi.fn().mockReturnValue('test-user'),
}));

describe('ProductListPage Intro Animation', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    sessionStorage.clear();
  });

  afterEach(() => {
    vi.clearAllTimers();
    vi.useRealTimers();
  });

  const renderComponent = () => {
    return render(
      <BrowserRouter>
        <ProductListPage />
      </BrowserRouter>
    );
  };

  it('runs animation on fresh session (flag absent)', async () => {
    // Flag is absent initially because of sessionStorage.clear() in beforeEach
    renderComponent();

    // The intro stage should be present initially
    let introStage = document.getElementById('intro-stage');
    expect(introStage).toBeTruthy();

    // Advance timers by 3500ms
    await act(async () => {
      vi.advanceTimersByTime(3500);
    });

    // The intro stage should be removed
    introStage = document.getElementById('intro-stage');
    expect(introStage).toBeFalsy();

    // Session storage flag should be set
    expect(sessionStorage.getItem('introAnimationPlayed')).toBe('true');
  });

  it('skips animation if already played in session (flag present)', async () => {
    // Simulate that the animation has already played in this session
    sessionStorage.setItem('introAnimationPlayed', 'true');

    renderComponent();

    // The intro stage should NOT be present initially
    const introStage = document.getElementById('intro-stage');
    expect(introStage).toBeFalsy();
  });
});
