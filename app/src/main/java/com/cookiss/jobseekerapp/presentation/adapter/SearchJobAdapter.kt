package com.cookiss.jobseekerapp.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.cookiss.jobseekerapp.R
import com.cookiss.jobseekerapp.domain.model.JobsResponseItem

class SearchJobAdapter(

    private var context: Context,
    val onItemClickListener: OnItemClickListener


) : RecyclerView.Adapter<SearchJobAdapter.ViewHolder>() {
    private val data: MutableList<JobsResponseItem> = mutableListOf()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.tv_title_job)
        val company: TextView = itemView.findViewById(R.id.tv_company_job)
        val location: TextView = itemView.findViewById(R.id.tv_location_job)
        val type: TextView = itemView.findViewById(R.id.tv_type_job)
        val cl_genre: ConstraintLayout = itemView.findViewById(R.id.cl_genre)

        fun bind(context: Context, jobs: JobsResponseItem, onItemClickListener: OnItemClickListener) {

            title.text = jobs.title
            company.text = jobs.company
            location.text = jobs.location
            type.text = jobs.type

            cl_genre.setOnClickListener {
                onItemClickListener.onListClick(itemView, absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchJobAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.jobs_list, parent, false)
        return ViewHolder(view)
    }

    fun setData(newListData: List<JobsResponseItem>?) {
        if (newListData == null) return
        data.clear()
        data.addAll(newListData)
        notifyDataSetChanged()
    }

    fun removeData(){
        data.clear()
        notifyDataSetChanged()
    }

    fun getId(position: Int) : String{
        return data[position].id
    }

    override fun onBindViewHolder(holder: SearchJobAdapter.ViewHolder, position: Int) {
        val data = data[position]

        holder.bind(context, data, onItemClickListener)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnItemClickListener{
        fun onListClick(v: View?, position: Int)
    }

}