package com.cookiss.jobseekerapp.presentation.adapter

import android.widget.TextView
import com.cookiss.jobseekerapp.R
import com.cookiss.jobseekerapp.domain.model.JobsResponseItem
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class JobListPagingAdapter(

    private val context: Context,
    private val itemClickListener: OnItemClickListener

) :
    PagingDataAdapter<JobsResponseItem, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<JobsResponseItem>() {
            override fun areItemsTheSame(oldItem: JobsResponseItem, newItem: JobsResponseItem): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: JobsResponseItem, newItem: JobsResponseItem): Boolean =
                oldItem.id == newItem.id
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as? DoggoImageViewHolder)?.bind(context, it, itemClickListener) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DoggoImageViewHolder.getInstance(parent)
    }

    fun getId(position: Int) : String?{
        return getItem(position)?.id
    }


    /**
     * view holder class
     */
    class DoggoImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            //get instance of the DoggoImageViewHolder
            fun getInstance(parent: ViewGroup): DoggoImageViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.jobs_list, parent, false)
                return DoggoImageViewHolder(view)
            }
        }

        val title: TextView = itemView.findViewById(R.id.tv_title_job)
        val company: TextView = itemView.findViewById(R.id.tv_company_job)
        val location: TextView = itemView.findViewById(R.id.tv_location_job)
        val type: TextView = itemView.findViewById(R.id.tv_type_job)
        val cl_genre: ConstraintLayout = itemView.findViewById(R.id.cl_genre)

        fun bind(context: Context, jobs: JobsResponseItem, itemClickListener: OnItemClickListener) {
            //loads image from network using coil extension function

//            Glide.with(context)
//                .load(Constants.IMAGE_URL+"${movies.poster_path}")
//                .into(image_list)

            title.text = jobs.title
            company.text = jobs.company
            location.text = jobs.location
            type.text = jobs.type

            cl_genre.setOnClickListener {
                itemClickListener.onItemClicked(itemView, absoluteAdapterPosition)
            }

        }

    }

    interface OnItemClickListener{
        fun onItemClicked(v: View, position: Int)
    }
}