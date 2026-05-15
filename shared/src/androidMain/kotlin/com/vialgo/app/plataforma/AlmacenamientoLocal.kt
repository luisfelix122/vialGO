package com.vialgo.app.plataforma

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vialgo_prefs")

class AlmacenamientoLocal(private val context: Context) {

    suspend fun guardar(clave: String, valor: String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey(clave)] = valor
        }
    }

    suspend fun leer(clave: String): String? =
        context.dataStore.data
            .map { prefs -> prefs[stringPreferencesKey(clave)] }
            .first()

    suspend fun eliminar(clave: String) {
        context.dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey(clave))
        }
    }

    suspend fun limpiar() {
        context.dataStore.edit { it.clear() }
    }
}
