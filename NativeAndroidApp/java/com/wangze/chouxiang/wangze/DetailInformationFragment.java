package com.wangze.chouxiang.wangze;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangze.chouxiang.DrawerActivity;
import com.wangze.chouxiang.R;
import com.wangze.chouxiang.sjx.AccessBaiduManager;
import com.wangze.chouxiang.sjx.Person;

import java.util.function.Function;

/**
 * 用于为提供用户在{@link com.wangze.chouxiang.sjx.ShowRecognizeResultFragment}中选择的
 * 人脸的更详细的信息.
 *
 * @author 王泽
 * @since 1.0
 */
public class DetailInformationFragment extends Fragment
{
    private Bitmap bitmap;
    private Person theUserFace;
    private TextView age, beauty, emo, userName, userInfo;
    private ImageView showFaceIv;

    public DetailInformationFragment()
    {
    }


    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

    public void setTheUserFace(Person theUserFace) { this.theUserFace = theUserFace; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_detect_detail, container, false);
        age = root.findViewById(R.id.rrdf_user_age);
        beauty = root.findViewById(R.id.rrdf_user_beauty);
        emo = root.findViewById(R.id.rrdf_user_emotion);
        userName = root.findViewById(R.id.rrdf_user_name);
        userInfo = root.findViewById(R.id.rrdf_user_info);
        showFaceIv = root.findViewById(R.id.rrdf_showFaceIv);
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart()
    {
        super.onStart();
        Thread thread = new Thread(() -> AccessBaiduManager.sendFaceForMoreInformation(getActivity(), bitmap));
        thread.start();
        try
        {
            thread.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        String res = ((DrawerActivity) getActivity()).getRecognizeDetailResult();
        AccessBaiduManager.handleResponseOfDetect(res, this, theUserFace);

        userInfo.setText(theUserFace.getInfo());
        userName.setText(theUserFace.getName());
        age.setText(String.valueOf(theUserFace.getAge()));
        beauty.setText(String.valueOf(theUserFace.getBeauty()));
        emo.setText(theUserFace.getEmotion());
        showFaceIv.setImageBitmap(bitmap);
    }

}
