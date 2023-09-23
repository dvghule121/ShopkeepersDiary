package com.example.sb_stores.Utils


import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import com.example.sb_stores.R


class SpinnerEditText(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {
    val spinner: Spinner
    val editText: EditText

    init {
        inflate(context, R.layout.spinner_edit_text, this)
        spinner = findViewById(R.id.spinner)
        editText = findViewById(R.id.edit_text)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                editText.setText(spinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Update the selected item in the spinner to match the text in the EditText
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing
            }
        })
    }
}
