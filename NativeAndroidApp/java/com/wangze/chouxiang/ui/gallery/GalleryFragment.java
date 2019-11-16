package com.wangze.chouxiang.ui.gallery;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.wangze.chouxiang.R;


import com.wangze.chouxiang.sjx.ShowRecognizeResultFragment;
import com.wangze.chouxiang.wangze.ImageViewFragment;

/**
 * 识别模块<code>fragment</code>，向用户提供打开相机/拍摄照片/发送人脸识别请求接口
 *
 * @author 王泽
 * @author 邵佳鑫
 * @implSpec 主要复用父类 {@link ImageViewFragment}的<code>openAlbum, takePicture, go</code>
 * 三个函数。该类仅实现setBitmapForIV()钩子函数，并实现权限请求。
 */
public class GalleryFragment extends ImageViewFragment
{

    private Button openAlbum, go, takePhoto;
    private ImageView showPhotoIv;

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    @Override
    public void setBitmapForIV()
    {
        // this bitmap is the member of ImageViewFragment
        if (bitmap == null)
            return;
        showPhotoIv.setImageBitmap(bitmap);
        go.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case IVF_REQ_ALBUM_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //判断若成功获取权限
                    openAlbum();
                } else
                    Toast.makeText(getContext(), "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
            default:
                Log.e("TAG", "onRequestPermissionsResult: error request code " + requestCode);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        showPhotoIv = root.findViewById(R.id.rf_showPhotoIv);
        openAlbum = root.findViewById(R.id.rec_open);
        takePhoto = root.findViewById(R.id.rec_make);
        go = root.findViewById(R.id.rec_go);

        openAlbum.setOnClickListener(v -> openAlbum());
        takePhoto.setOnClickListener(v -> takePicture());

        go.setOnClickListener(v -> getFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, ShowRecognizeResultFragment.getInstance(), "ShowRecognizeResultFragment")
                .addToBackStack("upload")
                .commit());

        getFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, GalleryFragment.this, "GalleryFragment")
                .commit();

        go.setEnabled(false);
        return root;
    }

    @Override
    public void onStart()
    {
        setBitmapForIV();
        super.onStart();
    }

}