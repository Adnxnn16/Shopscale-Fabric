import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ProductListPage } from './pages/ProductListPage';
import { CartPage } from './pages/CartPage';
import { CheckoutPage } from './pages/CheckoutPage';
import { OrderStatusPage } from './pages/OrderStatusPage';
import { LoginPage } from './pages/LoginPage';
import { AboutPage } from './pages/AboutPage';
import { PrivacyPolicyPage } from './pages/PrivacyPolicyPage';
import { TermsOfServicePage } from './pages/TermsOfServicePage';
import { ShippingPage } from './pages/ShippingPage';
import { ContactPage } from './pages/ContactPage';
import { CategoryPage } from './pages/CategoryPage';
import { NewArrivalsPage } from './pages/NewArrivalsPage';

/** Guards routes that require a valid JWT stored in sessionStorage. */
function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const token = sessionStorage.getItem('jwt_token');
  return token ? <>{children}</> : <Navigate to="/login" replace />;
}




function App() {
  return (
    <Router>
      <div className="min-h-screen bg-background text-text-primary font-body-md antialiased selection:bg-primary/20">
        <Routes>
          {/* Public */}
          <Route path="/login" element={<LoginPage />} />

          {/* Protected Main Flow */}
          <Route path="/" element={<ProtectedRoute><ProductListPage /></ProtectedRoute>} />
          <Route path="/cart" element={<ProtectedRoute><CartPage /></ProtectedRoute>} />
          <Route path="/checkout" element={<ProtectedRoute><CheckoutPage /></ProtectedRoute>} />
          <Route path="/order-status/:orderId" element={<ProtectedRoute><OrderStatusPage /></ProtectedRoute>} />
          
          {/* Navigation Routes */}
          <Route path="/new-arrivals" element={<ProtectedRoute><NewArrivalsPage /></ProtectedRoute>} />
          <Route path="/about" element={<ProtectedRoute><AboutPage /></ProtectedRoute>} />
          <Route path="/categories" element={<ProtectedRoute><ProductListPage /></ProtectedRoute>} />
          <Route path="/categories/:slug" element={<ProtectedRoute><CategoryPage /></ProtectedRoute>} />

          {/* Footer Static Pages */}
          <Route path="/privacy-policy" element={<ProtectedRoute><PrivacyPolicyPage /></ProtectedRoute>} />
          <Route path="/terms-of-service" element={<ProtectedRoute><TermsOfServicePage /></ProtectedRoute>} />
          <Route path="/shipping" element={<ProtectedRoute><ShippingPage /></ProtectedRoute>} />
          <Route path="/contact" element={<ProtectedRoute><ContactPage /></ProtectedRoute>} />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
