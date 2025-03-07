package com.example.broadcastreceiberphone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.CallLog
import android.telephony.TelephonyManager
import android.telephony.SmsManager
import android.util.Log


class CallReceiver : BroadcastReceiver() {
    //Método principal que se invoca cuando hay una señal
    override fun onReceive(context: Context?, intent: Intent?) {
        //Cambia conforme el estado de la llamada telefonica.
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            //Obtener el estado de la llamada
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            //Se comprueba si esta sonando la llamada.
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                //Obtenermos el numero de la última llamada realizada
                val incomingNumber = getLastCallNumber(context)
                Log.d("CallReceiver", "Llamada entrante de: $incomingNumber")

                //Variables para guardar datos de la UI
                val sharedPreferences = context?.getSharedPreferences("AutoResponderPrefs", Context.MODE_PRIVATE)
                val savedPhone = sharedPreferences?.getString("saved_phone", "")
                val autoReplyMessage = sharedPreferences?.getString("saved_message", "")

                if (incomingNumber == savedPhone) {
                    sendSMS(savedPhone, autoReplyMessage ?: "", context)
                }
            }
        }
    }

    //Método usado para obtener el numero ordenando la consulta de manera descendente asegurando obtener la llamada mas reciente
    private fun getLastCallNumber(context: Context?): String? {
        if (context == null) return null
        try {
            val cursor: Cursor? = context.contentResolver.query(
                //Uri de la base de datos donde se almacenan las llamadas de la columna NUMBER que es donde esta el numero
                CallLog.Calls.CONTENT_URI,arrayOf(CallLog.Calls.NUMBER),null,null,"${CallLog.Calls.DATE} DESC"
            )
            //Al usar use el cursor se cierra automaticamente al finalizar su uso
            cursor?.use {
                if (it.moveToFirst()) {
                    val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                    //Si el indice de la columna es valido se obtiene el valor del número de teléfono de la llamada más reciente
                    if (numberIndex >= 0) {
                        //Devolvemos el numero de telefono
                        return it.getString(numberIndex)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("CallReceiver", "No se tienen permisos para leer el registro de llamadas", e)
        }
        return null
    }

    //Funcion para enviar los mensajes
    private fun sendSMS(phoneNumber: String?, message: String, context: Context?) {
        try {
            if (phoneNumber != null) {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                Log.d("CallReceiver", "Mensaje enviado a $phoneNumber: $message")
            }
        } catch (e: Exception) {
            Log.e("CallReceiver", "Error enviando SMS", e)
        }
    }
}