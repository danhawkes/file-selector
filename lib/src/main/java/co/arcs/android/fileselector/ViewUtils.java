package co.arcs.android.fileselector;

import android.content.Context;
import android.util.DisplayMetrics;

class ViewUtils {

	static int dpToPx(Context context, int dp) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		return (int) ((dp * displayMetrics.density) + 0.5f);
	}
}