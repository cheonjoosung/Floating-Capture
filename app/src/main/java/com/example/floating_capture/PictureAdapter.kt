package com.example.floating_capture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.floating_capture.databinding.ItemPictureBinding


class PictureAdapter(
    private val list: MutableList<MyFile>
) : RecyclerView.Adapter<PictureAdapter.PictureViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PictureViewHolder {
        val binding = ItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PictureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size


    class PictureViewHolder(private val binding: ItemPictureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyFile) {
            binding.apply {
                ivImage.setImageBitmap(imagePathToBitmap(item.path))

                tvTitle.text = item.name
            }
        }

        private fun imagePathToBitmap(path: String): Bitmap {
            return BitmapFactory.decodeFile(path)
        }
    }

}