package com.wangze.lab.reclist;



/*
 *
 *   定义主页功能按钮类
 *   String btnName : 按钮功能名称 ， 如【拍照】，【上传信息】等
 *   int imageSrc   : 功能图标icon资源，放置在app/res/mipmap/ic_?.png
 * */

public class HomeBtnItem
{
    private String btnName;
    private int imageSrc;

    public HomeBtnItem(String btnName, int imageSrc)
    {
        this.btnName = btnName;
        this.imageSrc = imageSrc;
    }

    public String getBtnName()
    {
        return btnName;
    }

    public void setBtnName(String btnName)
    {
        this.btnName = btnName;
    }

    public int getImageSrc()
    {
        return imageSrc;
    }

    public void setImageSrc(int imageSrc)
    {
        this.imageSrc = imageSrc;
    }
}
