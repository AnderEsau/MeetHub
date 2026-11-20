package com.equipo7.meethub.vista

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.equipo7.meethub.R
import com.equipo7.meethub.presentador.clsRestablecerPasswordPresentador

class RestablecerPasswordActivity : AppCompatActivity() {

    private lateinit var presenter: clsRestablecerPasswordPresentador
    private lateinit var txtIdentificador: EditText
    private lateinit var btnConsultar: Button
    private lateinit var layoutPregunta: LinearLayout
    private lateinit var txtPregunta: TextView
    private lateinit var txtRespuesta: EditText
    private lateinit var btnValidar: Button
    private lateinit var layoutPaso2: LinearLayout
    private lateinit var txtNueva: EditText
    private lateinit var txtConfirmar: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnRegresar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_restablecer_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Enlazar componentes XML
        txtIdentificador = findViewById(R.id.txtIdentificador)
        btnConsultar = findViewById(R.id.btnConsultar)
        layoutPregunta = findViewById(R.id.layoutPregunta)
        txtPregunta = findViewById(R.id.txtPregunta)
        txtRespuesta = findViewById(R.id.txtRespuesta)
        btnValidar = findViewById(R.id.btnValidar)
        layoutPaso2 = findViewById(R.id.layoutPaso2)
        txtNueva = findViewById(R.id.txtNueva)
        txtConfirmar = findViewById(R.id.txtConfirmar)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnRegresar = findViewById(R.id.btnRegresar)

        // 2. Presentador y listeners
        presenter = clsRestablecerPasswordPresentador(this)
        btnRegresar.setOnClickListener { finish() }
        findViewById<ImageView>(R.id.imgVerRespuesta).setOnClickListener { alternarVista(txtRespuesta) }
        findViewById<ImageView>(R.id.imgVerNueva).setOnClickListener { alternarVista(txtNueva) }

        // Paso 1
        btnConsultar.setOnClickListener {
            presenter.consultarPregunta(txtIdentificador.text.toString().trim())
        }

        // Paso 2
        btnValidar.setOnClickListener {
            presenter.validarRespuesta(
                txtIdentificador.text.toString().trim(),
                txtRespuesta.text.toString().trim()
            )
        }

        // Paso 3
        btnGuardar.setOnClickListener {
            presenter.guardarPassword(
                txtIdentificador.text.toString().trim(),
                txtRespuesta.text.toString().trim(),
                txtNueva.text.toString().trim(),
                txtConfirmar.text.toString().trim()
            )
        }
    }

    // Muestra la pregunta secreta y los campos para responderla
    fun mostrarPregunta(pregunta: String) {
        txtPregunta.text = getString(R.string.password_pregunta, pregunta)
        layoutPregunta.visibility = View.VISIBLE
    }

    // La respuesta fue correcta: se muestra la sección de nueva contraseña
    fun respuestaValida(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        layoutPaso2.visibility = View.VISIBLE
    }

    // La contraseña se guardó: avisa y regresa al Login
    fun passwordRestablecida(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        finish()
    }

    fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    // Ojo de la contraseña: muestra u oculta lo que se escribe
    private fun alternarVista(campo: EditText) {
        val oculto = campo.transformationMethod != null
        campo.transformationMethod = if (oculto) null else PasswordTransformationMethod.getInstance()
        campo.setSelection(campo.text.length)
    }
}