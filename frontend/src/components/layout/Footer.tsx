import { Link } from 'react-router-dom';
import { Pill, MapPin, Phone, Mail, ExternalLink } from 'lucide-react';

export function Footer() {
  const currentYear = new Date().getFullYear();

  const footerLinks = {
    product: [
      { label: 'Search Medicines', href: '/search' },
      { label: 'Browse Pharmacies', href: '/pharmacies' },
      { label: 'Medicine Catalog', href: '/medicines' },
      { label: 'API Documentation', href: '/api-docs' },
    ],
    company: [
      { label: 'About Us', href: '/about' },
      { label: 'Careers', href: '/careers' },
      { label: 'Blog', href: '/blog' },
      { label: 'Press', href: '/press' },
    ],
    support: [
      { label: 'Help Center', href: '/help' },
      { label: 'Contact Us', href: '/contact' },
      { label: 'Privacy Policy', href: '/privacy' },
      { label: 'Terms of Service', href: '/terms' },
    ],
    legal: [
      { label: 'Data Policy', href: '/data-policy' },
      { label: 'Cookie Policy', href: '/cookie-policy' },
      { label: 'Accessibility', href: '/accessibility' },
    ],
  };

  const socialLinks = [
    { icon: ExternalLink, href: 'https://github.com/sohael02/medconnect-backend', label: 'GitHub' },
    { icon: ExternalLink, href: 'https://linkedin.com/company/medconnect', label: 'LinkedIn' },
  ];

  return (
    <footer className="bg-secondary-900 text-secondary-300" role="contentinfo">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-12 lg:py-16">
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-8">
          {/* Brand */}
          <div className="col-span-2 lg:col-span-1">
            <Link to="/" className="flex items-center gap-2 mb-4" aria-label="MedConnect Home">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary-500">
                <Pill className="h-5 w-5 text-white" />
              </div>
              <span className="text-xl font-bold text-white">MedConnect</span>
            </Link>
            <p className="text-sm text-secondary-400 mb-6">
              Find nearby pharmacies with the medicines you need. 
              Your health, connected.
            </p>
            <div className="flex gap-4">
              {socialLinks.map((social) => (
                <a
                  key={social.label}
                  href={social.href}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-secondary-400 hover:text-white transition-colors"
                  aria-label={social.label}
                >
                  <social.icon className="h-5 w-5" />
                </a>
              ))}
            </div>
          </div>

          {/* Product */}
          <nav aria-label="Product links">
            <h3 className="text-sm font-semibold text-white uppercase tracking-wider mb-4">Product</h3>
            <ul className="space-y-3">
              {footerLinks.product.map((link) => (
                <li key={link.label}>
                  <Link
                    to={link.href}
                    className="text-sm text-secondary-400 hover:text-white transition-colors"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </nav>

          {/* Company */}
          <nav aria-label="Company links">
            <h3 className="text-sm font-semibold text-white uppercase tracking-wider mb-4">Company</h3>
            <ul className="space-y-3">
              {footerLinks.company.map((link) => (
                <li key={link.label}>
                  <Link
                    to={link.href}
                    className="text-sm text-secondary-400 hover:text-white transition-colors"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </nav>

          {/* Support */}
          <nav aria-label="Support links">
            <h3 className="text-sm font-semibold text-white uppercase tracking-wider mb-4">Support</h3>
            <ul className="space-y-3">
              {footerLinks.support.map((link) => (
                <li key={link.label}>
                  <Link
                    to={link.href}
                    className="text-sm text-secondary-400 hover:text-white transition-colors"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </nav>

          {/* Contact */}
          <div>
            <h3 className="text-sm font-semibold text-white uppercase tracking-wider mb-4">Contact</h3>
            <address className="not-italic space-y-3 text-sm">
              <div className="flex items-start gap-3">
                <MapPin className="h-5 w-5 text-primary-400 flex-shrink-0 mt-0.5" />
                <span>Andhra Pradesh, India</span>
              </div>
              <a href="tel:+91-XXXXXXXXXX" className="flex items-center gap-3 hover:text-white transition-colors">
                <Phone className="h-5 w-5 text-primary-400 flex-shrink-0" />
                <span>+91-XXXXXXXXXX</span>
              </a>
              <a href="mailto:support@medconnect.example.com" className="flex items-center gap-3 hover:text-white transition-colors">
                <Mail className="h-5 w-5 text-primary-400 flex-shrink-0" />
                <span>support@medconnect.example.com</span>
              </a>
            </address>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="mt-12 pt-8 border-t border-secondary-800">
          <div className="flex flex-col md:flex-row items-center justify-between gap-4">
            <p className="text-sm text-secondary-500">
              © {currentYear} MedConnect. All rights reserved.
            </p>
            <div className="flex items-center gap-6">
              <Link
                to="/privacy"
                className="text-sm text-secondary-500 hover:text-white transition-colors"
              >
                Privacy Policy
              </Link>
              <Link
                to="/terms"
                className="text-sm text-secondary-500 hover:text-white transition-colors"
              >
                Terms of Service
              </Link>
              <Link
                to="/data-policy"
                className="text-sm text-secondary-500 hover:text-white transition-colors"
              >
                Data Policy
              </Link>
            </div>
          </div>
        </div>

        {/* Disclaimer */}
        <div className="mt-8 p-4 bg-secondary-800/50 rounded-lg">
          <p className="text-xs text-secondary-500 text-center">
            <strong>Disclaimer:</strong> MedConnect is an information service only. Medicine availability data is sourced from 
            public datasets and may not reflect real-time stock levels. Always contact the pharmacy directly to confirm 
            availability before visiting. MedConnect does not sell medicines or provide medical advice.
          </p>
        </div>
      </div>
    </footer>
  );
}