package com.icechao.klinelib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.icechao.klinelib.R;
import com.icechao.klinelib.base.BaseKLineChartView;
import com.icechao.klinelib.draw.KDJDraw;
import com.icechao.klinelib.draw.MACDDraw;
import com.icechao.klinelib.draw.MainDraw;
import com.icechao.klinelib.draw.RSIDraw;
import com.icechao.klinelib.draw.VolumeDraw;
import com.icechao.klinelib.draw.WRDraw;
import com.icechao.klinelib.utils.ViewUtil;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.utils
 * @FileName     : KLineChartView.java
 * @Author       : chao
 * @Date         : 2019/1/8
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class KLineChartView extends BaseKLineChartView {

    private View progressbar;

    private MACDDraw macdDraw;
    private RSIDraw rsiDraw;
    private MainDraw mainDraw;
    private KDJDraw kdjDraw;
    private WRDraw wrDraw;
    private VolumeDraw volumeDraw;
    private Context context;


    public KLineChartView(Context context) {
        this(context, null);
        initView(context);
    }

    public KLineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initView(context);
        initAttrs(attrs);
    }

    public KLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttrs(attrs);
    }

    private void initView(Context context) {
        this.context = context;
        volumeDraw = new VolumeDraw(context);
        macdDraw = new MACDDraw(context, getResources().getColor(R.color.chart_up), getResources().getColor(R.color.chart_down));
        wrDraw = new WRDraw(context);
        kdjDraw = new KDJDraw(context);
        rsiDraw = new RSIDraw(context);
        mainDraw = new MainDraw(context);
        addChildDraw(macdDraw);
        addChildDraw(kdjDraw);
        addChildDraw(rsiDraw);
        addChildDraw(wrDraw);
        setVolDraw(volumeDraw);
        setMainDraw(mainDraw);


        volumeDraw.setVolLeftColor(getResources().getColor(R.color.chart_text));
        setPriceLineColor(getResources().getColor(R.color.chart_text));
        setPriceLineWidth(ViewUtil.Dp2Px(context, 1));
        setPriceLineRightColor(getResources().getColor(R.color.right_index));
        setSelectCrossBigColor(getResources().getColor(R.color.cross_big_color));
        setSelectCrossColor(getResources().getColor(R.color.cross_color));
        setSelectedYColor(getResources().getColor(R.color.cross_color));

        setUpColor(getResources().getColor(R.color.chart_up));
        setDownColor(getResources().getColor(R.color.chart_down));
        setMinuteLineColor(getResources().getColor(R.color.chart_minute_line));


        //背景添加渐变色
        setBackgroundStartColor(getResources().getColor(R.color.kline_bg_start));
        setBackgroundEmdColor(getResources().getColor(R.color.info_kline_bg_end));

        setAreaTopColor(getResources().getColor(R.color.chart_line_start));
        setAreaBottomColor(getResources().getColor(R.color.chart_line_end));

        setEndPointColor(Color.WHITE);
        setLineEndPointWidth(ViewUtil.Dp2Px(context, 4));

        //添加Loadding窗口
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.KLineChartView);
        if (null != array) {
            try {
                //public
                setChartItemWidth(array.getDimension(R.styleable.KLineChartView_kc_point_width, getDimension(R.dimen.chart_point_width)));
                setTextSize(array.getDimension(R.styleable.KLineChartView_kc_text_size, getDimension(R.dimen.chart_text_size)));
                setTextColor(array.getColor(R.styleable.KLineChartView_kc_text_color, getColor(R.color.chart_text)));
                setMTextSize(array.getDimension(R.styleable.KLineChartView_kc_text_size, getDimension(R.dimen.chart_text_size)));
                setMTextColor(array.getColor(R.styleable.KLineChartView_kc_text_color, getColor(R.color.chart_text)));
                setLineWidth(array.getDimension(R.styleable.KLineChartView_kc_line_width, getDimension(R.dimen.chart_line_width)));
//                setBackgroundColor(array.getColor(R.styleable.KLineChartView_kc_background_color, getColor(R.color.chart_bac)));
                setSelectPointColor(array.getColor(R.styleable.KLineChartView_kc_background_color, getColor(R.color.chart_point_selected_price_bg)));

                setSelectedXLineColor(getResources().getColor(R.color.cross_color));
                setSelectedXLineWidth(getDimension(R.dimen.chart_line_width));

                setSelectedYLineColor(getResources().getColor(R.color.cross_vertical_color));
                setSelectedYLineWidth(getDimension(R.dimen.chart_point_width));

                setGridLineWidth(array.getDimension(R.styleable.KLineChartView_kc_grid_line_width, getDimension(R.dimen.chart_grid_line_width)));
                setGridLineColor(array.getColor(R.styleable.KLineChartView_kc_grid_line_color, getColor(R.color.chart_grid_line)));
                //macd
                setMACDWidth(array.getDimension(R.styleable.KLineChartView_kc_macd_width, getDimension(R.dimen.chart_candle_width)));
                setDIFColor(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma5)));
                setDEAColor(array.getColor(R.styleable.KLineChartView_kc_dea_color, getColor(R.color.chart_ma10)));
                setMACDColor(array.getColor(R.styleable.KLineChartView_kc_macd_color, getColor(R.color.chart_ma30)));
                //kdj
                setKColor(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma5)));
                setDColor(array.getColor(R.styleable.KLineChartView_kc_dea_color, getColor(R.color.chart_ma10)));
                setJColor(array.getColor(R.styleable.KLineChartView_kc_macd_color, getColor(R.color.chart_ma30)));
                //wr
                setR1Color(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma5)));
                setR2Color(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma10)));
                setR3Color(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma30)));
                //rsi
                setRSI1Color(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma5)));
                setRSI2Color(array.getColor(R.styleable.KLineChartView_kc_dea_color, getColor(R.color.chart_ma10)));
                setRSI3Color(array.getColor(R.styleable.KLineChartView_kc_macd_color, getColor(R.color.chart_ma30)));
                //main
                setMaOneColor(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma5)));
                setMaTwoColor(array.getColor(R.styleable.KLineChartView_kc_dea_color, getColor(R.color.chart_ma10)));
                setMaThreeColor(array.getColor(R.styleable.KLineChartView_kc_macd_color, getColor(R.color.chart_ma30)));
                setCandleWidth(array.getDimension(R.styleable.KLineChartView_kc_candle_width, getDimension(R.dimen.chart_candle_width)));
                setCandleLineWidth(array.getDimension(R.styleable.KLineChartView_kc_candle_line_width, getDimension(R.dimen.chart_candle_line_width)));
                setSelectorBackgroundColor(array.getColor(R.styleable.KLineChartView_kc_selector_background_color, getColor(R.color.chart_selector)));
                setSelectorTextSize(array.getDimension(R.styleable.KLineChartView_kc_selector_text_size, getDimension(R.dimen.chart_selector_text_size)));
                setCandleSolid(array.getBoolean(R.styleable.KLineChartView_kc_candle_solid, false));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                array.recycle();
            }
        }
    }

    private float getDimension(@DimenRes int resId) {
        return getResources().getDimension(resId);
    }

    private int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    @Override
    public void onLeftSide() {
//        showLoading();
    }

    @Override
    public void onRightSide() {
    }

    /**
     * 显示Loading同时显示K线
     */
    public void showLoading() {
        if (null != progressbar) {
            setDefaultLoading();
        }
        progressbar.setVisibility(View.VISIBLE);
        isAnimationLast = false;
    }

    /**
     * 只显示Loading窗口,不显示K线
     */
    public void justShowLoading() {
        if (null != progressbar) {
            progressbar.setVisibility(View.VISIBLE);
            isShowLoading = true;
        }
    }


    /**
     * 隐藏Loading窗口
     */
    public void hideLoading() {
        if (null != progressbar) {
            progressbar.setVisibility(View.GONE);
            isShowLoading = false;
        }
    }

    /**
     * 隐藏选择器内容
     */
    public void hideSelectData() {
        isLongPress = false;
    }


    /**
     * 设置是否可以放大
     *
     * @param scaleEnable
     */
    @Override
    public void setScaleEnable(boolean scaleEnable) {
        super.setScaleEnable(scaleEnable);

    }

    /**
     * 设置是否可以滚动
     *
     * @param scrollEnable
     */
    @Override
    public void setScrollEnable(boolean scrollEnable) {
        super.setScrollEnable(scrollEnable);
    }

    /**
     * 设置DIF颜色
     */
    public void setDIFColor(int color) {
        macdDraw.setDIFColor(color);
    }

    /**
     * 设置DEA颜色
     */
    public void setDEAColor(int color) {
        macdDraw.setDEAColor(color);
    }

    /**
     * 设置MACD颜色
     */
    public void setMACDColor(int color) {
        macdDraw.setMACDColor(color);
    }

    /**
     * 设置MACD的宽度
     *
     * @param MACDWidth width
     */
    public void setMACDWidth(float MACDWidth) {
        macdDraw.setMACDWidth(MACDWidth);
    }

    /**
     * 设置K颜色
     */
    public void setKColor(int color) {
        kdjDraw.setKColor(color);
    }

    /**
     * 设置D颜色
     */
    public void setDColor(int color) {
        kdjDraw.setDColor(color);
    }

    /**
     * 设置J颜色
     */
    public void setJColor(int color) {
        kdjDraw.setJColor(color);
    }

    /**
     * 设置R颜色
     */
    public void setR1Color(int color) {
        wrDraw.setR1Color(color);
    }

    /**
     * 设置R颜色
     */
    public void setR2Color(int color) {
        wrDraw.setR2Color(color);
    }

    /**
     * 设置R颜色
     */
    public void setR3Color(int color) {
        wrDraw.setR3Color(color);
    }

    /**
     * 设置ma5颜色
     *
     * @param color color
     */
    public void setMaOneColor(int color) {
        mainDraw.setMaOneColor(color);
        volumeDraw.setMa5Color(color);
    }

    /**
     * 设置ma10颜色
     *
     * @param color color
     */
    public void setMaTwoColor(int color) {
        mainDraw.setMaTwoColor(color);
        volumeDraw.setMa10Color(color);
    }

    /**
     * 设置ma20颜色
     *
     * @param color color
     */
    public void setMaThreeColor(int color) {
        mainDraw.setMaThreeColor(color);
    }

    /**
     * 设置选择器文字大小
     *
     * @param textSize textsize
     */
    public void setSelectorTextSize(float textSize) {
        mainDraw.setSelectorTextSize(textSize);
    }

    /**
     * 设置选择器背景
     *
     * @param color Color
     */
    public void setSelectorBackgroundColor(int color) {
        mainDraw.setSelectorBackgroundColor(color);
    }

    /**
     * 设置蜡烛宽度
     *
     * @param candleWidth candleWidth
     */
    public void setCandleWidth(float candleWidth) {
        mainDraw.setCandleWidth(candleWidth);
        //量的柱状图与蜡烛图同宽
        volumeDraw.setBarWidth(candleWidth);
    }

    /**
     * 设置蜡烛线宽度
     *
     * @param candleLineWidth candleLineWidth
     */
    public void setCandleLineWidth(float candleLineWidth) {
        mainDraw.setCandleLineWidth(candleLineWidth);
    }

    /**
     * 蜡烛是否空心
     */
    public void setCandleSolid(boolean candleSolid) {
        mainDraw.setStroke(candleSolid);
    }

    /**
     * Rsi1 线颜色
     *
     * @param color
     */
    public void setRSI1Color(int color) {
        rsiDraw.setRSI1Color(color);
    }

    /**
     * Rsi2 线颜色
     *
     * @param color
     */
    public void setRSI2Color(int color) {
        rsiDraw.setRSI2Color(color);
    }

    /**
     * Rsi3 线颜色
     *
     * @param color
     */
    public void setRSI3Color(int color) {
        rsiDraw.setRSI3Color(color);
    }

    /**
     * 设置K线中文本的文字字号
     *
     * @param textSize
     */
    @Override
    public void setTextSize(float textSize) {
        super.setTextSize(textSize);
        mainDraw.setTextSize(textSize);
        rsiDraw.setTextSize(textSize);
        macdDraw.setTextSize(textSize);
        kdjDraw.setTextSize(textSize);
        wrDraw.setTextSize(textSize);
        volumeDraw.setTextSize(textSize);
    }

    /**
     * 设置指标线的线宽
     *
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth) {
        mainDraw.setLineWidth(lineWidth);
        rsiDraw.setLineWidth(lineWidth);
        macdDraw.setLineWidth(lineWidth);
        kdjDraw.setLineWidth(lineWidth);
        wrDraw.setLineWidth(lineWidth);
        volumeDraw.setLineWidth(lineWidth);
    }

    /**
     * 主视图文本颜色
     *
     * @param color
     */
    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        mainDraw.setSelectorTextColor(color);
    }


    /**
     * 设置主视图是否是显示线否为K线
     *
     * @param isLine
     */
    public void setMainDrawLine(boolean isLine) {
        setShowLine(isLine);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (!isShowLoading) {
            super.onLongPress(e);
        }
    }

    /**
     * 设置Loading的View,如果有动画不会自动执行
     *
     * @param view   View
     * @param width  宽
     * @param height 高
     */
    public void setLoadingView(View view, int width, int height) {
        LayoutParams layoutParams = new LayoutParams(width, height);
        layoutParams.addRule(CENTER_IN_PARENT);
        addView(view, layoutParams);
        view.setVisibility(View.GONE);
        progressbar = view;
    }

    /**
     * 设置一个默认的LoadingView
     */
    private void setDefaultLoading() {
        ProgressBar progressbar = new ProgressBar(getContext());
        setLoadingView(progressbar, ViewUtil.Dp2Px(context, 50), ViewUtil.Dp2Px(context, 50));
    }
}
