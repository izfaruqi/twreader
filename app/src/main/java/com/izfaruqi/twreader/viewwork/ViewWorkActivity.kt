package com.izfaruqi.twreader.viewwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.izfaruqi.twreader.databinding.ActivityViewWorkBinding
import com.izfaruqi.twreader.work.Work
import java.text.SimpleDateFormat
import java.util.*

class ViewWorkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewWorkBinding
    private val viewModel: ViewWorkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewWorkBinding.inflate(layoutInflater)
        viewModel.work.value = intent.getSerializableExtra("work") as Work

        setSupportActionBar(binding.actionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "#??????"

        viewModel.initModel(this)
        if(!intent.getBooleanExtra("isFull", false)){
            viewModel.loadFullWork()
        }

        viewModel.isLoading.observe(this, Observer {
            if(viewModel.isLoading.value!!){
                binding.tblDetails.visibility = View.INVISIBLE
                binding.tblStats.visibility = View.INVISIBLE
                binding.progressLoading.visibility = View.VISIBLE
            } else {
                binding.tblDetails.visibility = View.VISIBLE
                binding.tblStats.visibility = View.VISIBLE
                binding.progressLoading.visibility = View.GONE
            }
        })

        viewModel.work.observe(this, Observer {
            val currentWork = viewModel.work.value!!
            supportActionBar?.title = "#" + currentWork.id.toString()
            binding.txtTitle.text = currentWork.title
            binding.txtAuthor.text = currentWork.author
            binding.txtSummary.text = currentWork.summary

            binding.txtRating.text = currentWork.ratings.joinToString(", ")
            binding.txtWarning.text = currentWork.warnings.joinToString(", ")
            binding.txtCategories.text = currentWork.categories.joinToString(", ")
            binding.txtFandoms.text = currentWork.fandoms.joinToString(", ")
            binding.txtRelationships.text = currentWork.relationships.joinToString(", ")
            binding.txtCharacters.text = currentWork.characters.joinToString(", ")
            binding.txtTags.text = currentWork.tags.joinToString(", ")
            binding.txtLanguage.text = currentWork.language
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.US)
            if(currentWork.published != null){
                binding.txtPublished.text = "Published " + dateFormatter.format(currentWork.published)
            }
            if(currentWork.updated != null){
                binding.txtUpdated.text = "Updated " + dateFormatter.format(currentWork.updated)
            }
            binding.txtWords.text = currentWork.words.toString()
            binding.txtKudos.text = currentWork.kudos.toString()
            binding.txtBookmarks.text = currentWork.bookmarks.toString()
            binding.txtHits.text = currentWork.hits.toString()
        })

        binding.btnLibrary.setOnClickListener {
            viewModel.insertWorkIntoDb()
        }

        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return super.onSupportNavigateUp()
    }
}