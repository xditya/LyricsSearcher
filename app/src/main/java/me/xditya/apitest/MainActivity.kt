package me.xditya.apitest

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.xditya.apitest.ui.theme.APITestTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            APITestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    HomePage()
                }
            }
        }
    }
}

fun doSomething(
    text: String,
    result: androidx.compose.runtime.MutableState<String>,
    ctx: android.content.Context
) {
    val srchText = text.replace(" ", "+")
    result.value = "Searching for $text..."
    val baseUrl = "https://apis.xditya.me/"
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
    val params = HashMap<String, String>()
    params["song"] = srchText
    val call = retrofitAPI.getData(params)
    call!!.enqueue(object : Callback<DataModel?> {
        override fun onResponse(call: Call<DataModel?>, response: Response<DataModel?>) {
            Toast.makeText(ctx, "Searching...", Toast.LENGTH_SHORT).show()
            val model: DataModel? = response.body()
            if (model != null) {
                if (model.lyrics == null) {
                    result.value = "No results found."
                    return
                }
            }
            val resp =
                "Artist: " + (model?.artist
                    ?: "No info") + "\n" + "Lyrics:\n" + model!!.lyrics
            result.value = resp
        }

        override fun onFailure(call: Call<DataModel?>, t: Throwable) {
            result.value = "Error found is : " + t.message
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage() {
    val ctx = LocalContext.current
    val ipText = remember { mutableStateOf("") }
    val result = remember { mutableStateOf("") }
    Scaffold { it ->
        Surface(
            modifier = Modifier.padding(it)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Text(text = "Lyrics Search", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(25.dp))
                TextField(
                    value = ipText.value,
                    onValueChange = {
                        ipText.value = it
                    },
                    singleLine = true,
                    label = { Text(text = "Enter song name") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (ipText.value == "") {
                        Toast.makeText(ctx, "Enter a song name!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    doSomething(text = ipText.value, result = result, ctx = ctx)
                }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = "Search for Lyrics!")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = result.value, modifier = Modifier.verticalScroll(rememberScrollState()))
            }
        }
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun GreetingPreview() {
    APITestTheme {
        HomePage()
    }
}