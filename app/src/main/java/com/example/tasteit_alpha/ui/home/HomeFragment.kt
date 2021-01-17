package com.example.tasteit_alpha.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
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
import com.example.tasteit_alpha.Services.LocationService
import com.example.tasteit_alpha.Utils.AppUtils.*
import com.example.tasteit_alpha.Utils.SharedPreferencesUtils
import com.example.tasteit_alpha.ui.Adapters.ImageDataAdapter
import com.example.tasteit_alpha.ui.Adapters.PhotosRvAdapter
import com.example.tasteit_alpha.ui.Dialogs.SearchSettingBottomSheetDialog
import com.example.tasteit_alpha.ui.ImagesType
import com.example.tasteit_alpha.ui.home.HomeFragment.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.appbar.AppBarLayout
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import java.util.*



@Suppress("UNCHECKED_CAST")
class HomeFragment : Fragment() , AppBarLayout.OnOffsetChangedListener   {

    private val settingPreferenceFileName = "tasteIt.setting_Preference"
    private var distance :Int = 10
    private lateinit var homeRecycler : RecyclerView
    private lateinit var progressBar: ProgressBar
    private var locality: String? = null
    private var currentUserLong : Double = DEFAULT_LANG_LAT
    private var currentUserLat : Double = DEFAULT_LANG_LAT
    private var queryState : QueryStates = QueryStates.DISTANCE


