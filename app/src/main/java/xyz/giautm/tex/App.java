package xyz.giautm.tex;

import android.app.Application;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * xyz.giautm.tex
 * 24/01/2016 - giau.tran.
 */
public class App extends Application {
    private static Firebase sFirebaseRootRef = null;
    private static App sInstance = null;
//  private static final TikiService sTikiService;

    //    static {
//        sTikiService = new Retrofit.Builder()
//                .baseUrl("http://test.tiki.vn")
//                .build()
//                .create(TikiService.class);
//    }
//
//    public static TikiService getTikiService() {
//        return sTikiService;
//    }
    private AuthData mAuthData = null;

    public static boolean isLogin() {
        return sFirebaseRootRef.getAuth() != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        sFirebaseRootRef = new Firebase("https://kotex.firebaseio.com");
        sFirebaseRootRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (mAuthData != null) {
                    sFirebaseRootRef.child("deliveryMan").child(mAuthData.getUid())
                            .removeEventListener(listener);
                }

                mAuthData = authData;

                if (mAuthData != null) {
                    sFirebaseRootRef.child("deliveryMan").child(mAuthData.getUid())
                            .addValueEventListener(listener);
                } else {
                    Log.d("A", "Vừa đăng xuất");
                }
            }
        });
    }

    public static App getInstance() {
        return sInstance;
    }


    public static Firebase getFirebaseRootRef() {
        return sFirebaseRootRef;
    }
    public static Firebase getTransportRef() {
        return sFirebaseRootRef.child("transport");
    }
    public static Firebase getOrdersRef() {
        return sFirebaseRootRef.child("orders");
    }
}