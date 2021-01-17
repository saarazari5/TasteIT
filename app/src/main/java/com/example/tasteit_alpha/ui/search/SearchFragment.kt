package com.example.tasteit_alpha.ui.search

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.tasteit_alpha.Activities.MainActivity
import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum
import com.example.tasteit_alpha.Model.Data.DataClasses.PresentAble
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.Photo
import com.example.tasteit_alpha.R
import com.example.tasteit_alpha.Utils.AppUtils.*
import com.example.tasteit_alpha.ui.Adapters.ImageDataAdapter
import com.example.tasteit_alpha.ui.Adapters.PhotosRvAdapter
import com.example.tasteit_alpha.ui.ImagesType
import com.google.android.material.appbar.AppBarLayout


@Suppress("UNCHECKED_CAST")
class SearchFragment : Fragment() ,  AppBarLayout.OnOffsetChangedListener  {
     lateinit var searchRecycler: RecyclerView
    private lateinit var searchViewModel :SearchViewModel
    private lateinit var progressBar : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val searchQuery: String? = intent.getStringExtra(SEARCH_QUERY)
            if(searchQuery.isNullOrBlank()) {
                progressBar.visibility = View.INVISIBLE
                return
            }
            progressBar.visibility=View.VISIBLE
            searchViewModel.fetchDataQuery(searchQuery)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchRecycler=view.findViewById(R.id.search_recycler)
        progressBar=view.findViewById(R.id.progress_bar)
        observe()
    }


    private fun observe() {
        val mSelectedPhoto = searchViewModel.selectedPhoto
        mSelectedPhoto.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            navigateToDetails(it, ImagesType.PLACESPHOTO)
            mSelectedPhoto.value = null
        })

        val mSelectedImage = searchViewModel.selectedImage
        mSelectedImage.observe(viewLifecycleOwner, Observer {
            if(it==null)return@Observer
            navigateToDetails(it, ImagesType.IMAGEDATUM)
            mSelectedImage.value = null
        })

        searchViewModel.mImages.observe(viewLifecycleOwner, Observer {
            progressBar.visibility = View.INVISIBLE
            if (it.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "there are no results to your search please try again",
                    Toast.LENGTH_LONG
                ).show()
                return@Observer
            }
            searchRecycler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            if (it[0] is ImageDatum) {
                val imagesData: ArrayList<ImageDatum> = it as ArrayList<ImageDatum>
                searchRecycler.adapter = ImageDataAdapter(imagesData, DEFAULT_LANG_LAT, DEFAULT_LANG_LAT, context, mSelectedImage)
            } else {
                searchRecycler.adapter = PhotosRvAdapter(
                    it as ArrayList<Photo>,
                    mSelectedPhoto,
                    context,
                    true
                )
            }
        })
    }


    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(mReceiver, IntentFilter(ACTION_SEND_QUERY))
        initScrollListeners()
    }

    private fun initScrollListeners() {
        searchRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(newState==RecyclerView.SCROLL_STATE_IDLE) {
                    (activity as? MainActivity)?.showBottomNav()
                }else if(newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    (activity as? MainActivity)?.hideBottomNav()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {}
        })
    }


    private fun navigateToDetails(data: PresentAble, type: ImagesType){
        val args = Bundle()
        when (type){
            ImagesType.IMAGEDATUM -> args.putParcelable(IMAGE_ARGS, data as ImageDatum)
            ImagesType.PLACESPHOTO -> args.putParcelable(PHOTO_ARGS, data as Photo)
        }
        findNavController().navigate(R.id.action_navigation_search_to_detailsFragment, args)
    }


    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(mReceiver)
        searchRecycler.adapter?.stateRestorationPolicy=RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        searchRecycler.clearOnScrollListeners()
    }


    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if(appBarLayout==null)return
        searchRecycler.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            topMargin=appBarLayout.height+verticalOffset+50
        }
    }

}