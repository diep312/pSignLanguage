package com.ptit.signlanguage.ui.predict

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.ptit.signlanguage.R
import com.ptit.signlanguage.ui.predict.adapter.SlideAdapter
import com.ptit.signlanguage.ui.predict.adapter.SlideItem


class ManualDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.dialog_manual, container, false)

        val slidesItemAdapter = SlideAdapter(
            listOf(
                SlideItem(
                    slideImage = R.drawable.im_slide_1,
                    slideTitle = getString(R.string.str_slidetitle_1),
                    slideDescription = getString(R.string.str_slidedesc_1)
                ),
                SlideItem(
                    slideImage = R.drawable.im_slide_2,
                    slideTitle = getString(R.string.str_slidetitle_2),
                    slideDescription = getString(R.string.str_slidedesc_2)
                ),
                SlideItem(
                    slideImage = R.drawable.im_slide_3,
                    slideTitle = getString(R.string.str_slidetitle_3),
                    slideDescription = getString(R.string.str_slidedesc_3)
                ),
            )
        )

        val dialogViewPager = view.findViewById<ViewPager2>(R.id.vp_slides)
        dialogViewPager.adapter = slidesItemAdapter
        val slideCountText = view.findViewById<TextView>(R.id.tv_slide_count)
        dialogViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                slideCountText.text = "%d/%d".format(dialogViewPager.currentItem + 1, slidesItemAdapter.itemCount)
            }
        })
        val nextBtn = view.findViewById<ImageView>(R.id.nextBtn)
        nextBtn.setOnClickListener{
            if(dialogViewPager.currentItem < slidesItemAdapter.itemCount - 1){
                dialogViewPager.currentItem = dialogViewPager.currentItem + 1
                slideCountText.text = "%d/%d".format(dialogViewPager.currentItem + 1, slidesItemAdapter.itemCount)
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
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
}