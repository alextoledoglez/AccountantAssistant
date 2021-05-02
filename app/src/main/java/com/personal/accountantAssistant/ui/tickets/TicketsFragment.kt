package com.personal.accountantAssistant.ui.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.utils.MenuHelper.initializeHomeOptions

class TicketsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initializeHomeOptions()
        return inflater.inflate(R.layout.fragment_tickets, container, false)
    }
}