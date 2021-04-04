package com.izfaruqi.twreader.library

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.izfaruqi.twreader.MainActivity
import com.izfaruqi.twreader.R
import com.izfaruqi.twreader.databinding.LibraryFragmentBinding
import com.izfaruqi.twreader.viewwork.ViewWorkActivity
import com.izfaruqi.twreader.work.WorkAdapter

class LibraryFragment : Fragment() {

    companion object {
        fun newInstance() = LibraryFragment()
    }

    private val viewModel: LibraryViewModel by activityViewModels()
    private var _binding: LibraryFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LibraryFragmentBinding.inflate(inflater, container, false)
        viewModel.initModel(requireContext().applicationContext)

        viewModel.works.observe(viewLifecycleOwner, Observer {
            if(viewModel.works.value!!.size == 0){
                binding.txtLibraryItemsStatus.visibility = View.VISIBLE
                binding.txtLibraryItemsStatus.text = "Your library is currently empty.\nSwipe right to open the search page.\nOr click the + button to add work by ID."
            } else {
                binding.txtLibraryItemsStatus.visibility = View.GONE
                (binding.recyclerLibrary.adapter as WorkAdapter).setData(viewModel.works.value!!)
            }
        })


        binding.recyclerLibrary.adapter = WorkAdapter(viewModel.works.value!!){
            val intent = Intent(activity, ViewWorkActivity::class.java)
            intent.putExtra("work", viewModel.works.value!![it])
            intent.putExtra("isFull", true)
            startActivity(intent)
        }
        binding.recyclerLibrary.layoutManager = LinearLayoutManager(activity)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle("Library")
        viewModel.getAllFromLibrary()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}