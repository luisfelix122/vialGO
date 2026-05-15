package com.vialgo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vialgo.app.presentacion.navegacion.NavHostRaiz
import com.vialgo.app.presentacion.tema.VialGoTema

class ActividadPrincipal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VialGoTema {
                NavHostRaiz()
            }
        }
    }
}
