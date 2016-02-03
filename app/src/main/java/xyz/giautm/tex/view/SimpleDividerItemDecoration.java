package xyz.giautm.tex.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static xyz.giautm.tex.R.drawable.line_divider;

/**
 * vn.tiki.tex
 * 30/01/2016 - giau.tran.
 */
public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable divider;

    public SimpleDividerItemDecoration(Context context) {
        final Resources resources = context.getResources();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            divider = resources.getDrawable(line_divider, context.getTheme());
        } else {
            divider = resources.getDrawable(line_divider);
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }
}