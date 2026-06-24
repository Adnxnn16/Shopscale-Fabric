import { useState, useEffect } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import type { Product } from '../api/apiClient';
import { productApi, cartApi, getUserId } from '../api/apiClient';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { ProductCard } from '../components/ProductCard';

export function ProductListPage() {
  const [featuredProducts, setFeaturedProducts] = useState<Product[]>([]);
  const [newArrivalProducts, setNewArrivalProducts] = useState<Product[]>([]);
  const [introFinished, setIntroFinished] = useState(() => {
    return sessionStorage.getItem('introAnimationPlayed') === 'true';
  });
  const [cartFeedback, setCartFeedback] = useState<{ type: 'success' | 'warning' | 'error'; msg: string } | null>(null);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (location.pathname === '/categories') {
      const el = document.getElementById('categories');
      if (el) {
        el.scrollIntoView({ behavior: 'smooth' });
      }
    }
  }, [location.pathname]);

  useEffect(() => {
    productApi.getFeatured()
      .then(setFeaturedProducts)
      .catch(err => {
        console.error('Failed to fetch featured products:', err);
        setFeaturedProducts([]);
      });

    productApi.getAll()
      .then(allProducts => {
        const newest = [...allProducts]
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
          .slice(0, 8);
        setNewArrivalProducts(newest);
      })
      .catch(err => console.error('Failed to fetch new arrivals:', err));

    if (!introFinished) {
      // Intro Sequence Timer
      const timer = setTimeout(() => {
        setIntroFinished(true);
        sessionStorage.setItem('introAnimationPlayed', 'true');
      }, 3500);

      return () => clearTimeout(timer);
    }
  }, [introFinished]);

  const handleAddToCart = async (product: Product) => {
    setCartFeedback(null);
    try {
      const userId = getUserId();
      const cart = await cartApi.addItem(userId, product.id, 1);
      
      window.dispatchEvent(new Event('cartUpdated'));

      if (cart?.priceAvailable === false) {
        setCartFeedback({ type: 'warning', msg: 'Price temporarily unavailable — item added, resolve at checkout.' });
        setTimeout(() => navigate('/cart'), 1500);
      } else {
        setCartFeedback({ type: 'success', msg: `${product.name} added to bag!` });
        setTimeout(() => setCartFeedback(null), 3000);
      }
    } catch (err: unknown) {
      const status = (err as { response?: { status?: number } })?.response?.status;
      if (status === 401) {
        navigate('/login');
      } else {
        setCartFeedback({ type: 'error', msg: 'Failed to add item. Please try again.' });
        setTimeout(() => setCartFeedback(null), 3000);
      }
    }
  };

  const categories = [
    { name: 'Electronics', slug: 'electronics', icon: 'devices', desc: 'High-performance computing and devices.', count: 8, gradient: 'linear-gradient(135deg, #1e3a8a 0%, #3730a3 50%, #1e1b4b 100%)' },
    { name: 'Gaming', slug: 'gaming', icon: 'sports_esports', desc: 'Next-gen consoles and peripherals.', count: 8, gradient: 'linear-gradient(135deg, #7f1d1d 0%, #991b1b 50%, #0f0a0a 100%)' },
    { name: 'Smart Living', slug: 'smart-living', icon: 'nest_eco_leaf', desc: 'Automate your modern sanctuary.', count: 8, gradient: 'linear-gradient(135deg, #14532d 0%, #0f766e 50%, #042f2e 100%)' },
    { name: 'Creator Tools', slug: 'creator-tools', icon: 'videocam', desc: 'Professional gear for visionaries.', count: 8, gradient: 'linear-gradient(135deg, #7c2d12 0%, #b45309 50%, #451a03 100%)' },
    { name: 'Digital Assets', slug: 'digital-assets', icon: 'token', desc: 'Exclusive digital collectibles.', count: 8, gradient: 'linear-gradient(135deg, #4a044e 0%, #5b21b6 50%, #1e1b4b 100%)' }
  ];

  return (
    <div className="font-body-md selection:bg-primary/30">
      {cartFeedback && (
        <div className={`fixed top-4 right-4 z-[200] px-6 py-3 rounded-lg shadow-xl font-body-sm text-sm transition-all
          ${cartFeedback.type === 'success' ? 'bg-green-600 text-white' : ''}
          ${cartFeedback.type === 'warning' ? 'bg-amber-500 text-white' : ''}
          ${cartFeedback.type === 'error' ? 'bg-red-600 text-white' : ''}`}>
          {cartFeedback.msg}
        </div>
      )}
      {!introFinished && (
        <div className={`fixed inset-0 z-[100] bg-surface-dim flex flex-col items-center justify-center transition-opacity duration-1000`} id="intro-stage">
          <div className="w-64 h-64 mb-8">
            <div className="w-full h-full" id="animated-svg-ANIMATION_1" style={{display: 'block'}}>
              <svg viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
                <defs>
                  <linearGradient id="logo-grad" x1="0%" x2="100%" y1="0%" y2="100%">
                    <stop offset="0%" style={{stopColor:'#FF6B35', stopOpacity:1}}></stop>
                    <stop offset="100%" style={{stopColor:'#FF8F65', stopOpacity:1}}></stop>
                  </linearGradient>
                </defs>
                <path d="M20,50 L50,20 L80,50 L50,80 Z" fill="none" stroke="url(#logo-grad)" strokeWidth="2">
                  <animate attributeName="stroke-dasharray" dur="2s" fill="freeze" from="0,240" to="240,0"></animate>
                </path>
                <path d="M35,50 L50,35 L65,50 L50,65 Z" fill="url(#logo-grad)">
                  <animate attributeName="opacity" begin="1s" dur="1s" fill="freeze" from="0" to="1"></animate>
                </path>
              </svg>
            </div>
          </div>
          <h1 className="font-display-xl text-display-xl text-primary tracking-[0.2em] animate-pulse">
            SHOPSCALE FABRIC
          </h1>
        </div>
      )}

      <Navbar />

      <main className="relative pt-20">
        {/* Hero Section */}
        <section className="relative min-h-[921px] flex items-center justify-center overflow-hidden px-margin-mobile md:px-margin-desktop">
          <div className="hero-gradient absolute inset-0 pointer-events-none"></div>
          <div className="relative z-10 text-center max-w-4xl">
            <div className="overflow-hidden mb-4">
              <h2 className="font-display-xl text-display-xl-mobile md:text-display-xl tracking-tight leading-none reveal-line">
                Premium Tech,<br/>
                <span className="text-primary">Curated For You.</span>
              </h2>
            </div>
            <p className="font-body-md text-text-muted max-w-xl mx-auto mb-12">
              Electronics, Gaming, Creator Tools, Smart Living and Digital Assets — all in one place.
            </p>
            <div className="flex flex-col md:flex-row gap-6 justify-center">
              <button onClick={() => navigate('/categories')} className="bg-primary-container text-on-primary-container px-8 py-4 font-label-caps text-label-caps hover:scale-105 transition-transform active:scale-95">
                EXPLORE CATEGORIES
              </button>
            </div>
          </div>
        </section>

        {/* Categories Section */}
        <section id="categories" className="max-w-max-width mx-auto px-margin-mobile md:px-margin-desktop py-24 border-t border-border-subtle/10">
          <div className="mb-12 text-center">
            <h3 className="font-headline-lg text-headline-lg mb-2 text-primary">Categories</h3>
            <p className="text-text-muted font-body-sm">Curated paths to power your next project.</p>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-6">
            {categories.map((cat) => (
              <Link 
                key={cat.slug} 
                to={`/categories/${cat.slug}`}
                className="group relative flex flex-col items-center justify-center p-8 h-[320px] rounded-2xl overflow-hidden transition-all duration-500 hover:-translate-y-2 hover:shadow-[0_8px_32px_rgba(255,107,53,0.25)] hover:border-primary border border-transparent"
                style={{ background: cat.gradient }}
              >
                <div className="absolute inset-0 bg-black/20 group-hover:bg-black/10 transition-colors duration-500"></div>
                
                <span className="material-symbols-outlined text-[64px] text-white/80 group-hover:text-white transition-all duration-500 group-hover:scale-110 mb-4 relative z-10">{cat.icon}</span>
                
                <span className="font-headline-sm text-center font-bold tracking-wide text-lg text-white group-hover:text-white transition-colors duration-300 relative z-10">{cat.name}</span>

                <span className="mt-2 text-center text-xs text-white/60 group-hover:text-white/80 transition-colors duration-300 relative z-10">{cat.count} Products</span>
                
                <p className="mt-4 text-center text-sm text-white/70 opacity-0 translate-y-4 group-hover:opacity-100 group-hover:translate-y-0 transition-all duration-500 delay-100 relative z-10">
                  {cat.desc}
                </p>
              </Link>
            ))}
          </div>
        </section>

        {/* New Arrivals Section */}
        <section id="new-arrivals-section" className="max-w-max-width mx-auto px-margin-mobile md:px-margin-desktop py-24 bg-surface-dim">
          <div className="flex justify-between items-end mb-16">
            <div>
              <h3 className="font-headline-lg text-headline-lg mb-2">New Arrivals</h3>
              <p className="text-text-muted font-body-sm">The latest drops across all categories.</p>
            </div>
            <Link to="/new-arrivals" className="font-label-caps text-primary border-b border-primary hover:opacity-80 transition-opacity">VIEW ALL</Link>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
            {newArrivalProducts.map((product) => (
              <ProductCard key={product.id} product={product} onAddToCart={handleAddToCart} />
            ))}
          </div>
        </section>

        {/* Product Grid Section (Featured Series) */}
        <section className="max-w-max-width mx-auto px-margin-mobile md:px-margin-desktop py-24">
          <div className="flex justify-between items-end mb-16">
            <div>
              <h3 className="font-headline-lg text-headline-lg mb-2">Featured Series</h3>
              <p className="text-text-muted font-body-sm">Handpicked series across every category.</p>
            </div>
          </div>
          <div id="product-grid" className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {featuredProducts.map((product) => (
              <ProductCard key={product.id} product={product} onAddToCart={handleAddToCart} />
            ))}
          </div>
        </section>

        {/* Newsletter / CTA */}
        <section className="max-w-max-width mx-auto px-margin-mobile md:px-margin-desktop py-24 border-t border-border-subtle/10">
          <div className="relative bg-surface-container-low border border-border-subtle/10 p-12 md:p-24 text-center overflow-hidden">
            <div className="relative z-10 max-w-2xl mx-auto">
              <h3 className="font-display-xl text-headline-lg md:text-display-xl mb-6">Stay ahead of the curve.</h3>
              <p className="text-text-muted mb-10 font-body-md">Get early access to limited edition archive drops and technical fabric breakthroughs.</p>
              <form className="flex flex-col md:flex-row gap-4" onSubmit={e => e.preventDefault()}>
                <input className="flex-1 bg-background border border-outline px-6 py-4 focus:ring-1 focus:ring-primary focus:border-primary outline-none transition-all" placeholder="Your corporate email" type="email" />
                <button className="bg-primary text-on-primary px-12 py-4 font-label-caps text-label-caps hover:scale-105 active:scale-95 transition-transform">JOIN LIST</button>
              </form>
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
}
