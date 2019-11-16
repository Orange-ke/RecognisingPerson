package com.wangze.chouxiang.wangze;

import com.wangze.chouxiang.sjx.Person;

/**
 * 已识别的人脸的坐标信息，用于 {@link com.wangze.chouxiang.sjx.RecognizeImageView}类
 * <code>onDraw</code>方法绘制人脸方框时使用
 *
 * @author 王泽
 * @since 1.0
 */
public class CoordinateInformation
{
    private int left;
    private int right;
    private int top;
    private int bottom;
    private int angle;
    private static CoordinateInformation my = null;

    public int getAngle()
    {
        return angle;
    }

    public int getLeft() { return left; }

    public int getRight() { return right; }

    public int getTop() { return top; }

    public int getBottom() { return bottom; }

    private CoordinateInformation(int left, int right, int top, int bottom, int angle)
    {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.angle = angle;
    }

    /**
     *静态工厂方法，复用一个坐标信息对象
     * @param who 对象
     */
    public static CoordinateInformation Of(Person who)
    {
        if (my == null)
        {
            my = new CoordinateInformation(who.getLeft(),
                    who.getLeft() + who.getWidth(),
                    who.getTop(), who.getTop() + who.getHeight(),
                    who.getRotation());
        } else
        {
            my.left = who.getLeft();
            my.top = who.getTop();
            my.right = who.getLeft() + who.getWidth();
            my.bottom = who.getTop() + who.getHeight();
            my.angle = who.getRotation();
        }
        return my;
    }


}
