package co.arcs.android.fileselector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.AbsListView;
import android.widget.ListView;

class FileSelectorListView extends ListView {

    public FileSelectorListView(Context context) {
        this(context, null);
    }

    public FileSelectorListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileSelectorListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // This view has a background by default as it's animated over another
        // instance of itself when navigating the filesystem
        setOpaqueBackgroundColor(context);

        setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void setOpaqueBackgroundColor(Context context) {

        // Set the background colour. When displayed in a regular activity, this
        // is the just the window background. When in an activity styled as a
        // dialog, we don't want the window background as it includes a border
        // and shadow - we want the colour in
        // 'drawable/dialog_full_holo_<theme>'. Unfortunately, it's not defined
        // elsewhere, so is duplicated in the app resources.

        TypedArray attr = context.getTheme().obtainStyledAttributes(new int[]{R.attr.showAsDialog});
        boolean inDialog = attr.getBoolean(0, false);
        attr.recycle();

        int resId = inDialog ? R.attr.colorDialogBackground : android.R.attr.windowBackground;

        TypedValue backgroundValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, backgroundValue, true);
        if (backgroundValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && backgroundValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            setBackgroundColor(backgroundValue.data);
        } else {
            Drawable d = context.getResources().getDrawable(backgroundValue.resourceId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(d);
            } else {
                setBackgroundDrawable(d);
            }
        }
    }
}
