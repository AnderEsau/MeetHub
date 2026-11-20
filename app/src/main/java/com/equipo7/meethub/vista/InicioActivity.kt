package com.equipo7.meethub.vista

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.graphics.Typeface
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.doAfterTextChanged
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
    private lateinit var btnVerCatalogo: TextView
    private lateinit var btnReservarSala: Button
    private lateinit var spnSalas: Spinner
    private lateinit var txtFecha: EditText
    private lateinit var rvHorarios: RecyclerView
    private lateinit var adapterHorarios: HorariosAdaptador

    private var tipoUsuario = ""
    private var idUsuario = 0
    private var nombreUsuario = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)

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

        tipoUsuario = intent.getStringExtra("TIPO_USUARIO") ?: ""
        idUsuario = intent.getIntExtra("ID_USUARIO", 0)
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: ""

        btnVerCatalogo = findViewById(R.id.btnVerCatalogo)
        btnReservarSala = findViewById(R.id.btnReservarSala)
        spnSalas = findViewById(R.id.spnSalas)
        txtFecha = findViewById(R.id.txtFecha)
        rvHorarios = findViewById(R.id.rvHorarios)

        rvHorarios.layoutManager = LinearLayoutManager(this)
        adapterHorarios = HorariosAdaptador(emptyList())
        rvHorarios.adapter = adapterHorarios

        findViewById<ImageView>(R.id.icInicio)
            .setBackgroundResource(R.drawable.fondo_nav_activo)

        findViewById<TextView>(R.id.txtNavInicio)
            .setTypeface(null, Typeface.BOLD)

        presenter = clsInicioPresentador(this)

        btnVerCatalogo.setOnClickListener {
            irA(SalasActivity::class.java)
        }

        btnReservarSala.setOnClickListener {
            val idSala = presenter.idSalaSeleccionada(
                spnSalas.selectedItemPosition
            )
            abrirNuevaSolicitud(idSala)
        }

        spnSalas.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    consultar()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        txtFecha.doAfterTextChanged {
            consultar()
        }

        findViewById<View>(R.id.btnSalas).setOnClickListener {
            irA(SalasActivity::class.java)
        }

        findViewById<View>(R.id.btnBuzon).setOnClickListener {
            irA(BuzonActivity::class.java)
        }

        presenter.iniciar()
    }

    private fun consultar() {
        val fecha = txtFecha.text.toString().trim()

        if (fecha.length == 10 && spnSalas.selectedItemPosition >= 0) {
            presenter.consultarDisponibilidad(
                spnSalas.selectedItemPosition,
                fecha
            )
        }
    }

    fun mostrarSalas(salas: List<dataSalas>) {
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
