import { createClient } from '@supabase/supabase-js'

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL!
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!

export const supabase = createClient(supabaseUrl, supabaseAnonKey)

export interface Modulo {
  id: string
  rol: string
  nombre: string
  descripcion: string | null
  orden: number
  esta_activo: boolean
}

export interface Leccion {
  id: string
  modulo_id: string
  nombre: string
  descripcion: string | null
  orden: number
  esta_activa: boolean
}

export interface CategoriaPregunta {
  id: string
  nombre: string
  rol: string
  descripcion: string | null
}

export interface Pregunta {
  id: string
  categoria_id: string
  leccion_id: string | null
  enunciado: string
  tipo_medio: string
  url_medio: string
  duracion_medio_seg: number | null
  texto_consecuencia: string
  es_clasificacion: boolean
  esta_activa: boolean
  creado_en: string
}

export interface OpcionPregunta {
  id: string
  pregunta_id: string
  texto: string
  imagen_url: string | null
  es_correcta: boolean
  orden: number
}

// Legacy type aliases for backward compat during migration
export type Categoria = CategoriaPregunta
