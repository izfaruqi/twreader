package com.izfaruqi.twreader.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.ferfalk.simplesearchview.SimpleSearchView
import com.izfaruqi.twreader.MainActivity
import com.izfaruqi.twreader.R
import com.izfaruqi.twreader.viewwork.ViewWorkActivity
import com.izfaruqi.twreader.databinding.SearchFragmentBinding
import com.izfaruqi.twreader.work.WorkAdapter

class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private val viewModel: SearchViewModel by activityViewModels()
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchQuery.value = newText
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.onSearchSubmit()
                searchView.clearFocus()
                return true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        binding.recyclerSearch.adapter = WorkAdapter(viewModel.works.value!!) {
            val currentWork = viewModel.works.value?.get(it)
            MaterialDialog(activity as Context).show {
                title(text = currentWork?.title ?: "")
                if(currentWork?.author == ""){
                    message(text = "by Anonymous\n\n${currentWork?.summary ?: ""}")
                } else {
                    message(text = "by ${currentWork?.author ?: ""}\n\n${currentWork?.summary ?: ""}")
                }
                positiveButton (text = "Details"){
                    val intent = Intent(activity, ViewWorkActivity::class.java)
                    intent.putExtra("work", currentWork)
                    intent.putExtra("isFull", false)
                    startActivity(intent)
                }
            }
        }
        binding.recyclerSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(viewModel.isLoading.value!!){
                    return
                }
                if(!recyclerView.canScrollVertically(1)){
                    viewModel.onSearchLoadNextPage()
                }
            }
        })
        binding.recyclerSearch.layoutManager = LinearLayoutManager(activity)

        viewModel.works.observe(viewLifecycleOwner, Observer {
            binding.txtResultStatus.text = if(viewModel.isFirstSearch) "Please perform a search query." else "No results found for this query :("
            binding.txtResultStatus.visibility = if(viewModel.works.value!!.size > 0) View.GONE else View.VISIBLE
            (binding.recyclerSearch.adapter as WorkAdapter).setData(viewModel.works.value!!)
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.indicatorLoadingBar.visibility = if(viewModel.isLoading.value!!) View.VISIBLE else View.GONE
        })

        viewModel.totalResult.observe(viewLifecycleOwner, Observer {
            if(viewModel.totalResult.value!! > 0){
                binding.txtResultTotal.visibility = View.VISIBLE
                binding.txtResultTotal.text = "${viewModel.totalResult.value!!} results found."
            } else {
                binding.txtResultTotal.visibility = View.GONE
            }
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle("Search")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}