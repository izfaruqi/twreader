package com.izfaruqi.twreader.work

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.izfaruqi.twreader.databinding.WorkRecyclerItemBinding

class WorkAdapter(private var workList: ArrayList<Work>, private val onItemClickListener: (pos: Int) -> Unit) : RecyclerView.Adapter<WorkAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WorkRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = workList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtTitle.text = workList[position].title
        if(workList[position].author == ""){
            holder.binding.txtAuthor.text = "Anonymous"
            holder.binding.txtAuthor.setTypeface(holder.binding.txtAuthor.typeface, Typeface.ITALIC)
        } else {
            holder.binding.txtAuthor.text = workList[position].author
        }
        holder.itemView.setOnClickListener { onItemClickListener(position) }
    }


    fun setData(newData: ArrayList<Work>){
        this.workList = newData
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: WorkRecyclerItemBinding): RecyclerView.ViewHolder(binding.root)
}