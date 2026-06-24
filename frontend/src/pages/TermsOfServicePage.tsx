import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';

export function TermsOfServicePage() {
  return (
    <div className="min-h-screen bg-background text-text-primary flex flex-col">
      <Navbar />
      
      <main className="flex-1 pt-32 pb-24 px-6 md:px-12 max-w-4xl mx-auto space-y-12">
        <h1 className="font-display-xl text-4xl md:text-5xl text-primary">Terms of Service</h1>
        
        <div className="space-y-8 text-text-secondary leading-relaxed">
          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">Acceptance of Terms</h2>
            <p>By accessing or using ShopScale Fabric, you agree to be bound by these Terms of Service and all applicable laws and regulations. If you do not agree with any part of these terms, you may not use our services.</p>
          </section>

          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">User Responsibilities</h2>
            <p>You are responsible for maintaining the confidentiality of your account and password and for restricting access to your computer. You agree to accept responsibility for all activities that occur under your account or password.</p>
          </section>

          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">Limitation of Liability</h2>
            <p>In no event shall ShopScale Fabric, nor its directors, employees, partners, agents, suppliers, or affiliates, be liable for any indirect, incidental, special, consequential or punitive damages, including without limitation, loss of profits, data, use, goodwill, or other intangible losses, resulting from your access to or use of or inability to access or use the Service.</p>
          </section>

          <section className="space-y-4">
            <h2 className="text-2xl font-headline-lg text-primary">Governing Law</h2>
            <p>These Terms shall be governed and construed in accordance with the laws, without regard to its conflict of law provisions. Our failure to enforce any right or provision of these Terms will not be considered a waiver of those rights.</p>
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
