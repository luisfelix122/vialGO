# Documento de Requerimientos de Producto (PRD) - Vial Go (MVP)

## 1. Visión General y Propósito
**Vial Go** es una plataforma móvil gamificada diseñada para transformar la educación vial en el Perú. Su objetivo es entrenar los reflejos y la toma de decisiones rápidas de conductores y peatones frente a situaciones reales del tránsito peruano, utilizando una metodología de aprendizaje basada en el microlearning y la repetición espaciada.

## 2. Objetivos del MVP
1.  **Validar la Metodología:** Confirmar que el entrenamiento de 5 segundos de respuesta mejora el criterio del usuario.
2.  **Retención:** Lograr que el usuario cree un hábito diario mediante rachas y notificaciones.
3.  **Registro:** Capturar una base de datos de usuarios (DNI) interesados en beneficios viales.
4.  **Distribución:** Probar el alcance orgánico mediante la distribución directa de APK.

## 3. Modelo de Usuario y Roles
### 3.1. Roles del Sistema
*   **Administrador (Admin):** Acceso al Dashboard web. Gestiona contenido y usuarios.
*   **Usuario Final:**
    *   **Conductor de Auto:** Enfocado en normas de tránsito y manejo defensivo.
    *   **Peatón:** Enfocado en seguridad peatonal y cruces críticos.
    *   *Nota:* Un usuario puede alternar entre roles desde su perfil. El cambio de rol modifica dinámicamente las vistas de aprendizaje, ranking y beneficios. El progreso de cada rol es independiente.

## 4. Experiencia de Usuario (UI/UX)
### 4.1. Diseño Visual
*   **Tema:** Modo Oscuro (Dark Mode) obligatorio por defecto.
*   **Mascota:** No incluida en el MVP.
*   **Feedback:** Sonidos de acierto/error y animaciones de celebración (confeti/monedas). Sin música de fondo.

### 4.2. Estructura de Navegación (Bottom Navigation)
1.  **Aprender (Inicio):** Interfaz de mapa con camino de lecciones secuenciales (Estilo Duolingo).
2.  **Ranking:** Clasificación global basada en el porcentaje de Reputación.
3.  **Beneficios:** Listado de recompensas bloqueadas (Estado "Próximamente").
4.  **Perfil:** Estadísticas personales ("Vidas salvadas"), configuración y botón de eliminación de cuenta.

## 5. Flujo de Onboarding y Registro
1.  **Prueba Inicial (Guest Mode):** El usuario puede jugar una sesión de 5 preguntas antes de registrarse.
2.  **Registro Post-Prueba:** Obligatorio para guardar progreso. Campos: **DNI**, **Contraseña** y **Pregunta de Seguridad** (para recuperación de cuenta).
3.  **Configuración inicial:**
    *   Selección de Rol (Peatón o Conductor).
    *   Nivel de Compromiso: 2, 3 o 5 minutos diarios (informativo).
4.  **Tutorial:** Sesión interactiva obligatoria a velocidad reducida para explicar la mecánica de los 5 segundos.
5.  **Test de Clasificación:** 10 preguntas específicas al rol (una sola oportunidad) para definir la reputación inicial. Si se cambia de rol, se repite este test para el nuevo rol.

## 6. Mecánicas de Juego y Core Loop
### 6.1. La Sesión de Entrenamiento
*   **Estructura:** 5 preguntas por sesión.
*   **Formato de Pregunta:** Video o imagen hiperlocalizada de 3 segundos -> Aparecen alternativas + Temporizador de 5 segundos.
*   **Puntos (XP):** Basado en la velocidad. Responder en el segundo 1 otorga más puntos que en el segundo 4.
*   **Feedback de Fallo:** Muestra la respuesta correcta y un texto con la consecuencia real de la mala decisión.

### 6.2. Gamificación y Retención
*   **Vidas:** 5 vidas iniciales. Fallar resta 1 vida.
*   **Recarga de Vidas:** Si llega a 0, recarga total automática tras 12 horas.
*   **Repetición Dinámica:** Las preguntas falladas se mueven al final de la sesión actual (se convierten en la pregunta 6, 7, etc.) hasta ser superadas correctamente. No restan vidas adicionales en la repetición.
*   **Rachas (Streaks):** Mantener la racha requiere completar al menos 1 sesión (5 preguntas) al día.
*   **Multiplicador:** Se activa un multiplicador de XP tras alcanzar una racha de **14 días (2 semanas)**.
*   **Notificaciones:** Alertas push a las **19:00, 21:00 y 23:00** si el usuario no ha cumplido su sesión diaria.

## 7. Reglas de Negocio: Reputación y Beneficios
*   **Reputación:** Promedio dinámico de aciertos vs errores.
*   **Bloqueo de Beneficios:** Si la reputación cae por debajo del **70%**, la pestaña de Beneficios se bloquea. El usuario debe mejorar su promedio entrenando para recuperarlos.
*   **Inactividad:** La reputación decae progresivamente si el usuario deja de jugar varios días.

## 8. Arquitectura y Estándares Técnicos
### 8.1. Desarrollo de Software
*   **Frontend:** Kotlin Multiplatform (KMP).
*   **Backend:** Supabase (PostgreSQL).
*   **Metodología:** **TDD (Test-Driven Development)** para toda la lógica de negocio.
*   **Arquitectura:** **Clean Architecture** (Capas: Dominio, Datos, Presentación).
*   **Estándar de Código:** Clean Code. **Todo el código fuente (clases, métodos, variables, comentarios y módulos) debe estar en Español**.

### 8.2. Base de Datos
*   **Normalización:** Mínimamente en **Tercera Forma Normal (3NF)**.
*   **Integridad:** Garantía estricta de **Integridad Referencial** (Constraints y Foreign Keys).
*   **DNI:** No es Primary Key. Se usa un ID interno y el DNI como columna relacional única.

## 9. Dashboard de Administración (Render)
*   **Gestión de Contenido:** Inyección de preguntas, textos de consecuencia y URLs de videos (links externos).
*   **Gestión de Usuarios:** Capacidad de banear, resetear contraseñas y **visualizar la respuesta a la pregunta de seguridad** para soporte.
*   **Analíticas:** Visualización de "Mapas de Calor" (preguntas más falladas) segmentados por Peatón y Conductor.
*   **Soporte:** Sección de "Errores" donde llegan los reportes enviados por los usuarios desde la app.

## 10. Metodología de Ejecución: SDD
El desarrollo seguirá las 9 fases de **Spec-Driven Development**:
1.  **Exploración (Explore)**
2.  **Propuesta (Propose)**
3.  **Especificación (Spec)**
4.  **Diseño (Design)**
5.  **Tareas (Tasks)**
6.  **Aplicación (Apply)**
7.  **Verificación (Verify)**
8.  **Revisión (Review)**
9.  **Archivo (Archive)**

## 11. Casos Borde y Seguridad
*   **Conexión:** Si se pierde el internet, sale un mensaje de error y el usuario debe reiniciar la sesión (no se guarda progreso parcial de la lección).
*   **Anti-Cheat:** El temporizador de 5 segundos sigue corriendo en segundo plano si la app es minimizada.
*   **Eliminación de Datos:** Botón obligatorio para eliminar cuenta y datos asociados en cumplimiento con normativas de privacidad.

## 12. Fuera del Alcance (Out of Scope)
*   Integración con hardware (RFID / Tercera Placa).
*   Canje real de beneficios (solo visualización con candado).
*   Distribución en Play Store o App Store (Solo APK).
*   Música de fondo y sistema de anuncios.
