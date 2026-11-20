package com.equipo7.meethub.vista

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.equipo7.meethub.R
import com.equipo7.meethub.modelo.dataReservas
import com.equipo7.meethub.presentador.clsBuzonPresentador

class BuzonActivity : AppCompatActivity() {

    private lateinit var presenter: clsBuzonPresentador
    private lateinit var spnSala: Spinner
    private lateinit var txtComentario: EditText
    private lateinit var btnEnviar: Button
    private lateinit var txtSinReservas: TextView
    private lateinit var layoutFormulario: LinearLayout

    private var tipoUsuario: String = ""
    private var idUsuario: Int = 0
    private var nombreUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buzon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Datos del usuario que llegaron por Intent
        tipoUsuario = intent.getStringExtra("TIPO_USUARIO") ?: ""
        idUsuario = intent.getIntExtra("ID_USUARIO", 0)
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: ""

        // 1. Enlazar componentes XML
        spnSala = findViewById(R.id.spnSala)
        txtComentario = findViewById(R.id.txtComentario)
        btnEnviar = findViewById(R.id.btnEnviar)
        txtSinReservas = findViewById(R.id.txtSinReservas)
        layoutFormulario = findViewById(R.id.layoutFormulario)

        // 2. Barra inferior
        findViewById<View>(R.id.btnInicio).setOnClickListener { irA(InicioActivity::class.java) }
        findViewById<View>(R.id.btnSalas).setOnClickListener { irA(SalasActivity::class.java) }

        // Marca la pestaña activa de la barra inferior
        findViewById<ImageView>(R.id.icBuzon).setBackgroundResource(R.drawable.fondo_nav_activo)
        findViewById<TextView>(R.id.txtNavBuzon).setTypeface(null, Typeface.BOLD)

        // 3. Presentador y listener del botón
        presenter = clsBuzonPresentador(this)
        btnEnviar.setOnClickListener {
            presenter.enviarSugerencia(
                tipoUsuario,
                idUsuario,
                spnSala.selectedItemPosition,
                txtComentario.text.toString().trim()
            )
        }

        presenter.cargarReservas(tipoUsuario, idUsuario)
    }

    // Llena el Spinner "Sala utilizada" con el nombre de la sala y la fecha
    fun mostrarReservas(reservas: List<dataReservas>) {
        txtSinReservas.visibility = View.GONE
        layoutFormulario.visibility = View.VISIBLE
        val textos = reservas.map { getString(R.string.buzon_item_sala, it.nombreSala, it.fechaReserva) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, textos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSala.adapter = adapter
    }

    // No hay salas que opinar: se oculta el formulario y se muestra un aviso
    fun mostrarSinReservas() {
        layoutFormulario.visibility = View.GONE
        txtSinReservas.visibility = View.VISIBLE
    }

    fun sugerenciaEnviada(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        txtComentario.setText("")
    }

    // Abre otra pantalla y le vuelve a mandar los datos del usuario
    private fun irA(destino: Class<*>) {
        val intent = Intent(this, destino)
        intent.putExtra("TIPO_USUARIO", tipoUsuario)
        intent.putExtra("ID_USUARIO", idUsuario)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
        startActivity(intent)
    }

    fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}