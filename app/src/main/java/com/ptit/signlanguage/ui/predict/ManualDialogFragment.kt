package com.ptit.signlanguage.ui.predict

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ptit.signlanguage.R
import com.ptit.signlanguage.ui.predict.fragment.Fragment1
import com.ptit.signlanguage.ui.predict.fragment.Fragment2
import com.ptit.signlanguage.ui.predict.fragment.Fragment3

class ManualDialogFragment : DialogFragment() {
    private var order = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.dialog_manual, container, false)

        // Add the inner fragment
        if (savedInstanceState == null) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment, Fragment1())
            transaction.commit()
        }
        val nextBtn = view.findViewById<Button>(R.id.nextBtn)
        nextBtn.setOnClickListener{
            if(order == 0 || order == 1){
                initReplace(++order)
            }
            else{
                dialog?.dismiss()
            }
        }

        return view
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
    private fun initReplace(order: Int){
        childFragmentManager.beginTransaction().replace(R.id.fragment,
            when(order){
                0 -> Fragment1()
                1 -> Fragment2()
                2 -> Fragment3()
                else -> {Fragment3()}
            }).commit()
    }
}