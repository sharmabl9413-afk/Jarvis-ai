package com.jarvis.ai

import android.app.Activity
import android.os.Bundle
import android.widget.*
import android.graphics.Color
import android.view.Gravity
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private lateinit var server: EditText
    private lateinit var input: EditText
    private lateinit var chat: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)
        layout.setBackgroundColor(Color.rgb(7, 11, 20))

        val title = TextView(this)

        title.text = "JARVIS"
        title.textSize = 30f
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        layout.addView(title)

        server = EditText(this)

        server.hint = "http://192.168.1.10:3000"
        server.setTextColor(Color.WHITE)
        server.setHintTextColor(Color.GRAY)

        layout.addView(server)

        chat = TextView(this)

        chat.text = "JARVIS: Ready...\n"
        chat.textSize = 17f
        chat.setTextColor(Color.WHITE)

        val scroll = ScrollView(this)
        scroll.addView(chat)

        layout.addView(
            scroll,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )

        input = EditText(this)

        input.hint = "Ask JARVIS..."
        input.setTextColor(Color.WHITE)
        input.setHintTextColor(Color.GRAY)

        layout.addView(input)

        val button = Button(this)

        button.text = "SEND"

        button.setOnClickListener {
            sendMessage()
        }

        layout.addView(button)

        setContentView(layout)
    }

    private fun sendMessage() {

        val message = input.text.toString().trim()

        if (message.isEmpty()) return

        chat.append("\nYou: $message\n")
        input.setText("")

        val serverUrl =
            server.text.toString().trim().removeSuffix("/")

        thread {

            try {

                val url =
                    URL("$serverUrl/api/chat")

                val connection =
                    url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doOutput = true

                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                val safeMessage =
                    message
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")

                val json =
                    """{"message":"$safeMessage"}"""

                connection.outputStream.use {
                    it.write(json.toByteArray())
                }

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                runOnUiThread {

                    chat.append(
                        "\nJARVIS: $response\n"
                    )

                }

            } catch (e: Exception) {

                runOnUiThread {

                    chat.append(
                        "\nError: ${e.message}\n"
                    )

                }
            }
        }
    }
}
