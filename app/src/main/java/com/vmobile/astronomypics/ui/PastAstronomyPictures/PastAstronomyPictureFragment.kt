package com.vmobile.astronomypics.ui.PastAstronomyPictures

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView.ScaleType
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.vmobile.astronomypics.R
import com.vmobile.astronomypics.databinding.FragmentShowPastPictureBinding
import com.vmobile.astronomypics.models.PlanetaryResponse
import com.vmobile.astronomypics.utils.DateUtils
import com.vmobile.astronomypics.utils.GenericConstants
import com.vmobile.astronomypics.utils.ImageUtils
import com.vmobile.astronomypics.viewModel.PastAstronomyPicturesViewModel
import java.util.*


class PastAstronomyPictureFragment : Fragment() {

    private var _binding: FragmentShowPastPictureBinding? = null
    private val binding get() = _binding!!

    private lateinit var pastAstronomyPicturesViewModel: PastAstronomyPicturesViewModel
    private lateinit var newDate: String
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var planetaryResponse: PlanetaryResponse
    private var isFavorite = false
    private lateinit var menuHost: MenuHost

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        menuHost = requireActivity()
        pastAstronomyPicturesViewModel = ViewModelProvider(this).get(PastAstronomyPicturesViewModel::class.java)

        _binding = FragmentShowPastPictureBinding.inflate(inflater, container, false)
        val root: View = binding.root

        newDate = DateUtils.getCurrentDate()

        initialSetup()
        enableDateSelection()
        return root
    }

    /**
     * Get the picture based on date
     *
     * @param selectedDate
     */
    @SuppressLint("CheckResult")
    private fun getThePicture(selectedDate: String) {
        pastAstronomyPicturesViewModel.pictureOfDay.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            planetaryResponse = it
            if (planetaryResponse.statusCode == GenericConstants.NO_SERVICE) {
                hideAllElementsWhenErrorOccur()
            } else if (planetaryResponse.statusCode in 200..300) {
                if (planetaryResponse.mediaType == GenericConstants.MEDIA_TYPE_IMAGE) {
                    binding.pictureOfDay.visibility = View.VISIBLE
                    binding.videoCLickText.visibility = View.GONE

                    binding.pictureOfDay.scaleType = ScaleType.FIT_XY

                    // load the image using picasso
                    ImageUtils.loadImage(requireContext(), planetaryResponse.mediaURL, binding.pictureOfDay)
                } else {
                    binding.pictureOfDay.visibility = View.GONE
                    binding.videoCLickText.visibility = View.VISIBLE

                    // launch browser when media type is video
                    binding.videoCLickText.setOnClickListener {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(planetaryResponse.mediaURL))
                        startActivity(Intent.createChooser(browserIntent, getString(R.string.open_link_title)))
                    }
                }
                setTheDetails()
                showHideElementsWhenDataPresent()
                showMenuIcons()
            } else {
                binding.noNetworkMessage.apply {
                    text = requireContext().getString(R.string.generic_error_text)
                    visibility = View.VISIBLE
                }
                hideAllElementsWhenErrorOccur()
            }
        })
        pastAstronomyPicturesViewModel.getPictureOfDay(selectedDate)
    }

    /**
     * Method to show date picker and hit the API to get pictures for selected day
     *
     */
    private fun enableDateSelection() {
        val calendar = Calendar.getInstance()

        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            newDate = DateUtils.getSelectedDate(year, monthOfYear, dayOfMonth)
            binding.progressBar.visibility = View.VISIBLE

            // get the picture for selected date
            getThePicture(newDate)
        }, currentYear, currentMonth, currentDay)

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis();
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {

                    // if the select cancel from date-picker, the picture for latest date
                    getThePicture(newDate)
                }
            }
        })
        datePickerDialog.show()
    }

    /**
     * Set the information got from the API response
     *
     */
    private fun setTheDetails() {
        binding.date.text = planetaryResponse.dateOfAPOD
        binding.title.text = planetaryResponse.imageTitle
        binding.description.text = planetaryResponse.explanation
        binding.copyRightText.text = planetaryResponse.copyrightAuthor
    }

    /**
     * show or Hide the view elements when data is obtained from server
     *
     */
    private fun showHideElementsWhenDataPresent() {
        binding.progressBar.visibility = View.GONE
        binding.divider.visibility = View.VISIBLE
        binding.secondDivider.visibility = View.VISIBLE
        if (planetaryResponse.copyrightAuthor.isNotEmpty()) {
            binding.copyRightLogo.visibility = View.VISIBLE
        } else {
            binding.copyRightLogo.visibility = View.GONE
        }
        binding.description.visibility = View.VISIBLE
        binding.date.visibility = View.VISIBLE
        binding.title.visibility = View.VISIBLE
        binding.copyRightText.visibility = View.VISIBLE
        binding.noNetworkMessage.visibility = View.GONE
    }

    /**
     * Hide the UI elements when error occur while getting data
     *
     */
    private fun hideAllElementsWhenErrorOccur() {
        binding.pictureOfDay.visibility = View.GONE
        binding.divider.visibility = View.GONE
        binding.secondDivider.visibility = View.GONE
        binding.copyRightLogo.visibility = View.GONE
        binding.noNetworkMessage.visibility = View.VISIBLE

        binding.description.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.date.visibility = View.GONE
        binding.title.visibility = View.GONE
        binding.copyRightText.visibility = View.GONE
    }

    /**
     * Show the menu icons on toolbar
     *
     */
    private fun showMenuIcons() {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                // Add menu items here
                menu.clear()
                menuInflater.inflate(R.menu.menu_previous, menu)
                val addFavoriteMenuItem = menu.findItem(R.id.favorite)

                // Show or hide favorite icon based on favorite flag
                if (planetaryResponse.isFavorite) {
                    isFavorite = true
                    addFavoriteMenuItem?.setIcon(R.drawable.ic_thumb_up_24)
                } else {
                    isFavorite = false
                    addFavoriteMenuItem?.setIcon(R.drawable.ic_empty_thumb_up_24)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                if (menuItem.itemId == R.id.dateRange) {

                    // show date picker
                    datePickerDialog.show()
                } else {

                    // once user manage the favorite picture, update the values in DB
                    if (isFavorite) {
                        planetaryResponse.isFavorite = false
                        menuItem.setIcon(R.drawable.ic_empty_thumb_up_24)
                        isFavorite = false
                    } else {
                        planetaryResponse.isFavorite = true
                        menuItem.setIcon(R.drawable.ic_thumb_up_24)
                        isFavorite = true
                    }

                    // update the values in DB for favorite pics
                    pastAstronomyPicturesViewModel.updateFavouritePic(planetaryResponse)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * Initial setup to show or hide UI element when fragment launched
     *
     */
    private fun initialSetup() {
        binding.progressBar.visibility = View.VISIBLE
        binding.divider.visibility = View.GONE
        binding.secondDivider.visibility = View.GONE
        binding.copyRightLogo.visibility = View.GONE
        binding.noNetworkMessage.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}