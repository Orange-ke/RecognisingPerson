package com.wangze.chouxiang.wangze;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.wangze.chouxiang.sjx.ShowRecognizeResultFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * 图片工具类，包括尺寸压缩/质量压缩/照相机预览方向修正
 *
 * @author 王泽
 *
 * */
public class ImageTools
{
    /**
     * 将图片进行长宽比固定压缩
     *
     * @param bitmap 要压缩的图片
     */
    public static Bitmap compress(Bitmap bitmap)
    {
        Matrix scalar;
        if (bitmap.getWidth() > ShowRecognizeResultFragment.BestFitSize.Width.getValue())
        {
            scalar = scaleFitForWidth(bitmap);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scalar, true);
        }
        if (bitmap.getHeight() > ShowRecognizeResultFragment.BestFitSize.Height.getValue())
        {
            scalar = scaleFitForHeight(bitmap);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scalar, true);
        }
        return bitmap;
    }


    /**
     * 图片进行适应最佳宽度压缩
     *
     * @param bitmap 要压缩的图片
     * @return 变换矩阵
     *
     * */
    public static Matrix scaleFitForWidth(Bitmap bitmap)
    {
        float bestWidth = ShowRecognizeResultFragment.BestFitSize.Width.getValue();
        float bestHeight = bestWidth * ((float) bitmap.getHeight() / (float) bitmap.getWidth());
        Matrix scalar = new Matrix();
        scalar.postScale(bestHeight / (float) bitmap.getHeight(), bestWidth / (float) bitmap.getWidth());
        return scalar;
    }

    /**
     * 图片进行适应最佳高度压缩
     *
     * @param bitmap 要压缩的图片
     * @return 变换矩阵
     *
     * */
    public static Matrix scaleFitForHeight(Bitmap bitmap)
    {
        float bestHeight = ShowRecognizeResultFragment.BestFitSize.Height.getValue();
        float bestWidth = bestHeight * ((float) bitmap.getWidth() / (float) bitmap.getHeight());
        Matrix scalar = new Matrix();
        scalar.postScale(bestHeight / (float) bitmap.getHeight(), bestWidth / (float) bitmap.getWidth());
        return scalar;
    }

    /**
     * 图片按照角度进行旋转
     *
     * @param angle 旋转角度
     * @param source 租用对象
     * @return 变换结果
     * */
    public static Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /**
     * 图片预览方向适应拍摄方向
     *
     * @param path 图片路径
     * @param bitmap 图片
     * */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Bitmap adjustImageOrientation(String path, Bitmap bitmap)
    {
        try
        {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将bitmap转换为Base64格式的简单封装
     *
     * @param bitmap 要转换的图片
     * @return 转换后的字符串
     */
    public static String ChangeBitmapToBase64(Bitmap bitmap)
    {
        String result = null;
        ByteArrayOutputStream baos = null;
        try
        {
            if (bitmap != null)
            {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (baos != null)
                {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;

    }
}
