package com.icechao.klinelib.utils;

import com.icechao.klinelib.entity.KLineEntity;

import java.util.List;

import static com.icechao.klinelib.base.BaseKLineChartView.GROUP_COUNT;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_BOLL_DN;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_BOLL_MB;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_BOLL_UP;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_CLOSE;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_HIGH;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_KDJ_D;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_KDJ_J;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_KDJ_K;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_LOW;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_MACD;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_MACD_DEA;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_MACD_DIF;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_MA_1;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_MA_2;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_MA_3;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_OPEN;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_RSI;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_VOL;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_VOL_MA_1;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_VOL_MA_2;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_WR_1;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_WR_2;
import static com.icechao.klinelib.base.BaseKLineChartView.INDEX_WR_3;

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
public class NewDataHelper {


    /**
     * 计算  重构:计算方法,过多循环~  把循环尽量放到一个循环种加快计算速度
     *
     * @param dataList
     */
    static float[] calculate(List<KLineEntity> dataList, float bollP, int bollN,
                             int priceMaOne, int priceMaTwo, int priceMaThree,
                             int s, int l, int m,
                             int maOne, int maTwo, float maThree,
                             int kdjDay,
                             int wr1, int wr2, int wr3) {
        float maSum1 = 0;
        float maSum2 = 0;
        float maSum3 = 0;


        float volumeMaOne = 0;
        float volumeMaTwo = 0;


        float preEma12 = 0;
        float preEma26 = 0;

        float preDea = 0;
        float[] buffer = new float[dataList.size() * GROUP_COUNT];

        int size = dataList.size();
        for (int i = 0; i < size; i++) {

            KLineEntity point = dataList.get(i);
            float closePrice = point.getClosePrice();
            int tmp = i * GROUP_COUNT;

            buffer[tmp + INDEX_OPEN] = point.open;
            buffer[tmp + INDEX_CLOSE] = closePrice;
            buffer[tmp + INDEX_HIGH] = point.high;
            buffer[tmp + INDEX_LOW] = point.low;
            buffer[tmp + INDEX_VOL] = point.volume;

            //ma计算
            maSum1 += closePrice;
            maSum2 += closePrice;
            maSum3 += closePrice;

            float tempMa;
            if (i == priceMaOne - 1) {
                tempMa = maSum1 / priceMaOne;
            } else if (i >= priceMaOne) {
                maSum1 -= buffer[(i - priceMaOne) * GROUP_COUNT + INDEX_CLOSE];
                tempMa = maSum1 / priceMaOne;
            } else {
                tempMa = Float.MIN_VALUE;
            }
            buffer[tmp + INDEX_MA_1] = tempMa;

            if (i == priceMaTwo - 1) {
                tempMa = maSum2 / priceMaTwo;
            } else if (i >= priceMaTwo) {
                maSum2 -= buffer[(i - priceMaTwo) * GROUP_COUNT + INDEX_CLOSE];
                tempMa = maSum2 / priceMaTwo;
            } else {
                tempMa = Float.MIN_VALUE;
            }
            buffer[tmp + INDEX_MA_2] = tempMa;

            if (i == priceMaThree - 1) {
                tempMa = maSum3 / priceMaThree;
            } else if (i >= priceMaThree) {
                maSum3 -= buffer[(i - priceMaThree) * GROUP_COUNT + INDEX_CLOSE];
                tempMa = maSum3 / priceMaThree;
            } else {
                tempMa = Float.MIN_VALUE;
            }
            buffer[tmp + INDEX_MA_3] = tempMa;

            //macd
            if (s > 0 && l > 0 && m > 0) {
                if (size >= m + l - 2) {
                    if (i < l - 1) {
                        buffer[tmp + INDEX_MACD_DIF] = Float.MIN_VALUE;
                    }

                    if (i >= s - 1) {
                        float ema12 = calculateEMA(dataList, s, i, preEma12);
                        preEma12 = ema12;
                        if (i >= l - 1) {
                            float ema26 = calculateEMA(dataList, l, i, preEma26);
                            preEma26 = ema26;
                            buffer[tmp + INDEX_MACD_DIF] = (ema12 - ema26);
                        } else {
                            buffer[tmp + INDEX_MACD_DIF] = Float.MIN_VALUE;
                        }
                    } else {
                        buffer[tmp + INDEX_MACD_DIF] = Float.MIN_VALUE;
                    }

                    if (i >= m + l - 2) {
                        boolean isFirst = i == m + l - 2;
                        float dea = calculateDEA(buffer, dataList, l, m, i, preDea, isFirst);
                        preDea = dea;
                        buffer[tmp + INDEX_MACD_DEA] = (dea);
                    } else {
                        buffer[tmp + INDEX_MACD_DEA] = Float.MIN_VALUE;
                    }

                    if (i >= m + l - 2) {
                        buffer[tmp + INDEX_MACD] = buffer[tmp + INDEX_MACD_DIF] - buffer[tmp + INDEX_MACD_DEA];
                    } else {
                        buffer[tmp + INDEX_MACD] = Float.MIN_VALUE;
                    }
                } else {
                    buffer[tmp + INDEX_MACD] = Float.MIN_VALUE;
                }
            }


            //boll计算
            if (i >= bollN - 1) {
                float boll = calculateBoll(dataList, i, bollN);
                float highBoll = boll + bollP * STD(dataList, i, bollN);
                float lowBoll = boll - bollP * STD(dataList, i, bollN);
                buffer[tmp + INDEX_BOLL_UP] = highBoll;
                buffer[tmp + INDEX_BOLL_MB] = boll;
                buffer[tmp + INDEX_BOLL_DN] = lowBoll;
            } else {
                buffer[tmp + INDEX_BOLL_UP] = Float.MIN_VALUE;
                buffer[tmp + INDEX_BOLL_MB] = Float.MIN_VALUE;
                buffer[tmp + INDEX_BOLL_DN] = Float.MIN_VALUE;
            }

            //vol ma计算
            volumeMaOne += buffer[tmp + INDEX_VOL];
            volumeMaTwo += buffer[tmp + INDEX_VOL];
            float ma;
            if (i == maOne - 1) {
                ma = (volumeMaOne / maOne);
            } else if (i > maOne - 1) {
                volumeMaOne -= buffer[(i - maOne) * GROUP_COUNT + INDEX_VOL];
                ma = volumeMaOne / maOne;
            } else {
                ma = Float.MIN_VALUE;
            }
            buffer[tmp + INDEX_VOL_MA_1] = ma;

            if (i == maTwo - 1) {
                ma = (volumeMaTwo / maTwo);
            } else if (i > maTwo - 1) {
                volumeMaTwo -= buffer[(i - maTwo) * GROUP_COUNT + INDEX_VOL];
                ma = volumeMaTwo / maTwo;
            } else {
                ma = Float.MIN_VALUE;
            }
            buffer[tmp + INDEX_VOL_MA_2] = ma;

            //kdj
            float k;
            float d;
            if (i < kdjDay - 1 || 0 == i) {
                buffer[tmp + INDEX_KDJ_K] = Float.MIN_VALUE;
                buffer[tmp + INDEX_KDJ_D] = Float.MIN_VALUE;
                buffer[tmp + INDEX_KDJ_J] = Float.MIN_VALUE;
            } else {
                int startIndex = i - kdjDay + 1;
                float maxRsi = Float.MIN_VALUE;
                float minRsi = Float.MAX_VALUE;
                for (int index = startIndex; index <= i; index++) {
                    maxRsi = Math.max(maxRsi, buffer[index * GROUP_COUNT + INDEX_HIGH]);
                    minRsi = Math.min(minRsi, buffer[index * GROUP_COUNT + INDEX_LOW]);
                }
                float rsv;
                try {
                    rsv = 100f * (closePrice - minRsi) / (maxRsi - minRsi);
                } catch (Exception e) {
                    rsv = 0f;
                }
                float k1 = buffer[tmp + INDEX_KDJ_K - GROUP_COUNT];
                k = 2f / 3f * (k1 == Float.MIN_VALUE ? 50 : k1) + 1f / 3f * rsv;
                float d1 = buffer[tmp + INDEX_KDJ_D - GROUP_COUNT];
                d = 2f / 3f * (d1 == Float.MIN_VALUE ? 50 : d1) + 1f / 3f * k;
                buffer[tmp + INDEX_KDJ_K] = k;
                buffer[tmp + INDEX_KDJ_D] = d;
                buffer[tmp + INDEX_KDJ_J] = 3f * k - 2 * d;
            }
            //计算3个 wr指标
            buffer[tmp + INDEX_WR_1] = getValueWR(dataList, wr1, i);
            buffer[tmp + INDEX_WR_2] = getValueWR(dataList, wr2, i);
            buffer[tmp + INDEX_WR_3] = getValueWR(dataList, wr3, i);
        }
        return buffer;
    }

