package com.vmobile.astronomypics.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vmobile.astronomypics.R
import com.vmobile.astronomypics.databinding.FragmentShowPictureBinding
import com.vmobile.astronomypics.models.PlanetaryResponse
import com.vmobile.astronomypics.utils.GenericConstants
import com.vmobile.astronomypics.utils.ImageUtils
import com.vmobile.astronomypics.viewModel.HomeViewModel

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private var _binding: FragmentShowPictureBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var planetaryResponse: PlanetaryResponse
    private lateinit var homeViewModel: HomeViewModel
    private var isFavorite = false

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentShowPictureBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initialSetup()

        homeViewModel.pictureOfDay.observe(viewLifecycleOwner, Observer {
            planetaryResponse = it
            if (planetaryResponse.statusCode == GenericConstants.NO_SERVICE) {
                hideAllElementsWhenErrorOccur()
            } else if (planetaryResponse.statusCode in 200..300) {
                if (planetaryResponse.mediaType == GenericConstants.MEDIA_TYPE_IMAGE) {

                    binding.pictureOfDay.visibility = View.VISIBLE
                    binding.videoCLickText.visibility = View.GONE

                    binding.pictureOfDay.scaleType = ImageView.ScaleType.FIT_XY

                    // load the image using picasso
                    ImageUtils.loadImage(requireContext(), planetaryResponse.mediaURL, binding.pictureOfDay)
                } else {
                    binding.pictureOfDay.visibility = View.GONE
                    binding.videoCLickText.visibility = View.VISIBLE

                    // launch browser when media type is video
                    binding.videoCLickText.setOnClickListener {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(planetaryResponse.mediaURL))
                        startActivity(Intent.createChooser(browserIntent, "Open Link !!"))
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
        homeViewModel.getPictureOfDay()
        return root
    }

    /**
     * Show icon on toolbar
     *
     */
    private fun showMenuIcons() {
        val menuHost: MenuHost = requireActivity()
        var addFavoriteMenuItem: MenuItem? = null

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                // clear any existing item from menu
                menu.clear()

                // Add menu items here
                menuInflater.inflate(R.menu.menu_home, menu)
                addFavoriteMenuItem = menu.findItem(R.id.favorite)

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
                // once user manage the favorite picture, update the values in DB
                if (isFavorite) {
                    planetaryResponse.isFavorite = false
                    homeViewModel.updateFavouritePic(planetaryResponse)
                    menuItem.setIcon(R.drawable.ic_empty_thumb_up_24)
                    isFavorite = false
                } else {
                    planetaryResponse.isFavorite = true
                    homeViewModel.updateFavouritePic(planetaryResponse)
                    menuItem.setIcon(R.drawable.ic_thumb_up_24)
                    isFavorite = true
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}