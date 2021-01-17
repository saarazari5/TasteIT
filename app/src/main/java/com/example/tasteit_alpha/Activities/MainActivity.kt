  package com.example.tasteit_alpha.Activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.tasteit_alpha.R
import com.example.tasteit_alpha.Utils.AppUtils.*
import com.example.tasteit_alpha.ui.BaseFragment
import com.example.tasteit_alpha.ui.NonSwipingViewPager
import com.example.tasteit_alpha.ui.home.HomeFragment
import com.example.tasteit_alpha.ui.home.HomeFragment.*
import com.example.tasteit_alpha.ui.home.StateChosenObserver
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*


  class MainActivity : AppCompatActivity(),ViewPager.OnPageChangeListener,
    BottomNavigationView.OnNavigationItemSelectedListener , BottomNavigationView.OnNavigationItemReselectedListener , StateChosenObserver{


    // overall back stack of containers
    private val   backStack = Stack<Int>()
    // map of navigation_id to container index
     private val indexToPage = mapOf(0 to R.id.home, 1 to R.id.search, 2 to R.id.info)
    // list of base destination containers
      val fragments = listOf(
        BaseFragment.newInstance(R.layout.container_home, R.id.home_toolbar, R.id.nav_host_home),
        BaseFragment.newInstance(
            R.layout.container_search,
            R.id.search_toolbar,
            R.id.nav_host_search
        ),
        BaseFragment.newInstance(R.layout.container_info, R.id.info_toolbar, R.id.nav_host_info)
    )
     lateinit var mainVp: NonSwipingViewPager
     lateinit var  bottomNavView :BottomNavigationView



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //method for auth if not authorized will move to AuthActivity
        checkAuth()

        configureBottomNav()
        configureViewPager()

        // initialize backStack with home page index
        if (backStack.empty()) backStack.push(0)



    }

    private fun configureBottomNav() {
        bottomNavView = findViewById(R.id.nav_view)
        bottomNavView.setOnNavigationItemSelectedListener(this)
        bottomNavView.setOnNavigationItemReselectedListener(this)


    }

    fun hideBottomNav(){
        bottomNavView.animate().translationY(bottomNavView.height.toFloat())
    }

      fun showBottomNav(){
        bottomNavView.animate().translationY(0.01f)
    }


    fun startCamera() {
        if(isCameraAvailable(this)) {
            val intent= Intent(this, CameraXActivity::class.java)
            if(compareSDK(Build.VERSION_CODES.LOLLIPOP)){
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return
            }
            startActivity(intent)
        }
    }



    private fun configureViewPager() {
        mainVp = findViewById(R.id.main_view_pager)
        mainVp.adapter=ViewPagerAdapter()
        mainVp.addOnPageChangeListener(this)
        mainVp.post(this::checkDeepLink)
        mainVp.offscreenPageLimit = fragments.size
    }

    private fun checkDeepLink() {
        fragments.forEachIndexed { index, fragment ->
            val hasDeepLink = fragment.handleDeepLink(intent)
            if (hasDeepLink) setItem(index)
        }
    }

    override fun onBackPressed() {
        // get the current page
        val fragment = fragments[mainVp.currentItem]
        // check if the page navigates up
        val navigatedUp = fragment.onBackPressed()
        // if no fragments were popped
        if (!navigatedUp) {
            if (backStack.size > 1) {
                // remove current position from stack
                backStack.pop()
                // set the next item in stack as current
                mainVp.currentItem = backStack.peek()
            }
        }
    }


    private fun checkAuth() {
        //observer for user auth state changes (if the user logged off)
        FirebaseAuth.getInstance().addAuthStateListener {
            //get the data for current user
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                val intent = Intent(this@MainActivity, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } 
        }
    }



    /// OnPageSelected Listener Implementation
    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
    override fun onPageSelected(page: Int) {
        val itemId = indexToPage[page] ?: R.id.home
        if (bottomNavView.selectedItemId != itemId)
            bottomNavView.selectedItemId = itemId
    }

    /// BottomNavigationView ItemSelected Implementation
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val position = indexToPage.values.indexOf(item.itemId)
        if (mainVp.currentItem != position) setItem(position)
        return true
    }


    private fun setItem(position: Int) {
        mainVp.currentItem = position
        backStack.push(position)
    }


    override fun onNavigationItemReselected(item: MenuItem) {
        val position = indexToPage.values.indexOf(item.itemId)
        val fragment = fragments[position]
        fragment.popToRoot()
    }





    inner class ViewPagerAdapter : FragmentPagerAdapter(
        supportFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getCount(): Int = fragments.size
        override fun getItem(position: Int): Fragment = fragments[position]

    }

      override fun onChose(queryState: QueryStates, distancePreference: Int) {
         val homeFragment : HomeFragment= fragments[0].childFragmentManager.fragments[0].childFragmentManager.fragments[0] as HomeFragment
          homeFragment.onStateChosen(queryState , distancePreference)
      }


  }


