package com.wangze.chouxiang.wangze;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.wangze.chouxiang.DrawerActivity;

import com.wangze.chouxiang.sjx.AccessBaiduManager;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static android.app.Activity.RESULT_OK;

/**
* 提供打开相机/打开相册读取资源功能，只供GalleryFragment和SendFragment继承。
*
* @author 王泽
* @author 邵佳鑫
*
* */
public abstract class ImageViewFragment extends Fragment
{
    private File outImg;
    public Bitmap bitmap;

    /**
    * 临时从相册或者相机中获取的照片的临时名
     * PM前缀源自邵佳鑫<code>PhotoManager</code>类自1.0之后被写入<code>ImageViewFragment</code>中
    * */
    public static final String PM_TEMP_IMAGE_FILENAME = "temp.jpg";

    /**
     * 安卓打开相机行为
     * PM前缀源自邵佳鑫<code>PhotoManager</code>类自1.0之后被写入<code>ImageViewFragment</code>中
     * */
    public static final String PM_OPEN_CAMERA_ACTION = "android.media.action.IMAGE_CAPTURE";
    /**
     * 安卓获取内容行为
     * PM前缀源自邵佳鑫<code>PhotoManager</code>类自1.0之后被写入<code>ImageViewFragment</code>中
     * */
    public static final String PM_OPEN_ALBUM_ACTION = "android.intent.action.GET_CONTENT";

    /**
     * <code>FileProvider</code>类名，应该被修改并去掉'wangze'前缀
     * PM前缀源自邵佳鑫<code>PhotoManager</code>类自1.0之后被写入<code>ImageViewFragment</code>中
     */
    public static final String PM_FILE_PROVIDER_AUTHORITY = "com.wangze.chouxiang.FileProvider"; // 'wangz' should be changed

    /**
     * <code>onActivityResult</code>的请求码，请求相机
     * IVF自1.0后由王泽加入
     * */
    public static final int IVF_REQ_TAKE_PICTURE = 1;

    /**
     * <code>onActivityResult</code>的请求码，请求相册
     * IVF自1.0后由王泽加入
     * */
    public static final int IVF_REQ_OPEN_ALBUM = 2;

    /**
     * <code>onActivityResult</code>的请求码，请求相册权限
     * IVF自1.0后由王泽加入
     * */
    public static final int IVF_REQ_ALBUM_PERMISSION = 3;




    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void handleTokenPicture()
    {
        Activity activity = getActivity();
        assert activity != null;

        File tempCachedImgOutFile = new File(activity.getExternalCacheDir(), PM_TEMP_IMAGE_FILENAME);
        Uri imgUri = FileProvider.getUriForFile(activity, PM_FILE_PROVIDER_AUTHORITY, tempCachedImgOutFile);
        try
        {
            bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imgUri));
            bitmap = ImageTools.adjustImageOrientation(tempCachedImgOutFile.getPath(),  bitmap);
            bitmap = ImageTools.compress(bitmap);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void handleChoosePhoto(Intent intent)
    {
        assert getActivity() != null;
        assert intent.getData() != null;

        byte[] fileBuff;
        Cursor cursor;
        Uri uri = intent.getData();
        ContentResolver contentResolver = getActivity().getContentResolver();
        cursor = contentResolver.query(uri, null, null, null, null);

        try (InputStream inputStream = contentResolver.openInputStream(uri))
        {
            fileBuff = convertToBytes(inputStream);
            bitmap = BitmapFactory.decodeByteArray(fileBuff, 0, fileBuff.length);
            bitmap = ImageTools.compress(bitmap);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        cursor.close();

    }

    private byte[] convertToBytes(InputStream inputStream) throws Exception
    {
        byte[] buf = new byte[1024];
        int len;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            while ((len = inputStream.read(buf)) > 0)
                out.write(buf, 0, len);
            return out.toByteArray();
        }
    }

    /**
     * 打开相册，获取图片资源，必要时获取读写权限
     * @implSpec 发起<code>Fragment.startActivityForResult</code>
     * */
    public void openAlbum()
    {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Activity activity = getActivity();
        assert activity != null;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), permissions, IVF_REQ_ALBUM_PERMISSION);
        } else
        {
            Intent intent = new Intent(PM_OPEN_ALBUM_ACTION);
            intent.setType("image/*");
            startActivityForResult(intent, IVF_REQ_OPEN_ALBUM);
        }
    }

    /**
     * 打开相机，获取拍摄图片
     * @implSpec 发起<code>Fragment.startActivityForResult</code>,会发生存取异常
     * @throws IllegalStateException
     * */
    public void takePicture() throws IllegalStateException
    {
        assert getActivity() != null;
        Activity activity = getActivity();
        File tempCachedImgOutFile = new File(activity.getExternalCacheDir(), PM_TEMP_IMAGE_FILENAME);
        try
        {
            boolean success = false;
            if (tempCachedImgOutFile.exists())
                success = tempCachedImgOutFile.delete() && tempCachedImgOutFile.createNewFile();
            else
                success = tempCachedImgOutFile.createNewFile();
            if (!success)
            {
                Log.e("storage", "ImageViewFragment.takePicture: file open error occur");
                throw new IllegalStateException("ImageViewFragment.takePicture: file open error occur");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        Uri imgUri = FileProvider.getUriForFile(activity, PM_FILE_PROVIDER_AUTHORITY, tempCachedImgOutFile);
        Intent intent = new Intent(PM_OPEN_CAMERA_ACTION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, IVF_REQ_TAKE_PICTURE);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
        {
            return;
        }
        assert data != null;
        switch (requestCode)
        {
            case IVF_REQ_TAKE_PICTURE:
                handleTokenPicture();
                break;
            case IVF_REQ_OPEN_ALBUM:
                handleChoosePhoto(data);
                break;
            // other case may delay to subclass's onActivityResult,
            // so there not need to define the default behave.
        }
    }

    /**
     * 钩子函数，子类用来设置布局中的<code>ImageView</code>
     * */
    // this is a hook for subclass to set its showImageIV field
    public abstract void setBitmapForIV();


    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public void setOutImg(File outImg)
    {
        this.outImg = outImg;
    }
}
