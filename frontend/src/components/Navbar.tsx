import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useEffect, useState, useRef } from 'react';
import { cartApi, getUserId } from '../api/apiClient';

export function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();
  const [cartItemCount, setCartItemCount] = useState(0);
  const [activeSection, setActiveSection] = useState<'home' | 'categories' | 'new-arrivals' | null>('home');
  const observerRef = useRef<IntersectionObserver | null>(null);

  // Cart fetching
  useEffect(() => {
    const fetchCart = async () => {
      try {
        const userId = getUserId();
        const cart = await cartApi.getCart(userId);
        setCartItemCount(cart.items?.reduce((acc: number, item: any) => acc + item.quantity, 0) || 0);
      } catch (err) {
        // ignore
      }
    };
    fetchCart();
    const handleCartUpdate = () => fetchCart();
    window.addEventListener('cartUpdated', handleCartUpdate);
    const interval = setInterval(fetchCart, 2000);
    return () => {
      window.removeEventListener('cartUpdated', handleCartUpdate);
      clearInterval(interval);
    };
  }, []);

  // Scroll-spy: only active on home page
  useEffect(() => {
    const isHomePage = location.pathname === '/' || location.pathname === '/categories';

    if (!isHomePage) {
      setActiveSection(null);
      observerRef.current?.disconnect();
      return;
    }

    setActiveSection('home');

    const intersecting = new Set<string>();

    observerRef.current = new IntersectionObserver(
      (entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            intersecting.add(entry.target.id);
          } else {
            intersecting.delete(entry.target.id);
          }
        });

        // Priority: bottom-most section wins
        if (intersecting.has('new-arrivals-section')) {
          setActiveSection('new-arrivals');
        } else if (intersecting.has('categories')) {
          setActiveSection('categories');
        } else {
          setActiveSection('home');
        }
      },
      { threshold: 0.2, rootMargin: '-80px 0px 0px 0px' }
    );

    const timer = setTimeout(() => {
      const categoriesEl = document.getElementById('categories');
      const newArrivalsEl = document.getElementById('new-arrivals-section');
      if (categoriesEl) observerRef.current?.observe(categoriesEl);
      if (newArrivalsEl) observerRef.current?.observe(newArrivalsEl);
    }, 150);

    return () => {
      clearTimeout(timer);
      observerRef.current?.disconnect();
    };
  }, [location.pathname]);

  const isHomePage = location.pathname === '/' || location.pathname === '/categories';

  // Returns active underline classes for each nav item
  const underline = (key: string, routePath?: string) => {
    const active = isHomePage
      ? activeSection === key
      : routePath ? location.pathname === routePath : false;
    return `absolute bottom-0 left-0 h-[2px] bg-primary rounded-full transition-all duration-500 ease-in-out ${active ? 'w-full' : 'w-0'}`;
  };

  const labelClass = (key: string, routePath?: string) => {
    const active = isHomePage
      ? activeSection === key
      : routePath ? location.pathname === routePath : false;
    return `relative pb-1 transition-colors duration-300 ${active ? 'text-primary font-semibold' : 'text-text-muted hover:text-on-surface'}`;
  };

  const handleCategoriesClick = (e: React.MouseEvent) => {
    e.preventDefault();
    if (isHomePage) {
      document.getElementById('categories')?.scrollIntoView({ behavior: 'smooth' });
    } else {
      navigate('/categories');
    }
  };

  const handleNewArrivalsClick = (e: React.MouseEvent) => {
    e.preventDefault();
    if (isHomePage) {
      document.getElementById('new-arrivals-section')?.scrollIntoView({ behavior: 'smooth' });
    } else {
      navigate('/new-arrivals');
    }
  };

  return (
    <header className="fixed top-0 w-full z-50 bg-surface/80 backdrop-blur-md border-b border-border-subtle/10 h-20" id="main-nav">
      <div className="flex justify-between items-center max-w-max-width mx-auto px-margin-mobile md:px-margin-desktop h-full">

        <Link to="/" className="font-display-xl text-headline-lg font-bold tracking-tighter text-on-surface">
          ShopScale Fabric
        </Link>

        <nav className="hidden md:flex items-center space-x-8 font-mono-label text-mono-label">

          {/* Home */}
          <Link to="/" className={labelClass('home', '/')}>
            Home
            <span className={underline('home', '/')} />
          </Link>

          {/* Categories */}
          <a href="#categories" onClick={handleCategoriesClick} className={labelClass('categories')}>
            Categories
            <span className={underline('categories')} />
          </a>

          {/* New Arrivals */}
          <a href="#new-arrivals-section" onClick={handleNewArrivalsClick} className={labelClass('new-arrivals', '/new-arrivals')}>
            New Arrivals
            <span className={underline('new-arrivals', '/new-arrivals')} />
          </a>

          {/* About */}
          <Link to="/about" className={labelClass('about', '/about')}>
            About
            <span className={underline('about', '/about')} />
          </Link>

        </nav>

        <div className="flex items-center space-x-6">
          <button className="text-primary hover:bg-surface-container-high/50 p-2 transition-all duration-400 ease-out">
            <span className="material-symbols-outlined">search</span>
          </button>
          <Link to="/cart" className="text-primary hover:bg-surface-container-high/50 p-2 transition-all duration-400 ease-out relative" id="cart-btn">
            <span className="material-symbols-outlined">shopping_bag</span>
            {cartItemCount > 0 && (
              <span className="absolute -top-1 -right-1 bg-primary-container text-on-primary-container text-[10px] w-4 h-4 flex items-center justify-center rounded-full font-bold" id="cart-count">
                {cartItemCount}
              </span>
            )}
          </Link>
        </div>

      </div>
    </header>
  );
}
