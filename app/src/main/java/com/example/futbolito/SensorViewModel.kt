import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

//Controlar los datos del acelerometro

//Hereda de model para tener acceso al contexto, Tenemos un SensorEventListener para recibir los eventos del acelerometro
class SensorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    //Obtenemos el sensor managger y acelelometro del dispositivo.
    private val sensorManager = application.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    //Guardamos los valores del sensor en el eje x, y, z ES MUTABLE STATE PARA REFLEJAR CAMBIOS RAPIDO EN LA INTERFAZ
    var sensorValues by mutableStateOf(SensorData(0f, 0f, 0f))
        private set

    //si el acelerómetro está disponible, se registra este ViewModel como listener del sensor.
    init {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) //configuración que prioriza una respuesta rápida del sensor, ideal para juegos.
        }
    }

    //Evento que se ejecuta al detectar cambios en el sensor y actualiza los nuevos valores del sensor
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            sensorValues = SensorData(it.values[0], it.values[1], it.values[2])
        }
    }

    //Funcion no se usa por ahora pero se necesita en SensorEventListener
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    //Cuando se destruye el view model se DESREGISTRA PARA AHORAR BATERIA Y EVITAR ERRORES.
    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}

//Esta clase es usada dentro del SensorViewModel para almacenar los valores del sensor de manera organizada:
data class SensorData(val x: Float, val y: Float, val z: Float)
