package xyz.giautm.tex.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import xyz.giautm.tex.App;
import xyz.giautm.tex.R;
import xyz.giautm.tex.model.Order;
import xyz.giautm.tex.model.Transport;
import xyz.giautm.tex.utilities.FirebaseRecyclerAdapter;
import xyz.giautm.tex.view.SimpleDividerItemDecoration;

public class TransportFragment extends Fragment {
    private static final String ARG_TRANSPORT_LIST = "list-name";
    private static final String ARG_TRANSPORTER_ID = "transporter-id";
    private static final String ARG_DATE_SEPARATED = "date-separated";
    private static final String ARG_SIMPLE_ACTIONS = "simple-actions";

    private OnItemClickListener mListener;
    private String mTransportList;
    private String mTransporterId;
    private boolean mDateSeparated = false;
    private boolean mSimpleActions = true;
    private FirebaseRecyclerAdapter<Transport, ViewHolder> mAdapter = null;

    public interface OnItemClickListener {
        void onItemViewClick(Order order);

        void onItemCallClick(Order order);

        void onItemSendClick(Order order);
    }

    private final Firebase rootRef = App.getFirebaseRootRef();

    public static TransportFragment newInstance(String transportList, String transporterId) {
        TransportFragment fragment = new TransportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRANSPORT_LIST, transportList);
        args.putString(ARG_TRANSPORTER_ID, transporterId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TransportFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickListener) {
            mListener = (OnItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTransportList = getArguments().getString(ARG_TRANSPORT_LIST);
            mTransporterId = getArguments().getString(ARG_TRANSPORTER_ID);
            mDateSeparated = getArguments().getBoolean(ARG_DATE_SEPARATED);
            mSimpleActions = getArguments().getBoolean(ARG_SIMPLE_ACTIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transport_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.transport_list);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Set the adapter
        if (mTransportList != null) {
            Firebase transportRef = App.getTransportRef()
                    .child(mTransporterId).child(mTransportList);

//            if (mDateSeparated) {
//                transportRef = transportRef.child(new Date().toString());
//            }
            mAdapter = new FirebaseRecyclerAdapter<Transport, ViewHolder>(
                    Transport.class, R.layout.fragment_transport, ViewHolder.class, transportRef) {
                @Override
                protected void populateViewHolder(final ViewHolder holder, final Transport model, int position) {
                    App.getOrdersRef().child(model.getOrderCode())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int calls = 0, sends = 0;
                                    Map callHistory = model.getCallHistory();
                                    if (callHistory != null) {
                                        calls = callHistory.size();
                                    }
                                    Map sendHistory = model.getSendHistory();
                                    if (sendHistory != null) {
                                        sends = sendHistory.size();
                                    }

                                    final Order order = dataSnapshot.getValue(Order.class);
                                    holder.setOrder(order, calls, sends);
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mListener.onItemViewClick(order);
                                        }
                                    });
                                    holder.call.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mListener.onItemCallClick(order);
                                        }
                                    });
                                    holder.send.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mListener.onItemSendClick(order);
                                        }
                                    });
                                    Log.d("D", mTransportList);
                                    holder.setActionVisibility(mTransportList.equals("current"));
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                }
            };
            recyclerView.setAdapter(mAdapter);
        }

        return view;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final Button call;
        private final Button send;
        private final TextView deliveryAddress;
        private final TextView recipientName;
        private final TextView orderCode;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();
            call = (Button) view.findViewById(R.id.button_call);
            send = (Button) view.findViewById(R.id.button_send);

            deliveryAddress = (TextView) view.findViewById(R.id.textview_delivery_address);
            recipientName = (TextView) view.findViewById(R.id.textview_recipient_name);
            orderCode = (TextView) view.findViewById(R.id.textview_order_code);
        }

        public void setActionVisibility(boolean visibility) {
            if (visibility) {
                call.setVisibility(View.VISIBLE);
                send.setVisibility(View.VISIBLE);
            } else {
                call.setVisibility(View.GONE);
                send.setVisibility(View.GONE);
            }
        }

        public void setOrder(Order order, int calls, int sends) {
            if (order == null) {
                call.setEnabled(false);
                send.setEnabled(false);

                deliveryAddress.setText(R.string.textview_not_found);
                recipientName.setText(R.string.textview_not_found);
                orderCode.setText(R.string.textview_not_found);
            } else {
                call.setEnabled(true);
                send.setEnabled(true);

                deliveryAddress.setText(order.getDeliveryAddress());
                recipientName.setText(order.getRecipientName());
                orderCode.setText(String.format(context.getString(R.string.text_order_code), order.getOrderCode()));

                if (order.getRecipientPhone() != null) {
                    if (calls > 0) {
                        call.setText(String.format(context.getString(R.string.button_calls), calls));
                    } else {
                        call.setText(R.string.button_call);
                    }

                    if (sends > 0) {
                        send.setText(String.format(context.getString(R.string.button_sends), sends));
                    } else {
                        send.setText(R.string.button_send);
                    }
                } else {
                    call.setVisibility(View.GONE);
                    send.setVisibility(View.GONE);
                }
            }
        }
    }
}
