package com.equipo7.meethub.vista

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.equipo7.meethub.adaptador.SalasAdaptador
import com.equipo7.meethub.modelo.dataSalas
import com.equipo7.meethub.presentador.clsSalasPresentador
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.equipo7.meethub.R

class SalasActivity : AppCompatActivity() {

    private lateinit var presenter: clsSalasPresentador
    private lateinit var rvSalas: RecyclerView
    private lateinit var adapterSalas: SalasAdaptador

    private var tipoUsuario = ""
    private var idUsuario = 0
    private var nombreUsuario = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_salas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Datos del usuario que llegaron por Intent
        tipoUsuario = intent.getStringExtra("TIPO_USUARIO") ?: ""
        idUsuario = intent.getIntExtra("ID_USUARIO", 0)
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: ""

        // 1. Enlazar componentes XML y configurar el RecyclerView
        rvSalas = findViewById(R.id.rvSalas)
        rvSalas.layoutManager = LinearLayoutManager(this)
        adapterSalas = SalasAdaptador(this, emptyList())
        rvSalas.adapter = adapterSalas

        // 2. Barra inferior
        findViewById<TextView>(R.id.btnInicio).setOnClickListener { irA(InicioActivity::class.java) }
        findViewById<TextView>(R.id.btnBuzon).setOnClickListener { irA(BuzonActivity::class.java) }

        // 3. Presentador
        presenter = clsSalasPresentador(this)
        presenter.cargarSalas()
    }

    fun mostrarSalas(salas: List<dataSalas>) {
        adapterSalas.actualizarLista(salas)
    }

    // El adaptador llama a estas dos funciones cuando se toca un botón de la tarjeta
    fun verDetalle(sala: dataSalas) {
        presenter.verDetalle(sala)
    }

    fun reservar(sala: dataSalas) {
        presenter.reservar(sala)
    }

    // El presentador llama a estas dos para abrir otra pantalla
    fun abrirDetalleSala(idSala: Int) {
        val intent = Intent(this, DetalleSalaActivity::class.java)
        intent.putExtra("SALA_ID", idSala)
        intent.putExtra("TIPO_USUARIO", tipoUsuario)
        intent.putExtra("ID_USUARIO", idUsuario)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
        startActivity(intent)
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