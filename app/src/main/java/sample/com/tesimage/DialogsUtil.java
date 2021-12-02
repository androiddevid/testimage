package sample.com.tesimage;

import android.app.AlertDialog;
import android.content.Context;

public class DialogsUtil {

    private Context mContext;

    public DialogsUtil(Context context) {
        this.mContext = context;
    }

    /**
     * Return an alert dialog
     *
     * @param message  message for the alert dialog
     * @param listener listener to trigger selection methods
     */
    public static void confirmDialog(Context context, String tittle, String message, String positiveBtnText, String negativeBtnText, final DialogConfirmInterface listener) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(positiveBtnText,
                (dialog, buttonId) -> listener.onPositiveButtonClicked());


        builder.setNegativeButton(negativeBtnText,
                (dialog, buttonId) -> {
                    dialog.dismiss();
                    listener.onNegativeButtonClicked();
                });
        builder.setTitle(tittle);
        builder.setMessage(message);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(false);
        builder.create().show();
    }


}


