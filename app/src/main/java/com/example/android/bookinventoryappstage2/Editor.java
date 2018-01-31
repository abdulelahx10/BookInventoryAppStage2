package com.example.android.bookinventoryappstage2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.bookinventoryappstage2.data.BookContract.BookEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Editor extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    private static final int GALLERY = 1;
    private static final int MAX_LENGTH_PHONE_NUMBER = 15;
    private static final int MAX_LENGTH = 9;

    private Uri currentBookUri;

    private ImageView imageView;
    private Button changeImgButton;
    private EditText nameText;
    private EditText priceText;
    private EditText quantityText;
    private EditText sNameText;
    private EditText sEmailText;
    private EditText sNumberText;

    private boolean bookHasChanged = false;
    private boolean imageAdded = false;

    private View.OnTouchListener TouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentBookUri = intent.getData();

        if (currentBookUri == null) {
            setTitle(getString(R.string.product_add));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.product_edit));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        imageView = findViewById(R.id.book_image);
        changeImgButton = findViewById(R.id.image_change_button);
        nameText = findViewById(R.id.book_name_edit_text_view);
        priceText = findViewById(R.id.price_edit_text_view);
        quantityText = findViewById(R.id.quantity_edit_text_view);
        sNameText = findViewById(R.id.supplier_name_edit_text_view);
        sEmailText = findViewById(R.id.supplier_email_edit_text_view);
        sNumberText = findViewById(R.id.supplier_number_edit_text_view);

        sNumberText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH_PHONE_NUMBER)});
        priceText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        quantityText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});


        nameText.setOnTouchListener(TouchListener);
        priceText.setOnTouchListener(TouchListener);
        quantityText.setOnTouchListener(TouchListener);
        sNameText.setOnTouchListener(TouchListener);
        sEmailText.setOnTouchListener(TouchListener);
        sNumberText.setOnTouchListener(TouchListener);

        changeImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY);
            }
        });
    }

    private void saveBook() {
        String name = nameText.getText().toString().trim();
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        String price = priceText.getText().toString().trim();
        String quantity = quantityText.getText().toString().trim();
        String sName = sNameText.getText().toString().trim();
        String sEmail = sEmailText.getText().toString().trim();
        String sNumber = sNumberText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(quantity)
                || TextUtils.isEmpty(sName) || TextUtils.isEmpty(sEmail) || TextUtils.isEmpty(sNumber)) {
            Toast.makeText(this, getString(R.string.fields_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, name);
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, Integer.parseInt(price));
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(quantity));
        byte[] imageByte = null;
        if (imageAdded) {
            imageByte = getBytes(image);
        }
        values.put(BookEntry.COLUMN_PRODUCT_IMAGE, imageByte);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, sName);
        values.put(BookEntry.COLUMN_SUPPLIER_EMAIL, sEmail);
        values.put(BookEntry.COLUMN_SUPPLIER_NUMBER, sNumber);

        if (currentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_book_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_book_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    imageView.setImageBitmap(bitmap);
                    imageAdded = true;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Editor.this, R.string.failed, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(Editor.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(Editor.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (currentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_Book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_Book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        setResult(9);
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_PRODUCT_IMAGE,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_EMAIL,
                BookEntry.COLUMN_SUPPLIER_NUMBER};


        return new CursorLoader(this,
                currentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_IMAGE);
            int sNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int sEmailColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_EMAIL);
            int sNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NUMBER);

            String bookName = cursor.getString(nameColumnIndex);
            int bookPrice = cursor.getInt(priceColumnIndex);
            int bookQuantity = cursor.getInt(quantityColumnIndex);
            byte[] bookImage = cursor.getBlob(imageColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            String sEmail = cursor.getString(sEmailColumnIndex);
            String sNumber = cursor.getString(sNumberColumnIndex);

            nameText.setText(bookName);
            if (bookImage != null) {
                imageView.setImageBitmap(getImage(bookImage));
            } else {// use default image
                imageView.setImageResource(R.drawable.book_thick_generic);
            }
            String price = Integer.toString(bookPrice);
            priceText.setText(price);
            quantityText.setText(Integer.toString(bookQuantity));
            sNameText.setText(sName);
            sEmailText.setText(sEmail);
            sNumberText.setText(sNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameText.setText("");
        priceText.setText("");
        quantityText.setText("");
        sNameText.setText("");
        sEmailText.setText("");
        sNumberText.setText("");
    }


    // convert from bitmap to byte array
    public byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
