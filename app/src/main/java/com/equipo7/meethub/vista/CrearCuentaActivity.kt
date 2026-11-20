package com.equipo7.meethub.vista

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Spinner
import android.widget.Toast
import com.equipo7.meethub.presentador.clsCrearCuentaPresentador
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.equipo7.meethub.R

class CrearCuentaActivity : AppCompatActivity() {

    private lateinit var presenter: clsCrearCuentaPresentador
    private lateinit var spnTipo: Spinner
    private lateinit var txtIdentificador: EditText
    private lateinit var txtNombreCompleto: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtTelefono: EditText
    private lateinit var txtPsw: EditText
    private lateinit var spnPregunta: Spinner
    private lateinit var txtRespuesta: EditText
    private lateinit var btnCrearCuenta: Button
    private lateinit var btnRegresar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_cuenta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Enlazar componentes XML
        spnTipo = findViewById(R.id.spnTipo)
        txtIdentificador = findViewById(R.id.txtIdentificador)
        txtNombreCompleto = findViewById(R.id.txtNombreCompleto)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtTelefono = findViewById(R.id.txtTelefono)
        txtPsw = findViewById(R.id.txtPsw)
        spnPregunta = findViewById(R.id.spnPregunta)
        txtRespuesta = findViewById(R.id.txtRespuesta)
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)
        btnRegresar = findViewById(R.id.btnRegresar)

        // 2. Llenar los dos Spinner con listas fijas
        val tipos = listOf("Alumno", "Docente", "Usuario Externo")
        val adapterTipos = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTipo.adapter = adapterTipos

        val preguntas = listOf(
            "¿Nombre de tu primera mascota?",
            "¿Ciudad donde naciste?",
            "¿Color favorito?",
            "¿Nombre de tu escuela primaria?"
        )
        val adapterPreguntas = ArrayAdapter(this, android.R.layout.simple_spinner_item, preguntas)
        adapterPreguntas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnPregunta.adapter = adapterPreguntas

        // 3. Presentador y listeners
        presenter = clsCrearCuentaPresentador(this)

        btnRegresar.setOnClickListener { finish() }
        findViewById<ImageView>(R.id.imgVerPsw).setOnClickListener { alternarVista(txtPsw) }
        findViewById<ImageView>(R.id.imgVerRespuesta).setOnClickListener { alternarVista(txtRespuesta) }

        btnCrearCuenta.setOnClickListener {
            presenter.crearCuenta(
                spnTipo.selectedItem.toString(),
                txtIdentificador.text.toString().trim(),
                txtNombreCompleto.text.toString().trim(),
                txtCorreo.text.toString().trim(),
                txtTelefono.text.toString().trim(),
                txtPsw.text.toString().trim(),
                spnPregunta.selectedItem.toString(),
                txtRespuesta.text.toString().trim()
            )
        }
    }

    // Lo llama el presentador cuando la cuenta se creó: avisa y regresa al Login
    fun cuentaCreada(mensaje: String) {
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