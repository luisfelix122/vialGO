'use client'

import { useState } from 'react'
import { supabase } from '@/lib/supabase'

type SeedStep = {
  label: string
  status: 'pending' | 'running' | 'done' | 'error'
  detail?: string
}

const YT_PLACEHOLDER = 'https://www.youtube.com/watch?v=dQw4w9WgXcQ'

const SEED_QUESTIONS = [
  {
    enunciado: '¿Qué indica una señal de PARE?',
    tipo_medio: 'video' as const,
    url_medio: YT_PLACEHOLDER,
    texto_consecuencia:
      'La señal de PARE indica que el conductor debe detenerse completamente antes de continuar.',
    opciones: [
      { texto: 'Detenerse completamente', es_correcta: true, orden: 1 },
      { texto: 'Reducir velocidad', es_correcta: false, orden: 2 },
      { texto: 'Ceder el paso', es_correcta: false, orden: 3 },
      { texto: 'Continuar con precaución', es_correcta: false, orden: 4 },
    ],
  },
  {
    enunciado: '¿Qué significa una luz amarilla en el semáforo?',
    tipo_medio: 'video' as const,
    url_medio: YT_PLACEHOLDER,
    texto_consecuencia:
      'La luz amarilla indica precaución. Debes detenerte si puedes hacerlo de manera segura.',
    opciones: [
      { texto: 'Precaución, prepararse para detenerse', es_correcta: true, orden: 1 },
      { texto: 'Acelerar para pasar', es_correcta: false, orden: 2 },
      { texto: 'Detenerse inmediatamente', es_correcta: false, orden: 3 },
      { texto: 'Ceder el paso', es_correcta: false, orden: 4 },
    ],
  },
  {
    enunciado: '¿Cuál es la velocidad máxima permitida en zona escolar en Perú?',
    tipo_medio: 'imagen' as const,
    url_medio: null,
    texto_consecuencia:
      'En zonas escolares de Perú, la velocidad máxima es de 30 km/h según el Reglamento Nacional de Tránsito.',
    opciones: [
      { texto: '30 km/h', es_correcta: true, orden: 1 },
      { texto: '40 km/h', es_correcta: false, orden: 2 },
      { texto: '50 km/h', es_correcta: false, orden: 3 },
      { texto: '20 km/h', es_correcta: false, orden: 4 },
    ],
  },
  {
    enunciado: '¿Qué debe hacer un conductor al acercarse a un cruce peatonal?',
    tipo_medio: 'video' as const,
    url_medio: YT_PLACEHOLDER,
    texto_consecuencia:
      'Los conductores deben reducir la velocidad y ceder el paso a los peatones en los cruces señalizados.',
    opciones: [
      { texto: 'Reducir velocidad y ceder el paso', es_correcta: true, orden: 1 },
      { texto: 'Tocar la bocina', es_correcta: false, orden: 2 },
      { texto: 'Acelerar para pasar primero', es_correcta: false, orden: 3 },
      { texto: 'Detenerse solo si hay policía', es_correcta: false, orden: 4 },
    ],
  },
  {
    enunciado: '¿Cuándo se debe usar el cinturón de seguridad?',
    tipo_medio: 'imagen' as const,
    url_medio: null,
    texto_consecuencia:
      'El cinturón de seguridad es obligatorio en todo momento para el conductor y todos los pasajeros.',
    opciones: [
      { texto: 'Siempre, conductor y pasajeros', es_correcta: true, orden: 1 },
      { texto: 'Solo en carretera', es_correcta: false, orden: 2 },
      { texto: 'Solo el conductor', es_correcta: false, orden: 3 },
      { texto: 'Solo en velocidades altas', es_correcta: false, orden: 4 },
    ],
  },
]

