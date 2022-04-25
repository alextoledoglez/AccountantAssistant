package com.personal.accountantAssistant.bases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.extensions.viewModelClass
import com.personal.accountantAssistant.providers.AnalyticsProvider
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.getViewModel

abstract class BaseFragment<V : BaseViewModel> : Fragment() {

    abstract val binding: ViewBinding
    abstract fun initComponents()
    abstract fun initObservers()

    val viewModel: V by lazy { getViewModel(clazz = viewModelClass()) }
    private val analytics: AnalyticsProvider? by inject()
    private val toolbarTitle = MutableLiveData<String>()

    open fun onActivityBackPressed(): Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
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