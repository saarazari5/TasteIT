package com.example.tasteit_alpha.ui.Details;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum;
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.OpeningHours;
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.Photo;
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.PlaceDetails;
import com.example.tasteit_alpha.R;
import com.example.tasteit_alpha.Utils.AppUtils;
import com.example.tasteit_alpha.ui.Adapters.PhotosRvAdapter;
import com.example.tasteit_alpha.ui.Dialogs.WebViewBottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailsFragment extends Fragment {
    @SuppressWarnings("FieldCanBeLocal")
    private DetailsViewModel detailsViewModel;
    private PlaceDetails placeDetails;
   private final MutableLiveData<Photo>mSelectedPlacePhoto = new MutableLiveData<>();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.details_fragment, container, false);
    }



    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       detailsViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);



        ImageView img=view.findViewById(R.id.selected_img);
        Bundle args = getArguments();
        if (args == null) return;
        ImageDatum imageDatum = args.getParcelable(AppUtils.IMAGE_ARGS);
        if (imageDatum != null) {
            if(imageDatum.getRelatedPlaceDetails()==null){
                fetchDataAndObserve(imageDatum);
            }else {
                placeDetails=imageDatum.getRelatedPlaceDetails();
                Glide.with(getContext()).load(Uri.parse(imageDatum.getUriString())).optionalFitCenter().into(img);
                initWithPlaceDetails(placeDetails , view);
            }
        } else {
            Photo imagePhoto = args.getParcelable(AppUtils.PHOTO_ARGS);
            if(imagePhoto ==null)return;
            if(imagePhoto.getRelatedPlaceDetails()==null)return;
            placeDetails=imagePhoto.getRelatedPlaceDetails();
            Glide.with(getContext()).load(imagePhoto.getFullReference()).optionalFitCenter().into(img);
            initWithPlaceDetails(placeDetails, view);
        }

        addClickListenersForBar();
    }

    private void addClickListenersForBar() {
        requireView().findViewById(R.id.tv_map).setOnClickListener(view -> initBottomSheetWebView(placeDetails.getUrl()));
        requireView().findViewById(R.id.img_map).setOnClickListener(view -> initBottomSheetWebView(placeDetails.getUrl()));

    }

    private void initBottomSheetWebView(String url) {
        if(url.isEmpty() || url==null){
            Toast.makeText(getContext(),"no website is linked with this page sorry ",Toast.LENGTH_SHORT).show();
            return;
        }
        WebViewBottomSheetDialog webViewBottomSheetDialog = new WebViewBottomSheetDialog();
        Bundle bundle =new Bundle();
        bundle.putString(AppUtils.LINK_TAG , placeDetails.getUrl());
        webViewBottomSheetDialog.setArguments(bundle);
        webViewBottomSheetDialog.show(getChildFragmentManager() , "web_viewBottomSheet");
    }

    private void fetchDataAndObserve(ImageDatum imageDatum) {

        detailsViewModel.fetchData(imageDatum.getAddress());

        detailsViewModel.getMPlaceDetails().observe(getViewLifecycleOwner(), placeDetails -> {
            if(placeDetails==null) return;
            this.placeDetails=placeDetails;
            initWithPlaceDetails(placeDetails,requireView());
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        mSelectedPlacePhoto.observe(getViewLifecycleOwner(), placePhoto -> {
            if(placePhoto == null){ return; }
            Bundle args = new Bundle();
            args.putParcelable(AppUtils.PHOTO_ARGS , placePhoto);
            NavHostFragment.findNavController(this).navigate(R.id.action_detailsFragment_self2,args);
            mSelectedPlacePhoto.setValue(null);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void disableScrolling() {
        //todo: disable the scrolling
       NestedScrollView lockAbleScrollView = getView().findViewById(R.id.lockable_scrollView);
       lockAbleScrollView.setOnTouchListener((view, motionEvent) -> true);
    }

    private void initWithPlaceDetails(PlaceDetails placeDetails, View view) {
        TextView textPlaceName=view.findViewById(R.id.tv_placeName);
        if(!initPlaceName(placeDetails.getName(),textPlaceName)){
            textPlaceName.setSelected(true);
            textPlaceName.setText(placeDetails.getFormattedAddress());
        }
        TextView textPlaceAddress=view.findViewById(R.id.tv_placeAddress);
        initPlaceAddress(placeDetails.getFormattedAddress(),textPlaceAddress);
        initContactListener(placeDetails.getInternationalPhoneNumber(),view.findViewById(R.id.btn_contact));
        String webLink=placeDetails.getWebsite();
        initWebLink(webLink,view);
        TextView tvPrice=view.findViewById(R.id.tv_priceLevel);
        initPriceLevel(placeDetails.getPriceLevel(),tvPrice);
        initOpeningHours(placeDetails.getOpeningHours(),view);
        initIcon(view.findViewById(R.id.fab_icon),placeDetails.getIcon());
        initRating(view.findViewById(R.id.rating),placeDetails.getRating());
        List<Photo> photos =placeDetails.getPhotos();
        if (photos ==null || photos.size()==0)return;
        initPhotos(photos,view.findViewById(R.id.rv_place_photos));
    }

    private void initPhotos(List<Photo> photos, RecyclerView rvPhotos) {
        if (photos.isEmpty()){
            disableScrolling();
            return;
        }
        rvPhotos.setAdapter(new PhotosRvAdapter(photos, mSelectedPlacePhoto,getContext(),false));
        rvPhotos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void initRating(View ratingBtn, Double rating) {
        if (rating==null){
            ratingBtn.setVisibility(View.INVISIBLE);
            return;
        }
        ((Button)ratingBtn).setText(String.valueOf(rating));
    }

    private void initIcon(View fabIcon, String icon) {
        if(icon==null)return;
        Picasso.get().load(icon).into(((FloatingActionButton) fabIcon));
    }

    private void initOpeningHours(OpeningHours openingHours, View view) {
        if (openingHours==null)return;
        TextView isOpen=view.findViewById(R.id.tvIsOpen);
        if(!openingHours.getOpenNow()){
           isOpen.setText(R.string.Close);
           isOpen.setTextColor(Color.RED);
        }
    }

    private void initPriceLevel(Integer priceLevel, TextView tvPrice) {
        if(priceLevel!=null){
            tvPrice.setText(String.valueOf(priceLevel));
           switch (priceLevel){
               case 4: tvPrice.setTextColor(Color.RED);
               case 5:tvPrice.setTextColor(Color.RED);
               default:tvPrice.setTextColor(Color.YELLOW);
           }
        }
    }

    private void initWebLink(String webLink, View view) {
        ImageView webImg=view.findViewById(R.id.img_web);
        webImg.setOnClickListener(view1 -> navigateToWebLink(webLink));
        TextView website=view.findViewById(R.id.tv_website);
        website.setOnClickListener(textView -> navigateToWebLink(webLink));
    }

    private void navigateToWebLink(String webLink) {
        if(webLink==null){
            Toast.makeText(getContext(),"no website is linked with this page sorry ",Toast.LENGTH_SHORT).show();
        }else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webLink));
            startActivity(browserIntent);
        }
    }

    private void initContactListener(String formattedPhoneNumber, View contactBtn) {
        contactBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+formattedPhoneNumber));
            startActivity(intent);
        });
    }

    private void initPlaceAddress(String formattedAddress, TextView textPlaceAddress) {
        if(formattedAddress==null||formattedAddress.equals("")){
            textPlaceAddress.setVisibility(View.INVISIBLE);
        }else {
           textPlaceAddress.setText(formattedAddress);
        }
    }

    private boolean initPlaceName(String name, TextView placeName) {
        if(name==null||name.equals(""))return false;
        placeName.setText(name);
        return true;
    }


}

