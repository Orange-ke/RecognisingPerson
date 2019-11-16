package com.wangze.chouxiang.sjx;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.wangze.chouxiang.DrawerActivity;
import com.wangze.chouxiang.R;
import com.wangze.chouxiang.ui.gallery.GalleryFragment;
import com.wangze.chouxiang.wangze.ImageTools;

import java.util.List;

/**
 * 显示人脸识别结果的<code>fragment</code>，可一次显示多张人脸，并提供人脸标记，
 * 人脸上传和人脸详细信息功能
 *
 * @author 王泽
 */
public class ShowRecognizeResultFragment extends Fragment
{
    private ListView personsListView;
    private List<Person> personList;
    private RecognizeImageView showFaceIv;
    private int errorCode;
    private Bitmap bitmap;
    private static ShowRecognizeResultFragment instance = null;
    public enum BestFitSize
    {
        Width(800F),
        Height(1000F);

        private final float value;

        public float getValue() {return value;}

        BestFitSize(float v) { value = v;}

    }

    public void setPersonList(List<Person> personList)
    {
        this.personList = personList;
    }

    public RecognizeImageView getShowFaceIv() { return showFaceIv; }

    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }
    private ShowRecognizeResultFragment(){}
    public static ShowRecognizeResultFragment getInstance()
    {
        if (instance == null)
            instance = new ShowRecognizeResultFragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        System.out.println("创建ShowRecognizeResultFragment "+this);
        View root = inflater.inflate(R.layout.fragment_recognizeresult, container, false);
        personsListView = root.findViewById(R.id.rrf_personLv);
        showFaceIv = root.findViewById(R.id.rrf_showFaceIv);
        bitmap = ((GalleryFragment) getFragmentManager().findFragmentByTag("GalleryFragment")).getBitmap();
        return root;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        bitmap = ImageTools.compress(bitmap);
        showFaceIv.setBitmap(bitmap);
        String res = ((DrawerActivity)getActivity()).getRecognizeResult();
        // 当从DetailInformationFragment返回时，不再需要发送请求，因此
        // 如果res为null，说明为当前bitmap的第一次访问，因此需要发送请求
        // 如果不为空，说明是从DetailInformationFragment返回，不需要再发送一遍请求，app响应速度更快
        if (res == null)
        {
            Thread toServer = new Thread(() -> AccessBaiduManager.sendPhotoToMultiRecognize(getActivity(), bitmap));
            toServer.start();
            try
            {
                toServer.join();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        // 不论res是否为空都需要再次获取一遍
        res = ((DrawerActivity)getActivity()).getRecognizeResult();
        AccessBaiduManager.handleResponseOfSearch(res, this);
        if (errorCode == AccessBaiduManager.CODE_UNKNOWN)
        {
            new AlertDialog.Builder(getContext())
                    .setTitle("识别")
                    .setMessage("未识别到人脸")
                    .setNegativeButton("确定", (dialog, which) -> getFragmentManager().popBackStack())
                    .show();
            return;
        }
        if (errorCode == AccessBaiduManager.CODE_SUCCESS)
        {
            PersonAdapter personAdapter = new PersonAdapter(getContext(), R.layout.person_item, personList);
            personAdapter.setFaceBitmap(bitmap);
            personsListView.setAdapter(personAdapter);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // 完成一次识别后将识别结果清空
        ((DrawerActivity)getActivity()).setRecognizeResult(null);
    }
}
