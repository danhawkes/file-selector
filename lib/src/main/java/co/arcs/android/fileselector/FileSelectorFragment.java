package co.arcs.android.fileselector;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.security.InvalidParameterException;
import java.util.Locale;

public class FileSelectorFragment extends Fragment {

    interface Listener {

        void onFilePicked(File file);
    }

    /**
     * Argument to configure the initial directory to display. The extra value
     * should be a {@link String}. If unspecified, the initial directory is
     * {@link Environment#getExternalStorageDirectory()}.
     */
    public static final String ARG_STR_INITIAL_DIRECTORY = "initialDirectory";

    /**
     * Argument to configure whether the user is allowed to navigate above the
     * initial directory. Defaults to false.
     */
    public static final String ARG_BOOL_ALLOW_UP_NAVIGATION_ABOVE_INITIAL_DIRECTORY = "allowUpNavigationAboveInitial";

    /**
     * Argument to configure the type of file the user is allowed to select. May
     * be either {@linkplain #TYPE_FILE} or {@linkplain #TYPE_DIRECTORY}.
     */
    public static final String ARG_SELECTION_TYPE = "selectionType";

    /**
     * Argument to configure whether files should be displayed. Defaults to
     * true.
     */
    public static final String ARG_SHOW_FILES = "showFiles";

    /**
     * Argument to configure whether hidden items should be displayed. Defaults
     * to false.
     */
    public static final String ARG_SHOW_HIDDEN = "showHidden";

    public static final String TYPE_FILE = "file";
    public static final String TYPE_DIRECTORY = "dir";

    private File initialDirectory;
    private boolean allowNavigateUpAboveInitial;
    private SelectionMode selectionType;
    private boolean showFiles;
    private boolean showHidden;

    private ViewGroup container;
    private File file;
    private File selectedFile;
    private FileSelectorListView listView;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public static FileSelectorFragment newInstance(
            String initialDirectory,
            boolean allowNavAboveInitial,
            SelectionMode selectionMode,
            boolean showFiles,
            boolean showHidden) {
        Bundle args = new Bundle();
        args.putString(ARG_STR_INITIAL_DIRECTORY, initialDirectory);
        args.putBoolean(ARG_BOOL_ALLOW_UP_NAVIGATION_ABOVE_INITIAL_DIRECTORY, allowNavAboveInitial);
        args.putString(ARG_SELECTION_TYPE,
                       (selectionMode == SelectionMode.FILE) ? TYPE_FILE : TYPE_DIRECTORY);
        args.putBoolean(ARG_SHOW_FILES, showFiles);
        args.putBoolean(ARG_SHOW_HIDDEN, showHidden);
        FileSelectorFragment fragment = new FileSelectorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }

        this.initialDirectory = new File(args.getString(ARG_STR_INITIAL_DIRECTORY,
                                                        Environment.getExternalStorageDirectory()
                                                                   .getPath()
        ));
        if (!initialDirectory.isDirectory()) {
            throw new InvalidParameterException(String.format(Locale.US,
                                                              "'%s' is not a directory",
                                                              initialDirectory));
        }
        this.allowNavigateUpAboveInitial = args.getBoolean(
                ARG_BOOL_ALLOW_UP_NAVIGATION_ABOVE_INITIAL_DIRECTORY,
                false);
        this.selectionType = (args.getString(ARG_SELECTION_TYPE, TYPE_FILE)
                                  .equals(TYPE_FILE)) ? SelectionMode.FILE : SelectionMode.DIRECTORY;
        this.showFiles = (args.getBoolean(ARG_SHOW_FILES, true));
        this.showHidden = (args.getBoolean(ARG_SHOW_HIDDEN, false));

