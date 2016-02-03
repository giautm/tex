package xyz.giautm.tex;

import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.Date;

import xyz.giautm.tex.model.Transport;

/**
 * xyz.giautm.tex
 * 31/01/2016 - giau.tran.
 */
public class FirebaseHelper {

    private static final String LOG_TAG = "FirebaseHelper";

    public static void moveToUpdated(final String orderCode, final int cause, final String causeNote) {
        if (orderCode != null) {
            final Firebase rootRef = App.getFirebaseRootRef();
            final AuthData authData = rootRef.getAuth();
            if (authData != null) {
                final Firebase transportHistory = rootRef.child("transport").child(authData.getUid());
                final Firebase currentTransport = transportHistory.child("current").child(orderCode);
                final Firebase updatedTransport = transportHistory.child("updated").child(orderCode);

                currentTransport.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        final Transport current = snapshot.getValue(Transport.class);
                        if (current != null) {
                            current.setOrderCode(orderCode);
                            current.setCause(cause);
                            current.setCauseNote(causeNote);
                            current.setUpdatedAt(new Date().getTime());
                            current.setLatLng(12.00, 12.00);

//                            updatedTransport.runTransaction(new Transaction.Handler() {
//                                @Override
//                                public Transaction.Result doTransaction(MutableData mutableData) {
//                                    return null;
//                                }
//
//                                @Override
//                                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
//
//                                }
//                            });
                            updatedTransport.setValue(current, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError error, Firebase firebase) {
                                    if (error == null) {
                                        currentTransport.removeValue();
                                        Toast.makeText(App.getInstance(), R.string.toast_message_update_order_complete,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(App.getInstance(), R.string.toast_message_update_order_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError error) {
                        Log.d(LOG_TAG, error.getMessage());
                    }
                });
            }
        }
    }
}
