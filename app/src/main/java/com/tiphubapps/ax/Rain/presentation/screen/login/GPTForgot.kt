import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tiphubapps.ax.Rain.R
import com.tiphubapps.ax.Rain.presentation.helper.LoginIcon

@Composable
fun GPTForgot(onButtonClick: (String) -> Unit,
              showLoginClick: () -> Unit) {
    var email by remember { mutableStateOf("") }

    val interactionSource = remember { MutableInteractionSource() }
    var buttonScale by remember { mutableStateOf(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginIcon()

            Text(
                text = "Reset Password",
                style = MaterialTheme.typography.h4,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Email",
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            )
            TextField(
                value = email,
                onValueChange = { email = it ;  /*onEvent(LoginViewModel.LoginViewEvent.EmailChanged(it))*/},
                label = { Text(text = "Email", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(color = Color.DarkGray, RoundedCornerShape(15.dp))
                    .border(2.dp, Color.DarkGray, RoundedCornerShape(15.dp))
            )

            Button(
                onClick = { onButtonClick(email) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Magenta),
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(buttonScale)
                    .pointerInput(interactionSource) {
                        detectTapGestures(
                            onPress = { buttonScale = 0.95f },
                            //l = { buttonScale = 1f }
                        )
                        //onPointerUp { buttonScale = 1f }
                        //onPointerCancel { buttonScale = 1f }
                    }
                    .padding(vertical = 16.dp)
                    .background(color = Color.Magenta, shape = RoundedCornerShape(25.dp))
            ) {
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.button,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.White
                )
            }




            /*Button(
                onClick = { showLoginClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(buttonScale)
                    .pointerInput(interactionSource) {
                        detectTapGestures(
                            onPress = { buttonScale = 0.95f },
                            //l = { buttonScale = 1f }
                        )
                        //onPointerUp { buttonScale = 1f }
                        //onPointerCancel { buttonScale = 1f }
                    }
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Back to Login",
                    style = MaterialTheme.typography.button,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.White
                )
            }*/

            Button(
                onClick = { showLoginClick() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(buttonScale)
                    .pointerInput(interactionSource) {
                        detectTapGestures(
                            onPress = { buttonScale = 0.95f },
                            //l = { buttonScale = 1f }
                        )
                        //onPointerUp { buttonScale = 1f }
                        //onPointerCancel { buttonScale = 1f }
                    }
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Back to Login",
                    style = MaterialTheme.typography.button,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Magenta
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewForgotScreen() {
    GPTForgot({},{})
}
