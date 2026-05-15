'use client'

import { useEffect, useState } from 'react'
import { supabase, type Pregunta, type Categoria, type Leccion, type OpcionPregunta } from '@/lib/supabase'

type PreguntaWithOpciones = Pregunta & { opciones: OpcionPregunta[] }

const emptyOpcion = (preguntaId: string, orden: number): Omit<OpcionPregunta, 'id'> => ({
  pregunta_id: preguntaId,
  texto: '',
  es_correcta: orden === 1,
  orden,
  imagen_url: null,
})

const emptyForm: Omit<Pregunta, 'id'> & { opciones: { texto: string; es_correcta: boolean; orden: number }[] } = {
  leccion_id: null,
  categoria_id: '',
  enunciado: '',
  tipo_medio: 'imagen',
  url_medio: null,
  duracion_medio_seg: null,
  texto_consecuencia: null,
  es_clasificacion: false,
  activa: true,
  orden: 0,
  opciones: [
    { texto: '', es_correcta: true, orden: 1 },
    { texto: '', es_correcta: false, orden: 2 },
    { texto: '', es_correcta: false, orden: 3 },
    { texto: '', es_correcta: false, orden: 4 },
  ],
}

export default function PreguntasPage() {
  const [preguntas, setPreguntas] = useState<PreguntaWithOpciones[]>([])
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [lecciones, setLecciones] = useState<Leccion[]>([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState<PreguntaWithOpciones | null>(null)
  const [expandedId, setExpandedId] = useState<string | null>(null)
  const [form, setForm] = useState(emptyForm)
  const [toast, setToast] = useState<{ type: 'success' | 'error'; msg: string } | null>(null)
  const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null)

  function showToast(type: 'success' | 'error', msg: string) {
    setToast({ type, msg })
    setTimeout(() => setToast(null), 3500)
  }

  async function fetchAll() {
    setLoading(true)
    const [pregRes, catRes, lecRes] = await Promise.all([
      supabase
        .from('preguntas')
        .select('*, opciones_pregunta(*)')
        .order('orden', { ascending: true }),
      supabase.from('categorias').select('*').order('nombre'),
      supabase.from('lecciones').select('*').order('titulo'),
    ])

    if (pregRes.error) showToast('error', pregRes.error.message)
    else {
      const data = (pregRes.data ?? []).map((p: Pregunta & { opciones_pregunta: OpcionPregunta[] }) => ({
        ...p,
        opciones: (p.opciones_pregunta ?? []).sort((a: OpcionPregunta, b: OpcionPregunta) => a.orden - b.orden),
      }))
      setPreguntas(data)
    }
    if (catRes.data) setCategorias(catRes.data)
    if (lecRes.data) setLecciones(lecRes.data)
    setLoading(false)
  }

  useEffect(() => {
    fetchAll()
  }, [])

  function getCategoriaName(id: string) {
    return categorias.find((c) => c.id === id)?.nombre ?? '—'
  }

  function getLeccionName(id: string | null) {
    if (!id) return null
    return lecciones.find((l) => l.id === id)?.titulo ?? null
  }

  function openCreate() {
    setEditing(null)
    setForm({ ...emptyForm, categoria_id: categorias[0]?.id ?? '' })
    setShowModal(true)
  }

  function openEdit(preg: PreguntaWithOpciones) {
    setEditing(preg)
    const sortedOpts = [...preg.opciones].sort((a, b) => a.orden - b.orden)
    setForm({
      leccion_id: preg.leccion_id,
      categoria_id: preg.categoria_id,
      enunciado: preg.enunciado,
      tipo_medio: preg.tipo_medio ?? 'imagen',
      url_medio: preg.url_medio,
      duracion_medio_seg: preg.duracion_medio_seg,
      texto_consecuencia: preg.texto_consecuencia,
      es_clasificacion: preg.es_clasificacion,
      activa: preg.activa,
      orden: preg.orden ?? 0,
      opciones: sortedOpts.length === 4
        ? sortedOpts.map((o) => ({ texto: o.texto, es_correcta: o.es_correcta, orden: o.orden }))
        : [
            { texto: '', es_correcta: true, orden: 1 },
            { texto: '', es_correcta: false, orden: 2 },
            { texto: '', es_correcta: false, orden: 3 },
            { texto: '', es_correcta: false, orden: 4 },
          ],
    })
    setShowModal(true)
  }

  function setCorrectOption(idx: number) {
    setForm({
      ...form,
      opciones: form.opciones.map((o, i) => ({ ...o, es_correcta: i === idx })),
    })
  }

  function updateOptionText(idx: number, texto: string) {
    setForm({
      ...form,
      opciones: form.opciones.map((o, i) => (i === idx ? { ...o, texto } : o)),
    })
  }

  async function handleSave() {
    if (!form.enunciado.trim()) {
      showToast('error', 'El enunciado es requerido')
      return
    }
    if (!form.categoria_id) {
      showToast('error', 'La categoría es requerida')
      return
    }
    const correctCount = form.opciones.filter((o) => o.es_correcta).length
    if (correctCount !== 1) {
      showToast('error', 'Debe haber exactamente 1 opción correcta')
      return
    }
    const emptyOptions = form.opciones.filter((o) => !o.texto.trim())
    if (emptyOptions.length > 0) {
      showToast('error', 'Todas las opciones deben tener texto')
      return
    }

    setSaving(true)

    const pregPayload = {
      leccion_id: form.leccion_id || null,
      categoria_id: form.categoria_id,
      enunciado: form.enunciado.trim(),
      tipo_medio: form.tipo_medio,
      url_medio: form.url_medio?.trim() || null,
      duracion_medio_seg: form.duracion_medio_seg,
      texto_consecuencia: form.texto_consecuencia?.trim() || null,
      es_clasificacion: form.es_clasificacion,
      activa: form.activa,
      orden: form.orden ?? 0,
    }

    if (editing) {
      const { error: pregError } = await supabase
        .from('preguntas')
        .update(pregPayload)
        .eq('id', editing.id)

      if (pregError) {
        showToast('error', pregError.message)
        setSaving(false)
        return
      }

      // Delete old options and re-insert
      await supabase.from('opciones_pregunta').delete().eq('pregunta_id', editing.id)
      const { error: optError } = await supabase.from('opciones_pregunta').insert(
        form.opciones.map((o) => ({
          pregunta_id: editing.id,
          texto: o.texto.trim(),
          es_correcta: o.es_correcta,
          orden: o.orden,
          imagen_url: null,
        }))
      )
      if (optError) showToast('error', optError.message)
      else {
        showToast('success', 'Pregunta actualizada')
        setShowModal(false)
        fetchAll()
      }
    } else {
      const { data: newPreg, error: pregError } = await supabase
        .from('preguntas')
        .insert(pregPayload)
        .select()
        .single()

      if (pregError || !newPreg) {
        showToast('error', pregError?.message ?? 'Error al crear pregunta')
        setSaving(false)
        return
      }

      const { error: optError } = await supabase.from('opciones_pregunta').insert(
        form.opciones.map((o) => ({
          pregunta_id: newPreg.id,
          texto: o.texto.trim(),
          es_correcta: o.es_correcta,
          orden: o.orden,
          imagen_url: null,
        }))
      )
      if (optError) showToast('error', optError.message)
      else {
        showToast('success', 'Pregunta creada')
        setShowModal(false)
        fetchAll()
      }
    }
    setSaving(false)
  }

  async function handleDelete(id: string) {
    await supabase.from('opciones_pregunta').delete().eq('pregunta_id', id)
    const { error } = await supabase.from('preguntas').delete().eq('id', id)
    if (error) showToast('error', error.message)
    else {
      showToast('success', 'Pregunta eliminada')
      fetchAll()
    }
    setDeleteConfirm(null)
  }

  async function toggleActiva(preg: PreguntaWithOpciones) {
    const { error } = await supabase
      .from('preguntas')
      .update({ activa: !preg.activa })
      .eq('id', preg.id)
    if (error) showToast('error', error.message)
    else fetchAll()
  }

  return (
    <div className="p-8">
      {toast && (
        <div
          className={`fixed top-6 right-6 z-50 px-5 py-3 rounded-lg shadow-lg text-sm font-medium ${
            toast.type === 'success' ? 'bg-vialgo-green text-white' : 'bg-red-600 text-white'
          }`}
        >
          {toast.msg}
        </div>
      )}

      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold text-white">Preguntas</h1>
          <p className="text-white/50 mt-1">Banco de preguntas educativas</p>
        </div>
        <button
          onClick={openCreate}
          className="px-5 py-2.5 bg-vialgo-green hover:bg-vialgo-green-light text-white rounded-lg font-medium transition-colors flex items-center gap-2"
        >
          <span>+</span> Nueva Pregunta
        </button>
      </div>

      <div className="bg-surface border border-white/10 rounded-xl overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-white/40">Cargando...</div>
        ) : preguntas.length === 0 ? (
          <div className="p-12 text-center text-white/40">No hay preguntas aún.</div>
        ) : (
          <div>
            {preguntas.map((preg, i) => (
              <div
                key={preg.id}
                className={`border-b border-white/5 ${i === preguntas.length - 1 ? 'border-b-0' : ''}`}
              >
                {/* Row */}
                <div className="flex items-center gap-4 px-6 py-4 hover:bg-white/5 transition-colors">
                  {/* Expand toggle */}
                  <button
                    onClick={() => setExpandedId(expandedId === preg.id ? null : preg.id)}
                    className="text-white/40 hover:text-white transition-colors w-6 text-center flex-shrink-0"
                  >
                    {expandedId === preg.id ? '▼' : '▶'}
                  </button>

                  {/* Content */}
                  <div className="flex-1 min-w-0">
                    <p className="text-white text-sm font-medium truncate">{preg.enunciado}</p>
                    <div className="flex items-center gap-3 mt-1">
                      <span className="text-white/40 text-xs">
                        {getCategoriaName(preg.categoria_id)}
                      </span>
                      {getLeccionName(preg.leccion_id) && (
                        <>
                          <span className="text-white/20 text-xs">·</span>
                          <span className="text-white/40 text-xs">
                            {getLeccionName(preg.leccion_id)}
                          </span>
                        </>
                      )}
                      {preg.tipo_medio && (
                        <>
                          <span className="text-white/20 text-xs">·</span>
                          <span
                            className={`px-2 py-0.5 rounded text-xs ${
                              preg.tipo_medio === 'video'
                                ? 'bg-red-500/20 text-red-400'
                                : 'bg-blue-500/20 text-blue-400'
                            }`}
                          >
                            {preg.tipo_medio}
                          </span>
                        </>
                      )}
                      {preg.es_clasificacion && (
                        <span className="px-2 py-0.5 bg-vialgo-amber/20 text-vialgo-amber rounded text-xs">
                          clasificación
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Status + Actions */}
                  <div className="flex items-center gap-3 flex-shrink-0">
                    <button
                      onClick={() => toggleActiva(preg)}
                      className={`relative inline-flex h-5 w-9 items-center rounded-full transition-colors ${
                        preg.activa ? 'bg-vialgo-green' : 'bg-white/20'
                      }`}
                    >
                      <span
                        className={`inline-block h-3.5 w-3.5 transform rounded-full bg-white transition-transform ${
                          preg.activa ? 'translate-x-4' : 'translate-x-1'
                        }`}
                      />
                    </button>
                    <button
                      onClick={() => openEdit(preg)}
                      className="px-3 py-1.5 text-sm bg-white/10 hover:bg-white/20 text-white rounded-lg transition-colors"
                    >
                      Editar
                    </button>
                    {deleteConfirm === preg.id ? (
                      <div className="flex items-center gap-1">
                        <button
                          onClick={() => handleDelete(preg.id)}
                          className="px-3 py-1.5 text-sm bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors"
                        >
                          Confirmar
                        </button>
                        <button
                          onClick={() => setDeleteConfirm(null)}
                          className="px-3 py-1.5 text-sm bg-white/10 hover:bg-white/20 text-white rounded-lg transition-colors"
                        >
                          Cancelar
                        </button>
                      </div>
                    ) : (
                      <button
                        onClick={() => setDeleteConfirm(preg.id)}
                        className="px-3 py-1.5 text-sm bg-red-500/20 hover:bg-red-500/40 text-red-400 rounded-lg transition-colors"
                      >
                        Eliminar
                      </button>
                    )}
                  </div>
                </div>

                {/* Expanded: inline options */}
                {expandedId === preg.id && (
                  <div className="px-12 pb-5 bg-background/50">
                    <div className="border border-white/10 rounded-lg p-4">
                      <p className="text-white/60 text-xs font-medium mb-3 uppercase tracking-wider">
                        Opciones ({preg.opciones.length})
                      </p>
                      {preg.opciones.length === 0 ? (
                        <p className="text-white/30 text-sm italic">Sin opciones definidas</p>
                      ) : (
                        <div className="space-y-2">
                          {preg.opciones.map((op) => (
                            <div key={op.id} className="flex items-center gap-3">
                              <div
                                className={`w-5 h-5 rounded-full border-2 flex-shrink-0 flex items-center justify-center ${
                                  op.es_correcta
                                    ? 'border-vialgo-green bg-vialgo-green'
                                    : 'border-white/30'
                                }`}
                              >
                                {op.es_correcta && (
                                  <span className="text-white text-xs">✓</span>
                                )}
                              </div>
                              <span
                                className={`text-sm ${
                                  op.es_correcta ? 'text-vialgo-green-light font-medium' : 'text-white/70'
                                }`}
                              >
                                {op.orden}. {op.texto}
                              </span>
                            </div>
                          ))}
                        </div>
                      )}
                      {preg.texto_consecuencia && (
                        <div className="mt-4 pt-4 border-t border-white/10">
                          <p className="text-white/40 text-xs font-medium mb-1 uppercase tracking-wider">
                            Consecuencia
                          </p>
                          <p className="text-white/60 text-sm">{preg.texto_consecuencia}</p>
                        </div>
                      )}
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4">
          <div className="bg-surface border border-white/10 rounded-xl w-full max-w-2xl shadow-2xl max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between p-6 border-b border-white/10 sticky top-0 bg-surface z-10">
              <h2 className="text-lg font-semibold text-white">
                {editing ? 'Editar Pregunta' : 'Nueva Pregunta'}
              </h2>
              <button
                onClick={() => setShowModal(false)}
                className="text-white/40 hover:text-white text-xl leading-none"
              >
                ✕
              </button>
            </div>

            <div className="p-6 space-y-5">
              {/* Enunciado */}
              <div>
                <label className="block text-sm text-white/60 mb-1.5">
                  Enunciado <span className="text-red-400">*</span>
                </label>
                <textarea
                  value={form.enunciado}
                  onChange={(e) => setForm({ ...form, enunciado: e.target.value })}
                  placeholder="¿Cuál es la velocidad máxima en zona urbana?"
                  rows={3}
                  className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm resize-none"
                />
              </div>

              {/* Categoria + Leccion */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm text-white/60 mb-1.5">
                    Categoría <span className="text-red-400">*</span>
                  </label>
                  <select
                    value={form.categoria_id}
                    onChange={(e) => setForm({ ...form, categoria_id: e.target.value })}
                    className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-vialgo-green text-sm"
                  >
                    <option value="">Seleccionar...</option>
                    {categorias.map((cat) => (
                      <option key={cat.id} value={cat.id}>
                        {cat.nombre}
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm text-white/60 mb-1.5">Lección (opcional)</label>
                  <select
                    value={form.leccion_id ?? ''}
                    onChange={(e) =>
                      setForm({ ...form, leccion_id: e.target.value || null })
                    }
                    className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-vialgo-green text-sm"
                  >
                    <option value="">Sin lección</option>
                    {lecciones.map((lec) => (
                      <option key={lec.id} value={lec.id}>
                        {lec.titulo}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Tipo medio + URL */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm text-white/60 mb-1.5">Tipo de medio</label>
                  <select
                    value={form.tipo_medio ?? 'imagen'}
                    onChange={(e) =>
                      setForm({ ...form, tipo_medio: e.target.value as 'video' | 'imagen' })
                    }
                    className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-vialgo-green text-sm"
                  >
                    <option value="imagen">Imagen</option>
                    <option value="video">Video (YouTube)</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm text-white/60 mb-1.5">
                    URL del medio{' '}
                    <span className="text-white/30 text-xs">(opcional)</span>
                  </label>
                  <input
                    type="url"
                    value={form.url_medio ?? ''}
                    onChange={(e) =>
                      setForm({ ...form, url_medio: e.target.value || null })
                    }
                    placeholder={
                      form.tipo_medio === 'video'
                        ? 'https://www.youtube.com/watch?v=...'
                        : 'https://...'
                    }
                    className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm"
                  />
                </div>
              </div>

              {/* Texto consecuencia */}
              <div>
                <label className="block text-sm text-white/60 mb-1.5">
                  Texto Consecuencia{' '}
                  <span className="text-white/30 text-xs">(explicación tras responder)</span>
                </label>
                <textarea
                  value={form.texto_consecuencia ?? ''}
                  onChange={(e) =>
                    setForm({ ...form, texto_consecuencia: e.target.value || null })
                  }
                  placeholder="Explicación educativa que se muestra después de responder..."
                  rows={3}
                  className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm resize-none"
                />
              </div>

              {/* Checkboxes */}
              <div className="flex items-center gap-6">
                <label className="flex items-center gap-2.5 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={form.es_clasificacion}
                    onChange={(e) => setForm({ ...form, es_clasificacion: e.target.checked })}
                    className="w-4 h-4 rounded accent-vialgo-green"
                  />
                  <span className="text-sm text-white/70">Es pregunta de clasificación</span>
                </label>
                <label className="flex items-center gap-2.5 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={form.activa}
                    onChange={(e) => setForm({ ...form, activa: e.target.checked })}
                    className="w-4 h-4 rounded accent-vialgo-green"
                  />
                  <span className="text-sm text-white/70">Activa</span>
                </label>
              </div>

              {/* Options */}
              <div>
                <label className="block text-sm text-white/60 mb-3 uppercase tracking-wider font-medium">
                  Opciones de respuesta (1 correcta requerida)
                </label>
                <div className="space-y-3">
                  {form.opciones.map((op, idx) => (
                    <div key={idx} className="flex items-center gap-3">
                      <button
                        type="button"
                        onClick={() => setCorrectOption(idx)}
                        className={`w-6 h-6 rounded-full border-2 flex-shrink-0 flex items-center justify-center transition-colors ${
                          op.es_correcta
                            ? 'border-vialgo-green bg-vialgo-green'
                            : 'border-white/30 hover:border-vialgo-green/60'
                        }`}
                        title="Marcar como correcta"
                      >
                        {op.es_correcta && <span className="text-white text-xs">✓</span>}
                      </button>
                      <span className="text-white/40 text-sm w-4">{op.orden}.</span>
                      <input
                        type="text"
                        value={op.texto}
                        onChange={(e) => updateOptionText(idx, e.target.value)}
                        placeholder={`Opción ${op.orden}`}
                        className={`flex-1 bg-background border rounded-lg px-4 py-2 text-white placeholder-white/30 focus:outline-none text-sm ${
                          op.es_correcta
                            ? 'border-vialgo-green/50 focus:border-vialgo-green'
                            : 'border-white/20 focus:border-white/40'
                        }`}
                      />
                    </div>
                  ))}
                </div>
                <p className="text-white/30 text-xs mt-2">
                  Haz clic en el círculo para marcar la respuesta correcta
                </p>
              </div>
            </div>

            <div className="flex items-center justify-end gap-3 p-6 border-t border-white/10">
              <button
                onClick={() => setShowModal(false)}
                className="px-5 py-2.5 text-sm bg-white/10 hover:bg-white/20 text-white rounded-lg transition-colors"
              >
                Cancelar
              </button>
              <button
                onClick={handleSave}
                disabled={saving}
                className="px-5 py-2.5 text-sm bg-vialgo-green hover:bg-vialgo-green-light disabled:opacity-50 text-white rounded-lg transition-colors font-medium"
              >
                {saving ? 'Guardando...' : editing ? 'Actualizar' : 'Crear'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
