import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Product } from '../api/apiClient';
import { productApi, cartApi, getUserId } from '../api/apiClient';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { ProductCard } from '../components/ProductCard';

export function NewArrivalsPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [cartFeedback, setCartFeedback] = useState<{ type: 'success' | 'warning' | 'error'; msg: string } | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    productApi.getAll()
      .then(allProducts => {
        // Sort by createdAt descending and take 8
        const newest = [...allProducts]
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
          .slice(0, 8);
        setProducts(newest);
      })
      .catch(err => {
        console.error('Failed to fetch new arrivals:', err);
      });
  }, []);

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

  return (
    <div className="min-h-screen bg-background text-text-primary flex flex-col font-body-md selection:bg-primary/30">
      <Navbar />

      {cartFeedback && (
        <div className={`fixed top-4 right-4 z-[200] px-6 py-3 rounded-lg shadow-xl font-body-sm text-sm transition-all
          ${cartFeedback.type === 'success' ? 'bg-green-600 text-white' : ''}
          ${cartFeedback.type === 'warning' ? 'bg-amber-500 text-white' : ''}
          ${cartFeedback.type === 'error' ? 'bg-red-600 text-white' : ''}`}>
          {cartFeedback.msg}
        </div>
      )}

      <main className="flex-1 pt-32 pb-24 px-margin-mobile md:px-margin-desktop max-w-max-width mx-auto w-full">
        <div className="mb-12">
          <h1 className="font-display-xl text-4xl md:text-5xl text-primary mb-4">New Arrivals</h1>
          <p className="text-text-muted font-body-md max-w-2xl">
            Discover the latest additions to ShopScale Fabric. Engineered for performance and aesthetics.
          </p>
        </div>
        
        {products.length === 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {[...Array(8)].map((_, i) => (
              <div key={i} className="animate-pulse bg-surface aspect-[4/5]"></div>
            ))}
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
            {products.map((product) => (
              <ProductCard key={product.id} product={product} onAddToCart={handleAddToCart} />
            ))}
          </div>
        )}
      </main>

      <Footer />
    </div>
  );
}
