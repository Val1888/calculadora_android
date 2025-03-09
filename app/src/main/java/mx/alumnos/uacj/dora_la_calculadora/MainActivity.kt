package mx.alumnos.uacj.dora_la_calculadora

import android.os.Bundle
import androidx.compose.animation.animateContentSize
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import mx.alumnos.uacj.dora_la_calculadora.ui.theme.Dora_la_calculadoraTheme

data class BotonModelo(
    val id: String,
    var numero: String,
    var operacion_aritmetica: OperacionesAritmeticas = OperacionesAritmeticas.Ninguna,
    var operacion_a_mostrar: String = ""
) {}

enum class EstadosCalculadora{
    CuandoEstaEnCero,
    AgregandoNumeros,
    SeleccionadoOperacion,
    MostrandoResultado
}

enum class OperacionesAritmeticas{
    Ninguna, // Esta es la opcion por default y sirve para hacer nada
    Suma,
    Resta,
    Multiplicacion,
    Division,
    Resultado
}

var hileras_de_botones_a_dibujar = arrayOf(
    arrayOf(
        BotonModelo("boton_9", "9", OperacionesAritmeticas.Multiplicacion, "*"),
        BotonModelo("boton_8", "8"),
        BotonModelo("boton_7", "7", OperacionesAritmeticas.Division, "/"),
    ),
    arrayOf(
        BotonModelo("boton_6", "6"),
        BotonModelo("boton_5", "5", OperacionesAritmeticas.Resultado, "="),
        BotonModelo("boton_4", "4"),
    ),
    arrayOf(
        BotonModelo("boton_3", "3", OperacionesAritmeticas.Suma, "+"),
        BotonModelo("boton_2", "2"),
        BotonModelo("boton_1", "1", OperacionesAritmeticas.Resta, "-"),
    ),
    arrayOf(
        BotonModelo("boton_punto", "."),
        BotonModelo("boton_0", "0"),
        BotonModelo("boton_operacion", "OP"),
    )

)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dora_la_calculadoraTheme {
                Calculadora()
            }
        }
    }
}