export default function SeedPage() {
  const [steps, setSteps] = useState<SeedStep[]>([
    { label: 'Crear categoría "Señales de Tránsito"', status: 'pending' },
    { label: 'Crear lección "Señales Básicas"', status: 'pending' },
    { label: 'Insertar 5 preguntas con opciones', status: 'pending' },
  ])
  const [running, setRunning] = useState(false)
  const [done, setDone] = useState(false)
  const [globalError, setGlobalError] = useState<string | null>(null)

  function updateStep(idx: number, update: Partial<SeedStep>) {
    setSteps((prev) => prev.map((s, i) => (i === idx ? { ...s, ...update } : s)))
  }

  async function runSeed() {
    setRunning(true)
    setDone(false)
    setGlobalError(null)
    setSteps([
      { label: 'Crear categoría "Señales de Tránsito"', status: 'pending' },
      { label: 'Crear lección "Señales Básicas"', status: 'pending' },
      { label: 'Insertar 5 preguntas con opciones', status: 'pending' },
    ])

    // Step 1: Categoria
    updateStep(0, { status: 'running' })
    const { data: catData, error: catError } = await supabase
      .from('categorias')
      .insert({
        nombre: 'Señales de Tránsito',
        descripcion: 'Aprende sobre las señales de tránsito más importantes',
        rol: 'conductor',
        orden: 1,
      })
      .select()
      .single()

    if (catError) {
      updateStep(0, { status: 'error', detail: catError.message })
      setGlobalError(catError.message)
      setRunning(false)
      return
    }
    updateStep(0, { status: 'done', detail: `ID: ${catData.id.slice(0, 8)}...` })

    // Step 2: Leccion
    updateStep(1, { status: 'running' })
    const { data: lecData, error: lecError } = await supabase
      .from('lecciones')
      .insert({
        categoria_id: catData.id,
        titulo: 'Señales Básicas',
        descripcion: 'Conoce las señales de tránsito fundamentales',
        orden: 1,
        puntaje_maximo: 100,
        tiempo_limite_seg: 30,
      })
      .select()
      .single()

    if (lecError) {
      updateStep(1, { status: 'error', detail: lecError.message })
      setGlobalError(lecError.message)
      setRunning(false)
      return
    }
    updateStep(1, { status: 'done', detail: `ID: ${lecData.id.slice(0, 8)}...` })

    // Step 3: Preguntas
    updateStep(2, { status: 'running' })
    let inserted = 0
    for (const q of SEED_QUESTIONS) {
      const { data: pregData, error: pregError } = await supabase
        .from('preguntas')
        .insert({
          leccion_id: lecData.id,
          categoria_id: catData.id,
          enunciado: q.enunciado,
          tipo_medio: q.tipo_medio,
          url_medio: q.url_medio,
          texto_consecuencia: q.texto_consecuencia,
          es_clasificacion: false,
          activa: true,
          orden: inserted,
        })
        .select()
        .single()

      if (pregError) {
        updateStep(2, { status: 'error', detail: pregError.message })
        setGlobalError(pregError.message)
        setRunning(false)
        return
      }

      const { error: optError } = await supabase.from('opciones_pregunta').insert(
        q.opciones.map((op) => ({
          pregunta_id: pregData.id,
          texto: op.texto,
          es_correcta: op.es_correcta,
          orden: op.orden,
          imagen_url: null,
        }))
      )

      if (optError) {
        updateStep(2, { status: 'error', detail: optError.message })
        setGlobalError(optError.message)
        setRunning(false)
        return
      }

      inserted++
      updateStep(2, { status: 'running', detail: `${inserted}/5 preguntas...` })
    }

    updateStep(2, { status: 'done', detail: '5 preguntas + 20 opciones insertadas' })
    setDone(true)
    setRunning(false)
  }

  const statusIcon = (status: SeedStep['status']) => {
    if (status === 'pending') return <span className="text-white/30">○</span>
    if (status === 'running') return <span className="text-vialgo-amber animate-pulse">◐</span>
    if (status === 'done') return <span className="text-vialgo-green-light">✓</span>
    return <span className="text-red-400">✗</span>
  }

  return (
    <div className="p-8 max-w-2xl">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white">Seed Data</h1>
        <p className="text-white/50 mt-1">
          Insertar datos de prueba para testear el flujo completo de VialGo
        </p>
      </div>

      {/* Info card */}
      <div className="bg-surface border border-vialgo-amber/30 rounded-xl p-5 mb-8">
        <div className="flex items-start gap-3">
          <span className="text-vialgo-amber text-xl mt-0.5">⚠️</span>
          <div>
            <p className="text-white font-medium mb-2">¿Qué se va a insertar?</p>
            <ul className="text-white/60 text-sm space-y-1 list-disc list-inside">
              <li>1 categoría: &quot;Señales de Tránsito&quot; (rol: conductor)</li>
              <li>1 lección: &quot;Señales Básicas&quot; vinculada a la categoría</li>
              <li>5 preguntas con links de YouTube de prueba</li>
              <li>20 opciones de respuesta (4 por pregunta)</li>
            </ul>
            <p className="text-white/40 text-xs mt-3">
              Los links de YouTube son placeholders. Reemplazalos con URLs reales en la página de Preguntas.
            </p>
          </div>
        </div>
      </div>

      {/* Action */}
      {!running && !done && (
        <button
          onClick={runSeed}
          className="w-full py-4 bg-vialgo-green hover:bg-vialgo-green-light text-white rounded-xl font-semibold text-lg transition-colors flex items-center justify-center gap-3"
        >
          <span>🌱</span> Insertar Datos de Prueba
        </button>
      )}

      {/* Progress */}
      {(running || done || globalError) && (
        <div className="bg-surface border border-white/10 rounded-xl p-6 space-y-4">
          <h3 className="text-white font-medium mb-4">Progreso</h3>
          {steps.map((step, i) => (
            <div key={i} className="flex items-start gap-3">
              <div className="text-lg mt-0.5 w-6 text-center flex-shrink-0">
                {statusIcon(step.status)}
              </div>
              <div className="flex-1">
                <p
                  className={`text-sm font-medium ${
                    step.status === 'done'
                      ? 'text-white'
                      : step.status === 'error'
                      ? 'text-red-400'
                      : step.status === 'running'
                      ? 'text-vialgo-amber'
                      : 'text-white/40'
                  }`}
                >
                  {step.label}
                </p>
                {step.detail && (
                  <p
                    className={`text-xs mt-0.5 ${
                      step.status === 'error' ? 'text-red-400' : 'text-white/40'
                    }`}
                  >
                    {step.detail}
                  </p>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {done && (
        <div className="mt-6 p-5 bg-vialgo-green/20 border border-vialgo-green/40 rounded-xl">
          <div className="flex items-center gap-3 mb-3">
            <span className="text-vialgo-green-light text-2xl">✅</span>
            <p className="text-white font-semibold">¡Datos de prueba insertados correctamente!</p>
          </div>
          <p className="text-white/60 text-sm mb-4">
            Ya podés probar el flujo completo en la app de VialGo. Recordá actualizar los links de
            YouTube por videos reales de educación vial.
          </p>
          <div className="flex gap-3">
            <a
              href="/preguntas"
              className="px-4 py-2 bg-vialgo-green hover:bg-vialgo-green-light text-white rounded-lg text-sm font-medium transition-colors"
            >
              Ver Preguntas →
            </a>
            <button
              onClick={() => {
                setDone(false)
                setGlobalError(null)
                setSteps([
                  { label: 'Crear categoría "Señales de Tránsito"', status: 'pending' },
                  { label: 'Crear lección "Señales Básicas"', status: 'pending' },
                  { label: 'Insertar 5 preguntas con opciones', status: 'pending' },
                ])
              }}
              className="px-4 py-2 bg-white/10 hover:bg-white/20 text-white rounded-lg text-sm transition-colors"
            >
              Insertar de nuevo
            </button>
          </div>
        </div>
      )}

      {globalError && !done && (
        <div className="mt-4 p-4 bg-red-500/20 border border-red-500/40 rounded-xl">
          <p className="text-red-300 text-sm">Error: {globalError}</p>
          <button
            onClick={() => {
              setGlobalError(null)
              setSteps([
                { label: 'Crear categoría "Señales de Tránsito"', status: 'pending' },
                { label: 'Crear lección "Señales Básicas"', status: 'pending' },
                { label: 'Insertar 5 preguntas con opciones', status: 'pending' },
              ])
            }}
            className="mt-3 px-4 py-2 bg-white/10 hover:bg-white/20 text-white rounded-lg text-sm"
          >
            Reintentar
          </button>
        </div>
      )}

      {/* Preview of what will be seeded */}
      <div className="mt-10">
        <h2 className="text-white/60 text-sm font-medium uppercase tracking-wider mb-4">
          Preguntas que se van a insertar
        </h2>
        <div className="space-y-3">
          {SEED_QUESTIONS.map((q, i) => (
            <div key={i} className="bg-surface border border-white/10 rounded-xl p-4">
              <div className="flex items-start gap-3">
                <span className="text-white/30 text-sm font-mono mt-0.5">{i + 1}.</span>
                <div className="flex-1">
                  <p className="text-white text-sm font-medium">{q.enunciado}</p>
                  <div className="flex items-center gap-2 mt-1">
                    <span
                      className={`px-2 py-0.5 rounded text-xs ${
                        q.tipo_medio === 'video'
                          ? 'bg-red-500/20 text-red-400'
                          : 'bg-blue-500/20 text-blue-400'
                      }`}
                    >
                      {q.tipo_medio}
                    </span>
                    <span className="text-white/30 text-xs">
                      {q.opciones.length} opciones
                    </span>
                  </div>
                  <div className="mt-2 space-y-1">
                    {q.opciones.map((op, j) => (
                      <div key={j} className="flex items-center gap-2">
                        <span
                          className={`text-xs ${
                            op.es_correcta ? 'text-vialgo-green-light font-medium' : 'text-white/40'
                          }`}
                        >
                          {op.es_correcta ? '✓' : '○'} {op.texto}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
