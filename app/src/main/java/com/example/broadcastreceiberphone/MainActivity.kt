package com.example.broadcastreceiberphone

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Obtener los datos que estan en la Interfaz
        val etPhoneNumber = findViewById<EditText>(R.id.etPhoneNumber)
        val etAutoReplyMessage = findViewById<EditText>(R.id.etAutoReplyMessage)
        val btnSave = findViewById<Button>(R.id.btnSave)

        //Entrar a la preferencias del sistemas.
        sharedPreferences = getSharedPreferences("AutoResponderPrefs", Context.MODE_PRIVATE)

        // Cargar datos guardados de las ultimas llamadas
        etPhoneNumber.setText(sharedPreferences.getString("saved_phone", ""))
        etAutoReplyMessage.setText(sharedPreferences.getString("saved_message", ""))

        //Eventos que se cargaran con el boton de guardar.
        btnSave.setOnClickListener {
            //Convertir y almacenar los datos.
            val phoneNumber = etPhoneNumber.text.toString()
            val autoReplyMessage = etAutoReplyMessage.text.toString()
            //Revisar las entradas de los datos y si todo esta bien guardamos en las preferencias.
            if (phoneNumber.isNotEmpty() && autoReplyMessage.isNotEmpty()) {
                // Guardar en SharedPreferences
                with(sharedPreferences.edit()) {
                    putString("saved_phone", phoneNumber)
                    putString("saved_message", autoReplyMessage)
                    apply()
                }
                //Retroalimentar al usuario.
                Toast.makeText(this, "Informacion GUARDADA", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Rellena AMBOS campos", Toast.LENGTH_SHORT).show()
            }
        }
        // Verificar y solicitar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.SEND_SMS
                ), 1)
        }
    }
    //Mas permisos que se solicitaran.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Todos los permisos fueron concedidos
                Toast.makeText(this, "Permisos concedidos CORRECTAMENTE", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Algunos permisos no fueron concedidos. La aplicación puede no funcionar correctamente.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