    private static float getValueWR(List<KLineEntity> dataList, int wr1, int i) {
        float valueWR;
        if (wr1 != 0 && i >= wr1) {
            valueWR = -calcWr(dataList, i, wr1);
        } else {
            valueWR = Float.MIN_VALUE;
        }
        return valueWR;
    }

    /**
     * 计算MA BOLL RSI KDJ MACD
     *
     * @param dataList
     */
    public static float[] calculate(List<KLineEntity> dataList) {
        float[] calculate = calculate(dataList, 2, 20,
                5, 10, 30,
                12, 26, 9,
                5, 10, 30,
                14,
                14, 0, 0);
        calculateRSI(dataList, 14, calculate);
        return calculate;
    }


    /**
     * 计算ema指标
     *
     * @param n      s或l
     * @param index  index
     * @param preEma 上一个ema
     * @return ema
     */
    private static float calculateEMA(List<KLineEntity> list, int n, int index, float preEma) {
        float y = 0;
        try {
            if (index + 1 < n) {
                return y;
            } else if (index + 1 == n) {
                for (int i = 0; i < n; i++) {
                    y += list.get(i).close;
                }
                return y / n;
            } else {
                return (preEma * (n - 1) + list.get(index).close * 2) / (n + 1);
            }
        } catch (Exception e) {
            return y;
        }
    }

