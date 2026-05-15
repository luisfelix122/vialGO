import { createClient } from '@supabase/supabase-js'

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL!
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!

export const supabase = createClient(supabaseUrl, supabaseAnonKey)

export type Categoria = {
  id: string
  nombre: string
  descripcion: string | null
  orden: number | null
  rol: 'conductor' | 'peaton' | null
}

export type Leccion = {
  id: string
  categoria_id: string
  titulo: string
  descripcion: string | null
  orden: number | null
  puntaje_maximo: number | null
  tiempo_limite_seg: number | null
  url_imagen_portada: string | null
}

export type Pregunta = {
  id: string
  leccion_id: string | null
  categoria_id: string
  enunciado: string
  tipo_medio: 'video' | 'imagen' | null
  url_medio: string | null
  duracion_medio_seg: number | null
  texto_consecuencia: string | null
  es_clasificacion: boolean
  activa: boolean
  orden: number | null
}

export type OpcionPregunta = {
  id: string
  pregunta_id: string
  texto: string
  es_correcta: boolean
  orden: number
  imagen_url: string | null
}
