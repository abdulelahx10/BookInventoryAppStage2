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
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookinventoryappstage2.data.BookContract.BookEntry;


public class BookDetails extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int FINISH_ACTIVITY = 9;
    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri currentBookUri;
    private TextView nameText;
    private ImageView imageView;
    private TextView priceText;
    private TextView quantityText;
    private EditText byEditText;
    private Button plusButton;
    private Button minusButton;
    private TextView sNameText;
    private TextView sEmailText;
    private TextView sNumberText;
    private Button orderButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Intent intent = getIntent();
        currentBookUri = intent.getData();

        nameText = findViewById(R.id.book_name_text_view);
        imageView = findViewById(R.id.img_view);
        priceText = findViewById(R.id.price_text_view);
        quantityText = findViewById(R.id.quantity_text_view);
        byEditText = findViewById(R.id.by_number_edit_view);
        plusButton = findViewById(R.id.increase_button);
        minusButton = findViewById(R.id.decrease_button);
        sNameText = findViewById(R.id.supplier_name_text_view);
        sEmailText = findViewById(R.id.supplier_email_text_view);
        sNumberText = findViewById(R.id.supplier_number_text_view);
        orderButton = findViewById(R.id.order_button);

        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);

        plusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int quantity = Integer.parseInt(quantityText.getText().toString());
                int by = Integer.parseInt(byEditText.getText().toString());
                int result = quantity + by;
                quantityText.setText(Integer.toString(result));
                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, result);
                getContentResolver().update(currentBookUri, values, null, null);
            }
        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int quantity = Integer.parseInt(quantityText.getText().toString());
                int by = Integer.parseInt(byEditText.getText().toString());
                int result = quantity - by;
                if (result >= 0) {
                    quantityText.setText(Integer.toString(result));
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, result);
                    getContentResolver().update(currentBookUri, values, null, null);
                } else {
                    Toast.makeText(BookDetails.this, R.string.negative_value,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrderDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(BookDetails.this, Editor.class);
                intent.setData(currentBookUri);
                startActivityForResult(intent, FINISH_ACTIVITY);
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();

                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(BookDetails.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            String price = Integer.toString(bookPrice) + getString(R.string.dollar);
            priceText.setText(price);
            quantityText.setText(Integer.toString(bookQuantity));
            sNameText.setText(sName);
            sEmailText.setText(sEmail);
            sNumberText.setText(sNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        byEditText.setText("1");
    }

    private void showOrderDialog() {
        CharSequence options[] = new CharSequence[]{getString(R.string.email_supplier), getString(R.string.call_supplier)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.options);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                switch (id) {
                    case 0:
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType(getString(R.string.mail_intent_type));
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{sEmailText.getText().toString()});
                        i.putExtra(Intent.EXTRA_SUBJECT, R.string.need_product);
                        i.putExtra(Intent.EXTRA_TEXT, R.string.send_product);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(BookDetails.this, R.string.no_email_client, Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case 1:
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + sNumberText.getText().toString()));
                        startActivity(intent);
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
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
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 9) {
            finish();
        }
    }

    // convert from byte array to bitmap
    public Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
