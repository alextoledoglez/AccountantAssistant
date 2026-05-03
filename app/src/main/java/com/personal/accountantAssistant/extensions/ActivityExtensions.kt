package com.personal.accountantAssistant.extensions

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.system.exitProcess

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) { bindingInflater.invoke(layoutInflater) }

@Suppress("UNCHECKED_CAST")
internal fun <V : Any> AppCompatActivity.viewModelClass(): KClass<V> {
    val type = javaClass.genericSuperclass as ParameterizedType
    val result = type.actualTypeArguments[0] as Class<V>
    return result.kotlin
}

fun ComponentActivity.setActivityForResult(
    callback: ActivityResultCallback<ActivityResult>
): ActivityResultLauncher<Intent?> = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult(), callback
) as ActivityResultLauncher<Intent?>

fun Activity.closeApp() {
    finishAffinity()
    exitProcess(Int.ZERO)
}