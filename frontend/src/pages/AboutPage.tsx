import { Navbar } from '../components/Navbar';

export function AboutPage() {
  return (
    <div className="min-h-screen bg-background text-text-primary">
      <Navbar />
      
      <main className="pt-32 pb-24 px-6 md:px-12 max-w-4xl mx-auto space-y-16">
        
        {/* Header Section */}
        <section className="space-y-6 text-center animate-fade-in-up">
          <h1 className="font-display-xl text-4xl md:text-5xl lg:text-6xl tracking-tight">
            ABOUT SHOPSCALE FABRIC
          </h1>
          <p className="font-headline-lg text-xl md:text-2xl text-primary font-medium">
            Built for Scale. Engineered for Resilience.
          </p>
        </section>

        {/* The Challenge */}
        <section className="space-y-6 animate-fade-in-up delay-100">
          <p className="text-lg md:text-xl leading-relaxed text-text-muted">
            Modern e-commerce platforms often fail when demand peaks.
          </p>
          <p className="text-lg md:text-xl leading-relaxed text-text-muted">
            During flash sales, seasonal campaigns, and high-traffic events, traditional systems struggle with cascading failures, checkout outages, inventory inconsistencies, and slow response times.
          </p>
        </section>

        {/* The Solution */}
        <section className="p-8 md:p-12 bg-surface border border-border-subtle/30 rounded-2xl space-y-6 shadow-xl animate-fade-in-up delay-200">
          <h2 className="font-display-lg text-2xl md:text-3xl text-primary">
            ShopScale Fabric was engineered to solve these challenges.
          </h2>
          <div className="space-y-4 text-base md:text-lg leading-relaxed text-text-secondary">
            <p>
              Built on an event-driven microservices architecture, ShopScale Fabric enables marketplace systems to remain responsive, scalable, and fault-tolerant even when individual services experience failures.
            </p>
            <p>
              Instead of relying on tightly coupled communication patterns, every critical business workflow is designed around asynchronous processing, resilient service boundaries, and distributed event streams.
            </p>
            <p className="font-medium text-text-primary">
              The result is a platform capable of handling rapid growth, maintaining operational continuity, and delivering a reliable customer experience under extreme load.
            </p>
          </div>
        </section>

        {/* The Problem We Solve */}
        <section className="space-y-10 pt-8 animate-fade-in-up delay-300">
          <h2 className="font-display-lg text-3xl border-b border-border-subtle/50 pb-4">
            The Problem We Solve
          </h2>
          <p className="text-lg text-text-muted">
            Traditional commerce platforms face three major challenges:
          </p>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="space-y-3 p-6 bg-surface/50 rounded-xl border border-border-subtle/20 hover:border-primary/40 transition-colors">
              <h3 className="font-headline-lg text-xl text-primary">Cascading Service Failures</h3>
              <p className="text-text-muted">A single slow service can trigger system-wide outages.</p>
            </div>

            <div className="space-y-3 p-6 bg-surface/50 rounded-xl border border-border-subtle/20 hover:border-primary/40 transition-colors">
              <h3 className="font-headline-lg text-xl text-primary">Limited Scalability</h3>
              <p className="text-text-muted">Monolithic architectures force businesses to scale entire applications instead of scaling only the services that require additional capacity.</p>
            </div>

            <div className="space-y-3 p-6 bg-surface/50 rounded-xl border border-border-subtle/20 hover:border-primary/40 transition-colors">
              <h3 className="font-headline-lg text-xl text-primary">Poor Operational Visibility</h3>
              <p className="text-text-muted">Without distributed tracing and observability, diagnosing production issues becomes time-consuming and expensive.</p>
            </div>
          </div>
        </section>

        {/* Conclusion */}
        <section className="pt-8 text-center animate-fade-in-up delay-400">
          <p className="text-xl md:text-2xl font-display-md text-primary leading-relaxed bg-primary/5 p-8 rounded-2xl border border-primary/20">
            ShopScale Fabric eliminates these bottlenecks through decoupled services, event-driven communication, and enterprise-grade observability.
          </p>
        </section>

      </main>
    </div>
  );
}
