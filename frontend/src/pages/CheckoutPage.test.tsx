/**
 * @vitest-environment jsdom
 */
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, act, cleanup } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { CheckoutPage } from './CheckoutPage';

// ---------- mocks ----------

// Mock matchMedia for jsdom
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});

// Cart with a single item: unitPrice 99900 (= $999.00), qty 1
const MOCK_CART = {
  userId: 'test-user',
  items: [
    {
      productId: 'prod-1',
      quantity: 1,
      unitPrice: 99900,
      totalPrice: 99900,
      priceAvailable: true,
    },
  ],
  subtotal: 99900,
  priceAvailable: true,
};

// Multi-item cart: two items totalling $1,498.00
const MOCK_MULTI_ITEM_CART = {
  userId: 'test-user',
  items: [
    {
      productId: 'prod-1',
      quantity: 1,
      unitPrice: 99900,
      totalPrice: 99900,
      priceAvailable: true,
    },
    {
      productId: 'prod-2',
      quantity: 2,
      unitPrice: 24950,
      totalPrice: 49900,
      priceAvailable: true,
    },
  ],
  subtotal: 149800,
  priceAvailable: true,
};

vi.mock('../api/apiClient', () => ({
  cartApi: {
    getCart: vi.fn(() => Promise.resolve(MOCK_CART)),
    clearCart: vi.fn(() => Promise.resolve()),
  },
  orderApi: {
    create: vi.fn(() => Promise.resolve({ id: 1 })),
  },
  getUserId: vi.fn(() => 'test-user'),
}));

// Re-import so we can swap the mock per test
import { cartApi } from '../api/apiClient';

// ---------- helpers ----------

function renderCheckout() {
  return render(
    <MemoryRouter initialEntries={['/checkout']}>
      <CheckoutPage />
    </MemoryRouter>,
  );
}

// ---------- tests ----------

describe('CheckoutPage — Order Summary pricing', () => {
  beforeEach(() => {
    vi.mocked(cartApi.getCart).mockResolvedValue(MOCK_CART);
  });

  afterEach(() => {
    cleanup();
    vi.clearAllMocks();
  });

  it('computes Total as exactly Subtotal + standard Shipping (reproduces screenshot bug)', async () => {
    await act(async () => {
      renderCheckout();
    });

    // Wait for cart to load and prices to render
    const subtotalEl = await screen.findByTestId('summary-subtotal');
    expect(subtotalEl.textContent).toBe('$999.00');

    // Standard shipping ($12.00) is the default
    const shippingEl = screen.getByTestId('summary-shipping');
    expect(shippingEl.textContent).toBe('$12.00');

    // Total MUST be $999.00 + $12.00 = $1,011.00
    const totalEl = screen.getByTestId('summary-total');
    expect(totalEl.textContent).toBe('$1,011.00');

    // The old hardcoded value must NOT appear anywhere
    const hardcoded = screen.queryByText('$1,252.00');
    expect(hardcoded).toBeNull();
  });

  it('updates Total when express shipping is selected', async () => {
    const user = userEvent.setup();

    await act(async () => {
      renderCheckout();
    });

    // Wait for cart data to load and subtotal to render
    const subtotalEl = await screen.findByTestId('summary-subtotal');
    expect(subtotalEl.textContent).toBe('$999.00');

    // Select express shipping
    const expressRadio = screen.getByDisplayValue('express');
    await act(async () => {
      await user.click(expressRadio);
    });

    // Shipping should now be $45.00
    const shippingEl = screen.getByTestId('summary-shipping');
    expect(shippingEl.textContent).toBe('$45.00');

    // Total should be $999.00 + $45.00 = $1,044.00
    const totalEl = screen.getByTestId('summary-total');
    expect(totalEl.textContent).toBe('$1,044.00');
  });

  it('sums all items for multi-item carts', async () => {
    vi.mocked(cartApi.getCart).mockResolvedValue(MOCK_MULTI_ITEM_CART);

    await act(async () => {
      renderCheckout();
    });

    // Subtotal: $999.00 + ($249.50 × 2 = $499.00) = $1,498.00
    const subtotalEl = await screen.findByTestId('summary-subtotal');
    expect(subtotalEl.textContent).toBe('$1,498.00');

    // Total: $1,498.00 + $12.00 = $1,510.00
    const totalEl = screen.getByTestId('summary-total');
    expect(totalEl.textContent).toBe('$1,510.00');
  });
});
