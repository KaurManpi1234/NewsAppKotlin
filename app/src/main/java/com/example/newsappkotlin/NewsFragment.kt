package com.example.newsappkotlin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappkotlin.databinding.FragmentNewsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class NewsFragment : Fragment() , ICategoryRVAdapter, INewsRVAdapter {

    private var binding: FragmentNewsBinding? = null
    private lateinit var navController: NavController
    private val newsAdapter = NewsRVAdapter(this)
    private lateinit var viewModel: NewsViewModel
    private val categories =
        listOf("General", "Business", "Entertainment", "Sports", "Health", "Science", "Technology")
    private val categoryAdapter = CategoryRVAdapter(categories, this)
    private var currentCategory = "General"
    private var currentPage = 1
    private var itemsDisplayed = 0
    private var totalArticles: Int? = null
    private var isScrolling: Boolean = false
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding= FragmentNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing navController
        navController = Navigation.findNavController(view)

        //initializing viewModel
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)

        setUpCategoriesRecyclerView()

        setUpArticlesRecyclerView()

        viewModel.getNews(requireContext(), currentCategory, 1, true)

        //observing news response using live data in news viewModel
        viewModel.news.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    binding?.progressBar?.visibility = View.GONE
                    newsAdapter.submitList(response.data)
                    isLoading = false
                    //getting total articles in response from dataStore
                    DataStoreManager(requireContext()).totalArticles.asLiveData().observe(viewLifecycleOwner) {
                        totalArticles = it
                    }
                    if (totalArticles != null) {
                        //setting boolean isLastPage according to the itemsDisplayed
                        isLastPage = itemsDisplayed + Constants.PAGE_SIZE_QUERY >= totalArticles!!
                    }

                    //increasing no of items displayed by 20
                    itemsDisplayed += Constants.PAGE_SIZE_QUERY
                }
                is Resource.Error -> {
                    binding?.progressBar?.visibility = View.GONE
                    Snackbar.make(binding?.root!!, "${response.error}", Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }
            }
        })

        binding?.switchTheme?.setOnCheckedChangeListener { _, isChecked ->
            val theme = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            if (isChecked) {
                binding?.rlTop?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black_bg))
                binding?.tvAppName?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                binding?.rlTop?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.off_white))
                binding?.tvAppName?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }

            lifecycleScope.launch {
                if (isChecked)
                    DataStoreManager(requireContext()).saveIsThemeDark(true)
                else
                    DataStoreManager(requireContext()).saveIsThemeDark(false)
            }

            AppCompatDelegate.setDefaultNightMode(theme)
            categoryAdapter.notifyDataSetChanged()
            newsAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()

        DataStoreManager(requireContext()).isThemeDark.asLiveData().observe(viewLifecycleOwner) { isThemeDark ->

            //changing switch state using isThemeDark boolean stored in DataStore
            binding?.switchTheme?.isChecked = isThemeDark

            val theme = if (isThemeDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            if (isThemeDark) {
                binding?.rlTop?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black_bg))
                binding?.tvAppName?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                binding?.rlTop?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.off_white))
                binding?.tvAppName?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }

            lifecycleScope.launch {
                if (isThemeDark)
                    DataStoreManager(requireContext()).saveIsThemeDark(true)
                else
                    DataStoreManager(requireContext()).saveIsThemeDark(false)
            }

            AppCompatDelegate.setDefaultNightMode(theme)
            categoryAdapter.notifyDataSetChanged()
            newsAdapter.notifyDataSetChanged()
        }
    }

    //implementing pagination
    private val paginationScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //checking if the user is scrolling
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            //getting the linear layout manager from recycler view
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            //now getting the first item position which is visible on page
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            //getting the total visible items count on page
            val visibleItemCount = layoutManager.childCount

            //getting the total items count in recycler view
            val totalItemsCount = layoutManager.itemCount

            //checking if user is not at the last page and is not loading news items
            val isNotAtLastPageAndNotLoading = !isLastPage && !isLoading

            //checking if user is at the last page of the response
            val isAtLastPage = firstVisibleItemPosition + visibleItemCount >= totalItemsCount

            //checking if user has scrolled from the first page
            val notAtBeginning = firstVisibleItemPosition >= 0

            //checking if the no.of items in 1 query response are all loaded in recycler view
            val isTotalMoreThanVisible = totalItemsCount >= Constants.PAGE_SIZE_QUERY

            //creating a boolean to know if to paginate or not
            val shouldPaginate = isNotAtLastPageAndNotLoading && isAtLastPage && isTotalMoreThanVisible && notAtBeginning && isScrolling
            if (shouldPaginate) {
                currentPage += 1
                viewModel.getNews(requireContext(), currentCategory, currentPage, false)
                isScrolling = false
            }
        }
    }

    private fun setUpCategoriesRecyclerView() {
        binding?.rvCategory?.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setUpArticlesRecyclerView() {
        binding?.rvNews?.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addOnScrollListener(paginationScrollListener)
        }
    }

    override fun onCategoryClicked(category: String) {
        currentCategory = category
        itemsDisplayed = 0
        currentPage = 1
        viewModel.getNews(requireContext(), category, 1, true)
    }

    override fun onArticleClicked(article: Article) {
        val bundle = bundleOf("OpenArticle" to article)
        navController.navigate(R.id.action_newsFragment_to_articleOpenFragment, bundle)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}