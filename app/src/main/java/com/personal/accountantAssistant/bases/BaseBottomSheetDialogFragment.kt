package com.personal.accountantAssistant.bases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.viewmodel.ext.android.getViewModel
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

abstract class BaseBottomSheetDialogFragment<V : BaseViewModel> : BottomSheetDialogFragment() {

    abstract val binding: ViewBinding

    abstract fun initView()

    protected lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    val viewModel: V by lazy { getViewModel(clazz = viewModelClass()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        initView()
    }

    protected fun setFullScreen() {
        binding.root.layoutParams.height = resources.displayMetrics.heightPixels
        bottomSheetBehavior.apply {
            isHideable = false
            skipCollapsed = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun viewModelClass(): KClass<V> =
        ((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<V>).kotlin
}