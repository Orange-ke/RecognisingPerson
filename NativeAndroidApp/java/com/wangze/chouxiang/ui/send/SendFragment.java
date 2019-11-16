package com.wangze.chouxiang.ui.send;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import androidx.annotation.NonNull;


import com.wangze.chouxiang.DrawerActivity;
import com.wangze.chouxiang.R;
import com.wangze.chouxiang.sjx.AccessBaiduManager;
import com.wangze.chouxiang.sjx.Person;

import com.wangze.chouxiang.sjx.ShowRecognizeResultFragment;
import com.wangze.chouxiang.wangze.ImageViewFragment;
import com.wangze.chouxiang.wangze.UploadInfoDialog;

import java.util.List;

/**
 * 提供上传人脸信息到服务器的功能实现，类似于 {@link com.wangze.chouxiang.ui.gallery.GalleryFragment}，
 * 主要复用父类 {@link ImageViewFragment}的打开相册/打开相机等功能.
 *
 * @author 王泽
 * @author 邵佳鑫
 */
public class SendFragment extends ImageViewFragment
{
    /* ----------- View Item --------------*/
    private Button takePictureBtn;
    private Button openAlbum;
    private Button confirm;
    private ImageView showPhotoIv;
    private EditText userName;
    private EditText userInfo;

    /* ----------- List View for All person ----------- */
    private List<Person> personList;
    private int errorCode;


    public void setPersonList(List<Person> personList)
    {
        this.personList = personList;
    }

    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }


    @Override
    public void setBitmapForIV()
    {
        if (bitmap != null)
        {
            showPhotoIv.setImageBitmap(bitmap);
            confirm.setEnabled(true);
        }
    }

    private boolean checkField() throws IllegalArgumentException
    {
        // TODO may not to throw error
        if (userName.length() < 1 || userInfo.length() < 1 || bitmap == null)
        {
            new AlertDialog.Builder(getContext())
                    .setTitle("上传")
                    .setMessage("用户名/信息/图片不完整")
                    .setNegativeButton("返回", (v1, v2) -> getFragmentManager().popBackStack())
                    .show();
            return false;
        }
        return true;
    }


    private void uploadHelper(Person person, Bitmap face, String uname, String uinform)
    {
        if (person.getScore() >= AccessBaiduManager.minScore)
        {
            new UploadInfoDialog.Builder(getContext())
                    .setFaceIV(face)
                    .setTitle("上传")
                    .setMessage(person.getName() + " 的脸部信息已存在")
                    .setNegBtn("返回", (v) ->
                    {})
                    .create()
                    .show();
        } else
        {
            Thread upLoad = new Thread(() -> AccessBaiduManager.uploadFaceInform(face, uname, uinform));
            upLoad.start();
            try
            {
                upLoad.join();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            new AlertDialog.Builder(getContext())
                    .setTitle("上传")
                    .setMessage("成功!")
                    .setPositiveButton("确定", (dialogInterface, i) ->
                    { })
                    .create()
                    .show();
        }
    }

    /**
     * 将用户填写的字段以及选择的图片一同上传至服务器，供查询使用
     */
    private void go()
    {
        if (!checkField()) return;
        final String uname = userName.getText().toString();
        final String uinform = userInfo.getText().toString();

        Thread toServer = new Thread(() -> AccessBaiduManager.sendPhotoToMultiRecognize(getActivity(), bitmap));

        toServer.start();
        try
        {
            toServer.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        String result = ((DrawerActivity) getActivity()).getRecognizeResult();
        AccessBaiduManager.handleResponseOfSearch(result, SendFragment.this);
        if (errorCode == AccessBaiduManager.CODE_SUCCESS)
        {
            if (personList.size() != 1)
            {
                Person person = personList.get(personList.size() - 1);
                final Bitmap theFace = Bitmap.createBitmap(bitmap, person.getLeft(), person.getTop(), person.getWidth(), person.getHeight());

                if (person.getScore() >= AccessBaiduManager.minScore)
                    uploadHelper(person, theFace, uname, uinform);
                else
                    new UploadInfoDialog.Builder(getContext())
                            .setFaceIV(theFace)
                            .setTitle("上传")
                            .setMessage("识别到多张人脸,仅选取得分最低(与库中相似程度最低的)的人脸, 相似度=" + person.getScore())
                            .setPosBtn("上传", v -> uploadHelper(person, theFace, uname, uinform))
                            .setNegBtn("取消", v ->
                            {})
                            .create()
                            .show();
            } else
            {
                Person person = personList.get(0);
                Bitmap theFace = Bitmap.createBitmap(bitmap, person.getLeft(), person.getTop(), person.getWidth(), person.getHeight());
                uploadHelper(person, theFace, uname, uinform);
            }
            ((DrawerActivity) getActivity()).setRecognizeResult(null);
        } else
        {
            if (errorCode == AccessBaiduManager.CODE_UNKNOWN)
            {
                new AlertDialog.Builder(getContext())
                        .setTitle("上传")
                        .setMessage("未识别到人脸!")
                        .setPositiveButton("返回", (dialogInterface, i) ->
                        {
                        })
                        .create()
                        .show();
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        takePictureBtn = root.findViewById(R.id.up_take_picture);
        openAlbum = root.findViewById(R.id.up_open_album);
        confirm = root.findViewById(R.id.up_confirm);
        confirm.setEnabled(false);
        showPhotoIv = root.findViewById(R.id.rf_showPhotoIv);
        userName = root.findViewById(R.id.up_userName);
        userInfo = root.findViewById(R.id.up_email);

        takePictureBtn.setOnClickListener(v -> takePicture());
        openAlbum.setOnClickListener(v -> openAlbum());
        confirm.setOnClickListener(v -> go());

        getFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, SendFragment.this, "SendFragment")
                .commit();

        return root;
    }


    @Override
    public void onStart()
    {
        super.onStart();
        setBitmapForIV();
    }
}