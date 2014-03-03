package co.arcs.android.fileselector;

import java.io.File;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class FileSelectorActivity extends Activity implements FileSelectorFragment.Listener {

	private static final String FRAGMENT_TAG = "listFragment";

	/**
	 * @see FileSelectorFragment#ARG_STR_INITIAL_DIRECTORY
	 */
	public static final String EXTRA_STR_INITIAL_DIRECTORY = FileSelectorFragment.ARG_STR_INITIAL_DIRECTORY;

	/**
	 * @see FileSelectorFragment#ARG_BOOL_ALLOW_UP_NAVIGATION_ABOVE_INITIAL_DIRECTORY
	 */
	public static final String EXTRA_BOOL_ALLOW_UP_NAVIGATION_ABOVE_INITIAL_DIRECTORY = FileSelectorFragment.ARG_BOOL_ALLOW_UP_NAVIGATION_ABOVE_INITIAL_DIRECTORY;

	/**
	 * @see FileSelectorFragment#ARG_SELECTION_TYPE
	 */
	public static final String EXTRA_STR_SELECTION_TYPE = FileSelectorFragment.ARG_SELECTION_TYPE;
	public static final String TYPE_DIRECTORY = FileSelectorFragment.TYPE_DIRECTORY;
	public static final String TYPE_FILE = FileSelectorFragment.TYPE_FILE;

	/**
	 * @see FileSelectorFragment#ARG_SHOW_FILES
	 */
	public static final String EXTRA_BOOL_SHOW_FILES = FileSelectorFragment.ARG_SHOW_FILES;

	/**
	 * @see FileSelectorFragment#ARG_SHOW_HIDDEN
	 */
	public static final String EXTRA_BOOL_SHOW_HIDDEN = FileSelectorFragment.ARG_SHOW_HIDDEN;

	/**
	 * Extra containing the {@linkplain File} selected by the user.
	 */
	public static final String EXTRA_PICKED_FILE = "file";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TypedArray attr = getTheme().obtainStyledAttributes(new int[] { R.attr.showAsDialog });
		boolean showAsDialog = attr.getBoolean(0, false);
		attr.recycle();

		if (showAsDialog) {
			configureAsDialog();
		}

		configureActionBarSubtitle();

		if (savedInstanceState == null) {
			FileSelectorFragment fragment = new FileSelectorFragment();
			fragment.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction()
					.add(android.R.id.content, fragment, FRAGMENT_TAG).commit();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
			if (fragment != null) {
				((FileSelectorFragment) fragment).onActionBarUpNavigation();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		FileSelectorFragment fragment = (FileSelectorFragment) getFragmentManager()
				.findFragmentByTag(FRAGMENT_TAG);
		if (fragment == null || !fragment.onBackPressed()) {
			super.onBackPressed();
		}
	}

	@Override
	public void onFilePicked(File file) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_PICKED_FILE, file);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	private void configureActionBarSubtitle() {
		// Configure ellipses to appear at start as subtitle will hold the file
		// path
		TextView actionBarTitle = findActionBarSubtitle();
		if (actionBarTitle != null) {
			actionBarTitle.setEllipsize(TruncateAt.START);
		}
	}

	public void configureAsDialog() {
		// http://stackoverflow.com/questions/11425020/actionbar-in-a-dialogfragment
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.height = getResources().getDimensionPixelSize(R.dimen.dialog_height);
		params.width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
		params.alpha = 1.0f;
		params.dimAmount = 0.5f;
		getWindow().setAttributes(params);
	}

	private TextView findActionBarSubtitle() {
		int id = getResources().getIdentifier("action_bar_subtitle", "id", "android");
		if (id != 0) {
			try {
				return (TextView) findViewById(id);
			} catch (ClassCastException ok) {
			}
		}
		return null;
	}
}
