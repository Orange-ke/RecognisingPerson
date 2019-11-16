package com.wangze.chouxiang.sjx;

public class Person
{
    private String info;
    private short id;
    private int score;
    private int rotation;
    private int left;
    private int top;
    private int width;
    private int height;
    private int age, beauty;
    private String emotion;
    private String name;

    @Override
    public String toString()
    {
        return "Person{" +
                "info='" + info + '\'' +
                ", id=" + id +
                ", score=" + score +
                ", rotation=" + rotation +
                ", left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                ", age=" + age +
                ", beauty=" + beauty +
                ", emotion='" + emotion + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public short getId()
    {
        return id;
    }

    public void setId(short id)
    {
        this.id = id;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public int getBeauty()
    {
        return beauty;
    }

    public void setBeauty(int beauty)
    {
        this.beauty = beauty;
    }

    public String getEmotion()
    {
        return emotion;
    }

    public void setEmotion(String emotion)
    {
        this.emotion = emotion;
    }

    public int getRotation()
    {
        return rotation;
    }

    public void setRotation(int rotation)
    {
        this.rotation = rotation;
    }

    public int getLeft()
    {
        return left;
    }

    public void setLeft(int left)
    {
        this.left = left;
    }

    public int getTop()
    {
        return top;
    }

    public void setTop(int top)
    {
        this.top = top;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }


    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }
}
