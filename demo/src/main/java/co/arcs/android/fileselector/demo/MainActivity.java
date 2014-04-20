package co.arcs.android.fileselector.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;

import co.arcs.android.fileselector.FileSelectorActivity;

public class MainActivity extends Activity {

    private Button createButton;
    private EditText initialDirectory;
    private RadioButton themeActivity;
    private RadioButton themeDark;
    private RadioButton selectionTypeFile;
    private CheckBox allowUpNavigationAboveInitial;
    private CheckBox showFiles;
    private CheckBox showHidden;

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        themeActivity = (RadioButton) findViewById(R.id.themeActivity);
        themeDark = (RadioButton) findViewById(R.id.themeDark);

        initialDirectory = (EditText) findViewById(R.id.initialDirectory);
        allowUpNavigationAboveInitial = (CheckBox) findViewById(R.id.allowUpNavigationAboveInitial);
        selectionTypeFile = (RadioButton) findViewById(R.id.selectionTypeFile);
        showFiles = (CheckBox) findViewById(R.id.showFiles);
        showHidden = (CheckBox) findViewById(R.id.showHidden);
        createButton = (Button) findViewById(R.id.createButton);

        createButton.setOnClickListener(onClickListener);
        selectionTypeFile.setOnCheckedChangeListener(onCheckedChangeListener);
        initialDirectory.setText(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {
            File file = (File) data.getSerializableExtra(FileSelectorActivity.EXTRA_PICKED_FILE);
            Toast.makeText(this, "Selected: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
    }

    private Intent buildIntent() {

        boolean activity = themeActivity.isChecked();
        boolean dark = themeDark.isChecked();
        Class<?> clazz;
        if (activity) {
            if (dark) {
                clazz = ThemedActivities.Dark.class;
            } else {
                clazz = ThemedActivities.Light.class;
            }
        } else {
            if (dark) {
                clazz = ThemedActivities.DarkDialog.class;
            } else {
                clazz = ThemedActivities.LightDialog.class;
            }
        }

        Intent i = new Intent(this, clazz);

        i.putExtra(FileSelectorActivity.EXTRA_STR_INITIAL_DIRECTORY,
                   initialDirectory.getText().toString());

        i.putExtra(FileSelectorActivity.EXTRA_BOOL_ALLOW_UP_NAVIGATION_ABOVE_INITIAL_DIRECTORY,
                   allowUpNavigationAboveInitial.isChecked());

        if (selectionTypeFile.isChecked()) {
            i.putExtra(FileSelectorActivity.EXTRA_STR_SELECTION_TYPE,
                       FileSelectorActivity.TYPE_FILE);
        } else {
            i.putExtra(FileSelectorActivity.EXTRA_STR_SELECTION_TYPE,
                       FileSelectorActivity.TYPE_DIRECTORY);
        }

        i.putExtra(FileSelectorActivity.EXTRA_BOOL_SHOW_FILES, showFiles.isChecked());

        i.putExtra(FileSelectorActivity.EXTRA_BOOL_SHOW_HIDDEN, showHidden.isChecked());

        return i;
    }

    private final OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            showFiles.setEnabled(!isChecked);
            showFiles.setChecked(true);
        }
    };

    private final OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            startActivityForResult(buildIntent(), REQUEST_CODE);
        }
    };
}