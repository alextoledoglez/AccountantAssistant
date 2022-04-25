package com.personal.accountantAssistant.bases

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.extensions.viewModelClass
import org.koin.android.viewmodel.ext.android.getViewModel

abstract class BaseActivity<V : BaseViewModel> : AppCompatActivity() {

    abstract val binding: ViewBinding
    val viewModel: V by lazy { getViewModel(clazz = viewModelClass()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}