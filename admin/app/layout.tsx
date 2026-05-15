import type { Metadata } from 'next'
import './globals.css'
import Link from 'next/link'

export const metadata: Metadata = {
  title: 'VialGo Admin',
  description: 'Admin dashboard for VialGo road safety app',
}

const navItems = [
  { href: '/', label: 'Dashboard', icon: '📊' },
  { href: '/categorias', label: 'Categorías', icon: '📁' },
  { href: '/lecciones', label: 'Lecciones', icon: '📚' },
  { href: '/preguntas', label: 'Preguntas', icon: '❓' },
  { href: '/seed', label: 'Seed Data', icon: '🌱' },
]

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="es">
      <body className="bg-background text-white min-h-screen flex">
        {/* Sidebar */}
        <aside className="w-64 bg-surface border-r border-white/10 flex flex-col fixed h-full z-10">
          {/* Logo */}
          <div className="p-6 border-b border-white/10">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-vialgo-green rounded-lg flex items-center justify-center text-xl">
                🚦
              </div>
              <div>
                <h1 className="text-white font-bold text-lg leading-tight">VialGo</h1>
                <p className="text-white/50 text-xs">Admin Panel</p>
              </div>
            </div>
          </div>

          {/* Navigation */}
          <nav className="flex-1 p-4 space-y-1">
            {navItems.map((item) => (
              <Link
                key={item.href}
                href={item.href}
                className="flex items-center gap-3 px-4 py-3 rounded-lg text-white/70 hover:text-white hover:bg-vialgo-green/20 hover:border-l-2 hover:border-vialgo-green transition-all duration-150 group"
              >
                <span className="text-lg">{item.icon}</span>
                <span className="text-sm font-medium">{item.label}</span>
              </Link>
            ))}
          </nav>

          {/* Footer */}
          <div className="p-4 border-t border-white/10">
            <p className="text-white/30 text-xs text-center">VialGo Admin v1.0</p>
          </div>
        </aside>

        {/* Main content */}
        <main className="flex-1 ml-64 min-h-screen bg-background">
          {children}
        </main>
      </body>
    </html>
  )
}
