import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { orderApi } from '../api/apiClient';

export function OrderStatusPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [status, setStatus] = useState<string>('PENDING');

  useEffect(() => {
    if (!id) return;

    // Load particles
    const createParticle = () => {
      const colors = ['#ffb59d', '#ffffff', '#ffdbd0', '#ffa07e'];
      const p = document.createElement('div');
      const size = Math.random() * 4 + 2;
      const color = colors[Math.floor(Math.random() * colors.length)];
      
      p.className = 'particle';
      p.style.width = `${size}px`;
      p.style.height = `${size}px`;
      p.style.backgroundColor = color;
      p.style.boxShadow = `0 0 10px ${color}80`;
      
      const startX = window.innerWidth / 2;
      const startY = window.innerHeight / 3;
      
      p.style.left = `${startX}px`;
      p.style.top = `${startY}px`;
      
      document.body.appendChild(p);
      
      const angle = Math.random() * Math.PI * 2;
      const distance = Math.random() * 300 + 100;
      
      const destX = Math.cos(angle) * distance;
      const destY = Math.sin(angle) * distance;
      
      const animation = p.animate([
          { transform: 'translate(0, 0) scale(1)', opacity: 1 },
          { transform: `translate(${destX}px, ${destY}px) scale(0)`, opacity: 0 }
      ], {
          duration: Math.random() * 2000 + 1500,
          easing: 'cubic-bezier(0.1, 0.8, 0.3, 1)',
          fill: 'forwards'
      });
      
      animation.onfinish = () => p.remove();
    };

    const timer = setTimeout(() => {
      for (let i = 0; i < 40; i++) {
          createParticle();
      }
    }, 1200);

    return () => clearTimeout(timer);
  }, [id]);

  useEffect(() => {
    // Polling logic
    const interval = setInterval(async () => {
      if (id) {
        try {
          const order = await orderApi.getById(id);
          setStatus(order.status);
        } catch (e) {
          console.error(e);
        }
      }
    }, 3000);
    return () => clearInterval(interval);
  }, [id]);

  return (
    <div className="font-body-md text-body-md antialiased min-h-screen pt-32 pb-24 flex flex-col items-center relative overflow-hidden">
      {/* Background Decoration (Soft Glows) */}
      <div className="absolute top-1/4 -left-20 w-96 h-96 bg-primary/5 rounded-full blur-[100px]"></div>
      <div className="absolute bottom-1/4 -right-20 w-96 h-96 bg-tertiary/5 rounded-full blur-[100px]"></div>

      {/* Stage 1 & 2: Animated Success Header */}
      <div className="flex flex-col items-center mb-12">
        <div className="check-container mb-6">
          <div className="halo halo-animate"></div>
          <svg className="check-svg" viewBox="0 0 100 100">
            <circle cx="50" cy="50" fill="none" r="45" stroke="#ffb59d" strokeLinecap="round" strokeWidth="4"></circle>
            <path className="check-mark" d="M30 52 L45 67 L70 35" fill="none" stroke="#ffb59d" strokeLinecap="round" strokeLinejoin="round" strokeWidth="6"></path>
          </svg>
        </div>
        <div className="text-center space-y-2 fade-in-up">
          <h1 className="font-display-xl text-headline-lg-mobile md:text-display-xl text-text-main">Order Confirmed</h1>
          <p className="text-on-surface-variant max-w-md mx-auto">We've received your order and our artisans are beginning their work on your selection.</p>
        </div>
      </div>

      {/* Stage 4: Order Status Card */}
      <div className="order-card w-full max-w-2xl rounded-xl p-8 space-y-8 fade-in-up backdrop-blur-luxury relative z-10">
        {/* Header Section */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div>
            <span className="font-label-caps text-label-caps text-on-surface-variant block mb-1">CURRENT STATUS</span>
            <div className="flex items-center gap-2">
              <span className={`w-2.5 h-2.5 rounded-full ${status === 'CONFIRMED' ? 'bg-success' : 'bg-warning'}`}></span>
              <span className={`font-mono-label text-mono-label ${status === 'CONFIRMED' ? 'text-success' : 'text-warning'}`}>
                {status === 'CONFIRMED' ? 'Order Processed' : 'Order Received (Processing)'}
              </span>
            </div>
          </div>
          <div className="text-right md:text-right w-full md:w-auto">
            <span className="font-label-caps text-label-caps text-on-surface-variant block mb-1">ORDER NUMBER</span>
            <span className="font-mono-label text-mono-label text-text-main">#{id}</span>
          </div>
        </div>

        {/* Detail Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 border-y border-outline-variant py-8">
          <div className="space-y-1">
            <span className="font-label-caps text-label-caps text-on-surface-variant block">ORDER DATE</span>
            <p className="font-body-md text-body-md text-text-main">{new Date().toLocaleDateString()}</p>
          </div>
          <div className="space-y-1">
            <span className="font-label-caps text-label-caps text-on-surface-variant block">EST. DELIVERY</span>
            <p className="font-body-md text-body-md text-text-main">3-5 Business Days</p>
          </div>
          <div className="space-y-1">
            <span className="font-label-caps text-label-caps text-on-surface-variant block">INVENTORY</span>
            <p className={`font-body-md text-body-md flex items-center gap-1 ${status === 'CONFIRMED' ? 'text-success' : 'text-warning'}`}>
              <span className="material-symbols-outlined text-[18px]">
                {status === 'CONFIRMED' ? 'verified' : 'pending'}
              </span>
              {status === 'CONFIRMED' ? 'Allocated' : 'Pending Allocation'}
            </p>
          </div>
        </div>

        {/* CTAs */}
        <div className="flex flex-col sm:flex-row gap-4 pt-4">
          <button className="flex-1 bg-primary-container text-on-primary-container font-headline-lg text-body-md py-4 rounded-lg flex items-center justify-center gap-2 animate-pulse-custom transition-all hover:opacity-90 active:scale-[0.98]">
            <span className="material-symbols-outlined">local_shipping</span>
            Track Order
          </button>
          <button onClick={() => navigate('/')} className="flex-1 bg-transparent border border-outline-variant text-on-surface font-headline-lg text-body-md py-4 rounded-lg flex items-center justify-center gap-2 hover:bg-surface-variant transition-all active:scale-[0.98]">
            <span className="material-symbols-outlined">shopping_bag</span>
            Continue Shopping
          </button>
        </div>
      </div>

      {/* Subtle Information */}
      <p className="mt-8 text-on-surface-variant font-body-sm text-body-sm fade-in-up text-center">
        A confirmation email with tracking details has been sent to your registered address.
      </p>
    </div>
  );
}
