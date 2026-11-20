package com.equipo7.meethub.vista


import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.equipo7.meethub.adaptador.HorariosAdaptador
import com.equipo7.meethub.modelo.dataHorarios
import com.equipo7.meethub.modelo.dataSalas
import com.equipo7.meethub.presentador.clsInicioPresentador
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.equipo7.meethub.R

class InicioActivity : AppCompatActivity() {
    private lateinit var presenter: clsInicioPresentador
    private lateinit var btnVerCatalogo: Button
    private lateinit var btnReservarSala: Button
    private lateinit var spnSalas: Spinner
    private lateinit var txtFecha: EditText
    private lateinit var btnConsultar: Button
    private lateinit var rvHorarios: RecyclerView
    private lateinit var adapterHorarios: HorariosAdaptador
    private var tipoUsuario = ""
    private var idUsuario = 0
    private var nombreUsuario = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
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
        btnVerCatalogo = findViewById(R.id.btnVerCatalogo)
        btnReservarSala = findViewById(R.id.btnReservarSala)
        spnSalas = findViewById(R.id.spnSalas)
        txtFecha = findViewById(R.id.txtFecha)
        btnConsultar = findViewById(R.id.btnConsultar)
        rvHorarios = findViewById(R.id.rvHorarios)

        rvHorarios.layoutManager = LinearLayoutManager(this)
        adapterHorarios = HorariosAdaptador(emptyList())
        rvHorarios.adapter = adapterHorarios

        // 2. Presentador
        presenter = clsInicioPresentador(this)

        // 3. Listeners
        btnVerCatalogo.setOnClickListener { irA(SalasActivity::class.java) }
        btnReservarSala.setOnClickListener {
            val idSala = presenter.idSalaSeleccionada(spnSalas.selectedItemPosition)
            abrirNuevaSolicitud(idSala)
        }
        btnConsultar.setOnClickListener {
            presenter.consultarDisponibilidad(
                spnSalas.selectedItemPosition,
                txtFecha.text.toString().trim()
            )
        }

        // Barra inferior
        findViewById<TextView>(R.id.btnSalas).setOnClickListener { irA(SalasActivity::class.java) }
        findViewById<TextView>(R.id.btnBuzon).setOnClickListener { irA(BuzonActivity::class.java) }

        presenter.iniciar()
    }

    fun mostrarSalas(salas: List<dataSalas>) {
        val nombres = salas.map { it.nombreSala }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSalas.adapter = adapter
    }

    fun mostrarHorarios(horarios: List<dataHorarios>) {
        adapterHorarios.actualizarLista(horarios)
    }

    fun abrirNuevaSolicitud(idSala: Int) {
        val intent = Intent(this, NuevaSolicitudActivity::class.java)
        intent.putExtra("SALA_ID", idSala)
        intent.putExtra("TIPO_USUARIO", tipoUsuario)
        intent.putExtra("ID_USUARIO", idUsuario)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
        startActivity(intent)
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