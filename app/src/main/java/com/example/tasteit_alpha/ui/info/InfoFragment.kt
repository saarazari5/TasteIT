package com.example.tasteit_alpha.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.tasteit_alpha.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class InfoFragment : Fragment() {

    private  val fragment = listOf(LikedFragment(), MyPhotosFragment())
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { return inflater.inflate(R.layout.fragment_infos, container, false) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment = parentFragment as NavHostFragment?
        val tabLayout : TabLayout = navHostFragment!!.requireParentFragment().requireView().findViewById(R.id.tabLayout)
        val viewPager2 : ViewPager2 = view.findViewById(R.id.info_viewpager2)
        viewPager2.adapter=InfoStateAdapter()
        TabLayoutMediator(
            tabLayout, viewPager2
        ) { tab, position ->
            when(position){
                0 -> tab.setIcon(R.drawable.icons8like)
                1 -> tab.setIcon(R.drawable.icons8folder)
            }
        }.attach()
    }





    inner class InfoStateAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = fragment.size

        override fun createFragment(position: Int): Fragment = fragment[position]
    }


}

