package com.equipo7.meethub.vista


import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.equipo7.meethub.adaptador.HorariosAdaptador
import com.equipo7.meethub.modelo.dataHorarios
import com.equipo7.meethub.modelo.dataSalas
import com.equipo7.meethub.presentador.clsNuevaSolicitudPresentador
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.equipo7.meethub.R

class NuevaSolicitudActivity : AppCompatActivity() {
    private lateinit var presenter: clsNuevaSolicitudPresentador
    private lateinit var txtSolicitante: TextView
    private lateinit var spnSalas: Spinner
    private lateinit var txtFecha: EditText
    private lateinit var btnVerHorarios: Button
    private lateinit var rvHorarios: RecyclerView
    private lateinit var adapterHorarios: HorariosAdaptador
    private lateinit var spnHoraInicio: Spinner
    private lateinit var spnHoraFin: Spinner
    private lateinit var txtAsistentes: EditText
    private lateinit var txtAforoMinimo: TextView
    private lateinit var txtActividad: EditText
    private lateinit var btnReservar: Button
    private lateinit var btnRegresar: Button
    private var listaSalas: List<dataSalas> = emptyList()
    private var salaIdInicial = 0
    private var tipoUsuario = ""
    private var idUsuario = 0
    private var nombreUsuario = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nueva_solicitud)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Enlazar componentes XML
        txtSolicitante = findViewById(R.id.txtSolicitante)
        spnSalas = findViewById(R.id.spnSalas)
        txtFecha = findViewById(R.id.txtFecha)
        btnVerHorarios = findViewById(R.id.btnVerHorarios)
        rvHorarios = findViewById(R.id.rvHorarios)
        spnHoraInicio = findViewById(R.id.spnHoraInicio)
        spnHoraFin = findViewById(R.id.spnHoraFin)
        txtAsistentes = findViewById(R.id.txtAsistentes)
        txtAforoMinimo = findViewById(R.id.txtAforoMinimo)
        txtActividad = findViewById(R.id.txtActividad)
        btnReservar = findViewById(R.id.btnReservar)
        btnRegresar = findViewById(R.id.btnRegresar)

        // Los horarios se muestran en 3 columnas
        rvHorarios.layoutManager = GridLayoutManager(this, 3)
        adapterHorarios = HorariosAdaptador(emptyList())
        rvHorarios.adapter = adapterHorarios

        // 2. Datos que llegaron por Intent
        salaIdInicial = intent.getIntExtra("SALA_ID", 0)

        // Datos del usuario que llegaron por Intent
        tipoUsuario = intent.getStringExtra("TIPO_USUARIO") ?: ""
        idUsuario = intent.getIntExtra("ID_USUARIO", 0)
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: ""

        txtSolicitante.text = getString(
            R.string.solicitud_solicitante, nombreUsuario, tipoUsuario, idUsuario
        )

        // 3. Presentador y listeners
        presenter = clsNuevaSolicitudPresentador(this)
        btnRegresar.setOnClickListener { finish() }

        spnSalas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                actualizarAforo(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnVerHorarios.setOnClickListener {
            presenter.consultarHorarios(
                spnSalas.selectedItemPosition,
                txtFecha.text.toString().trim()
            )
        }

        btnReservar.setOnClickListener {
            presenter.enviarSolicitud(
                tipoUsuario,
                idUsuario,
                spnSalas.selectedItemPosition,
                txtFecha.text.toString().trim(),
                spnHoraInicio.selectedItem?.toString() ?: "",
                spnHoraFin.selectedItem?.toString() ?: "",
                txtAsistentes.text.toString().trim(),
                txtActividad.text.toString().trim()
            )
        }

        presenter.iniciar(salaIdInicial)
    }

    // Llena el Spinner de salas y deja seleccionada la que llegó por Intent
    fun mostrarSalas(salas: List<dataSalas>, posicionInicial: Int) {
        listaSalas = salas
        val nombres = salas.map { it.nombreSala }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSalas.adapter = adapter
        spnSalas.setSelection(posicionInicial)
        actualizarAforo(posicionInicial)
    }

    private fun actualizarAforo(posicion: Int) {
        if (posicion in listaSalas.indices) {
            val minimo = listaSalas[posicion].capacidadMinima
            txtAforoMinimo.text = getString(R.string.solicitud_aforo, minimo)
        }
    }

    // Muestra las franjas y llena los Spinner de hora de inicio y de fin
    fun mostrarHorarios(horarios: List<dataHorarios>) {
        adapterHorarios.actualizarLista(horarios)

        val inicios = horarios.map { it.horaInicio }
        val adapterInicio = ArrayAdapter(this, android.R.layout.simple_spinner_item, inicios)
        adapterInicio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnHoraInicio.adapter = adapterInicio

        val fines = horarios.map { it.horaFin }
        val adapterFin = ArrayAdapter(this, android.R.layout.simple_spinner_item, fines)
        adapterFin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnHoraFin.adapter = adapterFin
    }

    // Lo llama el presentador cuando la solicitud se envió: avisa y regresa
    fun solicitudEnviada(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        finish()
    }

    fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}