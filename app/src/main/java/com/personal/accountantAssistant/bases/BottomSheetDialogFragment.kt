package com.personal.accountantAssistant.bases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.personal.accountantAssistant.providers.AnalyticsProvider
import org.koin.android.ext.android.inject

abstract class BottomSheetDialogFragment : BottomSheetDialogFragment() {

    abstract val binding: ViewBinding
    abstract fun initComponents()
    abstract fun initObservers()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private val analytics: AnalyticsProvider? by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        analytics?.trackScreenViewEvent(this::class.simpleName)
        initComponents()
        initObservers()
    }

    protected fun setFullScreen() {
        binding.root.layoutParams.height = resources.displayMetrics.heightPixels
        bottomSheetBehavior.apply {
            isHideable = false
            skipCollapsed = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}