package com.wangze.chouxiang.wangze;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wangze.chouxiang.R;

/**
 * 用于上传信息时，提示给用户三种可能的提示信息的Dialog
 *
 * @author 王泽
 * @since 1.0
 *
 * */
public class UploadInfoDialog extends Dialog
{
    private UploadInfoDialog(Context context, int themeResId)
    {
        super(context, themeResId);
    }

    public static class Builder
    {
        private ImageView faceIV;
        private UploadInfoDialog mDialog;
        private View mLayout;

        private TextView mTitle;
        private TextView mMessage;
        private Button mButtonPos;
        private Button mButtonNeg;

        public Builder(Context context)
        {
            mDialog = new UploadInfoDialog(context, R.style.UploadInfoDialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLayout = inflater.inflate(R.layout.upload_info_dialog, null, false);
            mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            faceIV = mDialog.findViewById(R.id.upload_dialog_image);
            mTitle = mDialog.findViewById(R.id.upload_dialog_title);
            mMessage = mDialog.findViewById(R.id.upload_dialog_msg);
            mButtonPos = mDialog.findViewById(R.id.upload_dialog_btn_pos);
            mButtonNeg = mDialog.findViewById(R.id.upload_dialog_btn_neg);
        }

        public UploadInfoDialog create()
        {
            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            return mDialog;
        }

        public Builder setPosBtn(@NonNull String text, View.OnClickListener listener)
        {
            mButtonPos.setText(text);
            mButtonPos.setOnClickListener(listener);
            return this;
        }

        public Builder setNegBtn(@NonNull String text, View.OnClickListener listener)
        {
            mButtonNeg.setText(text);
            mButtonNeg.setOnClickListener(v ->
            {
                listener.onClick(v);
                mDialog.dismiss();
            });
            return this;
        }

        public Builder setTitle(String mTitle)
        {
            this.mTitle.setText(mTitle);
            return this;
        }

        public Builder setMessage(String mMessage)
        {
            this.mMessage.setText(mMessage);
            return this;
        }

        public Builder setFaceIV(Bitmap faceIV)
        {
            this.faceIV.setImageBitmap(faceIV);
            return this;
        }
    }
}

