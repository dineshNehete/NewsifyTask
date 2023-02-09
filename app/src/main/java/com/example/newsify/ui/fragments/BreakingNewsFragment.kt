package com.example.newsify.ui.fragments


import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsify.R
import com.example.newsify.adapters.NewsAdapter
import com.example.newsify.databinding.FragmentBreakingNewsBinding
import com.example.newsify.databinding.ItemArticlePreviewBinding
import com.example.newsify.ui.Article
import com.example.newsify.ui.Constants
import com.example.newsify.viewmodels.NewsViewModel
import kotlin.collections.ArrayList

class BreakingNewsFragment : Fragment(), NewsAdapter.OnNewsItemClickedListener {

    private lateinit var newsMvvm: NewsViewModel
    private lateinit var binding: FragmentBreakingNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private var searchQuery: String? = null
    private lateinit var searchView: SearchView
    private lateinit var searchViewForResults: SearchView
    private lateinit var displayedList: ArrayList<Article>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initializing the respective variables.
        binding = FragmentBreakingNewsBinding.inflate(layoutInflater)
        newsMvvm = ViewModelProvider(this).get(NewsViewModel::class.java)
        newsAdapter = NewsAdapter(this)
        setHasOptionsMenu(true)
        // getting the search query when the orientation changes.
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString("search_query")!!
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareNewsRecyclerView()
        // to avoid the change of data when the orientation changes, if the request was made then the data is already stored in the variable, we just need to observe the data and display it in recycler view

        if (newsMvvm.observeNewsResponseLiveData().value == null) {
            newsMvvm.getBreakingNews()
        }
        observeBreakingNews()
        setUpSearchView()
    }

    // To save the text of the search box when the orientation changes.
    override fun onSaveInstanceState(outState: Bundle) {
        if (isAdded) {
            val searchText: String = searchView.query.toString()
            if (!TextUtils.isEmpty(searchText)) outState.putString(
                "search_text",
                searchText
            )
        }
        super.onSaveInstanceState(outState)
    }

    /**
     * This function sets up the recycler view by assigning the properties and the adapter.
     */
    private fun prepareNewsRecyclerView() {
        val divider = DividerItemDecoration(
            binding.rvBreakingNews.context, DividerItemDecoration.VERTICAL
        )
        divider.setDrawable(
            ContextCompat.getDrawable(
                binding.rvBreakingNews.context,
                R.drawable.divider
            )!!
        )

        binding.rvBreakingNews.addItemDecoration(divider)
        binding.rvBreakingNews.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter
        }
    }

    /**
     * Sets up the search view and performs the respective calls when the query is made.
     */
    private fun setUpSearchView() {
        searchViewForResults = binding.svNewsSearch
        searchViewForResults.clearFocus()
        searchViewForResults.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // once submitted make the respective call.
                filterList(query)
                searchViewForResults.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.search_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        if (!TextUtils.isEmpty(searchQuery))
            searchView.setQuery(searchQuery, false)

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                if (newsMvvm.observeNewsResponseLiveData().value == null) {
                    newsMvvm.getBreakingNews()
                }
                observeBreakingNews()
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchQuery = query
                    binding.rvBreakingNews.scrollToPosition(0)
                    searchNews(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotBlank() && newText.isNotEmpty()) {
//                    binding.rvBreakingNews.scrollToPosition(0)
//                    searchNews(newText)
//                    searchView.clearFocus()
                }
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Searches news according to the query
     */
    private fun searchNews(query: String?) {
        val searchQuery = "%$query%"
        newsMvvm.searchNews(searchQuery)
        observeSearchedBreakingNews()
    }


    /**
     * Observer for the fragment and activity. Observes the data fetched from the api with top headlines and then processes the data.
     */
    private fun observeBreakingNews() {
        newsMvvm.observeNewsResponseLiveData()
            .observe(
                viewLifecycleOwner
            ) { newsList ->
                validateNewsList(newsList)
            }

    }
    /**
     * Observer for the fragment and activity. Observes the data fetched from the api based on the user typed query and then processes the data.
     */
    private fun observeSearchedBreakingNews() {
        newsMvvm.observeNewsResponseSearchedLiveData()
            .observe(
                viewLifecycleOwner
            ) { newsList: List<Article>? ->
                validateNewsList(newsList)
            }
    }

    /**
     * Function to handle the error condition of the condition when the data is empty.
     */
    private fun validateNewsList(newsList: List<Article>?) {
        if (newsList.isNullOrEmpty()) {
            binding.rvBreakingNews.visibility = View.GONE
            binding.tvNoItemsToDisplay.visibility = View.VISIBLE
            binding.svNewsSearch.visibility = View.GONE
        } else {
            binding.rvBreakingNews.visibility = View.VISIBLE
            binding.tvNoItemsToDisplay.visibility = View.GONE
            binding.svNewsSearch.visibility = View.VISIBLE
            newsAdapter.differ.submitList(newsList)
            displayedList = ArrayList()
            displayedList = newsList as ArrayList<Article>
        }
    }

    /**
     * Matches the results from the already fetched data and then assigns the recycler view new list.
     * Searches for the query in description, content, title.
     */
    private fun filterList(text: String?) {
        val matchedResults: ArrayList<Article> = ArrayList()
        if (displayedList.size > 0) {
            for (article in displayedList) {
                var charSeq = article.title + article.content + article.description
                charSeq = Constants.convertToLowercase(charSeq)
                if (charSeq.contains(text!!)) {
                    matchedResults.add(article)
                }
            }
        }
        if (matchedResults.isEmpty()) {
            Toast.makeText(requireActivity(), "No matching data found", Toast.LENGTH_SHORT).show()
            newsAdapter.differ.submitList(matchedResults) // completely optional, as passes the empty list.
        } else {
            newsAdapter.differ.submitList(matchedResults)
        }
    }

    /**
     * This function is the listener for the recycler view item.
     * It changes the color when the item is clicked.
     */
    override fun onNewsClicked(
        newsArticle: Article,
        binding: ItemArticlePreviewBinding,
        position: Int,
    ) {
//        if (Constants.selectedItems.contains(newsArticle)) {
//            binding.root.background = ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_article_background)
//        } else {
//            binding.root.background = ContextCompat.getDrawable(requireActivity()   , R.drawable.selected_article_background)
//        }
//        if (Constants.selectedItems.contains(newsArticle)) {
//            Constants.selectedItems.remove(newsArticle)
//        } else {
//            Constants.selectedItems.add(newsArticle)
//        }
//        newsAdapter.notifyDataSetChanged()

        // logic for changing the color.
        newsArticle.isSelected = !newsArticle.isSelected
        binding.root.background = if (newsArticle.isSelected) {
            ContextCompat.getDrawable(requireActivity(), R.drawable.selected_article_background)
        } else {
            ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_article_background)
        }
        // notify that the data set has changed.
        newsAdapter.notifyDataSetChanged()
    }


}