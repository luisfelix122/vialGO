'use client'

import { useEffect, useState } from 'react'
import { supabase } from '@/lib/supabase'

type Stats = {
  categorias: number
  lecciones: number
  preguntas: number
  preguntasActivas: number
}

export default function DashboardPage() {
  const [stats, setStats] = useState<Stats>({
    categorias: 0,
    lecciones: 0,
    preguntas: 0,
    preguntasActivas: 0,
  })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function fetchStats() {
      try {
        const [catRes, lecRes, pregRes, pregActivasRes] = await Promise.all([
          supabase.from('categorias').select('*', { count: 'exact', head: true }),
          supabase.from('lecciones').select('*', { count: 'exact', head: true }),
          supabase.from('preguntas').select('*', { count: 'exact', head: true }),
          supabase.from('preguntas').select('*', { count: 'exact', head: true }).eq('activa', true),
        ])

        setStats({
          categorias: catRes.count ?? 0,
          lecciones: lecRes.count ?? 0,
          preguntas: pregRes.count ?? 0,
          preguntasActivas: pregActivasRes.count ?? 0,
        })
      } catch (err) {
        setError('Error connecting to Supabase')
        console.error(err)
      } finally {
        setLoading(false)
      }
    }

    fetchStats()
  }, [])

  const cards = [
    {
      label: 'Categorías',
      value: stats.categorias,
      icon: '📁',
      color: 'border-vialgo-green',
      bg: 'bg-vialgo-green/10',
      href: '/categorias',
    },
    {
      label: 'Lecciones',
      value: stats.lecciones,
      icon: '📚',
      color: 'border-blue-500',
      bg: 'bg-blue-500/10',
      href: '/lecciones',
    },
    {
      label: 'Preguntas Totales',
      value: stats.preguntas,
      icon: '❓',
      color: 'border-vialgo-amber',
      bg: 'bg-vialgo-amber/10',
      href: '/preguntas',
    },
    {
      label: 'Preguntas Activas',
      value: stats.preguntasActivas,
      icon: '✅',
      color: 'border-purple-500',
      bg: 'bg-purple-500/10',
      href: '/preguntas',
    },
  ]

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white">Dashboard</h1>
        <p className="text-white/50 mt-1">Overview de contenido en VialGo</p>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-500/20 border border-red-500/50 rounded-lg text-red-300 text-sm">
          {error}
        </div>
      )}

      {/* Stats Grid */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {[...Array(4)].map((_, i) => (
            <div key={i} className="bg-surface rounded-xl p-6 border border-white/10 animate-pulse">
              <div className="h-4 bg-white/10 rounded mb-4 w-2/3"></div>
              <div className="h-8 bg-white/10 rounded w-1/2"></div>
            </div>
          ))}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {cards.map((card) => (
            <a
              key={card.label}
              href={card.href}
              className={`bg-surface rounded-xl p-6 border-l-4 ${card.color} border border-white/10 hover:border-white/20 transition-all duration-200 cursor-pointer block`}
            >
              <div className={`inline-flex p-3 rounded-lg ${card.bg} mb-4`}>
                <span className="text-2xl">{card.icon}</span>
              </div>
              <div className="text-3xl font-bold text-white mb-1">
                {card.value}
              </div>
              <div className="text-white/50 text-sm">{card.label}</div>
            </a>
          ))}
        </div>
      )}

      {/* Quick Actions */}
      <div className="mt-10">
        <h2 className="text-xl font-semibold text-white mb-4">Acciones Rápidas</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <a
            href="/preguntas"
            className="bg-surface border border-white/10 hover:border-vialgo-green/50 rounded-xl p-5 flex items-center gap-4 transition-all duration-200 group"
          >
            <div className="w-12 h-12 bg-vialgo-green/20 rounded-lg flex items-center justify-center text-2xl group-hover:bg-vialgo-green/30 transition-colors">
              ➕
            </div>
            <div>
              <p className="text-white font-medium">Nueva Pregunta</p>
              <p className="text-white/40 text-sm">Agregar contenido educativo</p>
            </div>
          </a>
          <a
            href="/categorias"
            className="bg-surface border border-white/10 hover:border-blue-500/50 rounded-xl p-5 flex items-center gap-4 transition-all duration-200 group"
          >
            <div className="w-12 h-12 bg-blue-500/20 rounded-lg flex items-center justify-center text-2xl group-hover:bg-blue-500/30 transition-colors">
              📁
            </div>
            <div>
              <p className="text-white font-medium">Nueva Categoría</p>
              <p className="text-white/40 text-sm">Organizar el contenido</p>
            </div>
          </a>
          <a
            href="/seed"
            className="bg-surface border border-white/10 hover:border-vialgo-amber/50 rounded-xl p-5 flex items-center gap-4 transition-all duration-200 group"
          >
            <div className="w-12 h-12 bg-vialgo-amber/20 rounded-lg flex items-center justify-center text-2xl group-hover:bg-vialgo-amber/30 transition-colors">
              🌱
            </div>
            <div>
              <p className="text-white font-medium">Insertar Datos de Prueba</p>
              <p className="text-white/40 text-sm">5 preguntas con videos</p>
            </div>
          </a>
        </div>
      </div>

      {/* Info box */}
      <div className="mt-8 p-5 bg-surface border border-vialgo-green/30 rounded-xl">
        <div className="flex items-start gap-3">
          <span className="text-vialgo-green text-xl mt-0.5">ℹ️</span>
          <div>
            <p className="text-white font-medium mb-1">Estado del sistema</p>
            <p className="text-white/50 text-sm">
              Conectado a Supabase. Las preguntas sin leccion_id son preguntas de clasificación.
              Cada pregunta requiere exactamente 4 opciones, con una marcada como correcta.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
