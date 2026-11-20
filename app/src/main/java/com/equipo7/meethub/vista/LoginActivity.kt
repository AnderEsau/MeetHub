package com.equipo7.meethub.vista

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.equipo7.meethub.presentador.clsLoginPresentador
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.equipo7.meethub.R
class LoginActivity : AppCompatActivity() {
    private lateinit var presenter: clsLoginPresentador
    private lateinit var txtIdentificador: EditText
    private lateinit var txtPsw: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtOlvidaste: TextView
    private lateinit var txtCrearCuenta: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 1. Enlazar componentes XML
        txtIdentificador = findViewById(R.id.txtIdentificador)
        txtPsw = findViewById(R.id.txtPsw)
        btnLogin = findViewById(R.id.btnLogin)
        txtOlvidaste = findViewById(R.id.txtOlvidaste)
        txtCrearCuenta = findViewById(R.id.txtCrearCuenta)
        // 2. Inicializar Presentador
        presenter = clsLoginPresentador(this)
        // 3. Listeners
        btnLogin.setOnClickListener(this::ejecutarInicioSesion)
        txtOlvidaste.setOnClickListener {
            startActivity(Intent(this, RestablecerPasswordActivity::class.java))
        }
        findViewById<ImageView>(R.id.imgVerPsw).setOnClickListener { alternarVista(txtPsw) }
        txtCrearCuenta.setOnClickListener {
            startActivity(Intent(this, CrearCuentaActivity::class.java))
        }
    }
    fun ejecutarInicioSesion(v: View) {
        val identificador = txtIdentificador.text.toString().trim()
        val psw = txtPsw.text.toString().trim()
        presenter.realizarLogin(identificador, psw)
    }
    // Lo llama el presentador cuando el login es correcto
    fun loginExitoso(mensaje: String, tipo: String, id: Int, nombre: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, InicioActivity::class.java)
        intent.putExtra("TIPO_USUARIO", tipo)
        intent.putExtra("ID_USUARIO", id)
        intent.putExtra("NOMBRE_USUARIO", nombre)
        startActivity(intent)
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
