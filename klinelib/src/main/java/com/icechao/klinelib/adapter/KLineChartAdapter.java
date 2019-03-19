package com.icechao.klinelib.adapter;

import com.icechao.klinelib.base.BaseKLineChartAdapter;
import com.icechao.klinelib.entity.KLineEntity;
import com.icechao.klinelib.utils.DataHelper;
import com.icechao.klinelib.utils.NewDataHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据适配器
 * Created by tifezh on 2016/6/18.
 */
public class KLineChartAdapter extends BaseKLineChartAdapter {
    private KLineEntity lastData;


    private List<KLineEntity> datas = new ArrayList<>();
    private float[] points;

    public KLineChartAdapter() {

    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public KLineEntity getItem(int position) {
        try {
            int size = datas.size();
            if (size == 0 || position < 0 || position > size) {
                return null;
            }
            return datas.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getDate(int position) {
        return (datas.get(position).getDate());
    }

    /**
     * 向头部添加数据
     */
    public void addHeaderData(List<KLineEntity> data) {
        if (null != data && !data.isEmpty()) {
            datas.clear();
            datas.addAll(data);
        }
    }


    public void resetData(List<KLineEntity> data) {

        notifyDataWillChanged();
        if (null != data && data.size() > 0) {
            datas.addAll(data);
            this.lastData = data.get(data.size() - 1);
            points = NewDataHelper.calculate(datas);
            notifyDataSetChanged();
        }
    }

    /**
     * 向尾部添加数据
     */
    @Override
    public void addLast(KLineEntity entity) {
        if (null != entity) {
            datas.add(entity);
            DataHelper.calculate(datas);
            this.lastData = datas.get(datas.size() - 1);
            notifyDataSetChanged();
        }
    }


    /**
     * 获取当前K线最后一个数据
     *
     * @return 最后一根线的bean
     */
    public KLineEntity getLastData() {
        return lastData;
    }

    /**
     * 改变某个点的值
     *
     * @param position 索引值
     */
    public void changeItem(int position, KLineEntity data) {
        datas.set(position, data);
        this.lastData = datas.get(datas.size() - 1);
        DataHelper.calculate(datas);
        notifyDataSetChanged();
    }

    /**
     * 数据清除
     */
    public void clearData() {
        datas.clear();
        lastData = null;
        notifyDataSetChanged();
    }

    public List<KLineEntity> getData() {
        return datas;
    }

    public float[] getPoints() {
        return points;
    }
}
