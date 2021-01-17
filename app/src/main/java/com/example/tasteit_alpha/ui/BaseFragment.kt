package com.example.tasteit_alpha.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import com.example.tasteit_alpha.Activities.MainActivity
import com.example.tasteit_alpha.R
import com.example.tasteit_alpha.Utils.AppUtils
import com.example.tasteit_alpha.ui.home.HomeFragment
import com.example.tasteit_alpha.ui.search.SearchFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class BaseFragment: Fragment() {
    private lateinit var appBarLayout:AppBarLayout
    private lateinit var toolbar: Toolbar
    private val defaultInt = -1
    private var layoutRes: Int = -1
    private var toolbarId: Int = -1
    private var navHostId: Int = -1
    private val navController  by lazy {
        requireActivity().findNavController(navHostId)
    }

    // root destinations
    private val rootDestinations =
        setOf(R.id.home_dest, R.id.search_dest, R.id.navigation_info)
       // nav config with graph late init
       private val appBarConfig:AppBarConfiguration = AppBarConfiguration(rootDestinations)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // extract arguments from bundle
        arguments?.let {
            layoutRes = it.getInt(KEY_LAYOUT)
            toolbarId = it.getInt(KEY_TOOLBAR)
            navHostId = it.getInt(KEY_NAV_HOST)

        } ?: return
    }
    fun popToRoot() {
        // navigate to the start destination
        navController.popBackStack(
            navController.graph.startDestination, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return if (layoutRes == defaultInt) null
        else inflater.inflate(layoutRes, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(toolbarId==defaultInt)return
        toolbar=view.findViewById(toolbarId)
        initDataByID(toolbarId)

        configureToolbarListeners()
    }

    private fun initDataByID(toolbarId: Int) {
        val navHost = childFragmentManager.fragments[0] as? NavHostFragment ?: return
        val fragment = navHost.childFragmentManager.fragments[0]
        if(toolbarId==R.id.home_toolbar){
            val homeFragment = fragment as? HomeFragment ?: return
            appBarLayout=requireView().findViewById(R.id.home_appbar)
            appBarLayout.removeCallbacks {}
           appBarLayout.addOnOffsetChangedListener(homeFragment)
            initOnHomeToolBarItemsListeners(homeFragment)
        }else if(toolbarId==R.id.search_toolbar){
            appBarLayout=requireView().findViewById(R.id.search_appbar)
            appBarLayout.removeCallbacks {}
            appBarLayout.addOnOffsetChangedListener(fragment as SearchFragment)
            val searchMenuItem = toolbar.menu.getItem(0)
            setSearchViewQueryListeners(searchMenuItem.actionView as SearchView)
        }else{
            val userNameText = requireView().findViewById<TextView>(R.id.profile_name)
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return
            userNameText.text = currentUser.displayName ?: return
            Picasso.get().load(currentUser.photoUrl).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).fit().into(requireView().findViewById<ImageView>(R.id.user_img))

        }

    }

    private fun initOnHomeToolBarItemsListeners(homeFragment: HomeFragment) {
        val settingsImg = toolbar.findViewById<ImageView>(R.id.img_search_settings)
        settingsImg.setOnClickListener {
            homeFragment.showSearchSettings()
        }
    }

    private fun configureToolbarListeners() {
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.nav_camActivity -> startCamera()
                R.id.logout ->FirebaseAuth.getInstance().signOut()
            }
            true
        }
    }


    private fun setSearchViewQueryListeners(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchQuery: String): Boolean {
                closeKeyBoard()
                Intent().also{
                    it.action=AppUtils.ACTION_SEND_QUERY
                    it.putExtra(AppUtils.SEARCH_QUERY,searchQuery)
                    activity?.sendBroadcast(it)
                }
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })
    }

    private fun closeKeyBoard() {
        val view = activity?.currentFocus ?: return
        val imm = (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(view.windowToken,0)

    }


    private fun startCamera() { (activity as MainActivity).startCamera()}


    override fun onStart() {
        super.onStart()
        // return early if no arguments were parsed
        if (toolbarId == defaultInt || navHostId == defaultInt) return
        // setup navigation with root destinations and toolbar
        setupWithNavController(toolbar, navController, appBarConfig)
        configureNabControllerListener()
    }

    private fun configureNabControllerListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id==R.id.details_dest){
                appBarLayout.visibility =View.INVISIBLE
            }else if(destination.id == R.id.home_dest || destination.id==R.id.search_dest){
                appBarLayout.visibility=View.VISIBLE
            }else{
                toolbar.visibility=View.VISIBLE
            }
        }
    }

    companion object {
        private const val KEY_LAYOUT = "layout_key"
        private const val KEY_NAV_HOST = "nav_host_key"
        private const val KEY_TOOLBAR="toolbar_key"

        fun newInstance(layoutRes: Int, toolbarId: Int, navHostId: Int) = BaseFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_LAYOUT, layoutRes)
                putInt(KEY_NAV_HOST, navHostId)
                putInt(KEY_TOOLBAR, toolbarId)
            }
        }
    }

    fun handleDeepLink(intent: Intent): Boolean =
        requireActivity()
            .findNavController(navHostId)
            .handleDeepLink(intent)

    fun onBackPressed(): Boolean {
        return requireActivity()
            .findNavController(navHostId)
             .navigateUp(appBarConfig)
    }
}


enum class ImagesType{
    IMAGEDATUM , PLACESPHOTO
}