    /**
     * 计算DEA
     *
     * @param buffer
     * @param list    列表
     * @param l       L26
     * @param m       M9
     * @param index   index
     * @param preDea  上一个dea
     * @param isFirst 是否是第一个
     * @return dea
     */
    private static float calculateDEA(float[] buffer, List<KLineEntity> list, int l, int m, int index,
                                      float preDea,
                                      boolean isFirst) {
        float y = 0;
        try {
            if (isFirst) {
                for (int i = l - 1; i <= m + l - 2; i++) {
                    y += buffer[i * GROUP_COUNT + INDEX_MACD_DIF];
                }
                return y / m;
            } else {
                return ((preDea * (m - 1) + buffer[index * GROUP_COUNT + INDEX_MACD_DIF] * 2) / (m + 1));
            }
        } catch (Exception e) {
            return y;
        }
    }


    public static void calculateRSI(List<KLineEntity> klineInfos,
                                    int n, float[] calculate) {
        if (klineInfos.size() > n) {
            double firstValue = calculate[(n - 1) * GROUP_COUNT + INDEX_RSI];
            if (firstValue != 0 && firstValue != Float.MIN_VALUE) {
                calculateRSIChange(klineInfos, n, findStartIndex(klineInfos),
                        klineInfos.size(), calculate);
            } else {
                calculateRSIChange(klineInfos, n, 0, klineInfos.size(), calculate);
            }
        }
    }


