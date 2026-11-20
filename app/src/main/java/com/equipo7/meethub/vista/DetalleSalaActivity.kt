package com.equipo7.meethub.vista

import android.content.Intent
import android.os.Bundle
import android.graphics.Typeface
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.equipo7.meethub.modelo.dataDetalleSala
import com.equipo7.meethub.presentador.clsDetalleSalaPresentador
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.equipo7.meethub.R
class DetalleSalaActivity : AppCompatActivity() {
    private lateinit var presenter: clsDetalleSalaPresentador
    private lateinit var imgSala: ImageView
    private lateinit var txtNombre: TextView
    private lateinit var txtUbicacion: TextView
    private lateinit var txtCapacidad: TextView
    private lateinit var txtAforo: TextView
    private lateinit var txtHorario: TextView
    private lateinit var txtEquipamiento: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var btnReservar: Button
    private lateinit var btnRegresar: TextView
    private var salaId = 0
    private var tipoUsuario = ""
    private var idUsuario = 0
    private var nombreUsuario = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_sala)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 1. Enlazar componentes XML
        imgSala = findViewById(R.id.imgSala)
        txtNombre = findViewById(R.id.txtNombre)
        txtUbicacion = findViewById(R.id.txtUbicacion)
        txtCapacidad = findViewById(R.id.txtCapacidad)
        txtAforo = findViewById(R.id.txtAforo)
        txtHorario = findViewById(R.id.txtHorario)
        txtEquipamiento = findViewById(R.id.txtEquipamiento)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        btnReservar = findViewById(R.id.btnReservar)
        btnRegresar = findViewById(R.id.btnRegresar)
        // 2. Datos que llegaron por Intent (la sala elegida y el usuario)
        salaId = intent.getIntExtra("SALA_ID", 0)
        // Datos del usuario que llegaron por Intent
        tipoUsuario = intent.getStringExtra("TIPO_USUARIO") ?: ""
        idUsuario = intent.getIntExtra("ID_USUARIO", 0)
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: ""
        // 3. Listeners
        btnRegresar.setOnClickListener { finish() }
        btnReservar.setOnClickListener { abrirNuevaSolicitud() }
        // Barra inferior
        findViewById<View>(R.id.btnInicio).setOnClickListener { irA(InicioActivity::class.java) }
        findViewById<View>(R.id.btnSalas).setOnClickListener { irA(SalasActivity::class.java) }
        findViewById<View>(R.id.btnBuzon).setOnClickListener { irA(BuzonActivity::class.java) }
        // Marca la pestaña activa de la barra inferior
        findViewById<ImageView>(R.id.icSalas).setBackgroundResource(R.drawable.fondo_nav_activo)
        findViewById<TextView>(R.id.txtNavSalas).setTypeface(null, Typeface.BOLD)
        // 4. Inicializar presentador y cargar el detalle
        presenter = clsDetalleSalaPresentador(this)
        if (salaId != 0) {
            presenter.cargarDetalle(salaId)
        }
    }
    // Lo llama el presentador con los datos de la sala
    fun mostrarDetalle(sala: dataDetalleSala) {
        txtNombre.text = sala.nombreSala
        txtUbicacion.text = negritas(getString(R.string.detalle_ubicacion, sala.ubicacion))
        txtCapacidad.text = negritas(getString(R.string.detalle_capacidad, sala.capacidad))
        txtAforo.text = negritas(getString(R.string.detalle_aforo, sala.capacidadMinima))
        txtHorario.text = negritas(getString(R.string.detalle_horario, sala.horaApertura, sala.horaCierre))
        txtEquipamiento.text = negritas(getString(R.string.detalle_equipamiento, sala.equipamientoTexto))
        txtDescripcion.text = negritas(getString(R.string.detalle_descripcion, sala.descripcion))
        Glide.with(this)
            .load(sala.imagenUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.stat_notify_error)
            .into(imgSala)
    }
    fun abrirNuevaSolicitud() {
        val intent = Intent(this, NuevaSolicitudActivity::class.java)
        intent.putExtra("SALA_ID", salaId)
        intent.putExtra("TIPO_USUARIO", tipoUsuario)
        intent.putExtra("ID_USUARIO", idUsuario)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
        startActivity(intent)
    }
    // Convierte las etiquetas <b></b> del string en negritas reales
    private fun negritas(texto: String): CharSequence {
        return Html.fromHtml(texto, Html.FROM_HTML_MODE_LEGACY)
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