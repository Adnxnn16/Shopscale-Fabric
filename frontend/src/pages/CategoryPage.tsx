import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import type { Product } from '../api/apiClient';
import { productApi, cartApi, getUserId } from '../api/apiClient';
import { ProductCard } from '../components/ProductCard';

export function CategoryPage() {
  const { slug } = useParams<{ slug: string }>();
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [cartFeedback, setCartFeedback] = useState<{ type: 'success' | 'warning' | 'error'; msg: string } | null>(null);

  // Filters & Sorting state
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState('recommended');
  const [priceRange] = useState<[number, number]>([0, 500000]); // in paise
  const [inStockOnly, setInStockOnly] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 9;

  const categoryTitle = slug 
    ? slug.split('-').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ')
    : 'Category';

  const categoryDescription = `Explore our curated selection of high-performance ${categoryTitle.toLowerCase()} designed to elevate your workflow and aesthetics.`;

  useEffect(() => {
    if (slug) {
      productApi.getAll(slug).then(categoryProducts => {
        setProducts(categoryProducts);
        setCurrentPage(1); // Reset page on category change
      }).catch(console.error);
    }
  }, [slug]);

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

  // Apply filters, search, and sort
  let filteredProducts = products.filter(p => {
    const matchesSearch = p.name.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesPrice = p.price >= priceRange[0] && p.price <= priceRange[1];
    const matchesStock = inStockOnly ? (p.inventoryStatus as string) !== 'OUT_OF_STOCK' : true;
    return matchesSearch && matchesPrice && matchesStock;
  });

  if (sortBy === 'price-low') {
    filteredProducts.sort((a, b) => a.price - b.price);
  } else if (sortBy === 'price-high') {
    filteredProducts.sort((a, b) => b.price - a.price);
  } else if (sortBy === 'newest') {
    filteredProducts.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  }

  // Pagination logic
  const totalPages = Math.ceil(filteredProducts.length / productsPerPage) || 1;
  const paginatedProducts = filteredProducts.slice(
    (currentPage - 1) * productsPerPage,
    currentPage * productsPerPage
  );

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

      {/* Banner */}
      <section className="pt-32 pb-16 px-margin-mobile md:px-margin-desktop bg-surface-dim border-b border-border-subtle/10">
        <div className="max-w-max-width mx-auto">
          <h1 className="font-display-xl text-4xl md:text-5xl text-primary mb-4">{categoryTitle}</h1>
          <p className="text-text-muted font-body-md max-w-2xl">{categoryDescription}</p>
        </div>
      </section>

      <main className="flex-1 py-12 px-margin-mobile md:px-margin-desktop max-w-max-width mx-auto w-full flex flex-col lg:flex-row gap-12">
        
        {/* Sidebar Filters */}
        <aside className="w-full lg:w-64 shrink-0 flex flex-col gap-8">
          <div>
            <h3 className="font-headline-sm font-bold mb-4 uppercase tracking-wider text-sm text-text-muted">Search</h3>
            <div className="relative">
              <span className="material-symbols-outlined absolute left-3 top-3 text-text-muted">search</span>
              <input 
                type="text" 
                placeholder="Find a product..." 
                className="w-full bg-surface border border-outline px-4 py-3 pl-10 focus:ring-1 focus:ring-primary focus:border-primary outline-none transition-all text-sm"
                value={searchQuery}
                onChange={e => { setSearchQuery(e.target.value); setCurrentPage(1); }}
              />
            </div>
          </div>

          <div className="border-t border-border-subtle/10 pt-8">
            <h3 className="font-headline-sm font-bold mb-4 uppercase tracking-wider text-sm text-text-muted">Sort By</h3>
            <select 
              className="w-full bg-surface border border-outline px-4 py-3 focus:ring-1 focus:ring-primary focus:border-primary outline-none transition-all text-sm appearance-none"
              value={sortBy}
              onChange={e => setSortBy(e.target.value)}
            >
              <option value="recommended">Recommended</option>
              <option value="newest">Newest Arrivals</option>
              <option value="price-low">Price: Low to High</option>
              <option value="price-high">Price: High to Low</option>
            </select>
          </div>

          <div className="border-t border-border-subtle/10 pt-8">
            <h3 className="font-headline-sm font-bold mb-4 uppercase tracking-wider text-sm text-text-muted">Availability</h3>
            <label className="flex items-center gap-3 cursor-pointer">
              <input 
                type="checkbox" 
                checked={inStockOnly}
                onChange={e => { setInStockOnly(e.target.checked); setCurrentPage(1); }}
                className="w-4 h-4 bg-surface border-outline accent-primary cursor-pointer"
              />
              <span className="text-sm">In Stock Only</span>
            </label>
          </div>
        </aside>

        {/* Product Grid */}
        <div className="flex-1 flex flex-col">
          <div className="mb-6 flex justify-between items-center text-sm text-text-muted">
            <span>Showing {filteredProducts.length} results</span>
          </div>

          {filteredProducts.length === 0 ? (
            <div className="flex-1 flex flex-col items-center justify-center py-20 border border-dashed border-border-subtle/20 bg-surface/30">
              <span className="material-symbols-outlined text-[48px] text-border-subtle mb-4">inventory_2</span>
              <h3 className="font-headline-sm text-lg mb-2">No products found</h3>
              <p className="text-text-muted text-sm text-center max-w-md">Try adjusting your filters or search query to find what you're looking for.</p>
              <button 
                onClick={() => { setSearchQuery(''); setInStockOnly(false); setSortBy('recommended'); }}
                className="mt-6 text-primary border-b border-primary font-label-caps tracking-wider text-xs pb-1"
              >
                CLEAR FILTERS
              </button>
            </div>
          ) : (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
                {paginatedProducts.map((product) => (
                  <ProductCard key={product.id} product={product} onAddToCart={handleAddToCart} />
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="flex justify-center items-center gap-2 mt-auto border-t border-border-subtle/10 pt-12">
                  <button 
                    disabled={currentPage === 1}
                    onClick={() => setCurrentPage(p => p - 1)}
                    className="p-2 border border-outline hover:border-primary disabled:opacity-30 disabled:hover:border-outline transition-colors flex items-center justify-center"
                  >
                    <span className="material-symbols-outlined text-sm">chevron_left</span>
                  </button>
                  {[...Array(totalPages)].map((_, i) => (
                    <button 
                      key={i}
                      onClick={() => setCurrentPage(i + 1)}
                      className={`w-10 h-10 flex items-center justify-center font-mono-label text-sm border transition-colors ${
                        currentPage === i + 1 
                          ? 'bg-primary text-on-primary border-primary' 
                          : 'border-outline hover:border-primary'
                      }`}
                    >
                      {i + 1}
                    </button>
                  ))}
                  <button 
                    disabled={currentPage === totalPages}
                    onClick={() => setCurrentPage(p => p + 1)}
                    className="p-2 border border-outline hover:border-primary disabled:opacity-30 disabled:hover:border-outline transition-colors flex items-center justify-center"
                  >
                    <span className="material-symbols-outlined text-sm">chevron_right</span>
                  </button>
                </div>
              )}
            </>
          )}
        </div>

      </main>

      <Footer />
    </div>
  );
}
