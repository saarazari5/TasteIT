package com.example.tasteit_alpha.ui.info

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasteit_alpha.R
import com.example.tasteit_alpha.ui.Adapters.InfoAdapter

class MyPhotosFragment : Fragment() {

    private lateinit var myPhotosRecycler:RecyclerView
    private lateinit var viewModel: MyPhotosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_photos_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyPhotosViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myPhotosRecycler=view.findViewById(R.id.my_photos_recycler)
        myPhotosRecycler.isNestedScrollingEnabled=false
    }

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        viewModel.mImages.observe(viewLifecycleOwner, Observer {
            if(it.isNullOrEmpty())return@Observer
            myPhotosRecycler.layoutManager=
                GridLayoutManager(context , 2 , GridLayoutManager.VERTICAL,true)
           myPhotosRecycler.adapter= InfoAdapter(it)
        })
    }
}