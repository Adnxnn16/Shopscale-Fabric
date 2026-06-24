import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Cart, Product } from '../api/apiClient';
import { cartApi, productApi, getUserId } from '../api/apiClient';

export function CartPage() {
  const [cart, setCart] = useState<Cart | null>(null);
  const [products, setProducts] = useState<Record<string, Product>>({});
  const navigate = useNavigate();

  useEffect(() => {
    cartApi.getCart(getUserId())
      .then(async (cartData) => {
        setCart(cartData);
        const productData: Record<string, Product> = {};
        for (const item of cartData.items) {
          try {
            const prod = await productApi.getById(item.productId);
            productData[item.productId] = prod;
          } catch (e) {
            console.error("Failed to fetch product", item.productId, e);
          }
        }
        setProducts(productData);
      })
      .catch(console.error);
  }, []);

  return (
    <div className="font-body-md text-body-md min-h-screen pt-32 pb-24 px-margin-mobile md:px-margin-desktop max-w-max-width mx-auto">
      <h2 className="font-headline-lg text-headline-lg mb-12">Your Cart</h2>
      {cart && cart.items.length > 0 ? (
        <div className="flex flex-col lg:flex-row gap-gutter">
          <div className="w-full lg:w-[65%] flex flex-col gap-6">
            {cart.items.map(item => {
              const product = products[item.productId];
              const productName = product?.name || `Product ${item.productId}`;
              const productImg = product?.imageUrl || `https://picsum.photos/seed/${item.productId}/200/200`;
              
              return (
                <div key={item.productId} className="glass-panel p-6 rounded-xl flex gap-6 items-center">
                  <div className="w-24 h-24 bg-surface-container-highest rounded overflow-hidden flex-shrink-0">
                    <img src={productImg} alt={productName} className="w-full h-full object-cover" />
                  </div>
                  <div className="flex-grow">
                    <h4 className="font-body-md text-body-md font-semibold text-on-surface">{productName}</h4>
                    <p className="font-body-sm text-body-sm text-on-surface-variant">Qty: {item.quantity}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-mono-label text-mono-label text-primary">${(((item.unitPrice || 0) / 100) * item.quantity).toFixed(2)}</p>
                  </div>
                </div>
              );
            })}
          </div>
          <aside className="w-full lg:w-[35%]">
            <div className="sticky top-24 glass-panel rounded-xl p-8">
              <h3 className="font-headline-lg text-headline-lg-mobile md:text-headline-lg mb-8">Summary</h3>
              <div className="flex justify-between font-headline-lg text-headline-lg pt-4 border-t border-outline-variant mb-8">
                <span className="text-on-surface">Total</span>
                <span className="text-primary">${(cart.items.reduce((sum, item) => sum + ((item.unitPrice || 0) * item.quantity), 0) / 100).toFixed(2)}</span>
              </div>
              <button onClick={() => navigate('/checkout')} className="w-full bg-primary-container text-on-primary-container font-headline-lg text-headline-lg-mobile md:text-headline-lg py-5 rounded-lg flex items-center justify-center gap-3 hover:scale-[1.02] transition-all group shadow-lg">
                Proceed to Checkout
                <span className="material-symbols-outlined group-hover:translate-x-1 transition-transform">arrow_forward</span>
              </button>
            </div>
          </aside>
        </div>
      ) : (
        <div className="text-center py-24 glass-panel rounded-xl">
          <p className="text-text-muted mb-6">Your cart is currently empty.</p>
          <button onClick={() => navigate('/')} className="bg-primary text-on-primary font-label-caps px-6 py-3 rounded">
            BROWSE CATALOG
          </button>
        </div>
      )}
    </div>
  );
}
