package xyz.giautm.tex.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Locale;

import xyz.giautm.tex.App;
import xyz.giautm.tex.R;
import xyz.giautm.tex.model.Order;

public class OrderDetailFragment extends Fragment {
    private static final String LOG_TAG = "OrderDetailFragment";

    public static final String ARG_ORDER_CODE = "order-code";
    public static final String ARG_SHOW_ACTION = "show-action";

    private OrderActionListener mListener;
    private Firebase mOrderRef;
    private Order mOrder;
    private boolean mShowActions = false;

    private TextView orderCode;
    private TextView recipientName;
    private TextView deliveryAddress;
    private TextView receivables;
    private Button delivered;
    private Button cancelled;

    private ValueEventListener mValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (mShowActions) {
                delivered.setVisibility(View.VISIBLE);
                cancelled.setVisibility(View.VISIBLE);
            } else {
                delivered.setVisibility(View.GONE);
                cancelled.setVisibility(View.GONE);
            }

            Order order = snapshot.getValue(Order.class);
            if (mOrder != null) {
//                if (order != null) {
//                    Date date = new Date(order.getUpdateAt());
//                    String dateStr = SimpleDateFormat.getInstance().format(date);
//                }
                Toast.makeText(getContext(), R.string.message_order_detail_changed, Toast.LENGTH_SHORT)
                        .show();
            }

            mOrder = order;
            if (mOrder != null) {
                orderCode.setText(String.format(getString(R.string.text_order_code), mOrder.getOrderCode()));
                recipientName.setText(mOrder.getRecipientName());
                deliveryAddress.setText(mOrder.getDeliveryAddress());

                double value = mOrder.getReceivables();
                if (value <= 0) {
                    receivables.setText(R.string.textview_paid);
                } else {
                    receivables.setText(DecimalFormat.getNumberInstance(Locale.US).format(value));
                }
            } else {
                orderCode.setText(R.string.textview_not_found);
                recipientName.setText(R.string.textview_not_found);
                deliveryAddress.setText(R.string.textview_not_found);
                receivables.setText(R.string.textview_not_found);
            }
        }

        @Override
        public void onCancelled(FirebaseError error) {
            Log.e(LOG_TAG, error.getMessage());
        }
    };

    // Required empty public constructor
    public OrderDetailFragment() {

    }

    public static OrderDetailFragment newInstance(String orderCode) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_CODE, orderCode);
        fragment.setArguments(args);
        return fragment;
    }

    public static OrderDetailFragment newInstance(String orderCode, boolean showAction) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_CODE, orderCode);
        args.putBoolean(ARG_SHOW_ACTION, showAction);
        fragment.setArguments(args);
        return fragment;
    }

    public void changeOrder(String orderCode, boolean showActions) {
        if (mOrderRef != null && mValueListener != null) {
            mOrderRef.removeEventListener(mValueListener);
        }

        mShowActions = showActions;

        mOrderRef = App.getFirebaseRootRef().child("orders").child(orderCode);
        if (mOrderRef != null && mValueListener != null) {
            mOrderRef.addValueEventListener(mValueListener);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OrderActionListener) {
            mListener = (OrderActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OrderActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mShowActions = getArguments().getBoolean(ARG_SHOW_ACTION);

            String orderCode = getArguments().getString(ARG_ORDER_CODE);
            assert orderCode != null;
            mOrderRef = App.getFirebaseRootRef().child("orders").child(orderCode);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        orderCode = (TextView) view.findViewById(R.id.textview_order_code);
        recipientName = (TextView) view.findViewById(R.id.textview_recipient_name);
        deliveryAddress = (TextView) view.findViewById(R.id.textview_delivery_address);
        receivables = (TextView) view.findViewById(R.id.textview_receivables);

        delivered = (Button) view.findViewById(R.id.button_delivered);
        delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDeliveredClick(mOrder);
            }
        });

        cancelled = (Button) view.findViewById(R.id.button_cancelled);
        cancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelledClick(mOrder);
            }
        });

        mOrderRef.addValueEventListener(mValueListener);

        return view;
    }

    @Override
    public void onDestroyView() {
        if (mOrderRef != null && mValueListener != null) {
            mOrderRef.removeEventListener(mValueListener);
        }
        super.onDestroyView();
    }

    public interface OrderActionListener {
        void onDeliveredClick(Order order);

        void onCancelledClick(Order order);
    }
}
