package io.hustler.omnicurisassignment.ui.main

import Videos
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import io.hustler.omnicurisassignment.R
import io.hustler.omnicurisassignment.data.model.repository.DataRepository
import io.hustler.omnicurisassignment.data.model.repository.getData
import io.hustler.omnicurisassignment.data.model.repository.getThumbNails
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.playist_item.view.*

class PlayListAdapter(
    private val itemClickListener: ItemClickListener?
) : RecyclerView.Adapter<PlayListAdapter.PlayListItemViewHolder>() {

    private val data: DataRepository = getData()
    private val thumbsData: Array<String> = getThumbNails()


    interface ItemClickListener {
        fun onClick(position: Int, video: Videos)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PlayListItemViewHolder {
        return PlayListItemViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.playist_item, viewGroup, false)
        )
    }

    override fun onBindViewHolder(holder: PlayListItemViewHolder, i: Int) {
        val videoItem = data.categories!![0].videos[i]
        holder.itemView.video_name_text.text = videoItem.title
        holder.itemView.VideoDuration.text = videoItem.subtitle
        Picasso.get().load(thumbsData[i])
            .into(holder.itemView.thumbNail)

        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(i, videoItem)
        }
    }

    override fun getItemCount(): Int {
        return data.categories!![0].videos.size
    }

    class PlayListItemViewHolder(itemView: View) : ViewHolder(itemView)

}
