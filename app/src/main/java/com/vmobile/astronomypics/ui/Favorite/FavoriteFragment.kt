package com.vmobile.astronomypics.ui.Favorite

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmobile.astronomypics.R
import com.vmobile.astronomypics.adapter.FavoritePictureAdapter
import com.vmobile.astronomypics.databinding.FragmentFavouriteBinding
import com.vmobile.astronomypics.models.PlanetaryResponse
import com.vmobile.astronomypics.ui.bottomSheet.DescriptionBottomSheet
import com.vmobile.astronomypics.viewModel.FavouriteViewModel

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: FavoritePictureAdapter
    private lateinit var viewModel: FavouriteViewModel
    private lateinit var itemList: List<PlanetaryResponse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(this).get(FavouriteViewModel::class.java)

        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.favoritePictureList.observe(viewLifecycleOwner, Observer {
            itemList = it
            if (it.isEmpty()) {
                binding.noFavoriteMessage.visibility = View.VISIBLE
                binding.pictureGrid.visibility = View.GONE
            } else {
                if (!this::adapter.isInitialized) {
                    adapter = FavoritePictureAdapter(it, requireContext())
                    val layoutManager = LinearLayoutManager(requireContext())
                    binding.pictureGrid.layoutManager = layoutManager
                    binding.pictureGrid.adapter = adapter
                } else {

                    // refresh the adapter with new values
                    adapter.refreshRows(itemList)
                }
                enableListeners()
            }
            binding.progressBar.visibility = View.GONE
            showMenuIcons()
        })

        // API call to get favorite pictures from DB
        viewModel.getFavoritePics()

        // once click on save button, will get the updated values from DB
        // get notified here once DB operations are done
        viewModel.resultStatus.observe(viewLifecycleOwner, Observer {
            viewModel.getFavoritePics()
        })
        return root
    }

    /**
     * Show icon on toolbar, the save icon
     *
     */
    private fun showMenuIcons() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menu.clear()
                menuInflater.inflate(R.menu.menu_favorite, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                if (adapter.getUpdatedListToSave().isNotEmpty() == true) {
                    viewModel.updateFavouritePic(adapter.getUpdatedListToSave())
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * This method will enable callbacks when user tap on item and launch the bottomSheet
     *
     */
    private fun enableListeners() {
        if (this::adapter.isInitialized) {
            adapter.setFavoriteItemListener(object : FavoritePictureAdapter.AddFavoritePictureClickedListener {
                override fun onFavoriteItemClicked(planetaryResponse: PlanetaryResponse) {
                    showBottomSheet(planetaryResponse)
                }
            })
        }
    }

    /**
     * Launch bottomSheet
     *
     * @param planetaryResponse
     */
    private fun showBottomSheet(planetaryResponse: PlanetaryResponse) {
        val sheet = DescriptionBottomSheet(planetaryResponse)
        sheet.show(childFragmentManager, "")
    }
}