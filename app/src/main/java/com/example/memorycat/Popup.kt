package com.example.memorycat

// PopupWindowManager.kt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow

class Popup(private val context: Context) {
    private val popupView: View = LayoutInflater.from(context).inflate(R.layout.popup_layout, null)
    private val popupWindow: PopupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

    fun showPopup(anchorView: View) {
        // Show the pop-up above the anchorView
        popupWindow.showAsDropDown(anchorView)
    }

    fun dismissPopup() {
        // Dismiss the pop-up
        popupWindow.dismiss()
    }

    // Add any other methods or interactions as needed
}