package com.mxchip.roobitmap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author limh
 * @update StramChen
 * @function 机器人地图功能
 * @date 2018/3/8
 */
public class AreaRoomView extends TouchView {
    private static final String TAG = "AreaRoomView";

    private int lineColor;
    private int cleanColor;
    private int mRobotColor;
    private int barColor;
    //网格数量
    private int maxGrid;

    private static final int defaultSize = 300;
    private float width = 0;
    //网格间隔
    private float space;
    //机器人位置
    private float localX = 0, localY = 0;
    private float centerX = 0, centerY = 0;
    //Y轴网格线数量
    private int yGridNum;
    //X轴网格线数量
    private int xGridNum;
    //网格画笔
    private Paint paintGrid = new Paint();
    //路径画笔
    private Paint paintPath = new Paint();
    //障碍物画笔
    private Paint paintWall = new Paint();
    //机器人画笔
    private Paint paintRobot = new Paint();
    //路径数据
    private List<AreaBean> datas = new ArrayList<>();
    //新加进来的数据
    private List<AreaBean> newDatas;
    //地图数据格子缩放比例
    private float scale = 1f;
    //是否需要显示机器人的位置,默认是显示最后一个坐标点的位置
    private boolean isNeedToShowRobot = true;
    /**
     * 地图监听
     */
    private MapListenter mapListenter;
    /**
     * 地图是否可以滑动
     */
    private boolean mCanScrollMap = false;
    public AreaRoomView(Context context) {
        super(context);
        init(context, null);
    }

    public AreaRoomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AreaRoomView);
//            lineColor = typedArray.getColor(R.styleable.AreaRoomView_line_color, 0xFFBFEFFF);
            lineColor = typedArray.getColor(R.styleable.AreaRoomView_line_color, 0xFFF3F3F3);
            cleanColor = typedArray.getColor(R.styleable.AreaRoomView_clean_color, 0xFFFDD108);
            mRobotColor = typedArray.getColor(R.styleable.AreaRoomView_robot_color, 0XFF1086DD);
//            barColor = typedArray.getColor(R.styleable.AreaRoomView_bar_color, 0XFF606060);
            barColor = typedArray.getColor(R.styleable.AreaRoomView_bar_color, 0XFFFFFFFF);
            maxGrid = typedArray.getInt(R.styleable.AreaRoomView_max_grid, 200);
            typedArray.recycle();
        }

        paintGrid.setColor(lineColor);
        paintGrid.setAntiAlias(true);
        paintGrid.setStrokeWidth(1);

        paintPath.setColor(cleanColor);
        paintPath.setAntiAlias(true);

        paintWall.setColor(barColor);
        paintWall.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMap(canvas, datas);
    }

    private void drawMap(Canvas canvas, List<AreaBean> areaBeans) {
        //画纵向网格
        for (int i = 0; i < yGridNum; i++) {
            canvas.drawLine(0, i * space, width, i * space, paintGrid);
        }
        for (int i = 0; i < xGridNum; i++) {
            canvas.drawLine(i * space, 0, i * space, width, paintGrid);
        }

        paintPath.setColor(cleanColor);

        //通过填充方格 绘制路径
        //item.getType(){0:无障碍  1：有障碍  2：机器人点}
        float left, top, right, bottom;
        for (AreaBean item : datas) {
            left = localX + item.getX() * scale * space + 1f;
            top = localY + item.getY() * scale * space + 1f;
            right = left + space - 1f;
            bottom = top + space - 1f;
            if (item.getT() == 0) {
                //画机器人位置
                canvas.drawRect(left, top, right, bottom, paintPath);
            } else if (item.getT() == 1) {
                canvas.drawRect(left, top, right, bottom, paintWall);
            }
        }

        //画机器人
        if (datas.size() > 0 && isNeedToShowRobot) {
            float x = localX + datas.get(datas.size() - 1).getX() * scale * space;
            float y = localY + datas.get(datas.size() - 1).getY() * scale * space;
            float radius = space;
            paintRobot.setColor(mRobotColor);
            paintRobot.setAntiAlias(true);
            canvas.drawCircle(x, y, radius, paintRobot);
        }

        if(null != mapListenter){
            mapListenter.onGetArea(datas.size()*0.04f);
        }
        Log.d("map--data",datas.toString());
        Log.d("map--size",datas.size()+"");
    }

    private boolean addNewMapData() {
        if (null == newDatas) return false;
        for (AreaBean item : newDatas) {
            //用一个数组保存点，防止view过度渲染
            int size = datas.size();
            if(size == 0){
                datas.add(item);
                continue;
            }
            float x = item.getX()*scale;
            float y = item.getY()*scale;
            int t = item.getT();
            for (int i = 0; i < size; i++) {
                float preX = datas.get(i).getX()*scale;
                float preY = datas.get(i).getY()*scale;
                int preT = datas.get(i).getT();
                if (preX == x && preY == y) {
                    if(t == preT) break;
                    datas.set(i, item);
                    break;
                } else if (i == size - 1) {
                    datas.add(item);
                }
            }
        }
        newDatas.clear();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        width = getMeasureSpac(widthMeasureSpec);
        //根据Y轴要显示最多格子的数量计算每个格子的间隔
        space = width / maxGrid;
        //Y轴线条个数
        yGridNum = maxGrid+1;
        //X轴线条个数
        xGridNum = maxGrid+1;
        //中心点坐标
        centerX = maxGrid / 2f*space;
        centerY = maxGrid / 2f*space;
        Log.d(TAG, "WIDTH=" + width);
        setMeasuredDimension((int)width, (int)width);
    }

    private int getMeasureSpac(int measureSpec) {
        int mySize = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
        }
        return mySize;
    }

    /**
     * 渲染地图数据
     *
     * @param datas 数据源
     */
    public void setDatas(List<AreaBean> datas, boolean append) {
        this.newDatas = datas;
        if (!append && null != datas && datas.size() > 0) {
            this.datas.clear();
        }
        if (!addNewMapData()) return;
//            mCanvas.save();
//            drawMap(mCanvas,datas);
//            mCanvas.restore();
        invalidate();
    }

    public void clear() {
        datas.clear();
        invalidate();
    }

    public void setMapListenter(MapListenter mapListenter) {
        this.mapListenter = mapListenter;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    /**
     * 设置是否要展示机器人
     * @param needToShowRobot
     */
    public void setNeedToShowRobot(boolean needToShowRobot) {
        isNeedToShowRobot = needToShowRobot;
    }
}
