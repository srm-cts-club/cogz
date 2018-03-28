package karan.cogz;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdatesFragment extends Fragment {

    ArrayList<String> updates;
    Context context;
    SharedPreferences sharedPreferences;
    public UpdatesFragment() {
        // Required empty public constructor
    }

    public static UpdatesFragment newInstance(String param1, String param2) {
        UpdatesFragment fragment = new UpdatesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        updates = new ArrayList<String>();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_updates, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final RecyclerView.Adapter mAdapter = new RecyclerviewAdapter(updates);
        mRecyclerView.setAdapter(mAdapter);


        final SwipeRefreshLayout srl = view.findViewById(R.id.swiperefresh);
        final SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Log.d("refresh", "in onRefresh");
                    JSONObject jsonObject = new JSONObject();
                    JSONObject args = new JSONObject();
                    jsonObject.put("type", "select");
                    args.put("table", "updates");
                    JSONArray columns = new JSONArray();
                    columns.put("update_id");
                    columns.put("update");
                    args.put("columns", columns);
                    JSONArray order_by = new JSONArray();
                    JSONObject orderby = new JSONObject();
                    orderby.put("column", "update_id");
                    orderby.put("order", "desc");
                    order_by.put(orderby);
                    args.put("order_by", order_by);
                    jsonObject.put("args", args);
                    AndroidNetworking.post("https://data." + getString(R.string.cluster_name) + ".hasura-app.io/v1/query")
                            .addHeaders("Content-Type", "application/json")
                            .addHeaders("Authorization", "Bearer " + sharedPreferences.getString("token", ""))
                            .addJSONObjectBody(jsonObject)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONArray(new JSONArrayRequestListener() {
                                @Override
                                public void onResponse(JSONArray response) {

                                    Log.d("refresh", "received response");
                                    // do anything with response
                                    updates.clear();
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            updates.add(response.getJSONObject(i).getString("update"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (response.length() == 0) {
                                        updates.add("No updates");
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    srl.setRefreshing(false);
                                }

                                @Override
                                public void onError(ANError error) {
                                    Log.d("update", error.getErrorBody());
                                    updates.add("No updates");
                                    // handle error
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        srl.setOnRefreshListener(listener);

        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(true);
                listener.onRefresh();
            }
        });
        if(sharedPreferences.getString("acc_type","").equals("mentor")){
            final EditText msgbox = view.findViewById(R.id.editText);
            msgbox.setVisibility(View.VISIBLE);
            final Button post = view.findViewById(R.id.button2);
            post.setVisibility(View.VISIBLE);
            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        post.setEnabled(false);
                        Log.d("post","post clicked");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("update", msgbox.getText().toString());
                        jsonObject.put("userid", sharedPreferences.getInt("hasura_id",0));
                        jsonObject.put("token",sharedPreferences.getString("token",""));
                        AndroidNetworking.post("https://api." + getString(R.string.cluster_name) + ".hasura-app.io/updates/newupdate")
                                .addJSONObjectBody(jsonObject)
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        msgbox.setText("");
                                        post.setEnabled(true);
                                        listener.onRefresh();
                                    }

                                    @Override
                                    public void onError(ANError error) {
                                        Log.d("update",error.getErrorBody());
                                        post.setEnabled(true);
                                        // handle error
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
