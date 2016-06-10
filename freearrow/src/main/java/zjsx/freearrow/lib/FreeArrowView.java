package zjsx.freearrow.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by di.zhang on 2016/6/8.
 * 导航箭头的View，支持颜色渐变和方向调整
 */
public class FreeArrowView extends TextView {
    public static final int LEFT = 0;
    public static final int UP = 1;
    public static final int RIGHT = 2;
    public static final int DOWN = 3;
    Paint paint;
    int orientation;
    boolean withPole;
    int arrowColor;
    float poleRatio;
    float poleWidth;
    float lineStrokeWidth;
    boolean fillArrow;
    float arrowThick;
    boolean dashPole;
    float dashWidth;
    float dashGap;

    public FreeArrowView(Context context) {
        super(context);
        init(context, null);
    }

    public FreeArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FreeArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FreeArrowView);
        orientation = ta.getInt(R.styleable.FreeArrowView_orientation, LEFT);
        withPole = ta.getBoolean(R.styleable.FreeArrowView_withPole, false);
        arrowColor = ta.getColor(R.styleable.FreeArrowView_arrowColor, Color.BLACK);
        poleRatio = ta.getFloat(R.styleable.FreeArrowView_poleRatio, 0.5f);
        poleWidth = ta.getDimension(R.styleable.FreeArrowView_poleWidth, -1f);
        lineStrokeWidth = ta.getDimension(R.styleable.FreeArrowView_lineStrokeWidth, dip2px(context, 1));
        fillArrow = ta.getBoolean(R.styleable.FreeArrowView_fillArrow, false);
        arrowThick = ta.getDimension(R.styleable.FreeArrowView_arrowThick, 0);
        dashPole = ta.getBoolean(R.styleable.FreeArrowView_dashPole, false);
        dashWidth = ta.getDimension(R.styleable.FreeArrowView_dashWidth, 0);
        dashGap = ta.getDimension(R.styleable.FreeArrowView_dashGap, 0);
        ta.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        paint.setStrokeWidth(lineStrokeWidth);
        paint.setColor(arrowColor);
        drawPole(canvas);
        drawArrow(canvas);
    }

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_DOWN = 3;

    int getContentWidth() {
        return mViewWidth - getPaddingLeft() - getPaddingRight();
    }

    int getContentHeight() {
        return mViewHeight - getPaddingTop() - getPaddingBottom();
    }

    int getRatioWidth() {
        if (orientation == LEFT || orientation == RIGHT) {
            return getContentWidth();
        } else {
            return getContentHeight();
        }
    }

    int getArrowWidth() {
        if (withPole && poleRatio > 0) {
            return Math.round(getRatioWidth() * (1 - poleRatio));
        } else {
            return Math.round(getRatioWidth());
        }
    }

    Point getArrowStart() {
        int x, y;
        switch (orientation) {
            case UP:
                x = getPaddingLeft();
                y = getPaddingTop() + getArrowWidth();
                break;
            case RIGHT:
                x = Math.round(mViewWidth - getPaddingRight() - getArrowWidth());
                y = Math.round(getPaddingBottom());
                break;
            case DOWN:
                x = getPaddingLeft();
                y = mViewHeight - getPaddingBottom() - getArrowWidth();
                break;
            default:
                x = Math.round(getPaddingLeft() + getArrowWidth());
                y = Math.round(getPaddingBottom());
                break;
        }
        return new Point(x, y);
    }

    /**
     * 箭头外边中心
     *
     * @return
     */
    Point getArrowCenter() {
        int x, y;
        switch (orientation) {
            case UP:
                x = getPaddingLeft() + getContentWidth() / 2;
                y = getPaddingTop();
                break;
            case RIGHT:
                x = mViewWidth - getPaddingRight();
                y = getPaddingTop() + getContentHeight() / 2;
                break;
            case DOWN:
                x = getPaddingLeft() + getContentWidth() / 2;
                y = mViewHeight - getPaddingBottom();
                break;
            default:
                x = getPaddingLeft();
                y = getPaddingTop() + getContentHeight() / 2;
                break;
        }
        return new Point(x, y);
    }

    /**
     * 箭头内边中心
     *
     * @param center 箭头外边中心
     * @return
     */
    Point getArrowInnerCenter(Point center) {
        int x, y;
        switch (orientation) {
            case UP:
                x = center.x;
                y = Math.round(center.y + arrowThick);
                break;
            case RIGHT:
                x = Math.round(center.x + arrowThick);
                y = center.y;
                break;
            case DOWN:
                x = center.x;
                y = Math.round(center.y - arrowThick);
                break;
            default:
                x = Math.round(center.x + arrowThick);
                y = center.y;
                break;
        }
        return new Point(x, y);
    }

    Point getArrowEnd() {
        int x, y;
        switch (orientation) {
            case UP:
                x = mViewWidth - getPaddingRight();
                y = getPaddingTop() + getArrowWidth();
                break;
            case RIGHT:
                x = mViewWidth - getPaddingRight() - getArrowWidth();
                y = mViewHeight - getPaddingBottom();
                break;
            case DOWN:
                x = mViewWidth - getPaddingRight();
                y = mViewHeight - getPaddingBottom() - getArrowWidth();
                break;
            default:
                x = getPaddingLeft() + getArrowWidth();
                y = mViewHeight - getPaddingBottom();
                break;
        }
        return new Point(x, y);
    }

    Point getPoleStart() {
        int x, y;
        switch (orientation) {
            case DOWN:
                x = getPaddingLeft() + getContentWidth() / 2;
                y = getPaddingTop();
                break;
            case LEFT:
                x = mViewWidth - getPaddingRight();
                y = getPaddingTop() + getContentHeight() / 2;
                break;
            case UP:
                x = getPaddingLeft() + getContentWidth() / 2;
                y = mViewHeight - getPaddingBottom();
                break;
            default:
                x = getPaddingLeft();
                y = getPaddingTop() + getContentHeight() / 2;
                break;
        }
        return new Point(x, y);
    }

    /**
     * 实际的箭头厚度
     *
     * @return
     */
    float realArrowThick() {
        if (arrowThick < 0) {
            return getRatioWidth() * (1 - poleRatio);
        } else {
            return arrowThick;
        }
    }

    float getPoleWidth() {
        if (poleWidth < 0f) {
            float width = getRatioWidth() - lineStrokeWidth;
            return Math.min(width, getRatioWidth() - realArrowThick());
        } else {
            return poleWidth;
        }
    }

    Point getPoleEnd(Point start) {
        int x, y;
        switch (orientation) {
            case UP:
                x = getPaddingLeft() + getContentWidth() / 2;
                y = Math.round(start.y - getPoleWidth());
                break;
            case RIGHT:
                x = Math.round(start.x + getPoleWidth());
                y = getPaddingTop() + getContentHeight() / 2;
                break;
            case DOWN:
                x = getPaddingLeft() + getContentWidth() / 2;
                y = Math.round(start.y + getPoleWidth());
                break;
            default:
                x = Math.round(start.x - getPoleWidth());
                y = getPaddingTop() + getContentHeight() / 2;
                break;
        }
        return new Point(x, y);
    }

    void drawPole(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        PathEffect effects=null;
        if (dashPole) {
            effects = new DashPathEffect(new float[]{dashWidth,dashGap,dashWidth,dashGap}, 1);
        }
        paint.setPathEffect(effects);
        if (withPole && poleWidth != 0) {
            Point start = getPoleStart();
            Point end = getPoleEnd(start);
            Path p = new Path();
            p.moveTo(start.x, start.y);
            p.lineTo(end.x, end.y);
            p.close();
            canvas.drawPath(p, paint);
        }
    }

    void drawArrow(Canvas canvas) {
        paint.setPathEffect(null);
        if (fillArrow) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        Path path = new Path();
        Point start = getArrowStart();
        Point center = getArrowCenter();
        Point end = getArrowEnd();
        path.moveTo(start.x, start.y);
        path.lineTo(center.x, center.y);
        path.lineTo(end.x, end.y);
        if (arrowThick < 0) {
            path.close();
        } else if (arrowThick != 0) {
            Point innerCenter = getArrowInnerCenter(center);
            path.lineTo(innerCenter.x, innerCenter.y);
            path.close();
        }
        canvas.drawPath(path, paint);
    }

    int mViewWidth, mViewHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        Log.d(FreeArrowView.class.getName(), "mViewWidth：" + w + "-" + "mViewHeight:" + h);
    }

    // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
