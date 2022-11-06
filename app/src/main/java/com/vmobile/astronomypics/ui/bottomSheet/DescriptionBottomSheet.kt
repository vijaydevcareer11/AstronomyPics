package com.vmobile.astronomypics.ui.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vmobile.astronomypics.R
import com.vmobile.astronomypics.models.PlanetaryResponse

class DescriptionBottomSheet(val planetaryResponse: PlanetaryResponse) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_description_bottom_sheet, container, false)
        val title = view.findViewById<TextView>(R.id.title)
        val date = view.findViewById<TextView>(R.id.date)
        val description = view.findViewById<TextView>(R.id.description)
        val closeIcon = view.findViewById<ImageView>(R.id.close)
        title.text = planetaryResponse.imageTitle
        date.text = planetaryResponse.dateOfAPOD
        description.text = planetaryResponse.explanation
        closeIcon.setOnClickListener {
            dismiss()
        }

        return view
    }
}