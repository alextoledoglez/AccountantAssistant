package com.personal.accountantAssistant.bases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import org.koin.android.ext.android.inject

abstract class BaseFragment : Fragment() {

    abstract fun initComponents()
    abstract fun initObservers()

    @Composable
    abstract fun ScreenContent()

    private val analytics: AnalyticsProvider? by inject()
    private val toolbarTitle = MutableLiveData<String>()

    open fun onActivityBackPressed(): Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent { AccountantTheme { this@BaseFragment.ScreenContent() } }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analytics?.trackScreenViewEvent(this::class.simpleName)
        initComponents()
        initObservers()
    }

    fun onTitleChanged(): LiveData<String> = toolbarTitle

    fun setTitle(title: String) = toolbarTitle.postValue(title)
}