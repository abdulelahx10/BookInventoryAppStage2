package com.example.android.bookinventoryappstage2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookinventoryappstage2.data.BookContract.BookEntry;

/**
 * Created by abdulelah on 29/01/2018.
 */

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        ImageView imageView = view.findViewById(R.id.image);
        final TextView quantityTextView = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.sale_button);

        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_IMAGE);

        String bookName = cursor.getString(nameColumnIndex);
        int bookPrice = cursor.getInt(priceColumnIndex);
        int bookQuantity = cursor.getInt(quantityColumnIndex);
        byte[] bookImage = cursor.getBlob(imageColumnIndex);

        nameTextView.setText(bookName);
        String price = Integer.toString(bookPrice) + context.getString(R.string.dollar);
        priceTextView.setText(price);
        quantityTextView.setText(Integer.toString(bookQuantity));
        if (bookImage != null) {
            imageView.setImageBitmap(getImage(bookImage));
        } else {
            imageView.setImageResource(R.drawable.book_thick_generic);
        }

        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int bookId = cursor.getInt(idColumnIndex);
        final Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);

        saleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int bookQuantity = Integer.parseInt(quantityTextView.getText().toString());
                if (bookQuantity > 0) {
                    quantityTextView.setText("" + (--bookQuantity));
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, bookQuantity);
                    context.getContentResolver().update(currentBookUri, values, null, null);
                } else {
                    Toast.makeText(context, context.getString(R.string.out_of_stock),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // convert from byte array to bitmap
    public Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
