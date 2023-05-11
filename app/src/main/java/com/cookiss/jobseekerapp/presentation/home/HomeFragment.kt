package com.cookiss.jobseekerapp.presentation.home

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.cookiss.jobseekerapp.R
import com.cookiss.jobseekerapp.databinding.FragmentHomeBinding
import com.cookiss.jobseekerapp.presentation.adapter.JobListPagingAdapter
import com.cookiss.jobseekerapp.presentation.adapter.LoadingAdapter
import com.cookiss.jobseekerapp.presentation.adapter.SearchJobAdapter
import com.cookiss.jobseekerapp.presentation.detail.JobDetailFragmentArgs
import com.cookiss.jobseekerapp.util.CommonUtils
import com.cookiss.jobseekerapp.util.Status
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Boolean.FALSE
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(), JobListPagingAdapter.OnItemClickListener, SearchJobAdapter.OnItemClickListener {

    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel : HomeViewModel by viewModels()

    private lateinit var rv_job_list: RecyclerView
    private lateinit var rv_search_job: RecyclerView
    private lateinit var linearLayoutManager1: LinearLayoutManager
    private lateinit var linearLayoutManager2: LinearLayoutManager
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var jobAdapter: JobListPagingAdapter
    private lateinit var searchJobAdapter: SearchJobAdapter

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheet: ConstraintLayout
    private lateinit var closeBtn: ImageView
    private lateinit var cl_reset_filter: ConstraintLayout
    private lateinit var cl_apply_filter: ConstraintLayout
    private lateinit var cl_fulltime_filter: ConstraintLayout
    private lateinit var tv_reset_filter: TextView
    private lateinit var searchLocation: EditText
    private var location: String? = null
    private var description: String? = null
    private var fulltime: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheet  = view.findViewById(R.id.bottom_sheet_filter)
        closeBtn = bottomSheet.findViewById(R.id.back)
        cl_reset_filter = bottomSheet.findViewById(R.id.cl_reset_filter)
        cl_apply_filter = bottomSheet.findViewById(R.id.btn_terapkan_filter)
        tv_reset_filter = bottomSheet.findViewById(R.id.tv_reset_filter)
        cl_fulltime_filter = bottomSheet.findViewById(R.id.cl_filter_fulltime)
        searchLocation = bottomSheet.findViewById(R.id.search_location)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        rv_job_list = binding.rvJobList
        rv_search_job = binding.rvSearchJob
        initRecyclerView()
        initSearchRecyclerView()
        fetchJobs()

        //set the bottom sheet initial state to hidden
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        closeBtn.setOnClickListener {
            hideBottomSheet()
            CommonUtils().hideSoftKeyboard(view)
        }

        binding.ivFilter.setOnClickListener {
            showBottomSheet()
        }

        cl_reset_filter.setOnClickListener {
            binding.searchEditText.text = null
            location = null
            fulltime = false
            CommonUtils().hideSoftKeyboard(view)
            hideBottomSheet()
        }

        cl_apply_filter.setOnClickListener {
            description = binding.searchEditText.text.trim().toString()
            location = searchLocation.text.trim().toString()
            Log.d(TAG, "cl_apply_filter description: $description")
            Log.d(TAG, "cl_apply_filter location: $location")
            Log.d(TAG, "cl_apply_filter fulltime: $fulltime")
            CommonUtils().hideSoftKeyboard(view)
            hideBottomSheet()

            searchJob(description, location, fulltime)
        }

        cl_fulltime_filter.setOnClickListener {
            if(cl_fulltime_filter.isSelected){
                cl_fulltime_filter.isSelected = false
                fulltime = false
            }else{
                cl_fulltime_filter.isSelected = true
                fulltime = true
            }
        }

        binding.searchEditText.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                keyEvent == null ||
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                //User finished typing
                Log.d(TAG, "setelah enter: "+textView.text.toString())
                binding.searchEditText.clearFocus()
                if(!textView.text.toString().isNullOrBlank()){
                    CommonUtils().hideSoftKeyboard(view)

                    description = textView.text.toString()

                    searchJob(description, location, fulltime)
                }else{
                    Toast.makeText(context, "Pencarian tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                true
            }
            false
        }

        binding.clearSearchQuery.setOnClickListener {
            CommonUtils().hideSoftKeyboard(view)
            binding.searchEditText.clearFocus()
            binding.searchEditText.text = null
            description = null
            location = null
            fulltime = false
            fetchJobs()

        }
    }

    private fun initSearchRecyclerView() {
        searchJobAdapter = SearchJobAdapter(requireContext(), this)
        linearLayoutManager1 = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, FALSE)
        rv_search_job.layoutManager = linearLayoutManager1
        rv_search_job.adapter = searchJobAdapter

    }

    private fun initRecyclerView() {
        jobAdapter = JobListPagingAdapter(requireContext(), this)
        linearLayoutManager2 = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, FALSE)
        rv_job_list.layoutManager = linearLayoutManager2
        rv_job_list.adapter = jobAdapter

        rv_job_list.adapter = jobAdapter.withLoadStateHeaderAndFooter(
            header = LoadingAdapter { jobAdapter.retry() },
            footer = LoadingAdapter { jobAdapter.retry() }
        )
    }

    private fun fetchJobs() {
        lifecycleScope.launch {
            homeViewModel.fetchJob().collectLatest { pagingData ->
                Log.d(TAG, "fetchMovies: $pagingData")

                rv_search_job.visibility = View.GONE
                rv_job_list.visibility = View.VISIBLE
                jobAdapter.submitData(pagingData)
            }
        }
    }

    private fun searchJob(description: String?, location: String?, fulltime: Boolean?) {
        homeViewModel.searchJob(description, location, fulltime)
        homeViewModel.searchJobResult.observe(viewLifecycleOwner, Observer { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let { dataResult ->

                        searchJobAdapter.removeData()
                        searchJobAdapter.setData(dataResult)
                        rv_job_list.visibility = View.GONE
                        rv_search_job.visibility = View.VISIBLE

                    }
                    binding.progressBar.visibility = View.GONE

                }
                Status.LOADING -> {
                    Log.d(TAG, "test: loading")
                    binding.progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    _result.message?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                    Log.d(TAG, "test: error")
                    Handler().postDelayed({
                        binding.progressBar.visibility = View.GONE
                    }, 1000)
                }
            }
        })
    }

    override fun onItemClicked(v: View, position: Int) {

        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToJobDetailFragment()
                .setStringId(jobAdapter.getId(position) ?: "")
        )
    }

    private fun hideBottomSheet(){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.saveFlags = BottomSheetBehavior.SAVE_ALL

    }

    private fun showBottomSheet() {

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Do something for new state
                when(newState){

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset

            }
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        bottomSheetBehavior.saveFlags = BottomSheetBehavior.SAVE_ALL
    }

    override fun onListClick(v: View?, position: Int) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToJobDetailFragment()
                .setStringId(searchJobAdapter.getId(position) ?: "")
        )
    }
}