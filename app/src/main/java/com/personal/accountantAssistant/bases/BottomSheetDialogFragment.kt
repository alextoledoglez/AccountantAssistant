package com.personal.accountantAssistant.bases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.personal.accountantAssistant.extensions.asView
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import org.koin.android.ext.android.inject

abstract class BottomSheetDialogFragment : BottomSheetDialogFragment() {

    abstract fun initComponents()

    @StringRes
    abstract fun getActionBarTitle(): Int
    abstract fun cancel()
    abstract fun save()
    abstract fun initObservers()

    @Composable
    abstract fun ScreenContent()

    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private val analytics: AnalyticsProvider? by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AccountantTheme {
                Box(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
                    this@BottomSheetDialogFragment.ScreenContent()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.parent?.asView()?.let {
            bottomSheetBehavior = BottomSheetBehavior.from(it)
        }
        analytics?.trackScreenViewEvent(this::class.simpleName)
        initComponents()
        initObservers()
        setFullScreen()
    }

    protected fun setFullScreen() {
        bottomSheetBehavior?.apply {
            isHideable = false
            isDraggable = false
            skipCollapsed = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}