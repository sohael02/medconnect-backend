import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Pill, Search, Menu, X, Sun, Moon } from 'lucide-react';
import { cn } from '../../utils/helpers';
import { Button } from '../ui/Button';

export function Header() {
  const location = useLocation();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isDark, setIsDark] = useState(false);

  const navLinks = [
    { path: '/', label: 'Home' },
    { path: '/search', label: 'Search' },
    { path: '/pharmacies', label: 'Pharmacies' },
    { path: '/medicines', label: 'Medicines' },
  ];

  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-sm border-b border-secondary-200">
      <nav className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8" aria-label="Main navigation">
        <div className="flex h-16 items-center justify-between">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2" aria-label="MedConnect Home">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary-600">
              <Pill className="h-5 w-5 text-white" />
            </div>
            <span className="text-xl font-bold text-secondary-900">MedConnect</span>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex md:items-center md:gap-6">
            {navLinks.map((link) => (
              <Link
                key={link.path}
                to={link.path}
                className={cn(
                  'text-sm font-medium transition-colors',
                  location.pathname === link.path
                    ? 'text-primary-600'
                    : 'text-secondary-600 hover:text-secondary-900'
                )}
              >
                {link.label}
              </Link>
            ))}
          </div>

          {/* Actions */}
          <div className="flex items-center gap-3">
            <Link to="/search" className="hidden sm:flex">
              <Button size="sm">
                <Search className="h-4 w-4 mr-2" />
                Search
              </Button>
            </Link>

            <button
              onClick={() => setIsDark(!isDark)}
              className="p-2 rounded-lg text-secondary-500 hover:text-secondary-700 hover:bg-secondary-100 transition-colors"
              aria-label={isDark ? 'Switch to light mode' : 'Switch to dark mode'}
            >
              {isDark ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
            </button>

            {/* Mobile menu button */}
            <button
              className="md:hidden p-2 rounded-lg text-secondary-500 hover:text-secondary-700 hover:bg-secondary-100 transition-colors"
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              aria-label={isMenuOpen ? 'Close menu' : 'Open menu'}
              aria-expanded={isMenuOpen}
            >
              {isMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isMenuOpen && (
          <div className="md:hidden py-4 border-t border-secondary-200">
            <div className="flex flex-col gap-2">
              {navLinks.map((link) => (
                <Link
                  key={link.path}
                  to={link.path}
                  className={cn(
                    'px-3 py-2 rounded-lg text-base font-medium transition-colors',
                    location.pathname === link.path
                      ? 'bg-primary-50 text-primary-600'
                      : 'text-secondary-600 hover:bg-secondary-50'
                  )}
                  onClick={() => setIsMenuOpen(false)}
                >
                  {link.label}
                </Link>
              ))}
              <Link to="/search" onClick={() => setIsMenuOpen(false)}>
                <Button className="w-full justify-center">
                  <Search className="h-4 w-4 mr-2" />
                  Search Medicines
                </Button>
              </Link>
            </div>
          </div>
        )}
      </nav>
    </header>
  );
}