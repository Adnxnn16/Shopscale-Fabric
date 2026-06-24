import { Trash2 } from 'lucide-react';

interface CartItemProps {
  item: {
    productId: string;
    quantity: number;
    unitPrice: number;
    totalPrice: number;
  };
  productName: string;
  onRemove: (productId: string) => void;
}

export function CartItem({ item, productName, onRemove }: CartItemProps) {
  return (
    <div className="glass-panel cart-item animate-fade-in">
      <div className="cart-item-info">
        <h4 className="cart-item-title">{productName}</h4>
        <div className="cart-item-price">
          ${(item.unitPrice / 100).toFixed(2)} × {item.quantity}
        </div>
      </div>
      <div className="cart-item-total price">
        ${(item.totalPrice / 100).toFixed(2)}
      </div>
      <button className="btn-secondary" onClick={() => onRemove(item.productId)} style={{ padding: '8px', color: 'var(--danger)', borderColor: 'var(--danger)' }}>
        <Trash2 size={20} />
      </button>
    </div>
  );
}