    private val mReceiver : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val city = intent.getStringExtra(CITY_EXTRA)
            if(queryState==QueryStates.LOCALITY && city==locality && (homeRecycler.adapter!=null || homeRecycler.adapter?.itemCount!! > 0)) return
            currentUserLong = intent.getDoubleExtra(LONG_EXTRA, DEFAULT_LANG_LAT)
            currentUserLat = intent.getDoubleExtra(LAT_EXTRA, DEFAULT_LANG_LAT)
            checkQueryStateAndFetchData()
        }
    }



    private lateinit var homeViewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val instance=SharedPreferencesUtils.getSharedInstance(requireActivity(),settingPreferenceFileName)
        distance=instance.getInt(DISTANCE_TAG,30)
        val tempStateName = instance.getString(STATE_TAG,QueryStates.DISTANCE.name)
        queryState = if(tempStateName == QueryStates.DISTANCE.name){
            QueryStates.DISTANCE
        }else{
            QueryStates.LOCALITY
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeRecycler = view.findViewById(R.id.home_recycler)
        progressBar=view.findViewById(R.id.home_pb)

        val mSelectedPhoto = homeViewModel.selectedPhoto
        mSelectedPhoto.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            navigateToDetails(it, ImagesType.PLACESPHOTO)
            mSelectedPhoto.value = null
        })

        val mSelectedImage = homeViewModel.selectedImage
            mSelectedImage.observe(viewLifecycleOwner, Observer {
                if(it==null)return@Observer
                navigateToDetails(it, ImagesType.IMAGEDATUM)
                mSelectedImage.value = null
            })

        homeViewModel.mImages.observe(viewLifecycleOwner, Observer {
            progressBar.visibility=View.INVISIBLE

            if (it.isNullOrEmpty()) {
                Toast.makeText(context, "there are no results to your search please try again", Toast.LENGTH_LONG).show()
                return@Observer
            }
           homeRecycler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            if (it[0] is ImageDatum) {
                val imagesData: ArrayList<ImageDatum> = it as ArrayList<ImageDatum>
                homeRecycler.adapter = ImageDataAdapter(
                    imagesData,
                    DEFAULT_LANG_LAT,
                    DEFAULT_LANG_LAT,
                    context,
                    mSelectedImage
                )
            } else {
              homeRecycler.adapter = PhotosRvAdapter(
                    it as ArrayList<Photo>,
                    mSelectedPhoto,
                    context,
                    true
                )
                }
            })
        }




    private fun checkQueryStateAndFetchData() {
    progressBar.visibility=View.VISIBLE
        when (queryState){
            QueryStates.DISTANCE->{
                homeViewModel.fetchDataByDistance(distance,currentUserLong , currentUserLat)
            }

            QueryStates.LOCALITY->{
                locality.let {
                    homeViewModel.fetchDataByLocality(it)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        initScrollListeners()
        getCurrentLocation()
        activity?.registerReceiver(mReceiver , IntentFilter(ACTION_GET_ADDRESS))
    }

    private fun initScrollListeners() {
        homeRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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

    override fun onPause() {
        super.onPause()
        homeRecycler.adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        homeRecycler.clearOnScrollListeners()
        activity?.unregisterReceiver(mReceiver)
    }


    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
             if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                 buildPermRequestDialog()
             }else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION) , RC_LOCATION)
             }
            return
        }

            if(!checkLocSetting(context)){
                showLocationDialog(this)
                return
            }

            SmartLocation.with(context).location().oneFix().start {
                if(it==null){
                    //todo handle with a null location by fetching last location from sp
                    return@start
                }
                val geoIntent = Intent(requireContext() , LocationService::class.java)
                geoIntent.putExtra(LOC_KEY, LatLng(it.latitude, it.longitude))
                requireActivity().startService(geoIntent)

            }

    }

    //permissions and location services methods
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
           getCurrentLocation()
        }
    }



    private fun buildPermRequestDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setMessage(R.string.request_dialog_message)
            .setPositiveButton("enable permission") { dialog, _ ->
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RC_LOCATION)
                dialog.cancel()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                Toast.makeText(context, "please give location permission to use this app", Toast.LENGTH_LONG).show()
                moveToSearch()
                dialog.cancel()
            }
        builder.show()
    }

    private fun moveToSearch() {
        //todo : replace this method with setEmptyViewWithErrorCapturing and a snack bar
        (requireActivity() as MainActivity).mainVp.setCurrentItem(1 , true)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationGooglePlayServicesProvider.REQUEST_CHECK_SETTINGS) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                Toast.makeText(context,
                    "please enable location service if you want to use the home feature",
                    Toast.LENGTH_LONG).show()
                moveToSearch()
            }else{
             getCurrentLocation()
            }
        }
    }



    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if(appBarLayout==null)return
        homeRecycler.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            topMargin=appBarLayout.height+verticalOffset

        }
    }

    fun showSearchSettings(){
        if (progressBar.isVisible){
            Toast.makeText(context,"please wait for data to load" , Toast.LENGTH_LONG).show()
            return
        }
        val settings = SearchSettingBottomSheetDialog()
        val bundle = Bundle()
        bundle.putString(STATE_TAG , queryState.name)
        bundle.putInt(DISTANCE_TAG , distance)
        settings.arguments=bundle
        settings.show(childFragmentManager , "search_settings")
    }


    enum class QueryStates  {
        DISTANCE , LOCALITY
    }

    fun onStateChosen(queryState: QueryStates, distancePreference: Int) {
        if(this.queryState == queryState){
            if(queryState== QueryStates.DISTANCE){
                distance=distancePreference
            }
            return
        }

        distance=distancePreference
        this.queryState=queryState
        getCurrentLocation()
    }


    override fun onStop() {
        super.onStop()
        saveDataToPreferences()
    }

    private fun saveDataToPreferences() {
        val instance = SharedPreferencesUtils.getSharedInstance(requireActivity() , settingPreferenceFileName)
        instance.putString(STATE_TAG , queryState.name)
        instance.putInt(DISTANCE_TAG, distance)
    }


    private fun navigateToDetails(data: PresentAble, type: ImagesType){
        val args = Bundle()
        when (type){
            ImagesType.IMAGEDATUM -> args.putParcelable(IMAGE_ARGS, data as ImageDatum)
            ImagesType.PLACESPHOTO -> args.putParcelable(PHOTO_ARGS, data as Photo)
        }
        findNavController().navigate(R.id.action_navigation_home_to_detailsFragment, args)
    }

}




interface StateChosenObserver{
    fun onChose(queryState: QueryStates, distancePreference : Int)
}