    private static void calculateRSIChange(List<KLineEntity> klineInfos,
                                           int n, int start, int end, float[] calculate) {
        double upPriceRma = 0;
        double downPriceRma = 0;
        for (int i = start; i < end; i++) {
            double rsi;
            if (i == n) {
                double upPrice = 0;
                double downPrice = 0;
                for (int k = 1; k <= n; k++) {
                    KLineEntity kLineEntity = klineInfos.get(k);
                    double close = kLineEntity.close;
                    double lastClose = klineInfos.get(k - 1).close;
                    upPrice += Math.max(close - lastClose, 0);
                    downPrice += Math.max(lastClose - close, 0);
                }
                upPriceRma = upPrice / n;
                downPriceRma = downPrice / n;
                rsi = calculateRSI(upPriceRma, downPriceRma);
            } else if (i > n) {
                double close = klineInfos.get(i).close;
                double lastClose = klineInfos.get(i - 1).close;

                double upPrice = Math.max(close - lastClose, 0);
                double downPrice = Math.max(lastClose - close, 0);

                upPriceRma = (upPrice + (n - 1) * upPriceRma) / n;
                downPriceRma = (downPrice + (n - 1) * downPriceRma) / n;
                rsi = calculateRSI(upPriceRma, downPriceRma);
            } else {
                rsi = Float.MIN_VALUE;
            }

            calculate[i * GROUP_COUNT + INDEX_RSI] = (float) rsi;
        }
    }

    private static double calculateRSI(double upPriceRma, double downPriceRma) {
        if (downPriceRma == 0) {
            return 100;
        } else {
            if (upPriceRma == 0) {
                return 0;
            } else {
                return 100 - (100 / (1 + upPriceRma / downPriceRma));
            }
        }
    }

    private static int findStartIndex(List<KLineEntity> klineInfos) {
        int startIndex = 0;
        for (int i = klineInfos.size() - 1; i > 0; i--) {
            double maValue = klineInfos.get(i).rOne;
            if (maValue != 0) {
                startIndex = i + 1;
                break;
            }
        }
        return startIndex;
    }


    public static float calcWr(List<KLineEntity> dataDiction, int nIndex, int n) {
        float result = 0;
        float lowInNLowsValue = getLowestOfArray(dataDiction, nIndex, n);   //N日内最低价的最低值
        float highInHighsValue = getHighestOfArray(dataDiction, nIndex, n);   //N日内最低价的最低值
        float valueSpan = highInHighsValue - lowInNLowsValue;
        if (valueSpan > 0) {
            KLineEntity kLineData = dataDiction.get(nIndex);
            result = 100 * (highInHighsValue - kLineData.close) / valueSpan;
        } else
            result = 0;

        return result;
    }


    public static float getLowestOfArray(List<KLineEntity> valuesArray, int fromIndex, int nCount) {
        float result = Float.MAX_VALUE;
        int endIndex = fromIndex - (nCount - 1);
        if (fromIndex >= endIndex) {
            for (int itemIndex = fromIndex + 1; itemIndex > endIndex; itemIndex--) {
                KLineEntity klineData = valuesArray.get(itemIndex - 1);
                float lowPrice = klineData.low;
                result = result <= lowPrice ? result : lowPrice;
            }
        }

        return result;
    }


    public static float getHighestOfArray(List<KLineEntity> valuesArray,
                                          int fromIndex, int nCount) {
        float result = Float.MIN_VALUE;
        int endIndex = fromIndex - (nCount - 1);
        if (fromIndex >= endIndex) {
            for (int itemIndex = fromIndex + 1; itemIndex > endIndex; itemIndex--) {
                KLineEntity klineData = valuesArray.get(itemIndex - 1);
                float highPrice = klineData.high;
                result = result >= highPrice ? result : highPrice;
            }
        }
        return result;
    }


    /**
     * 布林线指标计算
     */
    private static float calculateBoll(List<KLineEntity> payloads, int index, int maN) {
        float sum = 0;
        for (int i = index; i >= index - maN + 1; i--) {
            sum = (sum + payloads.get(i).close);
        }
        return sum / maN;

    }

    private static float STD(List<KLineEntity> payloads, int index, int maN) {

        float sum = 0f;
        float std = 0f;
        for (int i = index; i >= index - maN + 1; i--) {
            sum += payloads.get(i).close;
        }
        float avg = sum / maN;
        //float avg = payloads.get(index).bollMa;
        //不能提前计算,会出现0.001的误差
        for (int i = index; i >= index - maN + 1; i--) {
            std += (payloads.get(i).close - avg) * (payloads.get(i).close - avg);
        }
        return (float) Math.sqrt(std / maN);
    }


}