@Composable
fun Calculadora() {
    var pantalla_calculadora = remember { mutableStateOf("0") }
    var numero_anterior = remember { mutableStateOf("0") }
    var estado_de_la_calculadora = remember { mutableStateOf(EstadosCalculadora.CuandoEstaEnCero) }
    var operacion_seleccionada = remember { mutableStateOf(OperacionesAritmeticas.Ninguna) }

    fun pulsar_boton(boton: BotonModelo){
        Log.v("BOTONES_INTERFAZ", "Se ha pulsado el boton ${boton.id} de la interfaz")
        Log.v("OPERACION_SELECCIONADA", "La operacion seleccionada es ${operacion_seleccionada.value}")

        when(estado_de_la_calculadora.value){
            EstadosCalculadora.CuandoEstaEnCero -> {
                if(boton.id == "boton_0"){
                    return
                }
                else if(boton.id == "boton_punto"){
                    pantalla_calculadora.value = pantalla_calculadora.value + boton.numero
                    return
                }

                pantalla_calculadora.value = boton.numero
                estado_de_la_calculadora.value = EstadosCalculadora.AgregandoNumeros

            }

            EstadosCalculadora.AgregandoNumeros -> {
                if(boton.id == "boton_operacion"){
                    estado_de_la_calculadora.value = EstadosCalculadora.SeleccionadoOperacion
                    return
                }

                pantalla_calculadora.value = pantalla_calculadora.value + boton.numero
            }

            EstadosCalculadora.SeleccionadoOperacion -> {
                if(     boton.operacion_aritmetica != OperacionesAritmeticas.Ninguna &&
                    boton.operacion_aritmetica != OperacionesAritmeticas.Resultado
                ){
                    operacion_seleccionada.value = boton.operacion_aritmetica
                    estado_de_la_calculadora.value = EstadosCalculadora.CuandoEstaEnCero

                    numero_anterior.value = pantalla_calculadora.value

                    pantalla_calculadora.value = "0"
                    return
                }
                // Aqui imprimimos el resultado
                else if (boton.operacion_aritmetica == OperacionesAritmeticas.Resultado &&
                    operacion_seleccionada.value != OperacionesAritmeticas.Ninguna) {

                    val num1 = numero_anterior.value.toDoubleOrNull() ?: 0.0
                    val num2 = pantalla_calculadora.value.toDoubleOrNull() ?: 0.0
                    var resultado = 0.0

                    resultado = when (operacion_seleccionada.value) {
                        OperacionesAritmeticas.Suma -> num1 + num2
                        OperacionesAritmeticas.Resta -> num1 - num2
                        OperacionesAritmeticas.Multiplicacion -> num1 * num2
                        OperacionesAritmeticas.Division -> if (num2 != 0.0) num1 / num2 else Double.NaN
                        else -> 0.0
                    }

                    pantalla_calculadora.value = if (resultado % 1 == 0.0) {
                        resultado.toInt().toString() // Si es un número entero, quitar los decimales
                    } else {
                        String.format("%.5f", resultado).trimEnd('0').trimEnd('.') // Muestra hasta 5 decimales, pero sin notación científica
                    }

                    // Muestra solo 3 decimales

                    pantalla_calculadora.value = resultado.toString()
                    estado_de_la_calculadora.value = EstadosCalculadora.MostrandoResultado
                    return
                }


                estado_de_la_calculadora.value = EstadosCalculadora.AgregandoNumeros
            }


            EstadosCalculadora.MostrandoResultado -> {
                numero_anterior.value  = ""

                pantalla_calculadora.value = "0"

                estado_de_la_calculadora.value = EstadosCalculadora.CuandoEstaEnCero
            }
        }
    }


    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = pantalla_calculadora.value,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.33f)
                .background(Color(0xFFF288BD)) // Fondo de la pantalla de números
                .height(50.dp)
                .animateContentSize(),
            textAlign = TextAlign.Right,
            color = Color(0xFFF0CEDF),
            fontSize = 56.sp

        )



        // Deberia jugar mas con el estilo de aqui
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF706068))) { //la cosa del fondo
            for(fila_de_botones in hileras_de_botones_a_dibujar){
                Row(horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()) {
                    for(boton_a_dibujar in fila_de_botones){
                        when(estado_de_la_calculadora.value){
                            EstadosCalculadora.SeleccionadoOperacion -> {
                                Boton(boton_a_dibujar.operacion_a_mostrar, alPulsar = {
                                    pulsar_boton(boton_a_dibujar)
                                })
                            }
                            else -> {
                                Boton(boton_a_dibujar.numero, alPulsar = {
                                    pulsar_boton(boton_a_dibujar)
                                })
                            }
                        }

                    }
                }
            }
        }
    }


}

@Composable
fun Boton(etiqueta: String, alPulsar: () -> Unit = {}){
    Button(onClick = alPulsar, modifier = Modifier
        .height(90.dp)
        .width(100.dp)
        .padding(5.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFF0CEDF))
    ) {
        Box{
            Image(
                painter = painterResource(R.drawable.mymelody), //la imagen
                contentDescription = "Una foto de perfil del conde de contar",
                modifier = Modifier.size(40.dp),

                )
            val letra_dos = FontFamily(Font(R.font.letra_dos))
            Text(
                etiqueta,
                modifier = Modifier,
                fontFamily = letra_dos,
                color = Color(0xFF703F58),
                fontSize = 20.sp // Aumenta el tamaño de la fuente
            )

        }

    }
}
@Composable
fun MyMelodyReaccion(pantalla_calculadora: String) {
    val resultado = pantalla_calculadora.toDoubleOrNull() ?: 0.0

    // Definir un desplazamiento animado
    val desplazamientoY by animateFloatAsState(
        targetValue = if (resultado > 100) -20f else 0f,  // Se mueve si el resultado es grande
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
    )

    Image(
        painter = painterResource(R.drawable.mymelody),
        contentDescription = "My Melody reacciona",
        modifier = Modifier
            .size(100.dp)
            .offset(y = desplazamientoY.dp)  // Aplica la animación de movimiento
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Dora_la_calculadoraTheme {
        Calculadora()
    }
}

@Preview(showBackground = true)
@Composable
fun mostrar_boton(){
    Dora_la_calculadoraTheme {
        Boton("4")
        }
}