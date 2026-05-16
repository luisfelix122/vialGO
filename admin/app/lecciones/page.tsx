'use client'

import { useEffect, useState } from 'react'
import { supabase, type Leccion, type Modulo } from '@/lib/supabase'

const emptyForm: Omit<Leccion, 'id'> = {
  modulo_id: '',
  nombre: '',
  descripcion: '',
  orden: 1,
  esta_activa: true,
}

export default function LeccionesPage() {
  const [lecciones, setLecciones] = useState<Leccion[]>([])
  const [modulos, setModulos] = useState<Modulo[]>([])
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
    const [lecRes, modRes] = await Promise.all([
      supabase.from('lecciones').select('*').order('orden', { ascending: true }),
      supabase.from('modulos').select('*').order('nombre'),
    ])
    if (lecRes.error) showToast('error', lecRes.error.message)
    else setLecciones(lecRes.data ?? [])
    if (modRes.data) setModulos(modRes.data)
    setLoading(false)
  }

  useEffect(() => {
    fetchAll()
  }, [])

  function getModuloName(id: string) {
    return modulos.find((m) => m.id === id)?.nombre ?? '—'
  }

  function openCreate() {
    setEditing(null)
    setForm({ ...emptyForm, modulo_id: modulos[0]?.id ?? '' })
    setShowModal(true)
  }

  function openEdit(lec: Leccion) {
    setEditing(lec)
    setForm({
      modulo_id: lec.modulo_id,
      nombre: lec.nombre,
      descripcion: lec.descripcion ?? '',
      orden: lec.orden,
      esta_activa: lec.esta_activa,
    })
    setShowModal(true)
  }

  async function handleSave() {
    if (!form.nombre.trim()) {
      showToast('error', 'El nombre es requerido')
      return
    }
    if (!form.modulo_id) {
      showToast('error', 'El módulo es requerido')
      return
    }
    setSaving(true)
    const payload = {
      modulo_id: form.modulo_id,
      nombre: form.nombre.trim(),
      descripcion: form.descripcion?.trim() || null,
      orden: form.orden,
      esta_activa: form.esta_activa,
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
          <p className="text-white/50 mt-1">Unidades de contenido dentro de cada módulo</p>
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
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Nombre</th>
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Módulo</th>
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Orden</th>
                <th className="text-left px-6 py-4 text-white/60 text-sm font-medium">Estado</th>
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
                      <p className="text-white font-medium">{lec.nombre}</p>
                      {lec.descripcion && (
                        <p className="text-white/40 text-xs mt-0.5 truncate max-w-xs">
                          {lec.descripcion}
                        </p>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className="px-2.5 py-1 bg-vialgo-green/20 text-vialgo-green-light rounded-full text-xs">
                      {getModuloName(lec.modulo_id)}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-white/60 text-sm">{lec.orden}</td>
                  <td className="px-6 py-4">
                    <span
                      className={`px-2.5 py-1 rounded-full text-xs font-medium ${
                        lec.esta_activa
                          ? 'bg-vialgo-green/20 text-vialgo-green-light'
                          : 'bg-white/10 text-white/40'
                      }`}
                    >
                      {lec.esta_activa ? 'Activa' : 'Inactiva'}
                    </span>
                  </td>
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
                  Nombre <span className="text-red-400">*</span>
                </label>
                <input
                  type="text"
                  value={form.nombre}
                  onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                  placeholder="Ej: Señales Básicas"
                  className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm"
                />
              </div>

              <div>
                <label className="block text-sm text-white/60 mb-1.5">
                  Módulo <span className="text-red-400">*</span>
                </label>
                <select
                  value={form.modulo_id}
                  onChange={(e) => setForm({ ...form, modulo_id: e.target.value })}
                  className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-vialgo-green text-sm"
                >
                  <option value="">Seleccionar módulo...</option>
                  {modulos.map((mod) => (
                    <option key={mod.id} value={mod.id}>
                      {mod.nombre}
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

              <div>
                <label className="block text-sm text-white/60 mb-1.5">Orden</label>
                <input
                  type="number"
                  value={form.orden}
                  onChange={(e) =>
                    setForm({ ...form, orden: parseInt(e.target.value) || 1 })
                  }
                  placeholder="1"
                  min="1"
                  className="w-full bg-background border border-white/20 rounded-lg px-4 py-2.5 text-white placeholder-white/30 focus:outline-none focus:border-vialgo-green text-sm"
                />
              </div>

              <div>
                <label className="flex items-center gap-2.5 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={form.esta_activa}
                    onChange={(e) => setForm({ ...form, esta_activa: e.target.checked })}
                    className="w-4 h-4 rounded accent-vialgo-green"
                  />
                  <span className="text-sm text-white/70">Lección activa</span>
                </label>
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
