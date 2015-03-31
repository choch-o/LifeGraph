package com.twodevs.chocho.lifegraphKEDI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

/**
 * Created by chocho on 2015-02-01.
 */
public class ColorPickerDialog extends DialogFragment implements ColorPickerSwatch.OnColorSelectedListener {
    DatabaseHandler db = null;
    protected AlertDialog mAlertDialog;
    private DialogInterface.OnDismissListener _listener;
    protected int[] mColors = null;
    protected int mColumns;
    protected ColorPickerSwatch.OnColorSelectedListener mListener;
    private ColorPickerPalette mPalette;
    private ProgressBar mProgress;
    protected int mSelectedColor;
    protected int mSize;
    protected int mTitleResId = R.string.color_picker_default_title;
    String new_cate_name;
    String new_cate_color;

    int call = -1;
    int categoryID = -1;

    private void refreshPalette() {
        if ((this.mPalette != null) && (this.mColors != null))
            this.mPalette.drawPalette(this.mColors, this.mSelectedColor);
    }

    public void initialize(int titleId, int[] colors, int selectedColor, int columns, int size, int tCall, int tCategoryID) {
        setArguments(titleId, columns, size);
        setColors(colors, selectedColor);

        this.call = tCall;
        this.categoryID = tCategoryID;
    }

    public void onColorSelected(int selectedColor) {
        if (this.mListener != null)
            this.mListener.onColorSelected(selectedColor);
        if ((getTargetFragment() instanceof ColorPickerSwatch.OnColorSelectedListener))
            ((ColorPickerSwatch.OnColorSelectedListener) getTargetFragment()).onColorSelected(selectedColor);
        if (selectedColor != this.mSelectedColor) {
            this.mSelectedColor = selectedColor;
            this.mPalette.drawPalette(this.mColors, this.mSelectedColor);
        }
        //dismiss();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mTitleResId = getArguments().getInt("title_id");
            this.mColumns = getArguments().getInt("columns");
            this.mSize = getArguments().getInt("size");
        }
        if (bundle != null) {
            this.mColors = bundle.getIntArray("colors");
            this.mSelectedColor = ((Integer) bundle.getSerializable("selected_color")).intValue();
        }
    }

    public Dialog onCreateDialog(Bundle bundle) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.color_picker_dialog, null);
        this.mProgress = ((ProgressBar) view.findViewById(android.R.id.progress));
        this.mPalette = ((ColorPickerPalette) view.findViewById(R.id.color_picker));
        this.mPalette.init(this.mSize, this.mColumns, this);
        if (this.mColors != null)
            showPaletteView();
        final EditText color_edit = (EditText) view.findViewById(R.id.color_dialog_edit);
        final AlertDialog.Builder color_dialog = new AlertDialog.Builder(getActivity());

        if(db == null) db = new DatabaseHandler(view.getContext());
        if(categoryID != -1) color_edit.setText(db.getCategory(categoryID).getName());

        color_dialog.setTitle(this.mTitleResId).setView(view);
        color_dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*new_cate_name = color_edit.getText().toString();
                new_cate_color = String.format("#%06X", (0xFFFFFF & (mSelectedColor)));
                Category new_cate = new Category(new_cate_name, new_cate_color);
                db.createCategory(new_cate);

                if(call == 1) {
                    EventActivity callingActivity = (EventActivity) getActivity();
                    callingActivity.updateCategorySpinner();
                }*/

                new_cate_name = color_edit.getText().toString();
                new_cate_color = String.format("#%06X", (0xFFFFFF & (mSelectedColor)));

                if(categoryID == -1) {
                    Category new_cate = new Category(new_cate_name, new_cate_color);
                    db.createCategory(new_cate);
                }
                else
                {
                    Category new_cate = new Category(categoryID, new_cate_name, new_cate_color);
                    db.updateCategory(new_cate);
                }

                if (call == 1) {
                    EventActivity callingActivity = (EventActivity) getActivity();
                    callingActivity.updateCategorySpinner();
                } else if (call == 2) {
                    CategoryListActivity callingActivity = (CategoryListActivity) getActivity();
                    callingActivity.initializeList();
                }

                dialog.dismiss();
            }
        });
        color_dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        this.mAlertDialog = color_dialog.create();

        return this.mAlertDialog;
    }
    public void setOnDismissListener(DialogInterface.OnDismissListener $listener) {
        _listener = $listener;
    }
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putIntArray("colors", this.mColors);
        bundle.putSerializable("selected_color", Integer.valueOf(this.mSelectedColor));
    }

    public void setArguments(int titleId, int columns, int size) {
        Bundle bundle = new Bundle();
        bundle.putInt("title_id", titleId);
        bundle.putInt("columns", columns);
        bundle.putInt("size", size);
        setArguments(bundle);
    }

    public void setColors(int[] colors, int selected) {
        if ((this.mColors != colors) || (this.mSelectedColor != selected)) {
            this.mColors = colors;
            this.mSelectedColor = selected;
            refreshPalette();
        }
    }

    public void setOnColorSelectedListener(ColorPickerSwatch.OnColorSelectedListener onColorSelectedListener) {
        this.mListener = onColorSelectedListener;
    }

    public void showPaletteView() {
        if ((this.mProgress != null) && (this.mPalette != null)) {
            this.mProgress.setVisibility(View.GONE);
            refreshPalette();
            this.mPalette.setVisibility(View.VISIBLE);
        }
    }

    public void showProgressBarView() {
        if ((this.mProgress != null) && (this.mPalette != null)) {
            this.mProgress.setVisibility(View.VISIBLE);
            this.mPalette.setVisibility(View.GONE);
        }
    }

}