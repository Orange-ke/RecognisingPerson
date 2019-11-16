package com.wangze.lab.reclist;

import com.wangze.lab.R;


/*
*
*   首页功能按钮静态工厂类，所有首页的功能按键均以枚举类提供单例模式
*   用于首页Activity的onCreate时，加载进List，传递给Adapter
* */
public class HomeBtnItemFactory
{
    public enum ButtonType
    {
        CAMERA(R.mipmap.ic_camera, "相机"),
        UPLOAD(R.mipmap.ic_upload, "上传"),
        OPEN(R.mipmap.ic_photoes, "相册"),
        SETTING(R.mipmap.ic_setting, "设置");

        private final int imageSrc;
        private final String btnName;

        ButtonType(int res, String name)
        {
            imageSrc = res;
            btnName = name;
        }
        public HomeBtnItem getBtnItem()
        {
            return new HomeBtnItem(btnName, imageSrc);
        }
    }

    public static HomeBtnItem getBtnByName(ButtonType buttonType)
    {
        switch (buttonType)
        {
            case SETTING:
                return ButtonType.SETTING.getBtnItem();
            case OPEN:
                return ButtonType.OPEN.getBtnItem();
            case CAMERA:
                return ButtonType.CAMERA.getBtnItem();
            case UPLOAD:
                return ButtonType.UPLOAD.getBtnItem();
            default:
                throw new IllegalArgumentException("buttonType "+buttonType+" have been implemented.");
        }
    }
}