package com.cookiss.jobseekerapp.presentation.detail

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cookiss.jobseekerapp.R
import com.cookiss.jobseekerapp.databinding.FragmentHomeBinding
import com.cookiss.jobseekerapp.databinding.FragmentJobDetailBinding
import com.cookiss.jobseekerapp.presentation.home.HomeViewModel
import com.cookiss.jobseekerapp.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobDetailFragment : Fragment() {

    private val TAG = this.javaClass.simpleName

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private var _binding: FragmentJobDetailBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val args: JobDetailFragmentArgs by navArgs()
    private var jobId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentJobDetailBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobId = args.stringId
        getDetailJob(jobId)

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getDetailJob(jobId: String) {
        homeViewModel.getDetailJob(jobId)
        homeViewModel.jobResult.observe(viewLifecycleOwner, Observer { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let { dataResult ->
                        binding.tvTitle.text = dataResult.title
                        binding.tvFulltime.text = if (dataResult.title == "Full Time") {
                            "Yes"
                        } else {
                            "No"
                        }
                        binding.tvDescription.text = dataResult.description

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
}