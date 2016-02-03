package xyz.giautm.tex.utilities;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import xyz.giautm.tex.R;
import xyz.giautm.tex.model.Order;
import xyz.giautm.tex.model.Transport;

/**
 * xyz.giautm.tex.utilities
 * 01/02/2016 - giau.tran.
 */
public class CauseDialog {
    public interface causeDialogListener {
        void onConfirmed(int cause, String causeNote);

        void onCancelled();
    }

    public static void show(Activity activity, final Order order, final causeDialogListener listener) {
        if (order != null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_cause, null);

            final EditText causeNote = (EditText) dialogView.findViewById(R.id.edit_text_cause_note);

            final ArrayAdapter adapter = new ArrayAdapter<>(activity,
                    android.R.layout.simple_spinner_item, Transport.CauseEnum.values());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner_cause);
            spinner.setAdapter(adapter);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(dialogView);
            builder.setTitle(R.string.dialog_cause_title);
            builder.setMessage(String.format(activity.getString(R.string.message_cause_question), order.getRecipientName()));
            builder.setPositiveButton(R.string.button_confirmed, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    int selected = spinner.getSelectedItemPosition();
                    if (selected > -1) {
                        Transport.CauseEnum cause = (Transport.CauseEnum) adapter.getItem(selected);
                        listener.onConfirmed(cause.getValue(), causeNote.getText().toString());
                        dialog.dismiss();
                    }
                }
            });
            builder.setNegativeButton(R.string.button_cancelled, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    listener.onCancelled();
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }
    }
}
