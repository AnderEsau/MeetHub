package com.equipo7.meethub.vista


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.graphics.Typeface
import androidx.core.widget.doAfterTextChanged
import android.widget.LinearLayout
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
    private lateinit var layoutHorarios: LinearLayout
    private lateinit var txtTituloHorarios: TextView
    private lateinit var rvHorarios: RecyclerView
    private lateinit var adapterHorarios: HorariosAdaptador
    private lateinit var spnHoraInicio: Spinner
    private lateinit var spnHoraFin: Spinner
    private lateinit var txtAsistentes: EditText
    private lateinit var txtAforoMinimo: TextView
    private lateinit var txtActividad: EditText
    private lateinit var btnReservar: Button
    private lateinit var btnRegresar: TextView

    private var listaSalas: List<dataSalas> = emptyList()
    private var salaIdInicial = 0
    private var tipoUsuario = ""
    private var idUsuario = 0
    private var nombreUsuario = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nueva_solicitud)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        txtSolicitante = findViewById(R.id.txtSolicitante)
        spnSalas = findViewById(R.id.spnSalas)
        txtFecha = findViewById(R.id.txtFecha)
        layoutHorarios = findViewById(R.id.layoutHorarios)
        txtTituloHorarios = findViewById(R.id.txtTituloHorarios)
        rvHorarios = findViewById(R.id.rvHorarios)
        spnHoraInicio = findViewById(R.id.spnHoraInicio)
        spnHoraFin = findViewById(R.id.spnHoraFin)
        txtAsistentes = findViewById(R.id.txtAsistentes)
        txtAforoMinimo = findViewById(R.id.txtAforoMinimo)
        txtActividad = findViewById(R.id.txtActividad)
        btnReservar = findViewById(R.id.btnReservar)
        btnRegresar = findViewById(R.id.btnRegresar)

        rvHorarios.layoutManager = GridLayoutManager(this, 3)

        adapterHorarios = HorariosAdaptador(
            emptyList(),
            R.layout.item_horario_chip
        )

        rvHorarios.adapter = adapterHorarios

        salaIdInicial = intent.getIntExtra("SALA_ID", 0)

        tipoUsuario = intent.getStringExtra("TIPO_USUARIO") ?: ""
        idUsuario = intent.getIntExtra("ID_USUARIO", 0)
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: ""

        txtSolicitante.text = getString(
            R.string.solicitud_solicitante,
            nombreUsuario,
            tipoUsuario,
            idUsuario
        )

        findViewById<ImageView>(R.id.icSalas)
            .setBackgroundResource(R.drawable.fondo_nav_activo)

        findViewById<TextView>(R.id.txtNavSalas)
            .setTypeface(null, Typeface.BOLD)

        presenter = clsNuevaSolicitudPresentador(this)

        btnRegresar.setOnClickListener {
            finish()
        }

        spnSalas.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    actualizarAforo(position)
                    consultarHorarios()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        txtFecha.doAfterTextChanged {
            consultarHorarios()
        }

        findViewById<View>(R.id.btnInicio).setOnClickListener {
            irA(InicioActivity::class.java)
        }

        findViewById<View>(R.id.btnSalas).setOnClickListener {
            irA(SalasActivity::class.java)
        }

        findViewById<View>(R.id.btnBuzon).setOnClickListener {
            irA(BuzonActivity::class.java)
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

    private fun consultarHorarios() {
        val fecha = txtFecha.text.toString().trim()

        if (fecha.length == 10 && spnSalas.selectedItemPosition >= 0) {
            presenter.consultarHorarios(
                spnSalas.selectedItemPosition,
                fecha
            )
        }
    }

    fun mostrarSalas(salas: List<dataSalas>, posicionInicial: Int) {
        listaSalas = salas

        val nombres = salas.map { it.nombreSala }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombres
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spnSalas.adapter = adapter
        spnSalas.setSelection(posicionInicial)

        actualizarAforo(posicionInicial)
    }

    private fun actualizarAforo(posicion: Int) {
        if (posicion in listaSalas.indices) {
            val minimo = listaSalas[posicion].capacidadMinima

            txtAforoMinimo.text = getString(
                R.string.solicitud_aforo,
                minimo
            )
        }
    }

    fun mostrarHorarios(horarios: List<dataHorarios>) {
        layoutHorarios.visibility = View.VISIBLE

        txtTituloHorarios.text = getString(
            R.string.solicitud_horarios_del,
            txtFecha.text.toString().trim()
        )

        adapterHorarios.actualizarLista(horarios)

        val inicios = horarios.map { it.horaInicio }

        val adapterInicio = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            inicios
        )

        adapterInicio.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spnHoraInicio.adapter = adapterInicio

        val fines = horarios.map { it.horaFin }

        val adapterFin = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            fines
        )

        adapterFin.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spnHoraFin.adapter = adapterFin
    }

    fun solicitudEnviada(mensaje: String) {
        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()

        finish()
    }

    private fun irA(destino: Class<*>) {
        val intent = Intent(this, destino)

        intent.putExtra("TIPO_USUARIO", tipoUsuario)
        intent.putExtra("ID_USUARIO", idUsuario)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)

        startActivity(intent)
    }

    fun mostrarError(mensaje: String) {
        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }
}
