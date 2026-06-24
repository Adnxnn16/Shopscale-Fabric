import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';

export function ShippingPage() {
  return (
    <div className="min-h-screen bg-background text-text-primary flex flex-col">
      <Navbar />
      
      <main className="flex-1 pt-32 pb-24 px-6 md:px-12 max-w-4xl mx-auto space-y-12">
        <h1 className="font-display-xl text-4xl md:text-5xl text-primary">Shipping Information</h1>
        
        <div className="space-y-8 text-text-secondary leading-relaxed">
          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">Shipping Methods</h2>
            <p>We offer standard, expedited, and next-day shipping options. All orders are processed within 1-2 business days. Orders are not shipped or delivered on weekends or holidays.</p>
          </section>

          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">Delivery Timeframes</h2>
            <ul className="list-disc list-inside space-y-2">
              <li>Standard Shipping: 3-5 business days</li>
              <li>Expedited Shipping: 2 business days</li>
              <li>Next-Day Shipping: 1 business day</li>
            </ul>
            <p className="text-sm mt-2">Delivery delays can occasionally occur due to high volume or carrier issues.</p>
          </section>

          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">Shipping Costs</h2>
            <p>Shipping charges for your order will be calculated and displayed at checkout. Standard shipping is free on orders over $200.</p>
          </section>

          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">International Shipping</h2>
            <p>We currently ship to select international destinations. Customs, duties, and taxes are the responsibility of the customer. International shipping generally takes 7-14 business days.</p>
          </section>
        </div>
      </main>

      <Footer />
    </div>
  );
}
