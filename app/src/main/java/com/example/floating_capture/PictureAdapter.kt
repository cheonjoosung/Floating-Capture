package com.example.floating_capture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.floating_capture.databinding.ItemPictureBinding

class PictureAdapter(
    private val list: MutableList<String>
) : RecyclerView.Adapter<PictureAdapter.PictureViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PictureViewHolder {
        val binding = ItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PictureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = list.size


    class PictureViewHolder(private val binding: ItemPictureBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.apply {
                //ivImage

                //tvTitle
            }
        }
    }

}