        if (selectionType == SelectionMode.DIRECTORY) {
            selectedFile = initialDirectory;
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.container = (ViewGroup) view.findViewById(R.id.container);
        navigate(initialDirectory, NavigationAnimation.NONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.file_picker, menu);
        MenuItem acceptItem = menu.findItem(R.id.accept);
        acceptItem.setVisible(selectedFile != null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_folder) {
            FilenamePickerDialogFragment fragment = new FilenamePickerDialogFragment();
            fragment.setListener(new FilenamePickerDialogFragment.Listener() {

                @Override
                public void onFilenameSet(String filename) {
                    File directory = new File(FileSelectorFragment.this.file, filename);
                    if (directory.exists()) {
                        showToast(R.string.new_folder_error_already_exists);
                    } else {
                        directory.mkdir();
                        navigate(FileSelectorFragment.this.file, NavigationAnimation.NONE);
                        showToast(R.string.new_folder_success);
                    }
                }

                private void showToast(int res) {
                    Toast.makeText(getActivity(), getString(res), Toast.LENGTH_LONG).show();
                }
            });
            fragment.show(getFragmentManager(), null);
            return true;
        } else if (id == R.id.accept) {
            Activity act = getActivity();
            if (act instanceof Listener) {
                ((Listener) act).onFilePicked(selectedFile);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public boolean onBackPressed() {
        if (canNavigateUp()) {
            navigate(file.getParentFile(), NavigationAnimation.UP);
            return true;
        } else {
            return false;
        }
    }

    public void onActionBarUpNavigation() {
        if (canNavigateUp()) {
            navigate(file.getParentFile(), NavigationAnimation.UP);
        }
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {

            File file = (File) adapterView.getAdapter().getItem(position);
            // Can only navigate into directories
            if (file.isDirectory()) {
                navigate(file, NavigationAnimation.DOWN);
            }

            selectedFile = getSelectedItem();

            // Update the 'accept' action bar item
            getActivity().invalidateOptionsMenu();
        }
    };

    private boolean canNavigateUp() {
        File parent = file.getParentFile();
        if (parent == null) {
            return false;
        }
        if (!parent.isDirectory()) {
            return false;
        }
        if (file.equals(initialDirectory) && !allowNavigateUpAboveInitial) {
            return false;
        }
        return true;
    }

    private void navigate(File nextDir, NavigationAnimation direction) {

        this.file = nextDir;

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setSubtitle(nextDir.getPath());
        boolean canNavigateUp = canNavigateUp();
        actionBar.setDisplayHomeAsUpEnabled(canNavigateUp);
        actionBar.setHomeButtonEnabled(canNavigateUp);

        // Stop responding to clicks on old view
        final FileSelectorListView oldListView = listView;
        if (oldListView != null) {
            oldListView.setOnItemClickListener(null);
        }

        // Create new view
        File[] files = getFilesToDisplayForPath(nextDir);

        FileListAdapter adapter = new FileListAdapter(getActivity(), selectionType);
        adapter.setData(files);
        listView = new FileSelectorListView(getActivity(), null, android.R.attr.listViewStyle);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);

        // Swap out / animate views
        if (direction == NavigationAnimation.NONE) {
            container.removeView(oldListView);
            container.addView(listView);
        } else {

            // Slide new view in over old, or old view out over new
            if (direction == NavigationAnimation.UP) {
                container.addView(listView, container.indexOfChild(oldListView));
            } else if (direction == NavigationAnimation.DOWN) {
                container.addView(listView);
            }
            float startPosition = (direction == NavigationAnimation.UP) ? 0 : container.getWidth();
            float endPosition = direction == (NavigationAnimation.UP) ? container.getWidth() : 0;
            float startAlpha = direction == (NavigationAnimation.UP) ? 1.0f : 0.0f;
            float endAlpha = direction == (NavigationAnimation.UP) ? 0.0f : 1.0f;
            View animatedView = (direction == NavigationAnimation.UP) ? oldListView : listView;

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(new AlphaAnimation(startAlpha, endAlpha));
            animationSet.addAnimation(new TranslateAnimation(startPosition, endPosition, 0, 0));
            animationSet.setInterpolator(new DecelerateInterpolator());
            animationSet.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            animationSet.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (oldListView != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                container.removeView(oldListView);
                            }
                        });
                    }
                }
            });

            animatedView.startAnimation(animationSet);
        }
    }

    /**
     * @return The selected item according to the selection rule, else null.
     */
    private File getSelectedItem() {

        // The current directory is implicitly selected
        if ((selectionType == SelectionMode.DIRECTORY) && file.isDirectory()) {
            return file;
        }

        // Get the checked item in the list
        int checked = listView.getCheckedItemPosition();
        if (checked != AdapterView.INVALID_POSITION) {
            File file = ((FileListAdapter) listView.getAdapter()).getItem(checked);
            return ((selectionType == SelectionMode.FILE) ^ !file.isFile()) ? file : null;
        } else {
            return null;
        }
    }

    /**
     * @return A list of files to display for the specified path.
     */
    private File[] getFilesToDisplayForPath(File nextDir) {
        return nextDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (!showFiles && pathname.isFile()) {
                    return false;
                }
                if (!showHidden && pathname.getName().startsWith(".")) {
                    return false;
                }
                return true;
            }
        });
    }

    private enum NavigationAnimation {
        UP, DOWN, NONE
    }
}
