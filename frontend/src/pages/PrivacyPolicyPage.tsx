import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';

export function PrivacyPolicyPage() {
  return (
    <div className="min-h-screen bg-background text-text-primary flex flex-col">
      <Navbar />
      
      <main className="flex-1 pt-32 pb-24 px-6 md:px-12 max-w-4xl mx-auto space-y-12">
        <h1 className="font-display-xl text-4xl md:text-5xl text-primary">Privacy Policy</h1>
        
        <div className="space-y-8 text-text-secondary leading-relaxed">
          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">Information We Collect</h2>
            <p>We collect information you provide directly to us, such as when you create or modify your account, request on-demand services, contact customer support, or otherwise communicate with us.</p>
          </section>

          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">How We Use It</h2>
            <p>We may use the information we collect about you to provide, maintain, and improve our services, including to facilitate payments, send receipts, provide products and services you request (and send related information), develop new features, provide customer support to Users, develop safety features, authenticate users, and send product updates and administrative messages.</p>
          </section>

          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">Cookies</h2>
            <p>We use cookies and similar tracking technologies to track the activity on our service and hold certain information. You can instruct your browser to refuse all cookies or to indicate when a cookie is being sent.</p>
          </section>

          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">Data Sharing</h2>
            <p>We may share your information with our vendors, consultants, marketing partners, and other service providers who need access to such information to carry out work on our behalf.</p>
          </section>

          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">Contact for Privacy Concerns</h2>
            <p>If you have any questions about this Privacy Policy, please contact us at privacy@shopscalefabric.com.</p>
          </section>
          
          <div className="p-4 bg-surface-container-low border border-border-subtle/30 rounded-lg text-sm text-text-muted mt-8 italic">
            Note: This is placeholder legal text for demonstration purposes and has not been professionally reviewed.
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}
