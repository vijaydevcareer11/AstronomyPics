package com.vmobile.astronomypics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vmobile.astronomypics.databinding.FavoritePicsRowBinding
import com.vmobile.astronomypics.models.PlanetaryResponse
import com.vmobile.astronomypics.utils.ImageUtils

class FavoritePictureAdapter(
    private var itemList: List<PlanetaryResponse>,
    private val context: Context,
) : RecyclerView.Adapter<FavoritePictureAdapter.PictureViewHolder>() {

    private var updatedList: MutableList<PlanetaryResponse> = arrayListOf()
    private lateinit var binding: FavoritePicsRowBinding
    private var addFavoritePictureClickedListener: AddFavoritePictureClickedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        binding = FavoritePicsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PictureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.bindData(itemList[position])
    }

    private fun prepareListToSave(response: PlanetaryResponse) {
        if (updatedList.isEmpty()) {
            updatedList.add(response)
        } else {
            var isFound: Boolean = false;
            for (item in updatedList) {
                if (item.dateOfAPOD == response.dateOfAPOD) {
                    item.isFavorite = response.isFavorite
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                updatedList.add(response)
            }
        }
    }

    fun getUpdatedListToSave(): List<PlanetaryResponse> {
        return updatedList
    }

    fun clearList() {
        updatedList.clear()
    }

    fun refreshRows(list: List<PlanetaryResponse>) {
        itemList = list
        notifyDataSetChanged()
    }

    fun setFavoriteItemListener(listener: AddFavoritePictureClickedListener) {
        addFavoritePictureClickedListener = listener
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class PictureViewHolder(private val binding: FavoritePicsRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(response: PlanetaryResponse) {
            ImageUtils.loadImage(context, response.mediaURL, binding.picture)
            binding.title.text = response.imageTitle
            binding.date.text = response.dateOfAPOD

            if (response.isFavorite) {
                binding.removeFromFavorite.visibility = View.GONE
                binding.addToFavorite.visibility = View.VISIBLE
            } else {
                binding.removeFromFavorite.visibility = View.VISIBLE
                binding.addToFavorite.visibility = View.GONE
            }
            if (response.copyrightAuthor.isNotEmpty()) {
                binding.credit.text = "Credit :" + response.copyrightAuthor
            } else {
                binding.credit.visibility = View.GONE
            }

            binding.addToFavorite.setOnClickListener {
                response.isFavorite = false

                binding.removeFromFavorite.visibility = View.VISIBLE
                binding.addToFavorite.visibility = View.GONE
                prepareListToSave(response)
            }

            binding.removeFromFavorite.setOnClickListener {
                response.isFavorite = true

                binding.removeFromFavorite.visibility = View.GONE
                binding.addToFavorite.visibility = View.VISIBLE
                prepareListToSave(response)
            }

            binding.favoriteLayout.setOnClickListener {
                addFavoritePictureClickedListener?.onFavoriteItemClicked(response)
            }
        }
    }

    interface AddFavoritePictureClickedListener {
        fun onFavoriteItemClicked(planetaryResponse: PlanetaryResponse)
    }
}