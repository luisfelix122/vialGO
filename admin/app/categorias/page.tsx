'use client'

import { useEffect, useState } from 'react'
import { supabase, type CategoriaPregunta } from '@/lib/supabase'

const emptyForm: Omit<CategoriaPregunta, 'id'> = {
  nombre: '',
  descripcion: '',
  rol: 'conductor',
}

export default function CategoriasPage() {
  const [categorias, setCategorias] = useState<CategoriaPregunta[]>([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState<CategoriaPregunta | null>(null)
  const [form, setForm] = useState<Omit<CategoriaPregunta, 'id'>>(emptyForm)
  const [toast, setToast] = useState<{ type: 'success' | 'error'; msg: string } | null>(null)
  const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null)

  function showToast(type: 'success' | 'error', msg: string) {
    setToast({ type, msg })
    setTimeout(() => setToast(null), 3500)
  }

  async function fetchCategorias() {
    setLoading(true)
    const { data, error } = await supabase
      .from('categorias_pregunta')
      .select('*')
      .order('nombre', { ascending: true })
    if (error) showToast('error', error.message)
    else setCategorias(data ?? [])
    setLoading(false)
  }

  useEffect(() => {
    fetchCategorias()
  }, [])

  function openCreate() {
    setEditing(null)
    setForm(emptyForm)
    setShowModal(true)
  }

  function openEdit(cat: CategoriaPregunta) {
    setEditing(cat)
    setForm({
      nombre: cat.nombre,
      descripcion: cat.descripcion ?? '',
      rol: cat.rol,
    })
    setShowModal(true)
  }

  async function handleSave() {
    if (!form.nombre.trim()) {
      showToast('error', 'El nombre es requerido')
      return
    }
    setSaving(true)
    const payload = {
      nombre: form.nombre.trim(),
      descripcion: form.descripcion?.trim() || null,
      rol: form.rol,
    }

    if (editing) {
      const { error } = await supabase
        .from('categorias_pregunta')
        .update(payload)
        .eq('id', editing.id)
      if (error) showToast('error', error.message)
      else {
        showToast('success', 'Categoría actualizada')
        setShowModal(false)
        fetchCategorias()
      }
    } else {
      const { error } = await supabase.from('categorias_pregunta').insert(payload)
      if (error) showToast('error', error.message)
      else {
        showToast('success', 'Categoría creada')
        setShowModal(false)
        fetchCategorias()
      }
    }
    setSaving(false)
  }

  async function handleDelete(id: string) {
    const { error } = await supabase.from('categorias_pregunta').delete().eq('id', id)
    if (error) showToast('error', error.message)
    else {
      showToast('success', 'Categoría eliminada')
      fetchCategorias()
    }
    setDeleteConfirm(null)
  }

  return (
    <div className="p-8">
      {/* Toast */}
      {toast && (
        <div
          className={`fixed top-6 right-6 z-50 px-5 py-3 rounded-lg shadow-lg text-sm font-medium ${
            toast.type === 'success'
              ? 'bg-vialgo-green text-white'
              : 'bg-red-600 text-white'
          }`}
        >
          {toast.msg}
        </div>
      )}

      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold text-white">Categorías</h1>
          <p className="text-white/50 mt-1">Categorías de preguntas por rol</p>
        </div>
        <button
          onClick={openCreate}
          className="px-5 py-2.5 bg-vialgo-green hover:bg-vialgo-green-light text-white rounded-lg font-medium transition-colors flex items-center gap-2"
        >
          <span>+</span> Nueva Categoría
        </button>
      </div>

      {/* Table */}
      <div className="bg-surface border border-white/10 rounded-xl overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-white/40">Cargando...</div>
        ) : categorias.length === 0 ? (
          <div className="p-12 text-center text-white/40">
            No hay categorías. ¡Crea la primera!
          </div>
        ) : (
          <table className="w-full">
            <thead>
              <tr className="border-b border-white/10 bg-white/5">
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Nombre</th>
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Descripción</th>
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Rol</th>
                <th className="text-right px-6 py-4 text-white/60 text-sm font-medium">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {categorias.map((cat, i) => (
                <tr
                  key={cat.id}
                  className={`border-b border-white/5 hover:bg-white/5 transition-colors ${
                    i === categorias.length - 1 ? 'border-b-0' : ''
                  }`}
                >
                  <td className="px-6 py-4">
                    <span className="text-white font-medium">{cat.nombre}</span>
                  </td>
                  <td className="px-6 py-4 text-white/50 text-sm max-w-xs truncate">
                    {cat.descripcion || '—'}
                  </td>
                  <td className="px-6 py-4">
                    <span
                      className={`px-2.5 py-1 rounded-full text-xs font-medium ${
                        cat.rol === 'conductor'
                          ? 'bg-vialgo-green/20 text-vialgo-green-light'
                          : cat.rol === 'peaton'
                          ? 'bg-blue-500/20 text-blue-400'
                          : 'bg-purple-500/20 text-purple-400'
                      }`}
                    >
                      {cat.rol}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center justify-end gap-2">
                      <button
                        onClick={() => openEdit(cat)}
                        className="px-3 py-1.5 text-sm bg-white/10 hover:bg-white/20 text-white rounded-lg transition-colors"
                      >
                        Editar
                      </button>
                      {deleteConfirm === cat.id ? (
                        <div className="flex items-center gap-1">
                          <button
                            onClick={() => handleDelete(cat.id)}
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
                          onClick={() => setDeleteConfirm(cat.id)}
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
          <div className="bg-surface border border-white/10 rounded-xl w-full max-w-md shadow-2xl">
            <div className="flex items-center justify-between p-6 border-b border-white/10">
              <h2 className="text-lg font-semibold text-white">
                {editing ? 'Editar Categoría' : 'Nueva Categoría'}
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
                  Nombre <span className="text-red-400">*</span>
                </label>
                <input
                  type="text"
                  value={form.nombre}
                  onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                  placeholder="Ej: Señales de Tránsito"
                  className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm"
                />
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

              <div>
                <label className="block text-sm text-white/60 mb-1.5">
                  Rol <span className="text-red-400">*</span>
                </label>
                <select
                  value={form.rol}
                  onChange={(e) => setForm({ ...form, rol: e.target.value })}
                  className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-vialgo-green text-sm"
                >
                  <option value="conductor">Conductor</option>
                  <option value="peaton">Peatón</option>
                  <option value="ambos">Ambos</option>
                </select>
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
