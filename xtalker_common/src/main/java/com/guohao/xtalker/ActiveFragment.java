package com.guohao.xtalker;

import android.util.Log;
import android.view.View;

import com.guohao.common.app.Fragment;
import com.guohao.common.widget.GalleryView;

public class ActiveFragment extends Fragment implements GalleryView.SelectedChangeListener {


    public ActiveFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
        //return R.layout.fragment_gallery;
    }

    GalleryView mGallery;
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        //inflater.inflate(R.layout.fragment_gallery, frameLayout, true);
        //mGallery = root.findViewById(R.id.galleryView);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        mGallery.setup(getLoaderManager(), this);
//    }
//
    @Override
    public void onSelectedCountChanged(int count) {
        Log.d("guohaox","onSelectedCountChanged");
        String[] paths = mGallery.getSelectedPath();
        if (count > 0)
            Log.d("guohaox","paths = " + paths[0]);
    }
}
