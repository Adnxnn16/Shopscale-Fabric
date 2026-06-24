import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { orderApi, cartApi, getUserId, type Cart } from '../api/apiClient';

export function CheckoutPage() {
  const [formData, setFormData] = useState({
    fname: '',
    lname: '',
    email: '',
    address: '',
    city: '',
    state: '',
    zip: '',
    phone: '',
    shipping: 'standard',
    notes: ''
  });
  const [submitting, setSubmitting] = useState(false);
  const [cart, setCart] = useState<Cart | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    cartApi.getCart(getUserId()).then(setCart).catch(console.error);
  }, []);

  // Pricing constants — all values in cents to avoid float precision issues
  const SHIPPING_RATES: Record<string, number> = { standard: 1200, express: 4500 };
  const shippingCost = SHIPPING_RATES[formData.shipping] ?? 1200;
  const subtotal = cart
    ? cart.items.reduce((s, i) => s + (i.unitPrice ?? 0) * i.quantity, 0)
    : 0;
  const total = subtotal + shippingCost;
  const formatCents = (cents: number) =>
    (cents / 100).toLocaleString('en-US', { style: 'currency', currency: 'USD' });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({
      ...formData,
      [e.target.id || e.target.name]: e.target.value
    });
  };

  const handleCheckout = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      if (!cart || cart.items.length === 0) {
        throw new Error('Cart is empty');
      }
      
      const orders = [];
      for (const item of cart.items) {
        const order = await orderApi.create(getUserId(), item.productId, item.quantity);
        orders.push(order);
      }
      
      await cartApi.clearCart(getUserId());
      navigate(`/order-status/${orders[0].id}`);
    } catch (err) {
      console.error(err);
      alert('Failed to place order.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="flex flex-col lg:flex-row gap-gutter">
      {/* Left Column: Checkout Forms (65%) */}
      <div className="w-full lg:w-[65%] flex flex-col gap-12">
        <form id="checkout-form" onSubmit={handleCheckout}>
          {/* Contact Information */}
          <section className="stagger-reveal reveal-delay-1 mb-12">
            <h2 className="font-headline-lg text-headline-lg-mobile md:text-headline-lg mb-6 flex items-center gap-3">
              <span className="material-symbols-outlined text-primary">contact_mail</span>
              Contact Information
            </h2>
            <div className="input-container">
              <input className="floating-input" id="email" placeholder=" " required type="email" value={formData.email} onChange={handleInputChange} />
              <label className="floating-label" htmlFor="email">Email Address</label>
            </div>
            <div className="flex items-center gap-2 mt-2">
              <input className="rounded border-outline-variant bg-surface text-primary focus:ring-primary" id="newsletter" type="checkbox" />
              <label className="font-body-sm text-body-sm text-on-surface-variant" htmlFor="newsletter">Keep me updated on new textile drops and technical news</label>
            </div>
          </section>

          {/* Shipping Information */}
          <section className="stagger-reveal reveal-delay-2 mb-12">
            <h2 className="font-headline-lg text-headline-lg-mobile md:text-headline-lg mb-6 flex items-center gap-3">
              <span className="material-symbols-outlined text-primary">local_shipping</span>
              Shipping Information
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="input-container">
                <input className="floating-input" id="fname" placeholder=" " required type="text" value={formData.fname} onChange={handleInputChange} />
                <label className="floating-label" htmlFor="fname">First Name</label>
              </div>
              <div className="input-container">
                <input className="floating-input" id="lname" placeholder=" " required type="text" value={formData.lname} onChange={handleInputChange} />
                <label className="floating-label" htmlFor="lname">Last Name</label>
              </div>
            </div>
            <div className="input-container">
              <input className="floating-input" id="address" placeholder=" " required type="text" value={formData.address} onChange={handleInputChange} />
              <label className="floating-label" htmlFor="address">Address</label>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="input-container col-span-1 md:col-span-1">
                <input className="floating-input" id="city" placeholder=" " required type="text" value={formData.city} onChange={handleInputChange} />
                <label className="floating-label" htmlFor="city">City</label>
              </div>
              <div className="input-container">
                <input className="floating-input" id="state" placeholder=" " required type="text" value={formData.state} onChange={handleInputChange} />
                <label className="floating-label" htmlFor="state">State / Province</label>
              </div>
              <div className="input-container">
                <input className="floating-input" id="zip" placeholder=" " required type="text" value={formData.zip} onChange={handleInputChange} />
                <label className="floating-label" htmlFor="zip">ZIP / Postal Code</label>
              </div>
            </div>
            <div className="input-container">
              <input className="floating-input" id="phone" placeholder=" " type="tel" value={formData.phone} onChange={handleInputChange} />
              <label className="floating-label" htmlFor="phone">Phone (Optional)</label>
            </div>
          </section>

          {/* Delivery Method */}
          <section className="stagger-reveal reveal-delay-3 mb-12">
            <h2 className="font-headline-lg text-headline-lg-mobile md:text-headline-lg mb-6 flex items-center gap-3">
              <span className="material-symbols-outlined text-primary">speed</span>
              Delivery Method
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <label className="glass-panel p-6 rounded cursor-pointer relative group border-2 border-transparent transition-all hover:border-outline-variant has-[:checked]:border-primary-container has-[:checked]:bg-surface-container-high">
                <input className="absolute top-4 right-4 text-primary-container focus:ring-primary-container" name="shipping" type="radio" value="standard" checked={formData.shipping === 'standard'} onChange={handleInputChange} />
                <div className="font-label-caps text-label-caps text-primary mb-1">Standard Logistics</div>
                <div className="font-body-md text-body-md text-on-surface font-semibold">5-7 Business Days</div>
                <div className="font-body-sm text-body-sm text-on-surface-variant mt-1">$12.00</div>
              </label>
              <label className="glass-panel p-6 rounded cursor-pointer relative group border-2 border-transparent transition-all hover:border-outline-variant has-[:checked]:border-primary-container has-[:checked]:bg-surface-container-high">
                <input className="absolute top-4 right-4 text-primary-container focus:ring-primary-container" name="shipping" type="radio" value="express" checked={formData.shipping === 'express'} onChange={handleInputChange} />
                <div className="font-label-caps text-label-caps text-primary mb-1">Priority Freight</div>
                <div className="font-body-md text-body-md text-on-surface font-semibold">1-2 Business Days</div>
                <div className="font-body-sm text-body-sm text-on-surface-variant mt-1">$45.00</div>
              </label>
            </div>
          </section>

          {/* Order Notes */}
          <section className="stagger-reveal reveal-delay-4 mb-12">
            <h2 className="font-headline-lg text-headline-lg-mobile md:text-headline-lg mb-6 flex items-center gap-3">
              <span className="material-symbols-outlined text-primary">edit_note</span>
              Order Notes
            </h2>
            <div className="input-container">
              <textarea className="floating-input" id="notes" placeholder=" " rows={3} value={formData.notes} onChange={handleInputChange}></textarea>
              <label className="floating-label" htmlFor="notes">Special instructions for shipping or handling...</label>
            </div>
          </section>
        </form>
      </div>

      {/* Right Column: Order Summary (35%) */}
      <aside className="w-full lg:w-[35%]">
        <div className="sticky top-24 flex flex-col gap-6">
          <div className="glass-panel rounded-xl p-8 border border-white/10">
            <h3 className="font-headline-lg text-headline-lg-mobile md:text-headline-lg mb-8">Order Summary</h3>
            {/* Price Breakdown */}
            <div className="space-y-4 mb-8">
              <div className="flex justify-between font-body-sm text-body-sm">
                <span className="text-on-surface-variant">Subtotal</span>
                <span className="text-on-surface" data-testid="summary-subtotal">
                  {cart ? formatCents(subtotal) : '--'}
                </span>
              </div>
              <div className="flex justify-between font-body-sm text-body-sm">
                <span className="text-on-surface-variant">Shipping</span>
                <span className="text-on-surface" data-testid="summary-shipping">{formatCents(shippingCost)}</span>
              </div>
              <div className="flex justify-between font-headline-lg text-headline-lg pt-4 border-t border-outline-variant mt-4">
                <span className="text-on-surface">Total</span>
                <span className="text-primary" data-testid="summary-total">{cart ? formatCents(total) : '--'}</span>
              </div>
            </div>
            
            {/* CTA */}
            <button form="checkout-form" type="submit" disabled={submitting} className="w-full bg-primary-container text-on-primary-container font-headline-lg text-headline-lg-mobile md:text-headline-lg py-5 rounded-lg flex items-center justify-center gap-3 hover:scale-[1.02] active:scale-[0.98] transition-all group shadow-lg shadow-primary-container/20">
              {submitting ? 'Processing...' : 'Place Order'}
              <span className="material-symbols-outlined group-hover:translate-x-1 transition-transform">arrow_forward</span>
            </button>
            <div className="mt-6 flex items-center justify-center gap-4 text-on-surface-variant opacity-60">
              <span className="material-symbols-outlined">lock</span>
              <span className="font-label-caps text-label-caps">Secure End-to-End Encryption</span>
            </div>
          </div>
          
          {/* Payment Assurance */}
          <div className="flex justify-between px-4 opacity-50 grayscale hover:grayscale-0 transition-all">
            <span className="material-symbols-outlined text-4xl">payments</span>
            <span className="material-symbols-outlined text-4xl">credit_card</span>
            <span className="material-symbols-outlined text-4xl">account_balance</span>
            <span className="material-symbols-outlined text-4xl">contactless</span>
          </div>
        </div>
      </aside>
    </div>
  );
}
