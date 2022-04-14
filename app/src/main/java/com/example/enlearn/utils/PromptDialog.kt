package com.example.enlearn.utils

import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.enlearn.R
import kotlinx.android.synthetic.main.prompt_dialog.*

/**
 * Created by Siva G Gurusamy on 10/Apr/2022
 * email : siva@paxel.co
 */

private const val TITLE = "username"
private const val MESSAGE = "token"
private const val OKBUTTON = "Okbutton"
private const val CANCELBUTTON = "cancelbutton"

class PromptDialog : DialogFragment() {
    private var mTittle: String = ""
    private var mMessage: CharSequence = ""
    private var mOkButton: String = ""
    private var mCancelButton: String = ""

    var okListener: DialogCallback.AlertCallback? = null
    var cancelListener: DialogCallback.AlertCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        arguments?.let {
            mTittle = it.getString(TITLE, "")
            mMessage = it.getCharSequence(MESSAGE, "")
            mOkButton = it.getString(OKBUTTON, "")
            mCancelButton = it.getString(CANCELBUTTON, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.prompt_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title.text = mTittle
        message.text = mMessage
        message.movementMethod = LinkMovementMethod.getInstance()
        message.highlightColor = Color.TRANSPARENT
        okButton.text = mOkButton
        cancelButton.text = mCancelButton

        if (mTittle.isEmpty()) title.visibility = View.GONE
        if (mMessage.isEmpty()) message.visibility = View.GONE
        if (mOkButton.isEmpty()) okButton.visibility = View.GONE
        if (mCancelButton.isEmpty()) cancelButton.visibility = View.GONE

        okButton.setOnClickListener {
            dialog?.let { okListener?.onButtonClicked(it) ?: run {
                dismiss()
            } }
        }
        cancelButton.setOnClickListener {
            dialog?.let {
                cancelListener?.onButtonClicked(it) ?: run {
                    dismiss()
                }
            }
        }

    }

    companion object {
        fun newInstance(title: String, message: CharSequence, okButton: String, cancelButton: String) =
            PromptDialog().apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                    putCharSequence(MESSAGE, message)
                    putString(OKBUTTON, okButton)
                    putString(CANCELBUTTON, cancelButton)
                }
            }
    }
}