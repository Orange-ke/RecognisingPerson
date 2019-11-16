package com.wangze.chouxiang.sjx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.wangze.chouxiang.wangze.CoordinateInformation;

import java.util.List;

/**
 * 自定义ImageView类，用于添加将识别的人脸框住的功能
 *
 * @author 王泽
 * */
public class RecognizeImageView extends ImageView
{

    private List<Person> personList;
    private Paint maLang = new Paint();
    private Bitmap bitmap;
    private Rect mSrcRect, mDestRect;

    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

    public void setPersonList(List<Person> personList) { this.personList = personList; }

    public RecognizeImageView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        setImageBitmap(bitmap);
        mSrcRect = new Rect(0, 0, getWidth(), getHeight());
        mDestRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawBitmap(bitmap, mSrcRect, mDestRect, maLang);
        if (personList == null)
            return;


        for (Person user : personList)
        {
            CoordinateInformation cf = CoordinateInformation.Of(user);
            maLang.setColor(Color.BLACK);
            maLang.setTextSize(70);
            maLang.setStyle(Paint.Style.FILL);
            canvas.drawText(String.valueOf(user.getId()), cf.getLeft(), cf.getTop(), maLang);

            RectF faceRect = new RectF(cf.getLeft(), cf.getTop(), cf.getRight(), cf.getBottom());
            Matrix matrix = new Matrix();
            matrix.postRotate(cf.getAngle(), cf.getLeft(), cf.getTop());
            matrix.mapRect(faceRect);

            maLang.setColor(Color.WHITE);
            maLang.setStyle(Paint.Style.STROKE);
            maLang.setStrokeWidth(2);
            canvas.drawRect(faceRect, maLang);

        }
    }

}
