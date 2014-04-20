package co.arcs.android.fileselector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FilenamePickerDialogFragment extends DialogFragment {

    /**
     * Callback interface used to indicate the user has chosen a filename.
     */
    interface Listener {

        void onFilenameSet(String filename);
    }

    private AlertDialog dialog;
    private EditText editText;
    private Listener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View contentView = LayoutInflater.from(getActivity())
                                         .inflate(R.layout.view_new_folder_dialog, null);
        this.editText = (EditText) contentView.findViewById(R.id.folder_name);
        editText.setFilters(new InputFilter[]{inputFilter});
        editText.addTextChangedListener(textWatcher);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.new_folder_title));
        builder.setView(contentView);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, onClickListener);

        dialog = builder.create();

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setEnabled(false);
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            String filename = getFilenameFromField();
            if (listener != null) {
                listener.onFilenameSet(filename);
            }
        }
    };

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Gets the file name from the field and removes leading/trailing spaces.
     */
    private String getFilenameFromField() {
        return editText.getText().toString().trim();
    }

    private final TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            okButton.setEnabled(getFilenameFromField().length() > 0);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Filter that only allows alphanumeric and space characters. Others are
     * restricted to prevent injection attacks, e.g. "$base" +
     * "../fileInParent".
     */
    private final InputFilter inputFilter = new InputFilter() {

        @Override
        public CharSequence filter(
                CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source instanceof SpannableStringBuilder) {
                SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder) source;
                for (int i = end - 1; i >= start; i--) {
                    char currentChar = source.charAt(i);
                    if (!checkChar(currentChar)) {
                        sourceAsSpannableBuilder.delete(i, i + 1);
                    }
                }
                return source;
            } else {
                StringBuilder filteredStringBuilder = new StringBuilder();
                for (int i = start; i < end; i++) {
                    char currentChar = source.charAt(i);
                    if (checkChar(currentChar)) {
                        filteredStringBuilder.append(currentChar);
                    }
                }
                return filteredStringBuilder.toString();
            }
        }

        private boolean checkChar(char c) {
            return Character.isLetterOrDigit(c) || (c == ' ');
        }
    };
    private Button okButton;
}
