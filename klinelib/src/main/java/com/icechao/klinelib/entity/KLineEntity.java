package com.icechao.klinelib.entity;


import com.google.gson.annotations.SerializedName;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.utils
 * @FileName     : KLineEntity.java
 * @Author       : chao
 * @Date         : 2019/1/8
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class KLineEntity implements IKLine {


    @Override
    public String getDate() {
        return date;
    }

    @Override
    public float getOpenPrice() {
        return open;
    }

    @Override
    public float getHighPrice() {
        return high;
    }

    @Override
    public float getLowPrice() {
        return low;
    }

    @Override
    public float getClosePrice() {
        return close;
    }

    @Override
    public float getMaOne() {
        return maOne;
    }

    @Override
    public float getMaTwo() {
        return maTwo;
    }

    @Override
    public float getMaThree() {
        return maThree;
    }

    @Override
    public float getBollMa() {
        return bollMa;
    }


    @Override
    public float getDea() {
        return dea;
    }

    @Override
    public float getDif() {
        return dif;
    }

    @Override
    public float getMacd() {
        return macd;
    }

    @Override
    public float getK() {
        return k;
    }

    @Override
    public float getD() {
        return d;
    }

    @Override
    public float getJ() {
        return j;
    }

    @Override
    public float getRsi() {
        return rsi;
    }

    @Override
    public float getUp() {
        return up;
    }

    @Override
    public float getMb() {
        return mb;
    }

    @Override
    public float getDn() {
        return dn;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public float getMa5Volume() {
        return ma5Volume;
    }

    @Override
    public float getMa10Volume() {
        return ma10Volume;
    }


    public String date;

    @SerializedName("Open")
    public float open;

    public float high;

    public float low;

    public float close;

    public float volume;

    public float maOne;

    public float maTwo;

    public float maThree;

    public float bollMa;

    public float dea;

    public float dif;

    public float macd;

    public float k;

    public float d;

    public float j;

    public float rOne;
    public float rTwo;
    public float rThree;

    public float wrOne;
    public float wrTwo;
    public float wrThree;

    public float rsi;

    public float up;

    public float mb;

    public float dn;

    public float ma5Volume;

    public float ma10Volume;

    public float MAVolume;


    @Override
    public float getWrOne() {
        return wrOne;
    }

    @Override
    public float getWrTwo() {
        return wrTwo;
    }

    @Override
    public float getWrThree() {
        return wrThree;
    }
}
