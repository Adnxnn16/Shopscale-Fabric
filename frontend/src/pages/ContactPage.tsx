import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';

export function ContactPage() {
  return (
    <div className="min-h-screen bg-background text-text-primary flex flex-col">
      <Navbar />
      
      <main className="flex-1 pt-32 pb-24 px-6 md:px-12 max-w-4xl mx-auto space-y-12">
        <h1 className="font-display-xl text-4xl md:text-5xl text-primary">Contact Us</h1>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
          <div className="space-y-8 text-text-secondary leading-relaxed">
            <p className="text-lg">We're here to help. Reach out to us for any questions regarding your order, our products, or any general inquiries.</p>
            
            <div className="space-y-4">
              <h2 className="text-xl font-headline-lg text-primary">Customer Support</h2>
              <p>Email: support@shopscalefabric.com<br/>Phone: 1-800-555-0198<br/>Hours: Mon-Fri, 9am - 6pm EST</p>
            </div>

            <div className="space-y-4">
              <h2 className="text-xl font-headline-lg text-primary">Corporate Headquarters</h2>
              <p>100 Fabric Way<br/>Suite 400<br/>New York, NY 10001</p>
            </div>
          </div>

          <div className="bg-surface-container-low p-8 rounded-xl border border-border-subtle/20 shadow-sm">
            <h2 className="text-2xl font-headline-lg text-primary mb-6">Send a Message</h2>
            <form className="space-y-6" onSubmit={(e) => { e.preventDefault(); alert("This is a non-functional placeholder contact form."); }}>
              <div className="space-y-2">
                <label htmlFor="name" className="block text-sm font-label-caps text-text-muted">Name</label>
                <input id="name" type="text" className="w-full bg-background border border-outline px-4 py-3 rounded focus:ring-1 focus:ring-primary focus:border-primary outline-none transition-all" placeholder="Your Name" required />
              </div>
              
              <div className="space-y-2">
                <label htmlFor="email" className="block text-sm font-label-caps text-text-muted">Email</label>
                <input id="email" type="email" className="w-full bg-background border border-outline px-4 py-3 rounded focus:ring-1 focus:ring-primary focus:border-primary outline-none transition-all" placeholder="your@email.com" required />
              </div>

              <div className="space-y-2">
                <label htmlFor="message" className="block text-sm font-label-caps text-text-muted">Message</label>
                <textarea id="message" rows={4} className="w-full bg-background border border-outline px-4 py-3 rounded focus:ring-1 focus:ring-primary focus:border-primary outline-none transition-all resize-none" placeholder="How can we help?" required></textarea>
              </div>

              <button type="submit" className="w-full bg-primary text-on-primary px-8 py-4 font-label-caps hover:opacity-90 active:scale-95 transition-all rounded">
                SEND MESSAGE
              </button>
            </form>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}
