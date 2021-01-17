package com.example.tasteit_alpha.ui.info

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasteit_alpha.R
import com.example.tasteit_alpha.ui.Adapters.InfoAdapter

class LikedFragment : Fragment() {

    private lateinit var viewModel: LikedViewModel
    private lateinit var likedRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.liked_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        likedRecyclerView = view.findViewById(R.id.liked_recycler)
        likedRecyclerView.isNestedScrollingEnabled=false

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LikedViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        viewModel.mImages.observe(viewLifecycleOwner, Observer {
            if(it.isNullOrEmpty())return@Observer
            likedRecyclerView.layoutManager=GridLayoutManager(context , 2 , GridLayoutManager.VERTICAL,true)
            likedRecyclerView.adapter=InfoAdapter(it)
        })
    }
}