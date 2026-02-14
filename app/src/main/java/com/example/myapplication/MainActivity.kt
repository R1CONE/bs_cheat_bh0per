package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasOverlayPermission(this)) {
            OverlayManager.showRedRectangle(this)
        } else {
            requestOverlayPermission(this)
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}
@Composable
fun MainScreen() {
    var check1 by remember { mutableStateOf(false) }
    var check2 by remember { mutableStateOf(false) }
    var check3 by remember { mutableStateOf(false) }

    var red by remember { mutableStateOf(0f) }
    var green by remember { mutableStateOf(0f) }
    var blue by remember { mutableStateOf(0f) }

    val selectedColor = Color(red, green, blue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

        CheckboxRow("Auto Shooting", check1, ) { check1 = it }
        CheckboxRow("Auto Shooting", check2, ) { check2 = it }
        CheckboxRow("Auto Dodge", check3, ) { check3 = it }

        Text(
            text = "Select color",
            style = MaterialTheme.typography.titleMedium
        )

        ColorSlider("Red", red) { red = it }
        ColorSlider("Green", green) { green = it }
        ColorSlider("Blue", blue) { blue = it }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(selectedColor)
        )
    }
}

@Composable
fun CheckboxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = text,
            color = Color.Black // ← ЯВНО чёрный
        )
    }
}

@Composable
fun ColorSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Text("$label: ${(value * 255).toInt()}")
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f
        )
    }
}


fun hasOverlayPermission(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

fun requestOverlayPermission(context: Context) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    )
    context.startActivity(intent)
}

object OverlayManager {

    private var overlayView: View? = null

    fun showRedRectangle(context: Context) {

        if (!Settings.canDrawOverlays(context)) return
        if (overlayView != null) return

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            400,
            300,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 200

        val view = View(context).apply {
            setBackgroundColor(android.graphics.Color.RED) // ✅ правильно
        }

        windowManager.addView(view, params)
        overlayView = view
    }

    fun removeOverlay(context: Context) {
        overlayView?.let {
            val windowManager =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.removeView(it)
            overlayView = null
        }
    }
}