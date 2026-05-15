'use client'

import { useEffect, useState } from 'react'
import { supabase, type Leccion, type Categoria } from '@/lib/supabase'

const emptyForm: Omit<Leccion, 'id'> = {
  categoria_id: '',
  titulo: '',
  descripcion: '',
  orden: null,
  puntaje_maximo: 100,
  tiempo_limite_seg: 30,
  url_imagen_portada: '',
}

export default function LeccionesPage() {
  const [lecciones, setLecciones] = useState<Leccion[]>([])
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState<Leccion | null>(null)
  const [form, setForm] = useState<Omit<Leccion, 'id'>>(emptyForm)
  const [toast, setToast] = useState<{ type: 'success' | 'error'; msg: string } | null>(null)
  const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null)

  function showToast(type: 'success' | 'error', msg: string) {
    setToast({ type, msg })
    setTimeout(() => setToast(null), 3500)
  }

  async function fetchAll() {
    setLoading(true)
    const [lecRes, catRes] = await Promise.all([
      supabase.from('lecciones').select('*').order('orden', { ascending: true }),
      supabase.from('categorias').select('*').order('nombre'),
    ])
    if (lecRes.error) showToast('error', lecRes.error.message)
    else setLecciones(lecRes.data ?? [])
    if (catRes.data) setCategorias(catRes.data)
    setLoading(false)
  }

  useEffect(() => {
    fetchAll()
  }, [])

  function getCategoriaName(id: string) {
    return categorias.find((c) => c.id === id)?.nombre ?? '—'
  }

  function openCreate() {
    setEditing(null)
    setForm({ ...emptyForm, categoria_id: categorias[0]?.id ?? '' })
    setShowModal(true)
  }

  function openEdit(lec: Leccion) {
    setEditing(lec)
    setForm({
      categoria_id: lec.categoria_id,
      titulo: lec.titulo,
      descripcion: lec.descripcion ?? '',
      orden: lec.orden,
      puntaje_maximo: lec.puntaje_maximo,
      tiempo_limite_seg: lec.tiempo_limite_seg,
      url_imagen_portada: lec.url_imagen_portada ?? '',
    })
    setShowModal(true)
  }

  async function handleSave() {
    if (!form.titulo.trim()) {
      showToast('error', 'El título es requerido')
      return
    }
    if (!form.categoria_id) {
      showToast('error', 'La categoría es requerida')
      return
    }
    setSaving(true)
    const payload = {
      categoria_id: form.categoria_id,
      titulo: form.titulo.trim(),
      descripcion: form.descripcion?.trim() || null,
      orden: form.orden,
      puntaje_maximo: form.puntaje_maximo,
      tiempo_limite_seg: form.tiempo_limite_seg,
      url_imagen_portada: form.url_imagen_portada?.trim() || null,
    }

    if (editing) {
      const { error } = await supabase.from('lecciones').update(payload).eq('id', editing.id)
      if (error) showToast('error', error.message)
      else {
        showToast('success', 'Lección actualizada')
        setShowModal(false)
        fetchAll()
      }
    } else {
      const { error } = await supabase.from('lecciones').insert(payload)
      if (error) showToast('error', error.message)
      else {
        showToast('success', 'Lección creada')
        setShowModal(false)
        fetchAll()
      }
    }
    setSaving(false)
  }

  async function handleDelete(id: string) {
    const { error } = await supabase.from('lecciones').delete().eq('id', id)
    if (error) showToast('error', error.message)
    else {
      showToast('success', 'Lección eliminada')
      fetchAll()
    }
    setDeleteConfirm(null)
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
          <h1 className="text-3xl font-bold text-white">Lecciones</h1>
          <p className="text-white/50 mt-1">Unidades dentro de cada categoría</p>
        </div>
        <button
          onClick={openCreate}
          className="px-5 py-2.5 bg-vialgo-green hover:bg-vialgo-green-light text-white rounded-lg font-medium transition-colors flex items-center gap-2"
        >
          <span>+</span> Nueva Lección
        </button>
      </div>

      <div className="bg-surface border border-white/10 rounded-xl overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-white/40">Cargando...</div>
        ) : lecciones.length === 0 ? (
          <div className="p-12 text-center text-white/40">No hay lecciones aún.</div>
        ) : (
          <table className="w-full">
            <thead>
              <tr className="border-b border-white/10 bg-white/5">
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Título</th>
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Categoría</th>
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Orden</th>
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Puntaje</th>
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Tiempo (s)</th>
                <th className="text-right px-6 py-4 text-white/60 text-sm font-medium">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {lecciones.map((lec, i) => (
                <tr
                  key={lec.id}
                  className={`border-b border-white/5 hover:bg-white/5 transition-colors ${
                    i === lecciones.length - 1 ? 'border-b-0' : ''
                  }`}
                >
                  <td className="px-6 py-4">
                    <div>
                      <p className="text-white font-medium">{lec.titulo}</p>
                      {lec.descripcion && (
                        <p className="text-white/40 text-xs mt-0.5 truncate max-w-xs">
                          {lec.descripcion}
                        </p>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className="px-2.5 py-1 bg-vialgo-green/20 text-vialgo-green-light rounded-full text-xs">
                      {getCategoriaName(lec.categoria_id)}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-white/60 text-sm">{lec.orden ?? '—'}</td>
                  <td className="px-6 py-4 text-white/60 text-sm">{lec.puntaje_maximo ?? '—'}</td>
                  <td className="px-6 py-4 text-white/60 text-sm">{lec.tiempo_limite_seg ?? '—'}s</td>
                  <td className="px-6 py-4">
                    <div className="flex items-center justify-end gap-2">
                      <button
                        onClick={() => openEdit(lec)}
                        className="px-3 py-1.5 text-sm bg-white/10 hover:bg-white/20 text-white rounded-lg transition-colors"
                      >
                        Editar
                      </button>
                      {deleteConfirm === lec.id ? (
                        <div className="flex items-center gap-1">
                          <button
                            onClick={() => handleDelete(lec.id)}
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
                          onClick={() => setDeleteConfirm(lec.id)}
                          className="px-3 py-1.5 text-sm bg-red-500/20 hover:bg-red-500/40 text-red-400 rounded-lg transition-colors"
                        >
                          Eliminar
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4">
          <div className="bg-surface border border-white/10 rounded-xl w-full max-w-lg shadow-2xl max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between p-6 border-b border-white/10 sticky top-0 bg-surface z-10">
              <h2 className="text-lg font-semibold text-white">
                {editing ? 'Editar Lección' : 'Nueva Lección'}
              </h2>
              <button
                onClick={() => setShowModal(false)}
                className="text-white/40 hover:text-white text-xl leading-none"
              >
                ✕
              </button>
            </div>

            <div className="p-6 space-y-4">
              <div>
                <label className="block text-sm text-white/60 mb-1.5">
                  Título <span className="text-red-400">*</span>
                </label>
                <input
                  type="text"
                  value={form.titulo}
                  onChange={(e) => setForm({ ...form, titulo: e.target.value })}
                  placeholder="Ej: Señales Básicas"
                  className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm"
                />
              </div>

              <div>
                <label className="block text-sm text-white/60 mb-1.5">
                  Categoría <span className="text-red-400">*</span>
                </label>
                <select
                  value={form.categoria_id}
                  onChange={(e) => setForm({ ...form, categoria_id: e.target.value })}
                  className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-vialgo-green text-sm"
                >
                  <option value="">Seleccionar categoría...</option>
                  {categorias.map((cat) => (
                    <option key={cat.id} value={cat.id}>
                      {cat.nombre}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm text-white/60 mb-1.5">Descripción</label>
                <textarea
                  value={form.descripcion ?? ''}
                  onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
                  placeholder="Descripción opcional..."
                  rows={3}
                  className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm resize-none"
                />
              </div>

              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm text-white/60 mb-1.5">Orden</label>
                  <input
                    type="number"
                    value={form.orden ?? ''}
                    onChange={(e) =>
                      setForm({ ...form, orden: e.target.value ? parseInt(e.target.value) : null })
                    }
                    placeholder="1"
                    className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm"
                  />
                </div>
                <div>
                  <label className="block text-sm text-white/60 mb-1.5">Puntaje máx</label>
                  <input
                    type="number"
                    value={form.puntaje_maximo ?? ''}
                    onChange={(e) =>
                      setForm({
                        ...form,
                        puntaje_maximo: e.target.value ? parseInt(e.target.value) : null,
                      })
                    }
                    placeholder="100"
                    className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm"
                  />
                </div>
                <div>
                  <label className="block text-sm text-white/60 mb-1.5">Tiempo (seg)</label>
                  <input
                    type="number"
                    value={form.tiempo_limite_seg ?? ''}
                    onChange={(e) =>
                      setForm({
                        ...form,
                        tiempo_limite_seg: e.target.value ? parseInt(e.target.value) : null,
                      })
                    }
                    placeholder="30"
                    className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm text-white/60 mb-1.5">URL Imagen Portada</label>
                <input
                  type="url"
                  value={form.url_imagen_portada ?? ''}
                  onChange={(e) => setForm({ ...form, url_imagen_portada: e.target.value })}
                  placeholder="https://..."
                  className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm"
                />
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
