import { useState } from 'react';
import type { Product } from '../api/apiClient';

interface ProductCardProps {
  product: Product;
  onAddToCart: (product: Product) => Promise<void>;
}

export function ProductCard({ product, onAddToCart }: ProductCardProps) {
  const [adding, setAdding] = useState(false);

  const handleAddClick = async () => {
    if (adding) return;
    setAdding(true);
    await onAddToCart(product);
    setAdding(false);
  };

  const formatCategory = (cat: string) => {
    return cat.split('-').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
  };

  const formatInventoryStatus = (status: string) => {
    switch (status) {
      case 'IN_STOCK': return { label: 'In Stock', color: 'bg-green-500/10 text-green-500 border-green-500/20' };
      case 'LIMITED_STOCK': return { label: 'Limited Stock', color: 'bg-amber-500/10 text-amber-500 border-amber-500/20' };
      case 'BEST_SELLER': return { label: 'Best Seller', color: 'bg-primary/10 text-primary border-primary/20' };
      case 'NEW_ARRIVAL': return { label: 'New Arrival', color: 'bg-blue-500/10 text-blue-400 border-blue-500/20' };
      default: return { label: 'In Stock', color: 'bg-green-500/10 text-green-500 border-green-500/20' };
    }
  };

  const statusBadge = formatInventoryStatus(product.inventoryStatus);

  return (
    <div className="group product-card flex flex-col bg-surface border border-border-subtle/20 overflow-hidden hover:border-primary/40 transition-colors duration-500 relative">
      <div className="absolute top-4 left-4 z-10 flex flex-col gap-2">
        <span className={`text-[10px] font-bold px-2 py-1 uppercase tracking-wider border backdrop-blur-sm ${statusBadge.color}`}>
          {statusBadge.label}
        </span>
      </div>
      
      <div className="absolute top-4 right-4 z-10 flex flex-col gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
        <button className="bg-surface/80 backdrop-blur-md p-2 rounded-full hover:bg-primary hover:text-on-primary transition-colors text-text-muted">
          <span className="material-symbols-outlined text-[20px]">favorite</span>
        </button>
        <button className="bg-surface/80 backdrop-blur-md p-2 rounded-full hover:bg-primary hover:text-on-primary transition-colors text-text-muted">
          <span className="material-symbols-outlined text-[20px]">visibility</span>
        </button>
      </div>

      <div className="aspect-[4/5] overflow-hidden relative bg-surface-dim">
        <img 
          src={product.imageUrl ?? `https://picsum.photos/seed/${product.id}/800/1000`} 
          alt={product.name} 
          className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" 
        />
        <div className="absolute inset-0 bg-background/20 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
          <button
            onClick={handleAddClick}
            disabled={adding}
            className="bg-primary text-on-primary font-label-caps text-label-caps py-3 px-6 transform translate-y-4 group-hover:translate-y-0 transition-all duration-500 disabled:opacity-60 flex items-center gap-2">
            <span className="material-symbols-outlined text-sm">shopping_bag</span>
            {adding ? 'ADDING...' : 'ADD TO BAG'}
          </button>
        </div>
      </div>
      <div className="p-6 flex flex-col flex-grow">
        <div className="flex justify-between items-start mb-2">
          <div className="flex flex-col">
            <span className="text-xs text-text-muted font-mono-label uppercase mb-1">{formatCategory(product.category)}</span>
            <h4 className="font-headline-lg text-body-md font-bold text-on-surface line-clamp-1" title={product.name}>{product.name}</h4>
          </div>
          <span className="font-mono-label text-primary ml-4 shrink-0">
            {new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(product.price / 100)}
          </span>
        </div>
        
        <div className="flex items-center gap-1 mt-auto pt-4">
          <span className="material-symbols-outlined text-[14px] text-amber-500">star</span>
          <span className="text-xs font-bold">{product.rating.toFixed(1)}</span>
        </div>
      </div>
    </div>
  );
}
