package com.icechao.klinelib.utils;

import com.icechao.klinelib.entity.KLineEntity;

import java.util.List;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.utils
 * @FileName     : DataHelper.java
 * @Author       : chao
 * @Date         : 2019/1/8
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class DataHelper {

/**
     * 计算  重构:计算方法,过多循环~  把循环尽量放到一个循环种加快计算速度
     *
     * @param dataList
     */
    static void calculate(List<KLineEntity> dataList, float bollP, int bollN,
                          float priceMaOne, float priceMaTwo, float priceMaThree,
                          float macdEma1, float macdEma2, float macddDif,
                          float maOne, float maTwo, float maThree,
                          int rsiDay,
                          int kdjDay,
                          int one, int two, int three) {
        float ma5 = 0;
        float ma10 = 0;
        float ma20 = 0;
        float ma30 = 0;

        float ema12 = 0;
        float ema26 = 0;
        float dif = 0;
        float dea = 0;
        float macd = 0;

        float volumeMaOne = 0;
        float volumeMaTwo = 0;
        float volumeMaThree = 0;


        double rsi;
        double rsiMaxEma;
        double rsiABSEma;


        float k = 0;
        float d = 0;

        Float r;


        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            float closePrice = point.getClosePrice();
            //ma计算
            ma5 += closePrice;
            ma10 += closePrice;
            ma20 += closePrice;
            ma30 += closePrice;
            if (i == priceMaOne - 1) {
                point.maOne = ma5 / priceMaOne;
            } else if (i >= priceMaOne) {
                ma5 -= dataList.get((int) (i - priceMaOne)).getClosePrice();
                point.maOne = ma5 / priceMaOne;
            } else {
                point.maOne = Float.MIN_VALUE;
            }
            if (i == priceMaTwo - 1) {
                point.maTwo = ma10 / priceMaTwo;
            } else if (i >= priceMaTwo) {
                ma10 -= dataList.get((int) (i - priceMaTwo)).getClosePrice();
                point.maTwo = ma10 / priceMaTwo;
            } else {
                point.maTwo = Float.MIN_VALUE;
            }
            if (i == priceMaThree - 1) {
                point.maThree = ma20 / priceMaThree;
            } else if (i >= priceMaThree) {
                ma20 -= dataList.get((int) (i - priceMaThree)).getClosePrice();
                point.maThree = ma20 / priceMaThree;
            } else {
                point.maThree = Float.MIN_VALUE;
            }
            if (i == bollP - 1) {
                point.bollMa = ma30 / bollP;
            } else if (i >= bollP) {
                ma30 -= dataList.get((int) (i - bollP)).getClosePrice();
                point.bollMa = ma30 / bollP;
            } else {
                point.bollMa = 0;
            }


            //macd计算
            if (0 == i) {
                ema12 = closePrice;
                ema26 = closePrice;
            } else {
                // EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
                ema12 = ema12 * (macdEma1 - 1) / (macdEma1 + 1) + closePrice * 2f / (macdEma1 + 1);
                // EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                ema26 = ema26 * (macdEma2 - 1) / (macdEma2 + 1) + closePrice * 2f / (macdEma2 + 1);
            }
            // DIF = EMA（12） - EMA（26） 。
            // 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
            // 用（DIF-DEA）*2即为MACD柱状图。
            dif = ema12 - ema26;
            dea = dea * (macddDif - 1) / (macddDif + 1) + dif * 2f / (macddDif + 1);
            macd = (dif - dea) * 2f;
            point.dif = dif;
            point.dea = dea;
            point.macd = macd;


            //boll计算
            if (i < bollP - 1) {
                point.mb = 0f;
                point.up = 0f;
                point.dn = 0f;
            } else {
                int n = (int) bollP;
                float md = 0;
                for (int j = i - n + 1; j <= i; j++) {
                    float c = dataList.get(j).getClosePrice();
                    float m = point.getBollMa();
                    float value = c - m;
                    md += value * value;
                }
                md = md / (n - 1);
                md = (float) Math.sqrt(md);
                point.mb = point.getBollMa();
                point.up = point.mb + bollN * md;
                point.dn = point.mb - bollN * md;
            }

            //vol ma计算
            volumeMaOne += point.getVolume();
            volumeMaTwo += point.getVolume();
            volumeMaThree += point.getVolume();

            if (i == maOne - 1) {
                point.MA5Volume = (volumeMaOne / maOne);
            } else if (i > maOne - 1) {
                volumeMaOne -= dataList.get((int) (i - maOne)).getVolume();
                point.MA5Volume = volumeMaOne / maOne;
            } else {
                point.MA5Volume = Float.MIN_VALUE;
            }

            if (i == maTwo - 1) {
                point.MA10Volume = (volumeMaTwo / maTwo);
            } else if (i > maTwo - 1) {
                volumeMaTwo -= dataList.get((int) (i - maTwo)).getVolume();
                point.MA10Volume = volumeMaTwo / maTwo;
            } else {
                point.MA10Volume = Float.MIN_VALUE;
            }

            if (i == maThree - 1) {
                point.MAVolume = (volumeMaThree / maThree);
            } else if (i > maThree - 1) {
                volumeMaThree -= dataList.get((int) (i - maThree)).getVolume();
                point.MAVolume = volumeMaThree / maThree;
            } else {
                point.MAVolume = Float.MIN_VALUE;
            }


//            以14日RSI指标为例，从当起算，倒推包括当日在内的15个收盘价，以每一日的收盘价减去上一日的收盘价，得到14个数值，这些数值有正有负。这样，RSI指标的计算公式具体如下：
//
//            A=14个数字中正数之和
//                    B=14个数字中负数之和乘以（-1）
//            RSI（14）=A÷（A＋B）×100
            if (i == 0 || i < rsiDay) {
                rsi = Float.MIN_VALUE;
            } else {

                rsiMaxEma = 0;
                rsiABSEma = 0;

                for (int j = i - 1; j > i - rsiDay; j--) {
                    double v = dataList.get(j + 1).getClosePrice() - dataList.get(j).getClosePrice();
                    if (v > 0) {
                        rsiMaxEma += v;
                    } else {
                        rsiABSEma += v;
                    }
                }
                rsi = rsiMaxEma / (rsiMaxEma - rsiABSEma) * 100f;
            }
            point.rOne = (float) rsi;


//            kdj
            if (i < kdjDay - 1) {
                point.k = Float.MIN_VALUE;
                point.d = Float.MIN_VALUE;
                point.j = Float.MIN_VALUE;
            } else {

//              KDJ是随机指标，计算比较复杂，首先要计算周期（n日、n周等）的RSV值，即未成熟随机指标值，然后再计算K值、D值、J值等。以n日KDJ数值的计算为例，其计算公式为
                int startIndex = i - kdjDay + 1;
                float maxRsi = Float.MIN_VALUE;
                float minRsi = Float.MAX_VALUE;
                for (int index = startIndex; index <= i; index++) {
                    maxRsi = Math.max(maxRsi, dataList.get(index).getHighPrice());
                    minRsi = Math.min(minRsi, dataList.get(index).getLowPrice());
                }
//                n日RSV=（Cn－Ln）/（Hn－Ln）×100
//                公式中，Cn为第n日收盘价；Ln为n日内的最低价；Hn为n日内的最高价。
                Float rsv = null;
                try {
                    rsv = 100f * (closePrice - minRsi) / (maxRsi - minRsi);
                } catch (Exception e) {
                    rsv = 0f;
                }
//                    计算K值与D值：
//                    当日K值=2/3×前一日K值+1/3×当日RSV
                KLineEntity kLineEntity = dataList.get(i - 1);
                float k1 = kLineEntity.getK();
                k = 2f / 3f * (k1 == Float.MIN_VALUE ? 50 : k1) + 1f / 3f * rsv;
//                            当日D值=2/3×前一日D值+1/3×当日K值
                float d1 = kLineEntity.getD();
                d = 2f / 3f * (d1 == Float.MIN_VALUE ? 50 : d1) + 1f / 3f * k;
//                    若无前一日K 值与D值，则可分别用50来代替。
//                    J值=3*当日K值-2*当日D值


                point.k = k;
                point.d = d;
                point.j = 3f * k - 2 * d;
            }


            //wr
//            W%R=（Hn—C）÷（Hn—Ln）×100
//            其中：C为计算日的收盘价，Ln为N周期内的最低价，Hn为N周期内的最高价，公式中的N为选定的计算时间参数，一般为4或14。
//            以计算周期为14日为例，其计算过程如下：
//            W%R（14日）=（H14—C）÷（H14—L14）×100
//            其中，C为第14天的收盘价，H14为14日内的最高价，L14为14日内的最低价。


            if (i < one - 1) {
                r = Float.MIN_VALUE;
            } else {
                int startIndexOne = i - one + 1;
                float maxWr = Float.MIN_VALUE;
                float minWr = Float.MAX_VALUE;
                for (int index = startIndexOne; index <= i; index++) {
                    maxWr = Math.max(maxWr, dataList.get(index).getHighPrice());
                    minWr = Math.min(minWr, dataList.get(index).getLowPrice());
                }

                try {
                    r = 100 * (maxWr - dataList.get(i).getClosePrice()) / (maxWr - minWr);
                } catch (Exception e) {
                    r = 0f;
                }
            }
            point.wrOne = r;

            if (i < two - 1) {
                r = Float.MIN_VALUE;
            } else {
                int startIndexTwo = i - two + 1;
                float maxTwo = Float.MIN_VALUE;
                float minTwo = Float.MAX_VALUE;
                for (int index = startIndexTwo; index <= i; index++) {
                    maxTwo = Math.max(maxTwo, dataList.get(index).getHighPrice());
                    minTwo = Math.min(minTwo, dataList.get(index).getLowPrice());
                }
                try {
                    r = 100 * (maxTwo - dataList.get(i).getClosePrice()) / (maxTwo - minTwo);
                } catch (Exception e) {
                    r = 0f;
                }
            }
            point.wrTwo = r;

            if (i < three - 1) {
                r = Float.MIN_VALUE;
            } else {
                int startIndexThree = i - three + 1;
                float maxThree = Float.MIN_VALUE;
                float minTree = Float.MAX_VALUE;
                for (int index = startIndexThree; index <= i; index++) {
                    maxThree = Math.max(maxThree, dataList.get(index).getHighPrice());
                    minTree = Math.min(minTree, dataList.get(index).getLowPrice());
                }
                try {
                    r = 100 * (maxThree - dataList.get(i).getClosePrice()) / (maxThree - minTree);
                } catch (Exception e) {
                    r = 0f;
                }
            }
            point.wrThree = r;
        }
    }

    /**
     * 计算MA BOLL RSI KDJ MACD
     *
     * @param dataList
     */
    public static void calculate(List<KLineEntity> dataList) {
        calculate(dataList, 20, 2,
                5, 10, 30,
                12, 26, 9,
                5, 10, 30,
                14,
                14,
                14, 0, 0);
    }
}
