package com.writingstar.autotypingandtextexpansion.ClassHelp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.writingstar.autotypingandtextexpansion.ClassActView.MainActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.PremiumScreenActivity;
import com.writingstar.autotypingandtextexpansion.R;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HelperClass {
    public static String IS_FROM_NOTIFICATION = "isfromnotification";
    public static String IS_FROM_NOTIFICATION_MESSAGE = "isfromnotificationmessage";
    public static String IS_FROM_NOTIFICATION_type = "isfromnotificationtype";
    public static final String IS_FULLPRO = "fullpro";
    public static final Boolean IS_TRUE = true;
    public static final Boolean IS_FALSE = false;
    public static String deviceConnectiontype = "Mobile";
    static Boolean isInternetPresent = false;
    public static int val, rate;
    public static TextView txt_rate, txt_rate_title, txt_rate_message;
    public static ImageView rate_main;
    public static Boolean check_internet(Context context) {
        ConnectionDetector cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            return true;
        } else {
            return false;
        }
    }

    static ProgressDialog pDialog;

    public static void showProgressDialog(Context context, String message) {
        pDialog = new ProgressDialog(context,R.style.AlertDialogCustom);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        try {
            pDialog.show();
        } catch (Exception e) {
        }
    }

    public static void updateProgressDialog(Context context, String message) {
        if (pDialog != null && pDialog.isShowing()) {
            Log.d("UPDATE__", "call");
            pDialog.setMessage(message);
        }
    }

    /* dismiss ProgressDialog */
    public static void dismissProgressDialog() {
        if (pDialog != null) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }


    public static float convertDpToPixel(Context context, float dp) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {

        if (bitmap.getWidth() >= bitmap.getHeight()) {

            bitmap = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );

        } else {

            bitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCornerBitmapPreview(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static void rate(final Context context) {
        val = 0;
        try {
            final View dialogView = View.inflate(context, R.layout.dialog_say_thanks, null);
            final android.app.AlertDialog mAlertDialog = new android.app.AlertDialog.Builder(context)
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();


            Button buttonPositive = (Button) dialogView.findViewById(R.id.btn_rate_now);
            Button buttonNegative = (Button) dialogView.findViewById(R.id.btn_later);


            final ImageView star1, star2, star3, star4, star5;

            star1 = dialogView.findViewById(R.id.star1);
            star2 = dialogView.findViewById(R.id.star2);
            star3 = dialogView.findViewById(R.id.star3);
            star4 = dialogView.findViewById(R.id.star4);
            star5 = dialogView.findViewById(R.id.star5);
            rate_main = dialogView.findViewById(R.id.img_gif);
            txt_rate = dialogView.findViewById(R.id.txt_rate);
            txt_rate_title = dialogView.findViewById(R.id.txt_rate_title);
            txt_rate_message = dialogView.findViewById(R.id.txt_rate_message);

            Glide.with(context).asGif().load(R.drawable.hand).into(rate_main);

            star1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    star1.setImageResource(R.drawable.ic_star_fill);
                    star2.setImageResource(R.drawable.non_fillstar);
                    star3.setImageResource(R.drawable.non_fillstar);
                    star4.setImageResource(R.drawable.non_fillstar);
                    star5.setImageResource(R.drawable.non_fillstar);
                    val = 1;
                    rate = 1;
                    chngeVal();
                    txt_rate.setText(context.getResources().getString(R.string.hated_it));
                }


            });
            star2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    star1.setImageResource(R.drawable.ic_star_fill);
                    star2.setImageResource(R.drawable.ic_star_fill);
                    star3.setImageResource(R.drawable.non_fillstar);
                    star4.setImageResource(R.drawable.non_fillstar);
                    star5.setImageResource(R.drawable.non_fillstar);
                    val = 1;
                    rate = 2;
                    chngeVal();
                    txt_rate.setText(context.getResources().getString(R.string.dislike_it));
                }
            });
            star3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    star1.setImageResource(R.drawable.ic_star_fill);
                    star2.setImageResource(R.drawable.ic_star_fill);
                    star3.setImageResource(R.drawable.ic_star_fill);
                    star4.setImageResource(R.drawable.non_fillstar);
                    star5.setImageResource(R.drawable.non_fillstar);
                    val = 1;
                    rate = 3;
                    chngeVal();
                    txt_rate.setText(context.getResources().getString(R.string.its_ok));
                }
            });
            star4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    star1.setImageResource(R.drawable.ic_star_fill);
                    star2.setImageResource(R.drawable.ic_star_fill);
                    star3.setImageResource(R.drawable.ic_star_fill);
                    star4.setImageResource(R.drawable.ic_star_fill);
                    star5.setImageResource(R.drawable.non_fillstar);
                    val = 1;
                    rate = 4;
                    chngeVal();
                    txt_rate.setText(context.getResources().getString(R.string.liked_it));
                }
            });
            star5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    star1.setImageResource(R.drawable.ic_star_fill);
                    star2.setImageResource(R.drawable.ic_star_fill);
                    star3.setImageResource(R.drawable.ic_star_fill);
                    star4.setImageResource(R.drawable.ic_star_fill);
                    star5.setImageResource(R.drawable.ic_star_fill);
                    val = 2;
                    chngeVal();
                    txt_rate.setText(context.getResources().getString(R.string.loved_it));
                }
            });


            buttonPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (val == 2) {
                        showRateDialog(context);
                        mAlertDialog.dismiss();
                    } else if (val == 1) {
                        mAlertDialog.dismiss();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,R.style.AlertDialogCustom);
                        alertDialog.setMessage(context.getString(R.string.thanks_for_rate));
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        final AlertDialog alertDialog2 = alertDialog.create();
                        alertDialog2.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                alertDialog2.getButton(alertDialog2.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
                            }
                        });
                        alertDialog2.show();
                    } else {
                        Snackbar.make(dialogView, context.getString(R.string.rate_app_zero_star_error), Snackbar.LENGTH_SHORT).show();
                    }


                }
            });

            buttonNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        } catch (Exception e) {

        }
    }

    public static void showRateDialog(final Context mContext) {

        final View dialogView = View.inflate(mContext, R.layout.play_store_dialog, null);
        final AlertDialog mAlertDialog = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(true)
                .create();


        Button buttonPositive = (Button) dialogView.findViewById(R.id.btn_rate_now);
        Button buttonNegative = (Button) dialogView.findViewById(R.id.btn_later);

        ImageView rating = (ImageView) dialogView.findViewById(R.id.rating);

        if (SharedPreferenceClass.getBoolean(mContext, "isDark", false))
            rating.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rating_night));
        else
            rating.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rating));


        buttonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName())));
                } catch (ActivityNotFoundException e) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                }

            }
        });

        buttonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    private static void chngeVal() {
        rate_main.setVisibility(View.GONE);
        txt_rate_title.setVisibility(View.GONE);
        txt_rate.setVisibility(View.VISIBLE);
        txt_rate_message.setVisibility(View.INVISIBLE);
    }

    public static void showDialog(Context mContext, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext,R.style.AlertDialogCustom);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public static void showProDialog(Context mContext, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext,R.style.AlertDialogCustom);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("PRO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mContext.startActivity(new Intent(mContext, PremiumScreenActivity.class));
                    }
                });
        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public static void shareApp(Context mContext) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.app_name));
            String sAux = mContext.getString(R.string.share_txt) + "\n" + mContext.getString(R.string.share_link);
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            mContext.startActivity(Intent.createChooser(i, mContext.getResources().getString(R.string.share_intentName)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


