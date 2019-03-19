package com.icechao.klinelib.base;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.icechao.klinelib.R;
import com.icechao.klinelib.adapter.KLineChartAdapter;
import com.icechao.klinelib.draw.Status;
import com.icechao.klinelib.entity.ICandle;
import com.icechao.klinelib.formatter.DateFormatter;
import com.icechao.klinelib.formatter.TimeFormatter;
import com.icechao.klinelib.formatter.ValueFormatter;
import com.icechao.klinelib.utils.ViewUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*************************************************************************
 * Description   : 去掉所有的Invalidate调用,只在价格发生变化时动画变化,或者显示分时线时分一直刷新页面
 *
 * @PackageName  : com.huobi.klinelib.utils
 * @FileName     : ViewUtil.java
 * @Author       : chao
 * @Date         : 2019/1/8
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public abstract class BaseKLineChartView extends ScrollAndScaleView {

    //是否以动画的方式绘制最后一根线
    protected boolean isAnimationLast = true;

    /**
     * 是否正在显示loading
     */
    protected boolean isShowLoading;

    /**
     * 动画执行时长
     */
    private long duration = 500;

    /**
     * 价格框距离屏幕右侧的边距
     */
    private float priceBoxMarginRight = 120;

    /**
     * 当前子视图的索引
     */
    private int childDrawPosition = 0;

    /**
     * 绘制K线时画板平移的距离
     */
    private float canvasTranslateX = 1f;

    private int width = 0;

    /**
     * 整体上部的padding
     */
    private int topPadding;

    private int childPadding;
    /**
     * 整体底部padding
     */
    private int bottomPadding;
    /**
     * y轴的缩放比例
     */
    private float mainScaleY = 1;

    /**
     * 成交量y轴缩放比例
     */
    private float volScaleY = 1;
    /**
     * 子视图y轴缩放比例
     */
    private float childScaleY = 1;

    /**
     * 主视图的最大值
     */
    private float mainMaxValue = Float.MAX_VALUE;
    /**
     * 主视图的最小值
     */
    private float mainMinValue = Float.MIN_VALUE;

    /**
     * 主视图K线的的最大值
     */
    private float mainHighMaxValue = 0;
    /**
     * 主视图的K线的最小值
     */
    private float mainLowMinValue = 0;
    /**
     * X轴最大值坐标索引
     */
    private int mainMaxIndex = 0;
    /**
     * X轴最小值坐标索引
     */
    private int mainMinIndex = 0;

    /**
     * 成交量最大值
     */
    private Float volMaxValue = Float.MAX_VALUE;
    /**
     * 成交量最小值
     */
    private Float childMaxValue = Float.MAX_VALUE;
    /**
     * 当前显示K线最左侧的索引
     */
    private int screenLeftIndex = 0;
    /**
     * 当前显示K线最右侧的索引
     */
    private int screenRightIndex = 0;

    /**
     * K线宽度
     */
    private float chartItemWidth = 6;

    /**
     * K线网格行数
     */
    private int gridRows = 5;

    /**
     * K线网格列数
     */
    private int gridColumns = 5;

    /**
     * 尾线画笔
     */
    private Paint lineEndPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 尾线下填充画笔
     */
    private Paint lineEndFillPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 视图背景画笔
     */
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 网络画笔
     */
    private Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 十字线画笔
     */
    private Paint selectedCrossPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint selectedbigCrossPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 文字画笔
     */
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 最大值最小值画笔
     */
    private Paint maxMinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 十字线横线画笔
     */
    private Paint selectedXLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 十字线竖线画笔
     */
    private Paint selectedYLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 十字线相交点画笔
     */
    private Paint selectedPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint selectorFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 价格线画笔
     */
    private Paint priceLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 当前价格边框画笔
     */
    private Paint priceLineBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 当前价格背景画笔
     */
    private Paint priceLineBoxBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 价格线右侧虚线画笔
     */
    private Paint priceLineBoxRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 当前选中索引
     */
    private int selectedIndex;

    /**
     * 价格框高度
     */
    private int priceLineBoxHeight = 40;

    /**
     * 交易量视图
     */

    /**
     * 当前K线的最新价
     */
    private float lastPrice, lastVol, lastHigh, lastLow;

    /**
     * K线数据适配器
     */
    private KLineChartAdapter dataAdapter;

    /**
     * 量视图是否显示为线
     */
    private boolean isLine;


    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mGreenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint volLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float pillarWidth = 0;
    private ValueFormatter volValueFormater = new ValueFormatter();
    private float[] points;


    /**
     * 重置所有数据
     * <p>
     * 可能会出现没效果,动画执行过程中,值改变
     */
    protected void resetValues() {
        lastPrice = 0;
        lastVol = 0;
        lastPrice = 0;
        lastHigh = 0;
        lastLow = 0;
        selectedIndex = -1;
        itemsCount = 0;
        screenLeftIndex = 0;
        screenRightIndex = 0;
    }

    /**
     * 数据观察者,当数据变化
     */
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            int dataCount = getAdapter().getCount();

            if (dataCount == 0) {
                setItemCount(dataCount);
            } else {
                int count = itemsCount;
                setItemCount(dataCount);
                ICandle item = getAdapter().getItem(itemsCount - 1);
                float closePrice = 0f;
                float vol = 0f;
                float high = 0f;
                float low = 0f;
                if (null != item) {
                    closePrice = item.getClosePrice();
                    vol = item.getVolume();
                    high = item.getHighPrice();
                    low = item.getLowPrice();
                }
                if (itemsCount > count) {
                    lastPrice = closePrice;
                    lastVol = vol;
                    lastHigh = high;
                    lastLow = low;
                    if (screenRightIndex == itemsCount - 2) {
                        setTranslatedX(canvasTranslateX - chartItemWidth * getScaleX());
                    }
                } else if (count == itemsCount) {
                    if (lastPrice != closePrice || lastVol != closePrice ||
                            lastHigh != high || low != low) {
                        excuteAnimChange(item, closePrice, vol);
                    }
                }
            }
            points = dataAdapter.getPoints();
            notifyChanged();
        }

        @Override
        public void onInvalidated() {
            isAnimationLast = false;
            canvasTranslateX = 1f;
            setItemCount(0);
            postDelayed(action, 500);
        }
    };
    private float selectedPointRadius = 5;

    /**
     * 当重置数据时,延时1s显示最后的加载动画
     */
    protected Runnable action = () -> isAnimationLast = true;

    /**
     * 执行动画渐变
     *
     * @param item
     * @param closePrice
     * @param vol
     */
    private void excuteAnimChange(ICandle item, float closePrice, float vol) {
        generaterAnimator(lastVol, vol, animation -> lastVol = (Float) animation.getAnimatedValue());
        generaterAnimator(lastPrice, closePrice, animation -> {
            lastPrice = (Float) animation.getAnimatedValue();
            if (isLine) {
                return;
            }
            animValidate();
        });
    }

    /**
     * 当前数据个数
     */
    private int itemsCount;
    @SuppressWarnings("unchecked")
    private List<IChartDraw> mChildDraws = new ArrayList<>();

    /**
     * Y轴上值的格式化
     */
    private IValueFormatter mValueFormatter = new ValueFormatter();

    /**
     * 日期格式化
     */
    private IDateTimeFormatter mDateTimeFormatter = new DateFormatter();

    /**
     * K线显示动画
     */
    private ValueAnimator mAnimator;

    private float mOverScrollRange = 0;

    private OnSelectedChangedListener mOnSelectedChangedListener = null;

    /**
     * 主视图
     */
    private Rect mainRect;

    /**
     * 量视图
     */
    private Rect volRect;

    /**
     * 子视图
     */
    private Rect childRect;

    /**
     * 分时线尾部点半径
     */
    private float lineEndPointWidth;

    /**
     * 最新数据变化的执行动画
     */
    private ValueAnimator valueAnimator;

    /**
     * 分时线填充渐变的上部颜色
     */
    private int areaTopColor;
    /**
     * 分时线填充渐变的下部颜色
     */
    private int areaBottomColor;

    /**
     * 十字线Y轴的宽度
     */
    private float selectedWidth;

    /**
     * 十字线Y轴的颜色
     */
    private int selectedYColor;

    /**
     * 背景色渐变上部颜色
     */
    private int backGroundTopColor;
    /**
     * 背景色渐变下部颜色
     */
    private int backGroundBottomColor;

    public BaseKLineChartView(Context context) {
        super(context);
        init();
    }

    public BaseKLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseKLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setWillNotDraw(false);
        gestureDetector = new GestureDetectorCompat(getContext(), this);
        scaleDetector = new ScaleGestureDetector(getContext(), this);
        topPadding = (int) getResources().getDimension(R.dimen.chart_top_padding);
        childPadding = (int) getResources().getDimension(R.dimen.child_top_padding);
        bottomPadding = (int) getResources().getDimension(R.dimen.chart_bottom_padding);

        mAnimator = ValueAnimator.ofFloat(0f, 1f);
        mAnimator.setDuration(duration);
        mAnimator.addUpdateListener(animation -> animValidate());
        selectorFramePaint.setStrokeWidth(ViewUtil.Dp2Px(getContext(), 0.6f));
        selectorFramePaint.setStyle(Paint.Style.STROKE);
        selectorFramePaint.setColor(Color.WHITE);
        priceLinePaint.setAntiAlias(true);
        priceLineBoxRightPaint.setStyle(Paint.Style.STROKE);

        priceLineBoxPaint.setColor(Color.WHITE);
        priceLineBoxBgPaint.setColor(Color.BLACK);

        priceLineBoxPaint.setStyle(Paint.Style.STROKE);
        priceLineBoxPaint.setStrokeWidth(1);

        int upColor = ContextCompat.getColor(getContext(), R.color.chart_up);
        mRedPaint.setColor(upColor);
        int downColor = ContextCompat.getColor(getContext(), R.color.chart_down);
        mGreenPaint.setColor(downColor);
        pillarWidth = ViewUtil.Dp2Px(getContext(), 4);


        //
        selectorBorderPaint.setStyle(Paint.Style.STROKE);
        upPaint.setStyle(Paint.Style.FILL);
        upLinePaint.setStyle(Paint.Style.STROKE);
        upLinePaint.setAntiAlias(true);
        downPaint.setStyle(Paint.Style.FILL);
        downLinePaint.setStyle(Paint.Style.STROKE);
        downLinePaint.setAntiAlias(true);
        padding = ViewUtil.Dp2Px(getContext(), 5);
        margin = ViewUtil.Dp2Px(getContext(), 5);
        marketInfoText[0] = ("时间   ");
        marketInfoText[1] = ("开     ");
        marketInfoText[2] = ("高     ");
        marketInfoText[3] = ("低     ");
        marketInfoText[4] = ("收     ");
        marketInfoText[5] = ("涨跌额  ");
        marketInfoText[6] = ("涨跌幅  ");
        marketInfoText[7] = ("成交量  ");


        mRedPaint.setColor(upColor);
        mGreenPaint.setColor(downColor);

        mRedPaint.setColor(upColor);
        mGreenPaint.setColor(downColor);
    }

    private float padding;
    private float margin;
    private String[] strings = new String[8];


    private float candleWidth = 0;
    private Paint lineAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint upPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint upLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint downPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint downLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private Paint indexPaintOne = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint indexPaintTwo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint indexPaintThree = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint selectorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint selectorBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint selectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String[] marketInfoText = new String[8];


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        displayHeight = h - topPadding - bottomPadding;
        initRect();
    }

    int displayHeight = 0;

    /**
     * 初始化视图区域
     * 主视图
     * 成交量视图
     * 子视图
     */
    private void initRect() {
        if (childDrawPosition > 0) {
            int mMainHeight = (int) (displayHeight * 0.6f);
            int mVolHeight = (int) (displayHeight * 0.2f);
            int mChildHeight = (int) (displayHeight * 0.2f);
            mainRect = new Rect(0, topPadding, width, topPadding + mMainHeight);
            volRect = new Rect(0, mainRect.bottom + childPadding, width, mainRect.bottom + mVolHeight);
            childRect = new Rect(0, volRect.bottom + childPadding, width, volRect.bottom + mChildHeight);
        } else {
            int mMainHeight = (int) (displayHeight * 0.8f);
            int mVolHeight = (int) (displayHeight * 0.2f);
            mainRect = new Rect(0, topPadding, width, topPadding + mMainHeight);
            volRect = new Rect(0, mainRect.bottom + childPadding, width, mainRect.bottom + mVolHeight);
        }
    }


    @Override
    public void onDraw(Canvas canvas) {
        long l = SystemClock.currentThreadTimeMillis();
        //当正在显示loading时,没有初始化好,数据为空时不绘制
        if (isShowLoading || width == 0 || mainRect.height() == 0 || itemsCount == 0) {
            return;
        }
        calculateValue();
        drawBackground(canvas);
        drawGird(canvas);

        if (isLine) {
            drawK(canvas);
            drawText(canvas);
            drawPriceLine(canvas);
        } else {
            drawPriceLine(canvas);
            drawK(canvas);
            drawText(canvas);
        }
        drawValue(canvas, isLongPress ? selectedIndex : screenRightIndex);
    }

    /**
     * 绘制整体背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        int mid = width / 2;
        canvas.drawColor(getContext().getResources().getColor(R.color.chart_background));
        backgroundPaint.setAlpha(18);
        backgroundPaint.setShader(new LinearGradient(mid, 0, mid, mainRect.bottom, backGroundTopColor, backGroundBottomColor, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, mainRect.bottom, backgroundPaint);
        backgroundPaint.setShader(new LinearGradient(mid, volRect.top - topPadding, mid, volRect.bottom, backGroundTopColor, backGroundBottomColor, Shader.TileMode.CLAMP));
        canvas.drawRect(0, mainRect.bottom, width, volRect.bottom, backgroundPaint);
        if (0 != childDrawPosition) {
            backgroundPaint.setShader(new LinearGradient(mid, childRect.top - topPadding, mid, childRect.bottom, backGroundTopColor, backGroundBottomColor, Shader.TileMode.CLAMP));
            canvas.drawRect(0, childRect.top, width, childRect.bottom, backgroundPaint);
        }
    }

    /**
     * 获取主视图value对应的Y值
     *
     * @param value
     * @return
     */
    public float getMainY(float value) {
        return (mainMaxValue - value) * mainScaleY + mainRect.top;
    }

    /**
     * 设置当前K线总数据个数
     *
     * @param itemsCount
     */
    public void setItemCount(int itemsCount) {
        //数据个数为0时重置本地保存数据,重置平移
        if (itemsCount == 0) {
            this.itemsCount = itemsCount;
            resetValues();
            canvasTranslateX = 1f;
        } else {
            this.itemsCount = itemsCount;
        }
        int size = mChildDraws.size();
        for (int i = 0; i < size; i++) {
            IChartDraw iChartDraw = mChildDraws.get(i);
            iChartDraw.setItemCount(0);
            iChartDraw.resetValues();
        }
        animValidate();
    }

    /**
     * 获取交易量视图上的value对应的Y值
     *
     * @param value
     * @return
     */
    public float getVolY(float value) {
        return (volMaxValue - value) * volScaleY + volRect.top;
    }

    /**
     * 获取子视图上的value对应的Y值
     *
     * @param value
     * @return
     */
    public float getChildY(float value) {
        return (childMaxValue - value) * childScaleY + childRect.top;
    }

    /**
     * 解决text居中的问题
     */
    public float fixTextYBaseBottom(float y) {
        return y + (textHeight) / 2 - textDecent;
    }

    /**
     * 画表格
     *
     * @param canvas canvas
     */
    private void drawGird(Canvas canvas) {
        canvas.drawLine(0, 0, width, 0, gridPaint);
        //横向的grid
        float rowSpace = displayHeight / gridRows;
        for (int i = 0; i <= gridRows; i++) {
            float y = rowSpace * i;
            canvas.drawLine(0, y + mainRect.top, width, y + mainRect.top, gridPaint);
        }
        canvas.drawLine(0, volRect.bottom, width, volRect.bottom, gridPaint);
        float columnSpace = width / gridColumns;
        for (int i = 1; i < gridColumns; i++) {
            float stopX = columnSpace * i;
            canvas.drawLine(stopX, 0, stopX, mainRect.bottom, gridPaint);
            canvas.drawLine(stopX, mainRect.bottom, stopX, volRect.bottom, gridPaint);
            if (0 != childDrawPosition) {
                canvas.drawLine(stopX, volRect.bottom, stopX, childRect.bottom, gridPaint);
            }
        }
    }


    private float maOne;
    private float maTwo;
    private float maThree;

    private float bollUp;
    private float bollMb;
    private float bollDn;


    private float lineVolWidth = 4;

    /**
     * 绘制k线图
     *
     * @param canvas canvas
     */
    private void drawK(Canvas canvas) {
        if (null == points || points.length == 0) {
            return;
        }
        canvas.save();
        canvas.translate(canvasTranslateX, 0);
        for (int i = screenLeftIndex; i <= screenRightIndex; i++) {

            int temp = GROUP_COUNT * i;

            float curHigh = points[temp + INDEX_HIGH];
            float curLow = points[temp + INDEX_LOW];
            float curOpen = points[temp + INDEX_OPEN];
            float curClose = points[temp + INDEX_CLOSE];
            float curVol = points[temp + INDEX_VOL];

            float lastX = getX(i - 1);
            float curX = getX(i);

            if (isLine()) {
                if (i == 0) {
                    continue;
                }
                float lastClosePrice = points[temp + INDEX_CLOSE - GROUP_COUNT];
                if (i == itemsCount - 1) {
                    drawEndMinutsLine(canvas, linePaint, lastX, lastClosePrice, curX);
                    drawEndMinutsLineArea(canvas, lineAreaPaint, lastX, lastClosePrice, curX);

                } else {
                    drawLine(canvas, linePaint, lastX, lastClosePrice, curX, curClose);
                    drawMinutsLineArea(canvas, lineAreaPaint, lastX, lastClosePrice, curX, curClose);
                }

            } else {
                drawCandle(canvas, curX, curHigh, curLow, curOpen, curClose, i);
                if (i == 0) {
                    continue;
                }
                Status status = getStatus();
                if (status == Status.MA) {
                    //画第一根ma
                    float lastMa = points[temp + INDEX_MA_1 - GROUP_COUNT];
                    float curMa = points[temp + INDEX_MA_1];
                    drawIndexLine(canvas, indexPaintOne, i, lastX, curX, lastMa, curMa, maOne);
                    //画第二根ma
                    lastMa = points[temp + INDEX_MA_2 - GROUP_COUNT];
                    curMa = points[temp + INDEX_MA_2];
                    drawIndexLine(canvas, indexPaintTwo, i, lastX, curX, lastMa, curMa, maTwo);
                    //画第三根ma
                    lastMa = points[temp + INDEX_MA_3 - GROUP_COUNT];
                    curMa = points[temp + INDEX_MA_3];
                    drawIndexLine(canvas, indexPaintThree, i, lastX, curX, lastMa, curMa, maThree);
                } else if (status == Status.BOLL) {
                    //画boll
                    float laseIndex = points[temp + INDEX_BOLL_UP - GROUP_COUNT];
                    float curIndex = points[temp + INDEX_BOLL_UP];
                    drawIndexLine(canvas, indexPaintOne, i, lastX, curX, laseIndex, curIndex, bollUp);
                    laseIndex = points[temp + INDEX_BOLL_MB - GROUP_COUNT];
                    curIndex = points[temp + INDEX_BOLL_MB];
                    drawIndexLine(canvas, indexPaintTwo, i, lastX, curX, laseIndex, curIndex, bollMb);
                    laseIndex = points[temp + INDEX_BOLL_DN - GROUP_COUNT];
                    curIndex = points[temp + INDEX_BOLL_DN];
                    drawIndexLine(canvas, indexPaintThree, i, lastX, curX, laseIndex, curIndex, bollDn);
                }
            }
            drawVol(canvas, i, curX, curOpen, curClose, curVol);
            float laseIndex = points[temp + INDEX_VOL_MA_1 - GROUP_COUNT];
            float curIndex = points[temp + INDEX_VOL_MA_1];
            drawVolLine(canvas, indexPaintOne, i, lastX, curX, laseIndex, curIndex, volMa1);
            laseIndex = points[temp + INDEX_VOL_MA_2 - GROUP_COUNT];
            curIndex = points[temp + INDEX_VOL_MA_2];
            drawVolLine(canvas, indexPaintTwo, i, lastX, curX, laseIndex, curIndex, volMa2);


            switch (childDrawPosition) {
                case CHILD_POSITION_MACD:
                    float r = MACDWidth / 2;
                    float macd = points[temp + INDEX_MACD];
                    if (macd > 0) {
                        canvas.drawRect(curX - r, getChildY(macd), curX + r, getChildY(0), mRedPaint);
                    } else {
                        canvas.drawRect(curX - r, getChildY(0), curX + r, getChildY(macd), mGreenPaint);
                    }

                    laseIndex = points[temp + INDEX_MACD_DEA - GROUP_COUNT];
                    curIndex = points[temp + INDEX_MACD_DEA];
                    drawChildLine(canvas, mDEAPaint, i, lastX, curX, laseIndex, curIndex, 0);
                    laseIndex = points[temp + INDEX_MACD_DIF - GROUP_COUNT];
                    curIndex = points[temp + INDEX_MACD_DIF];
                    drawChildLine(canvas, mDIFPaint, i, lastX, curX, laseIndex, curIndex, 0);


                    break;
                case CHILD_POSITION_KDJ:
                    laseIndex = points[temp + INDEX_KDJ_K - GROUP_COUNT];
                    curIndex = points[temp + INDEX_KDJ_K];
                    drawChildLine(canvas, mKPaint, i, lastX, curX, laseIndex, curIndex, 0);
                    laseIndex = points[temp + INDEX_KDJ_D - GROUP_COUNT];
                    curIndex = points[temp + INDEX_KDJ_D];
                    drawChildLine(canvas, mDPaint, i, lastX, curX, laseIndex, curIndex, 0);
                    laseIndex = points[temp + INDEX_KDJ_J - GROUP_COUNT];
                    curIndex = points[temp + INDEX_KDJ_J];
                    drawChildLine(canvas, mJPaint, i, lastX, curX, laseIndex, curIndex, 0);
                    break;
                case CHILD_POSITION_RSI:
                    laseIndex = points[temp + INDEX_RSI - GROUP_COUNT];
                    curIndex = points[temp + INDEX_RSI];
                    drawChildLine(canvas, mRSI1Paint, i, lastX, curX, laseIndex, curIndex, 0);

                    break;
                case CHILD_POSITION_WR:
                    laseIndex = points[temp + INDEX_WR_1 - GROUP_COUNT];
                    curIndex = points[temp + INDEX_WR_1];
                    drawChildLine(canvas, indexPaintOne, i, lastX, curX, laseIndex, curIndex, 0);
                    break;
            }
        }


        drawSelectedCross(canvas);

        drawMaxAndMin(canvas);
        canvas.restore();
    }


    private float volMa1;
    private float volMa2;
    private float MACDWidth;

    /**
     * 设置MACD的宽度
     *
     * @param MACDWidth
     */
    public void setMACDWidth(float MACDWidth) {
        this.MACDWidth = MACDWidth;
    }

    public static final int CHILD_POSITION_MACD = 1;
    public static final int CHILD_POSITION_KDJ = 2;
    public static final int CHILD_POSITION_RSI = 3;
    public static final int CHILD_POSITION_WR = 4;


    private void drawIndexLine(Canvas canvas, Paint indexPaint, int i, float lastX, float curX, float lastMa, float curMa, float animMa) {
        if (Float.MIN_VALUE != lastMa) {
            if (itemsCount - 1 == i && 0 != animMa) {
                drawLine(canvas, indexPaint, lastX, lastMa, curX, animMa);
            } else {
                drawLine(canvas, indexPaint, lastX, lastMa, curX, curMa);
            }
        }
    }

    private void drawVolLine(Canvas canvas, Paint indexPaint, int i, float lastX, float curX, float lastMa, float curMa, float animMa) {
        if (Float.MIN_VALUE != lastMa) {
            if (itemsCount - 1 == i && 0 != animMa) {
                drawVolLine(canvas, indexPaint, lastX, lastMa, curX, animMa);
            } else {
                drawVolLine(canvas, indexPaint, lastX, lastMa, curX, curMa);
            }
        }
    }

    private void drawChildLine(Canvas canvas, Paint indexPaint, int i, float lastX, float curX, float lastMa, float curMa, float animMa) {
        if (Float.MIN_VALUE != lastMa) {
            if (itemsCount - 1 == i && 0 != animMa) {
                drawChildLine(canvas, indexPaint, lastX, lastMa, curX, animMa);
            } else {
                drawChildLine(canvas, indexPaint, lastX, lastMa, curX, curMa);
            }
        }
    }

    private void drawVol(Canvas canvas, int i, float curX, float curOpen, float curClose, float curVol) {
        float r = pillarWidth / 2 * scaleX;
        float top;
        if (i == itemsCount - 1) {
            top = getVolY(lastVol);
        } else {
            top = getVolY(curVol);
        }
        //添加成交量柱状图高度限制
        int bottom = getVolRect().bottom;
        if (top > bottom) {
            top = bottom;
        }
        if (0 != lastVol && top > bottom - 1) {
            top = bottom - 1;
        }
        if (isLine()) {
            canvas.drawRect(curX - lineVolWidth, top, curX + lineVolWidth, bottom, linePaint);
        } else if (curClose > curOpen) {//涨
            canvas.drawRect(curX - r, top, curX + r, bottom, mRedPaint);
        } else {
            canvas.drawRect(curX - r, top, curX + r, bottom, mGreenPaint);
        }
    }


    private void drawCandle(Canvas canvas, float x, float high, float low, float open, float close, int position) {
        high = getMainY(high);
        low = getMainY(low);
        open = getMainY(open);
        if (position == itemsCount - 1) {
            close = getMainY(lastPrice);
        } else {
            close = getMainY(close);
        }
        float r = candleWidth / 2 * scaleX;
        float cancleLeft = x - r;
        float candleright = x + r;
//        Path path = new Path();

        if (open > close) {
//            path.moveTo(x, high);
//            path.lineTo(x, open);
//            path.moveTo(x, close);
//            path.lineTo(x, low);
            canvas.drawRect(cancleLeft, close, candleright, open, upPaint);
//            canvas.drawPath(path, upLinePaint);
            canvas.drawLine(x, high, x, open, upPaint);
            canvas.drawLine(x, close, x, low, upPaint);
        } else if (open < close) {
//            path.moveTo(x, high);
//            path.lineTo(x, close);
//            path.moveTo(x, open);
//            path.lineTo(x, low);
            canvas.drawRect(cancleLeft, open, candleright, close, downPaint);
//            canvas.drawPath(path, downLinePaint);
            canvas.drawLine(x, high, x, close, upPaint);
            canvas.drawLine(x, close, x, low, upPaint);
        } else {

//            path.moveTo(x, high);
//            path.lineTo(x, low);
            canvas.drawRect(cancleLeft, open, candleright, close + 1, upPaint);
            canvas.drawLine(x, high, x, low, upLinePaint);
        }
    }


    private void drawSelectedCross(Canvas canvas) {
        //画选择线
        if (isLongPress) {
//            IKLine point = (IKLine) getItem(selectedIndex);
            float x = getX(selectedIndex);
            float y = getMainY(points[selectedIndex * GROUP_COUNT + INDEX_CLOSE]);
            // k线图竖线
            float halfWidth = selectedWidth / 2 * scaleX;
            float left = x - halfWidth;
            float right = x + halfWidth;
            float bottom = displayHeight + topPadding;
            Path path = new Path();
            path.moveTo(left, topPadding);
            path.lineTo(right, topPadding);
            path.lineTo(right, bottom);
            path.lineTo(left, bottom);
            path.close();
            LinearGradient linearGradient = new LinearGradient(x, topPadding, x, bottom,
                    new int[]{Color.TRANSPARENT, selectedYColor, selectedYColor, Color.TRANSPARENT},
                    new float[]{0f, 0.2f, 0.8f, 1f}, Shader.TileMode.CLAMP);
            selectedYLinePaint.setShader(linearGradient);
            canvas.drawPath(path, selectedYLinePaint);
            canvas.drawLine(-canvasTranslateX, y, -canvasTranslateX + width - 5, y, selectedXLinePaint);
            canvas.drawCircle(x, y, chartItemWidth, selectedbigCrossPaint);
            canvas.drawCircle(x, y, selectedPointRadius, selectedCrossPaint);
        }
    }


    /**
     * 绘制文字
     *
     * @param canvas canvas
     */
    private void drawText(Canvas canvas) {

        int textY = mainRect.top - 5;
        float rowValue;
        int gridRowCount;
        float rowSpace = displayHeight / gridRows;
        //当显示子视图时,y轴label减少显示一个
        if (0 != childDrawPosition) {
            gridRowCount = gridRows - 2;
            rowValue = (mainMaxValue - mainMinValue) / gridRowCount;
        } else {
            gridRowCount = gridRows - 1;
            rowValue = (mainMaxValue - mainMinValue) / gridRowCount;
        }

        //Y轴上网络的值
        for (int i = 0; i <= gridRowCount; i++) {
            String text = formatValue(mainMaxValue - i * rowValue);
            canvas.drawText(text, width - textPaint.measureText(text), rowSpace * i + textY, textPaint);
        }

        //交易量图的Y轴label
        // TODO: 2019/3/19
        String maxVol = getValueFormatter().format(volMaxValue);
        canvas.drawText(maxVol, width - textPaint.measureText(maxVol), mainRect.bottom + baseLine, textPaint);

        //子图Y轴label
        if (0 != childDrawPosition) {
            String childLable = getValueFormatter().format(childMaxValue);
            canvas.drawText(childLable, width - textPaint.measureText(childLable), volRect.bottom + baseLine, textPaint);
        }
        //画时间
        float columnSpace = width / gridColumns;
        float y;
        if (0 != childDrawPosition) {
            y = childRect.bottom + baseLine + 5;
        } else {
            y = volRect.bottom + baseLine + 5;
        }

        float halfWidth = chartItemWidth / 2 * getScaleX();
        float startX = getX(screenLeftIndex) - halfWidth;
        float stopX = getX(screenRightIndex) + halfWidth;

        for (int i = 1; i < gridColumns; i++) {
            float tempX = columnSpace * i;
            float translateX = xToTranslateX(tempX);
            if (translateX >= startX && translateX <= stopX) {
                int index = indexOfTranslateX(translateX);
                String text = dataAdapter.getDate(index);
                canvas.drawText(text, tempX - textPaint.measureText(text) / 2, y, textPaint);
            }
        }
        //X轴最左侧的值
        float translateX = xToTranslateX(0);
        if (translateX >= startX && translateX <= stopX) {
            canvas.drawText((getAdapter().getDate(screenLeftIndex)), 0, y, textPaint);
        }
        //X轴最右侧的值
        translateX = xToTranslateX(width);
        if (translateX >= startX && translateX <= stopX) {
            String text = (getAdapter().getDate(screenRightIndex));
            canvas.drawText(text, width - textPaint.measureText(text), y, textPaint);
        }
        if (isLongPress) {
            // 选中状态下的Y值
//            IKLine point = (IKLine) getItem(selectedIndex);
            float textHorizentalPadding = ViewUtil.Dp2Px(getContext(), 5);
            float textVerticalPadding = ViewUtil.Dp2Px(getContext(), 3);
            float r = textHeight / 2 + textVerticalPadding;
            float closePrice = points[selectedIndex * GROUP_COUNT + INDEX_CLOSE];
            y = getMainY(closePrice);
            float x;
            String text = formatValue(closePrice);
            float textWidth = textPaint.measureText(text);
            float tempX = textWidth + 2 * textHorizentalPadding;
            //左侧框
            float boxTop = y - r;
            float boXBottom = y + r;
            if (translateXtoX(getX(selectedIndex)) < getChartWidth() / 2) {
                x = 1;
                Path path = new Path();
                path.moveTo(x, boxTop);
                path.lineTo(x, boXBottom);
                path.lineTo(tempX, boXBottom);
                path.lineTo(tempX + textVerticalPadding * 2, y);
                path.lineTo(tempX, boxTop);
                path.close();
                canvas.drawPath(path, selectedPointPaint);
                canvas.drawPath(path, selectorFramePaint);
                canvas.drawText(text, x + textHorizentalPadding, fixTextYBaseBottom(y), textPaint);
            } else {//右侧框
                x = width - textWidth - 1 - 2 * textHorizentalPadding - textVerticalPadding;
                Path path = new Path();
                path.moveTo(x, y);
                path.lineTo(x + textVerticalPadding * 2, boXBottom);
                path.lineTo(width - 2, boXBottom);
                path.lineTo(width - 2, boxTop);
                path.lineTo(x + textVerticalPadding * 2, boxTop);
                path.close();
                canvas.drawPath(path, selectedPointPaint);
                canvas.drawPath(path, selectorFramePaint);
                canvas.drawText(text, x + textVerticalPadding + textHorizentalPadding, fixTextYBaseBottom(y), textPaint);
            }

            // 画X值
            String date = (dataAdapter.getDate(selectedIndex));
            textWidth = textPaint.measureText(date);
            r = textHeight / 2;
            x = translateXtoX(getX(selectedIndex));
            if (0 != childDrawPosition) {
                y = childRect.bottom;
            } else {
                y = volRect.bottom;
            }

            float halfTextWidth = textWidth / 2;
            if (x < tempX) {
                x = 1 + halfTextWidth + textHorizentalPadding;
            } else if (width - x < tempX) {
                x = width - 1 - halfTextWidth - textHorizentalPadding;
            }

            float tempLeft = x - halfTextWidth;
            float left = tempLeft - textHorizentalPadding;
            float right = x + halfTextWidth + textHorizentalPadding;
            float bottom = y + baseLine + r - 2;
            canvas.drawRect(left, y, right, bottom, selectedPointPaint);
            canvas.drawRect(left, y, right, bottom, selectorFramePaint);
            canvas.drawText(date, tempLeft, fixTextYBaseBottom((bottom + y) / 2), textPaint);
        }
    }

    /**
     * 绘制最后一个呼吸灯效果
     *
     * @param canvas canvas
     * @param stopX  x
     */
    public void drawEndPoint(Canvas canvas, float stopX) {
        RadialGradient radialGradient = new RadialGradient(stopX, getMainY(lastPrice), endShadowLayerWidth, lineEndPointPaint.getColor(), Color.TRANSPARENT, Shader.TileMode.CLAMP);
        lineEndPointPaint.setShader(radialGradient);
        canvas.drawCircle(stopX, getMainY(lastPrice), lineEndPointWidth * 4, lineEndPointPaint);

    }


    /**
     * 绘制横向的价格线
     *
     * @param canvas
     */
    private void drawPriceLine(Canvas canvas) {
        float y = getMainY(lastPrice);
        String priceString = getValueFormatter().format(lastPrice);
        //多加2个像素防止文字宽度有小的变化
        float textWidth = textPaint.measureText(priceString);
        float textLeft = width - textWidth;
        float endLineRight = getX(screenRightIndex) + canvasTranslateX;
        if (screenRightIndex == itemsCount - 1 && endLineRight < textLeft) {
            //两个价格图层在点之间所以放在价格线中绘制
            if (isLine) {
                drawEndPoint(canvas, endLineRight);
            }
            //8 : 4 虚线
            Path path = new Path();
            for (float i = endLineRight; i < textLeft - 2; i += 12) {
                path.moveTo(i, y);
                path.lineTo(i + 8, y);
            }
            canvas.drawPath(path, priceLineBoxRightPaint);
            canvas.drawText(priceString, textLeft, fixTextYBaseBottom(y), priceLineBoxRightPaint);
            //绘制价格圆点
            if (isLine) {
                canvas.drawCircle(endLineRight, y, lineEndPointWidth, lineEndFillPointPaint);
            }
        } else {
            float halfPriceBoxHeight = priceLineBoxHeight >> 1;
            //修改价格信息框Y轴计算保证,只会绘制在主区域中
            if (lastPrice > mainMaxValue) {
                y = mainRect.top + halfPriceBoxHeight;
            } else if (lastPrice < mainMinValue) {
                y = mainRect.bottom - halfPriceBoxHeight;
            }

            Path path = new Path();
            for (int i = 0; i < width; i += 12) {
                path.moveTo(i, y);
                path.lineTo(i + 8, y);
            }

            canvas.drawPath(path, priceLinePaint);
            //绘制价格框
            float halfHeight = textHeight / 2;
            float boxRight = width - priceBoxMarginRight;
            float boxLeft = boxRight - textWidth - priceLineBoxHeight - halfHeight;
            float boxTop = y - halfPriceBoxHeight;
            float boxBottom = y + halfPriceBoxHeight;
            canvas.drawRoundRect(new RectF(boxLeft, boxTop, boxRight, boxBottom), halfPriceBoxHeight, halfPriceBoxHeight, priceLineBoxBgPaint);
            canvas.drawRoundRect(new RectF(boxLeft, boxTop, boxRight, boxBottom), halfPriceBoxHeight, halfPriceBoxHeight, priceLineBoxPaint);

            //价格框三角形
            float top = y - halfHeight / 2;
            float bottom = y + halfHeight / 2;
            float shapeLeft = boxRight - halfPriceBoxHeight;
            Path shape = new Path();
            shape.moveTo(shapeLeft, top);
            shape.lineTo(shapeLeft, bottom);
            shape.lineTo(shapeLeft + halfHeight / 2, y);
            shape.close();
            canvas.drawPath(shape, textPaint);
            canvas.drawText(priceString, shapeLeft - textWidth - halfHeight, (y + (halfHeight - textDecent)), textPaint);
        }
    }

    protected float getDataLength() {
        return chartItemWidth * getScaleX() * (itemsCount - 1) + getmOverScrollRange();
    }

    /**
     * 开启循环刷新绘制
     */
    public void startFreshPage() {
        if (null != valueAnimator && valueAnimator.isRunning()) {
            return;
        }
        valueAnimator = ValueAnimator.ofFloat(lineEndPointWidth, lineEndPointWidth * 4);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(10000);
        valueAnimator.addUpdateListener(animation -> {
            endShadowLayerWidth = (Float) animation.getAnimatedValue();
            animValidate();
        });
        valueAnimator.start();
    }

    /**
     * 关闭循环刷新
     */
    public void stopFreshPage() {
        if (null != valueAnimator && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = null;
    }


    /**
     * 画文字
     *
     * @param canvas canvas
     */
    private void drawMaxAndMin(Canvas canvas) {
        if (!isLine) {
            //绘制最大值和最小值
            float x = (getX(mainMinIndex));
            float y = getMainY(mainLowMinValue);
            //计算显示位置
            y = y + maxMinTextHeight / 2 - maxMinTextDecent;
            String LowString;
            float stringWidth, screenMid = getX((screenRightIndex + screenLeftIndex) / 2);
            if (x < screenMid) {
                LowString = "── " + Float.toString(mainLowMinValue);
            } else {
                LowString = Float.toString(mainLowMinValue) + " ──";
                stringWidth = maxMinPaint.measureText(LowString);
                x -= stringWidth;
            }
            canvas.drawText(LowString, x, y, maxMinPaint);
            x = getX(mainMaxIndex);
            y = getMainY(mainHighMaxValue);
            String highString;
            y = y + maxMinTextHeight / 2 - maxMinTextDecent;
            if (x < screenMid) {
                highString = "── " + Float.toString(mainHighMaxValue);
            } else {
                highString = Float.toString(mainHighMaxValue) + " ──";
                stringWidth = maxMinPaint.measureText(highString);
                x -= stringWidth;
            }
            canvas.drawText(highString, x, y, maxMinPaint);
        }
    }

    /**
     * 画值
     *
     * @param canvas canvas
     * @param i      显示某个点的值
     */
    private void drawValue(Canvas canvas, int i) {
        float x = 10;
        float y;
//        mainDraw.drawText(canvas, this, i, x, y);
        y = textHeight;
        int temp = i * GROUP_COUNT;
        if (!isLine()) {
            y += 10;
            Status status = getStatus();
            if (status == Status.MA) {
                String text;
                float point = points[temp + INDEX_MA_1];
                if (Float.MIN_VALUE != point) {
                    text = "MA5:" + formatValue(point) + "  ";
                    canvas.drawText(text, x, y, indexPaintOne);
                    x += indexPaintOne.measureText(text);
                }
                point = points[temp + INDEX_MA_2];
                if (Float.MIN_VALUE != point) {
                    text = "MA10:" + formatValue(point) + "  ";
                    canvas.drawText(text, x, y, indexPaintTwo);
                    x += indexPaintTwo.measureText(text);
                }
                point = points[temp + INDEX_MA_3];
                if (Float.MIN_VALUE != point) {
                    text = "MA30:" + formatValue(point);
                    canvas.drawText(text, x, y, indexPaintThree);
                }
            } else if (status == Status.BOLL) {
                float point = points[temp + INDEX_BOLL_MB];
                if (Float.MIN_VALUE != point) {
                    String text = "BOLL:" + formatValue(point) + "  ";
                    canvas.drawText(text, x, y, indexPaintTwo);
                    x += indexPaintOne.measureText(text);
                    point = points[temp + INDEX_BOLL_UP];
                    text = "UB:" + formatValue(point) + "  ";
                    canvas.drawText(text, x, y, indexPaintOne);
                    x += indexPaintTwo.measureText(text);
                    point = points[temp + INDEX_BOLL_DN];
                    text = "LB:" + formatValue(point);
                    canvas.drawText(text, x, y, indexPaintThree);
                }
            }
        }
        if (isLongPress()) {
            drawSelector(this, canvas);
        }


//        volDraw.drawText(canvas, this, i, x, );

        //当没被选中时使用动画效果
        String text;
        y = mainRect.bottom + baseLine;
        x = 10;
        if (i == itemsCount - 1) {
            text = "VOL:" + volValueFormater.format(getLastVol()) + "  ";
        } else {
            text = "VOL:" + volValueFormater.format(points[temp + INDEX_VOL]) + "  ";
        }
        canvas.drawText(text, x, y, volLeftPaint);
        x += getTextPaint().measureText(text);
        text = "MA5:" + getValueFormatter().format(points[temp + INDEX_VOL_MA_1]) + "  ";
        canvas.drawText(text, x, y, ma5Paint);
        x += ma5Paint.measureText(text);
        text = "MA10:" + getValueFormatter().format(points[temp + INDEX_VOL_MA_2]);
        canvas.drawText(text, x, y, ma10Paint);

//        if (null != childDraw) {
//            childDraw.drawText(canvas, this, i, x, volRect.bottom + baseLine);
//        }

        x = 10;
        y = volRect.bottom + baseLine;
        switch (childDrawPosition) {
            case CHILD_POSITION_MACD:
                text = "MACD(12,26,9)  ";
                canvas.drawText(text, x, y, getTextPaint());
                x += mMACDPaint.measureText(text);
                text = "MACD:" + formatValue(points[temp + INDEX_MACD]) + "  ";
                canvas.drawText(text, x, y, mMACDPaint);
                x += mMACDPaint.measureText(text);
                text = "DIF:" + formatValue(points[temp + INDEX_MACD_DIF]) + "  ";
                canvas.drawText(text, x, y, mDIFPaint);
                x += mDIFPaint.measureText(text);
                text = "DEA:" + formatValue(points[temp + INDEX_MACD_DEA]);
                canvas.drawText(text, x, y, mDEAPaint);
                break;
            case CHILD_POSITION_KDJ:
                float point = points[temp + INDEX_KDJ_K];
                if (Float.MIN_VALUE != point) {
                    text = "KDJ(14,1,3)  ";
                    canvas.drawText(text, x, y, getTextPaint());
                    x += getTextPaint().measureText(text);
                    text = "K:" + formatValue(points[temp + INDEX_KDJ_K]) + " ";
                    canvas.drawText(text, x, y, mKPaint);
                    x += mKPaint.measureText(text);
                    point = points[temp + INDEX_KDJ_D];
                    if (Float.MIN_VALUE != point) {
                        text = "D:" + formatValue(points[temp + INDEX_KDJ_D]) + " ";
                        canvas.drawText(text, x, y, mDPaint);
                        x += mDPaint.measureText(text);
                        text = "J:" + formatValue(points[temp + INDEX_KDJ_J]) + " ";
                        canvas.drawText(text, x, y, mJPaint);
                    }
                }
                break;
            case CHILD_POSITION_RSI:
                point = points[temp + INDEX_RSI];

                if (Float.MIN_VALUE != point) {
                    text = "RSI(14)  ";
                    Paint textPaint = getTextPaint();
                    canvas.drawText(text, x, y, textPaint);
                    x += textPaint.measureText(text);
                    text = formatValue(point);
                    canvas.drawText(text, x, y, mRSI1Paint);
                }

                break;
            case CHILD_POSITION_WR:

                point = points[temp + INDEX_WR_1];
                if (Float.MIN_VALUE != point) {
                    text = "WR(14):";
                    canvas.drawText(text, x, y, getTextPaint());
                    x += getTextPaint().measureText(text);
                    text = formatValue(point) + " ";
                    canvas.drawText(text, x, y, r1Paint);
                }


                break;
        }
    }


    private Paint mDIFPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDEAPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mMACDPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mKPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mJPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mRSI1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRSI2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRSI3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint r1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint r2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint r3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    /**
     * 格式化值
     */
    public String formatValue(float value) {
        if (null == getDateTimeFormatter()) {
            setValueFormatter(new ValueFormatter());
        }
        return getValueFormatter().format(value);
    }

    /**
     * 重新计算并刷新线条
     */
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public void notifyChanged() {
        if (1f == canvasTranslateX && width != 0) {
            setTranslatedX(-(getDataLength() - width));
        }
        animValidate();
    }

    /**
     * MA/BOLL切换及隐藏
     *
     * @param status MA/BOLL/NONE
     */
    public void changeMainDrawType(Status status) {
        if (this.status != status) {
            this.status = status;
            animValidate();
        }
    }

    public Status getStatus() {
        return this.status;
    }

    private Status status = Status.MA;

    /**
     * 计算当前选中item的X的坐标
     *
     * @param x index of selected item
     */
    private void calculateSelectedX(float x) {
        selectedIndex = indexOfTranslateX(xToTranslateX(x));
        if (selectedIndex > screenRightIndex) {
            selectedIndex = screenRightIndex;
        }
        if (selectedIndex < screenLeftIndex) {
            selectedIndex = screenLeftIndex;
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        int lastIndex = selectedIndex;
        calculateSelectedX(e.getX());
        if (lastIndex != selectedIndex) {
            onSelectedChanged(this, getItem(selectedIndex), selectedIndex);
        }
        invalidate();
    }

    private float getMinTranslate() {

        float dataLength = getDataLength();
        if (width == 0) {
            width = getMeasuredWidth();
        }
        if (dataLength >= width) {
            return -(dataLength - width);
        }
        return width - dataLength;
    }


    /**
     * 获取平移的最大值
     *
     * @return 最大值
     */
    private float getMaxTranslateX() {
        float dataLength = getDataLength();
        if (dataLength >= width) {
            return isLine ? 0 : chartItemWidth * getScaleX() / 2;
        }
        return width - dataLength + getmOverScrollRange() - (isLine ? 0 : chartItemWidth * getScaleX() / 2);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        super.onScrollChanged(l, t, oldl, oldt);
        setTranslatedX(canvasTranslateX + (l - oldl));
        if (isLine && getX(screenRightIndex) + canvasTranslateX <= width) {
            startFreshPage();
        } else {
            stopFreshPage();
        }
    }

    @Override
    protected void onScaleChanged(float scale, float oldScale) {
        //通过 放大和左右左右个数设置左移
        if (scale == oldScale) {
            return;
        }
        float tempWidth = chartItemWidth * scale;
        float newCount = (width / tempWidth);
        float oldCount = (width / chartItemWidth / oldScale);
        float difCount = (newCount - oldCount) / 2;
        setTranslatedX(canvasTranslateX / oldScale * scale + difCount * tempWidth);
        super.onScaleChanged(scale, oldScale);
    }


    /**
     * 设置当前平移
     *
     * @param mTranslateX
     */
    public void setTranslatedX(float mTranslateX) {
        if (mTranslateX < getMinTranslate()) {
            mTranslateX = getMinTranslate();
        } else if (mTranslateX > getMaxTranslateX()) {
            mTranslateX = getMaxTranslateX();
        }
        this.canvasTranslateX = mTranslateX;
    }

    /**
     * 计算当前显示的数据以及显示的数据的最大最小值
     */
    private void calculateValue() {
        if (!isLongPress()) {
            selectedIndex = -1;
        }
        float scaleWidth = chartItemWidth * scaleX;

        if (canvasTranslateX <= scaleWidth / 2) {
            screenLeftIndex = (int) ((-canvasTranslateX) / scaleWidth);
            if (screenLeftIndex < 0) {
                screenLeftIndex = 0;
            }
            screenRightIndex = (int) (screenLeftIndex + width / scaleWidth + 0.5) + 1;

        } else {
            screenLeftIndex = 0;
            screenRightIndex = itemsCount - 1;
        }
        if (screenRightIndex > itemsCount - 1) {
            screenRightIndex = itemsCount - 1;
        }
        mainMaxValue = Float.MIN_VALUE;
        mainMinValue = Float.MAX_VALUE;
        volMaxValue = Float.MIN_VALUE;
        Float mVolMinValue = Float.MAX_VALUE;
        childMaxValue = Float.MIN_VALUE;
        Float mChildMinValue = Float.MAX_VALUE;
        mainMaxIndex = screenLeftIndex;
        mainMinIndex = screenLeftIndex;
        mainHighMaxValue = Float.MIN_VALUE;
        mainLowMinValue = Float.MAX_VALUE;
        //为保证页面效果正常,右侧取值时取值时多取一个,在计算最大值时需要抛出

        int tempLeft = screenLeftIndex > 0 ? screenLeftIndex + 1 : 0;
        for (int i = tempLeft; i <= screenRightIndex - 1; i++) {

            if (i < 0) {
                continue;
            }
            int temp = i * GROUP_COUNT;

            mainMaxValue = Math.max(mainMaxValue, getMaxValue(points[temp + INDEX_HIGH],
                    points[temp + INDEX_BOLL_UP], points[temp + INDEX_MA_1],
                    points[temp + INDEX_MA_2], points[temp + INDEX_MA_3],
                    status));
            mainMinValue = Math.min(mainMinValue, getMinValue(points[temp + INDEX_LOW],
                    points[temp + INDEX_BOLL_DN], points[temp + INDEX_MA_1],
                    points[temp + INDEX_MA_2], points[temp + INDEX_MA_3],
                    status));
            if (mainHighMaxValue != Math.max(mainHighMaxValue, points[temp + INDEX_HIGH])) {
                mainHighMaxValue = points[temp + INDEX_HIGH];
                mainMaxIndex = i;
            }
            if (mainLowMinValue != Math.min(mainLowMinValue, points[temp + INDEX_LOW])) {
                mainLowMinValue = points[temp + INDEX_LOW];
                mainMinIndex = i;
            }

            float volume = points[temp + INDEX_VOL];
            volMaxValue = Math.max(volume, volMaxValue);
            //最后一个量不计入最小值,防止量图跳
            if (volume < mVolMinValue) {
                mVolMinValue = volume;
            }
            switch (childDrawPosition) {
                case CHILD_POSITION_MACD:
                    childMaxValue = Math.max(childMaxValue, Math.max(points[temp + INDEX_MACD], Math.max(points[temp + INDEX_MACD_DEA], points[temp + INDEX_MACD_DIF])));
                    mChildMinValue = Math.min(mChildMinValue, Math.min(points[temp + INDEX_MACD], Math.min(points[temp + INDEX_MACD_DEA], points[temp + INDEX_MACD_DIF])));
                    break;
                case CHILD_POSITION_KDJ:
                    childMaxValue = Math.max(childMaxValue, Math.max(points[temp + INDEX_KDJ_K], Math.max(points[temp + INDEX_KDJ_D], points[temp + INDEX_KDJ_J])));
                    mChildMinValue = Math.min(mChildMinValue, Math.min(points[temp + INDEX_KDJ_K], Math.min(points[temp + INDEX_KDJ_D], points[temp + INDEX_KDJ_J])));
                    break;
                case CHILD_POSITION_RSI:
                    childMaxValue = Math.max(childMaxValue, points[temp + INDEX_RSI]);
                    mChildMinValue = Math.min(mChildMinValue, points[temp + INDEX_RSI]);
                case CHILD_POSITION_WR:
                    childMaxValue = Math.max(childMaxValue, points[temp + INDEX_WR_1]);
                    mChildMinValue = Math.min(mChildMinValue, points[temp + INDEX_WR_1]);
                    break;
            }
        }
        if (mainMaxValue != mainMinValue) {
            float padding = (mainMaxValue - mainMinValue) * 0.05f;
            mainMaxValue += padding;
            mainMinValue -= padding;
        } else {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            mainMaxValue += Math.abs(mainMaxValue * 0.05f);
            mainMinValue -= Math.abs(mainMinValue * 0.05f);
            if (mainMaxValue == 0) {
                mainMaxValue = 1;
            }
        }

        if (Math.abs(volMaxValue) < 0.01) {
            volMaxValue = 15.00f;
        }

        if (childMaxValue.equals(mChildMinValue)) {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            childMaxValue += Math.abs(childMaxValue * 0.05f);
            mChildMinValue -= Math.abs(mChildMinValue * 0.05f);
            if (childMaxValue == 0) {
                childMaxValue = 1f;
            }
        }

        if (5 == childDrawPosition) {
            childMaxValue = 0f;
            if (Math.abs(mChildMinValue) < 0.01)
                mChildMinValue = -10.00f;
        }
        mainScaleY = mainRect.height() * 1f / (mainMaxValue - mainMinValue);
        volScaleY = volRect.height() * 1f / (volMaxValue - mVolMinValue);
        if (null != childRect)
            childScaleY = childRect.height() * 1f / (childMaxValue - mChildMinValue);
        if (mAnimator.isRunning()) {
            float value = (float) mAnimator.getAnimatedValue();
            this.screenRightIndex = screenLeftIndex + Math.round(value * (this.screenRightIndex - screenLeftIndex));
        }

    }

    public int indexOfTranslateX(float translateX) {
        return (int) (translateX / chartItemWidth / getScaleX());
    }


    public float getMaxValue(float high, float up, float maOne, float maTwo, float maThree, Status status) {
        float[] temp;
        if (status == Status.BOLL) {
            return Math.min(up, high);
        } else {
            temp = new float[]{maOne, maTwo, maThree, high};
        }
        Arrays.sort(temp);
        return temp[temp.length - 1];
    }

    public float getMinValue(float low, float dn, float maOne, float maTwo, float maThree, Status status) {
        float[] temp;
        if (status == Status.BOLL) {
            return Math.min(dn, low);
        } else {
            temp = new float[]{maOne, maTwo, maThree, low};
        }
        Arrays.sort(temp);
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != Float.MIN_VALUE) {
                return temp[i];
            }
        }
        return 0;
    }


    /**
     * 在主区域画线
     *
     * @param startX    开始点的横坐标
     * @param stopX     开始点的值
     * @param stopValue 结束点的值
     */
    public void drawLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue) {
        canvas.drawLine(startX, getMainY(startValue), stopX, getMainY(stopValue), paint);
    }


    /**
     * 绘制分时线尾部
     *
     * @param canvas
     * @param paint
     * @param startX
     * @param startValue
     * @param stopX
     */
    public void drawEndMinutsLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX) {
        canvas.drawLine(startX, getMainY(startValue), stopX, getMainY(lastPrice), paint);
    }


    /**
     * 绘制分时线尾部填充色
     *
     * @param canvas
     * @param paint
     * @param startX
     * @param startValue
     * @param stopX
     */
    public void drawEndMinutsLineArea(Canvas canvas, Paint paint, float startX, float startValue, float stopX) {
        int y = displayHeight + topPadding + bottomPadding;
        LinearGradient linearGradient = new LinearGradient(startX, topPadding,
                stopX, y, areaTopColor, areaBottomColor, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        Path path = new Path();
        path.moveTo(startX, y);
        path.lineTo(startX, getMainY(startValue));
        path.lineTo(stopX, getMainY(lastPrice));
        path.lineTo(stopX, y);
        path.close();
        canvas.drawPath(path, paint);


    }

    private float endShadowLayerWidth = 20;

    /**
     * 在主区域画分时线
     *
     * @param startX    开始点的横坐标
     * @param stopX     开始点的值
     * @param stopValue 结束点的值
     */
    public void drawMinutsLineArea(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue) {

        int y = displayHeight + topPadding + bottomPadding;
        LinearGradient linearGradient = new LinearGradient(startX, topPadding,
                stopX, y, areaTopColor, areaBottomColor, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        float mainY = getMainY(stopValue);
        Path path = new Path();
        path.moveTo(startX, y);
        path.lineTo(startX, getMainY(startValue));
        path.lineTo(stopX, mainY);
        path.lineTo(stopX, y);
        path.close();
        canvas.drawPath(path, paint);
    }

    /**
     * 在子区域画线
     *
     * @param startX     开始点的横坐标
     * @param startValue 开始点的值
     * @param stopX      结束点的横坐标
     * @param stopValue  结束点的值
     */
    public void drawChildLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue) {
        canvas.drawLine(startX, getChildY(startValue), stopX, getChildY(stopValue), paint);
    }

    /**
     * 在子区域画线
     *
     * @param startX     开始点的横坐标
     * @param startValue 开始点的值
     * @param stopX      结束点的横坐标
     * @param stopValue  结束点的值
     */
    public void drawVolLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue) {
        canvas.drawLine(startX, getVolY(startValue), stopX, getVolY(stopValue), paint);
    }

    /**
     * 根据索引获取实体
     *
     * @param i 索引值
     * @return 实体
     */
    public ICandle getItem(int i) {
        if (null != dataAdapter) {
            return dataAdapter.getItem(i);
        } else {
            return null;
        }
    }

    /**
     * 画板上的坐标点
     *
     * @param i 索引值
     * @return x坐标
     */
    public float getX(int i) {
        return i * chartItemWidth * scaleX;
    }

    /**
     * 获取适配器
     *
     * @return 获取适配器
     */
    public IAdapter<ICandle> getAdapter() {
        return dataAdapter;
    }

    /**
     * 设置当前子图
     *
     * @param i i
     */
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public void setChildDraw(int i) {
        if (childDrawPosition != i) {
            childDrawPosition = i;
            initRect();
            animValidate();
        }
    }

    /**
     * 隐藏子图
     */
    public void hideChildDraw() {
        childDrawPosition = -1;
        initRect();
        animValidate();
    }

    /**
     * 获取ValueFormatter
     *
     * @return IValueFormatter
     */
    public IValueFormatter getValueFormatter() {
        return mValueFormatter;
    }

    /**
     * 设置ValueFormatter
     *
     * @param valueFormatter value格式化器
     */
    public void setValueFormatter(IValueFormatter valueFormatter) {
        this.mValueFormatter = valueFormatter;
//        mainDraw.setValueFormatter(valueFormatter);
        for (int i = 0; i < mChildDraws.size(); i++) {
            mChildDraws.get(i).setValueFormatter(valueFormatter);
        }
    }

    /**
     * 获取DatetimeFormatter
     *
     * @return 时间格式化器
     */
    public IDateTimeFormatter getDateTimeFormatter() {
        return mDateTimeFormatter;
    }

    /**
     * 设置dateTimeFormatter
     *
     * @param dateTimeFormatter 时间格式化器
     */
    public void setDateTimeFormatter(IDateTimeFormatter dateTimeFormatter) {
        mDateTimeFormatter = dateTimeFormatter;
    }

    /**
     * 格式化时间
     *
     * @param date date
     */
    @SuppressWarnings("unused")
    public String formatDateTime(Date date) {
        if (null == getDateTimeFormatter()) {
            setDateTimeFormatter(new TimeFormatter());
        }
        return getDateTimeFormatter().format(date);
    }


    /**
     * 设置数据适配器
     */
    public void setAdapter(KLineChartAdapter adapter) {
        if (null != dataAdapter && null != dataSetObserver) {
            dataAdapter.unregisterDataSetObserver(dataSetObserver);
        }
        dataAdapter = adapter;
        if (null == dataAdapter || null == dataSetObserver) {
            itemsCount = 0;
            return;

        }
        dataAdapter.registerDataSetObserver(dataSetObserver);
        if (dataAdapter.getCount() > 0) {
            dataAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 开始动画
     */
    public void startAnimation() {
        if (null != mAnimator) {
            mAnimator.start();
        }
    }

    /**
     * 设置动画时间
     */
    @SuppressWarnings("unused")
    public void setAnimationDuration(long duration) {
        if (null != mAnimator) {
            mAnimator.setDuration(duration);
        }
    }

    /**
     * 设置表格行数
     */
    public void setGridRows(int gridRows) {
        if (gridRows < 1) {
            gridRows = 1;
        }
        this.gridRows = gridRows;
    }

    /**
     * 设置表格列数
     */
    public void setGridColumns(int gridColumns) {
        if (gridColumns < 1) {
            gridColumns = 1;
        }
        this.gridColumns = gridColumns;
    }

    /**
     * view中的x转化为TranslateX
     *
     * @param x x
     * @return translateX
     */
    public float xToTranslateX(float x) {
        return -canvasTranslateX + x;
    }

    /**
     * translateX转化为view中的x
     *
     * @param translateX translateX
     * @return x
     */
    public float translateXtoX(float translateX) {
        return translateX + canvasTranslateX;
    }

    /**
     * 获取上方padding
     */
    public float getTopPadding() {
        return topPadding;
    }

    /**
     * 获取上方padding
     */
    @SuppressWarnings("unused")
    public float getChildPadding() {
        return childPadding;
    }

    /**
     * 获取子试图上方padding
     */
    @SuppressWarnings("unused")
    public float getmChildScaleYPadding() {
        return childPadding;
    }

    /**
     * 获取图的宽度
     *
     * @return 宽度
     */
    public int getChartWidth() {
        return width;
    }

    /**
     * 是否长按
     */
    public boolean isLongPress() {
        return isLongPress;
    }

    /**
     * 获取选择索引
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }


    public Rect getVolRect() {
        return volRect;
    }

    /**
     * 设置选择监听
     */
    @SuppressWarnings("unused")
    public void setOnSelectedChangedListener(OnSelectedChangedListener l) {
        this.mOnSelectedChangedListener = l;
    }

    public void onSelectedChanged(BaseKLineChartView view, Object point, int index) {
        if (null != this.mOnSelectedChangedListener) {
            mOnSelectedChangedListener.onSelectedChanged(view, point, index);
        }
    }

    /**
     * 设置超出右方后可滑动的范围
     */
    public void setOverScrollRange(float overScrollRange) {
        if (overScrollRange < 0) {
            overScrollRange = 0;
        }
        mOverScrollRange = overScrollRange;
    }


    /**
     * 设置上方padding
     *
     * @param topPadding topPadding
     */
    @SuppressWarnings("unused")
    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    /**
     * 设置下方padding
     *
     * @param bottomPadding bottomPadding
     */
    @SuppressWarnings("unused")
    public void setBottomPadding(int bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    /**
     * 设置表格线宽度
     */
    public void setGridLineWidth(float width) {
        gridPaint.setStrokeWidth(width);
    }

    /**
     * 设置表格线颜色
     */
    public void setGridLineColor(int color) {
        gridPaint.setColor(color);
    }

    /**
     * 设置选择器横线宽度
     */
    public void setSelectedXLineWidth(float width) {
        selectedXLinePaint.setStrokeWidth(width);
    }

    /**
     * 设置选择器横线颜色
     */
    public void setSelectedXLineColor(int color) {
        selectedXLinePaint.setColor(color);
    }

    /**
     * 设置选择器竖线宽度
     */
    public void setSelectedYLineWidth(float width) {
        selectedWidth = width;
    }

    /**
     * 设置选择器竖线颜色
     */
    public void setSelectedYLineColor(int color) {
        selectedYLinePaint.setColor(color);
    }

    /**
     * 设置文字颜色
     */
    public void setTextColor(int color) {
        textPaint.setColor(color);
        selectedYColor = color;
    }

    private float textHeight;
    private float baseLine;
    private float textDecent;


    public void setPriceLineColor(int color) {
        priceLinePaint.setColor(color);
    }

    /**
     * 设置最大值/最小值文字颜色
     */
    public void setMTextColor(int color) {
        maxMinPaint.setColor(color);
    }

    private float maxMinTextHeight;
    private float maxMinTextDecent;

    /**
     * 设置最大值/最小值文字大小
     */
    public void setMTextSize(float textSize) {
        maxMinPaint.setTextSize(textSize);
        Paint.FontMetrics fm = maxMinPaint.getFontMetrics();
        maxMinTextHeight = fm.descent - fm.ascent;
        maxMinTextDecent = fm.descent;

    }

    /**
     * 设置背景颜色
     */
    public void setBackgroundColor(int color) {
        this.backgroundPaint.setColor(color);
    }

    /**
     * 设置选中point 值显示背景
     */
    public void setSelectPointColor(int color) {
        selectedPointPaint.setColor(color);
    }

    public float getLastPrice() {
        return lastPrice;
    }

    public float getLastVol() {
        return lastVol;
    }

    public void setVolValueFormatter(ValueFormatter valueFormatter) {
        this.volValueFormater = valueFormatter;
    }

    /**
     * 选中点变化时的监听
     */
    public interface OnSelectedChangedListener {
        /**
         * 当选点中变化时
         *
         * @param view  当前view
         * @param point 选中的点
         * @param index 选中点的索引
         */
        void onSelectedChanged(BaseKLineChartView view, Object point, int index);

    }

    public void setPriceLineWidth(float lineWidth) {
        priceLinePaint.setStrokeWidth(lineWidth);
        priceLinePaint.setStyle(Paint.Style.STROKE);
    }

    public void setPriceLineRightColor(int color) {
        priceLineBoxRightPaint.setColor(color);
    }

    public float getmOverScrollRange() {
        return mOverScrollRange;
    }

    /**
     * 设置每个点的宽度
     */
    public void setChartItemWidth(float pointWidth) {
        chartItemWidth = pointWidth;
    }

    public float getChartItemWidth() {
        return chartItemWidth;
    }

    public Paint getTextPaint() {
        return textPaint;
    }

    public void setEndPointColor(int color) {
        lineEndPointPaint.setColor(color);
        lineEndFillPointPaint.setColor(color);
    }

    public void setLineEndPointWidth(float width) {
        this.lineEndPointWidth = width;
    }


    /**
     * 执行一个动画变换
     *
     * @param start    start
     * @param end      end
     * @param listener listener
     * @return ValueAnimator
     */
    @SuppressWarnings("all")
    public ValueAnimator generaterAnimator(Float start, float end, ValueAnimator.AnimatorUpdateListener listener) {
        ValueAnimator animator = ValueAnimator.ofFloat(0 == start ? end - 0.01f : start, end);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (isAnimationLast) {
                    listener.onAnimationUpdate(valueAnimator);
                }
            }
        });
        animator.start();
        return animator;
    }

    public void setShowLine(boolean isLine) {
        if (isLine != this.isLine) {
            setItemCount(0);
        }
        if (isLine && getX(screenRightIndex) + canvasTranslateX <= width) {
            startFreshPage();
        } else {
            stopFreshPage();
        }
        this.isLine = isLine;
        setTranslatedX(getMinTranslate());
        animValidate();
    }

    public boolean isLine() {
        return isLine;
    }

    /**
     * 设置价格框离右边的距离
     *
     * @param priceBoxMarginRight priceBoxMarginRight
     */
    @SuppressWarnings("unused")
    public void setPriceBoxMarginRight(float priceBoxMarginRight) {
        this.priceBoxMarginRight = priceBoxMarginRight;
    }

    /**
     * 设置价格框高度
     *
     * @param priceLineBoxHeight priceLineBoxHeight
     */
    @SuppressWarnings("unused")
    public void setPriceLineBoxHeight(int priceLineBoxHeight) {
        this.priceLineBoxHeight = priceLineBoxHeight;
    }

    /**
     * 设置选中框前面的文本
     *
     * @param marketInfoText 默认中文
     */
    @SuppressWarnings("unused")
    public void setMarketInfoText(String[] marketInfoText) {
//        ((MainDraw) mainDraw).setMarketInfoText(marketInfoText);
        this.marketInfoText = marketInfoText;
    }

    /**
     * 设置价格框背景色
     *
     * @param color default black
     */
    @SuppressWarnings("unused")
    public void setPriceBoxBgColor(int color) {
        priceLineBoxBgPaint.setColor(color);
    }

    /**
     * 设置选中点的颜色
     *
     * @param color default wihte
     */
    @SuppressWarnings("unused")
    public void setSelectCrossColor(int color) {
        selectedCrossPaint.setColor(color);
    }

    /**
     * 设置选中点外圆颜色
     *
     * @param color default wihte
     */
    @SuppressWarnings("unused")
    public void setSelectCrossBigColor(int color) {
        selectedbigCrossPaint.setColor(color);
    }

    /**
     * 设置价格框边框颜色
     *
     * @param color default wihte
     */
    @SuppressWarnings("unused")
    public void setPriceBoxBorderColor(int color) {
        priceLineBoxPaint.setColor(color);
    }

    /**
     * 设置价格框边框宽度
     *
     * @param width default 1
     */
    @SuppressWarnings("unused")
    public void setPriceBoxBorderWidth(int width) {
        priceLineBoxPaint.setStrokeWidth(width);
    }

    /**
     * 设置圆点半径
     *
     * @param radius
     */
    public void setSelectedPointRadius(float radius) {
        selectedPointRadius = radius;
    }


    public void setSelectedYColor(int color) {
        this.selectedYColor = color;
    }

    public void setBackgroundStartColor(int color) {
        this.backGroundTopColor = color;
    }

    public void setBackgroundEmdColor(int color) {
        this.backGroundBottomColor = color;
    }

    public void setUpColor(int color) {
        upPaint.setColor(color);
        upLinePaint.setColor(color);

    }

    public void setDownColor(int color) {
        downPaint.setColor(color);
        downLinePaint.setColor(color);
    }

    public void setMinuteLineColor(int color) {
        linePaint.setColor(color);
    }

    public void setAreaTopColor(int color) {
        this.areaTopColor = color;
    }

    public void setAreaBottomColor(int color) {
        this.areaBottomColor = color;
    }


    public boolean isAnimationLast() {
        return isAnimationLast;
    }


    /**
     * draw选择器
     *
     * @param view   view
     * @param canvas canvas
     */
    @SuppressLint("DefaultLocale")
    private void drawSelector(BaseKLineChartView view, Canvas canvas) {

        int index = view.getSelectedIndex();

        ICandle point = view.getItem(index);
        strings[0] = String.valueOf(view.getAdapter().getDate(index));
        strings[1] = (String.valueOf(point.getOpenPrice()));
        strings[2] = (String.valueOf(point.getHighPrice()));
        strings[3] = (String.valueOf(point.getLowPrice()));
        strings[4] = (String.valueOf(point.getClosePrice()));
        float tempDiffPrice = point.getClosePrice() - point.getOpenPrice();
        strings[5] = (String.valueOf(tempDiffPrice));
        strings[6] = (String.format("%.2f", (tempDiffPrice) * 100 / point.getOpenPrice()) + "%");
        strings[7] = (String.valueOf(point.getVolume()));

        float width = 0, left, top = margin + view.getTopPadding();
        //上下多加两个padding值的间隙
        int length = strings.length;
        float height = padding * ((length - 1) + 4) + selectedTextHeight * length;
        for (int i = 0; i < length; i++) {
            String tempString = marketInfoText[i] + strings[i];
            width = Math.max(width, selectorTextPaint.measureText(tempString));
        }
        width += padding * 2;

        float x = view.translateXtoX(view.getX(index));
        if (x > view.getChartWidth() / 2) {
            left = margin;
        } else {
            left = view.getChartWidth() - width - margin;
        }

        float right = left + width;
        RectF r = new RectF(left, top, right, top + height);
        canvas.drawRoundRect(r, padding, padding, selectorBackgroundPaint);
        canvas.drawRoundRect(r, padding, padding, selectorBorderPaint);
        float y = top + padding * 2 + selectedTextBaseLine;
        float tempX = right - padding;
        for (int i = 0; i < length; i++) {
            String s = strings[i];
            canvas.drawText(marketInfoText[i], left + padding, y, selectorTextPaint);
            if (i == 5 || i == 6) {
                if (tempDiffPrice >= 0) {
                    canvas.drawText(s, tempX - selectorTextPaint.measureText(s), y, upPaint);
                } else {
                    canvas.drawText(s, tempX - selectorTextPaint.measureText(s), y, downPaint);
                }
            } else {
                canvas.drawText(s, tempX - selectorTextPaint.measureText(s), y, selectorTextPaint);
            }
            y += selectedTextHeight + padding;
        }

    }

    private float selectedTextHeight;
    private float selectedTextBaseLine;


    /**
     * 设置 vol图左上角文字 线的颜色
     *
     * @param color
     */
    public void setVolLeftColor(int color) {
        this.volLeftPaint.setColor(color);
    }


    /**
     * 设置ma1颜色
     *
     * @param color color
     */
    public void setMaOneColor(int color) {
        this.indexPaintOne.setColor(color);
        this.ma5Paint.setColor(color);
    }

    /**
     * 设置ma2颜色
     *
     * @param color color
     */
    public void setMaTwoColor(int color) {
        this.indexPaintTwo.setColor(color);
        this.ma10Paint.setColor(color);
    }

    /**
     * 设置ma3颜色
     *
     * @param color color
     */
    public void setMaThreeColor(int color) {
        this.indexPaintThree.setColor(color);
    }


    /**
     * 设置选择器文字大小
     *
     * @param textSize textsize
     */
    public void setSelectorTextSize(float textSize) {
        selectorTextPaint.setTextSize(textSize);
        downPaint.setTextSize(textSize);
        upPaint.setTextSize(textSize);
        Paint.FontMetrics metrics = selectorTextPaint.getFontMetrics();
        selectedTextHeight = metrics.descent - metrics.ascent;
        selectedTextBaseLine = (selectedTextHeight - metrics.bottom - metrics.top) / 2;
    }

    /**
     * 设置选择器背景
     *
     * @param color color
     */
    public void setSelectorBackgroundColor(int color) {
        selectorBackgroundPaint.setColor(color);
    }

    /**
     * 设置蜡烛宽度
     *
     * @param candleWidth candle width
     */
    public void setCandleWidth(float candleWidth) {
        this.candleWidth = candleWidth;
    }

    /**
     * 设置蜡烛线宽度
     *
     * @param candleLineWidth lineWidth
     */
    public void setCandleLineWidth(float candleLineWidth) {
        downLinePaint.setStrokeWidth(candleLineWidth);
        upLinePaint.setStrokeWidth(candleLineWidth);
    }


    public void setBarWidth(float candleWidth) {
        pillarWidth = candleWidth;
    }


    public void setStroke(boolean isStroke) {
        if (isStroke) {
            upPaint.setStyle(Paint.Style.STROKE);
            downPaint.setStyle(Paint.Style.STROKE);
        } else {
            upPaint.setStyle(Paint.Style.FILL);
            downPaint.setStyle(Paint.Style.FILL);
        }
    }


    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {

        textPaint.setTextSize(textSize);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        textHeight = fm.descent - fm.ascent;
        textDecent = fm.descent;
        baseLine = (textHeight - fm.bottom - fm.top) / 2;
        priceLineBoxRightPaint.setTextSize(textSize);

        indexPaintThree.setTextSize(textSize);
        indexPaintTwo.setTextSize(textSize);
        indexPaintOne.setTextSize(textSize);
        Paint.FontMetrics metrics = indexPaintOne.getFontMetrics();
        textHeight = metrics.descent - metrics.ascent;


        this.ma5Paint.setTextSize(textSize);
        this.ma10Paint.setTextSize(textSize);
        this.volLeftPaint.setTextSize(textSize);

        mDEAPaint.setTextSize(textSize);
        mDIFPaint.setTextSize(textSize);
        mMACDPaint.setTextSize(textSize);

        mKPaint.setTextSize(textSize);
        mDPaint.setTextSize(textSize);
        mJPaint.setTextSize(textSize);


        mRSI2Paint.setTextSize(textSize);
        mRSI3Paint.setTextSize(textSize);
        mRSI1Paint.setTextSize(textSize);

        r1Paint.setTextSize(textSize);
        r2Paint.setTextSize(textSize);
        r3Paint.setTextSize(textSize);
    }


    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        indexPaintThree.setStrokeWidth(width);
        indexPaintTwo.setStrokeWidth(width);
        indexPaintOne.setStrokeWidth(width);
        linePaint.setStrokeWidth(width);
        selectorBorderPaint.setStrokeWidth(width);

        mDEAPaint.setStrokeWidth(width);
        mDIFPaint.setStrokeWidth(width);
        mMACDPaint.setStrokeWidth(width);


        mKPaint.setStrokeWidth(width);
        mDPaint.setStrokeWidth(width);
        mJPaint.setStrokeWidth(width);


        this.linePaint.setStrokeWidth(width);
        this.ma5Paint.setStrokeWidth(width);
        this.ma10Paint.setStrokeWidth(width);
        this.lineVolWidth = width / 2;

        mRSI1Paint.setStrokeWidth(width);
        mRSI2Paint.setStrokeWidth(width);
        mRSI3Paint.setStrokeWidth(width);

        r1Paint.setStrokeWidth(width);
        r2Paint.setStrokeWidth(width);
        r3Paint.setStrokeWidth(width);
    }


    /**
     * 设置选择器文字颜色
     *
     * @param color color
     */
    public void setSelectorTextColor(int color) {
        selectorTextPaint.setColor(color);
        selectorBorderPaint.setColor(color);
    }


    /**
     * 设置K颜色
     */
    public void setKColor(int color) {
        mKPaint.setColor(color);
    }

    /**
     * 设置D颜色
     */
    public void setDColor(int color) {
        mDPaint.setColor(color);
    }

    /**
     * 设置J颜色
     */
    public void setJColor(int color) {
        mJPaint.setColor(color);
    }

    public void setRSI1Color(int color) {
        mRSI1Paint.setColor(color);
    }

    public void setRSI2Color(int color) {
        mRSI2Paint.setColor(color);
    }

    public void setRSI3Color(int color) {
        mRSI3Paint.setColor(color);
    }

    /**
     * 设置%R颜色
     */
    public void setR1Color(int color) {
        r1Paint.setColor(color);
    }

    /**
     * 设置%R颜色
     */
    public void setR2Color(int color) {
        r2Paint.setColor(color);
    }

    /**
     * 设置%R颜色
     */
    public void setR3Color(int color) {
        r3Paint.setColor(color);
    }


    /**
     * 设置DIF颜色
     */
    public void setDIFColor(int color) {
        this.mDIFPaint.setColor(color);
    }

    /**
     * 设置DEA颜色
     */
    public void setDEAColor(int color) {
        this.mDEAPaint.setColor(color);
    }

    /**
     * 设置MACD颜色
     */
    public void setMACDColor(int color) {
        this.mMACDPaint.setColor(color);
    }


    public static final int INDEX_OPEN = 0;
    public static final int INDEX_CLOSE = 1;
    public static final int INDEX_HIGH = 2;
    public static final int INDEX_LOW = 3;
    public static final int INDEX_VOL = 4;
    public static final int INDEX_MA_1 = 5;
    public static final int INDEX_MA_2 = 6;
    public static final int INDEX_MA_3 = 7;
    public static final int INDEX_BOLL_UP = 8;
    public static final int INDEX_BOLL_MB = 9;
    public static final int INDEX_BOLL_DN = 10;
    public static final int INDEX_KDJ_K = 11;
    public static final int INDEX_KDJ_D = 12;
    public static final int INDEX_KDJ_J = 13;
    public static final int INDEX_WR_1 = 14;
    public static final int INDEX_WR_2 = 15;
    public static final int INDEX_WR_3 = 16;
    public static final int INDEX_VOL_MA_1 = 17;
    public static final int INDEX_VOL_MA_2 = 18;
    public static final int INDEX_RSI = 19;
    public static final int INDEX_MACD_DEA = 20;
    public static final int INDEX_MACD_DIF = 21;
    public static final int INDEX_MACD = 22;

    public static final int GROUP_COUNT = 23;
}
