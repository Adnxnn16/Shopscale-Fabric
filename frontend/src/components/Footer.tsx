import { Link } from 'react-router-dom';

export function Footer() {
  return (
    <footer className="w-full mt-auto border-t border-border-subtle/10 bg-surface-container-lowest">
      <div className="flex flex-col md:flex-row justify-between items-center px-margin-desktop py-12 max-w-max-width mx-auto">
        <div className="font-display-xl text-headline-lg-mobile text-primary mb-8 md:mb-0">ShopScale</div>
        <div className="flex flex-wrap justify-center gap-8 font-body-sm text-body-sm">
          <Link to="/privacy-policy" className="text-text-muted hover:text-on-surface transition-colors">Privacy Policy</Link>
          <Link to="/terms-of-service" className="text-text-muted hover:text-on-surface transition-colors">Terms of Service</Link>
          <Link to="/shipping" className="text-text-muted hover:text-on-surface transition-colors">Shipping</Link>
          <Link to="/contact" className="text-text-muted hover:text-on-surface transition-colors">Contact</Link>
        </div>
        <div className="text-text-muted font-body-sm mt-8 md:mt-0">
          © 2024 ShopScale Fabric. All Rights Reserved.
        </div>
      </div>
    </footer>
  );
}
