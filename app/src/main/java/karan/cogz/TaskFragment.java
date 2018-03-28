package karan.cogz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TaskFragment extends Fragment {

    String college;
    String task;
    String deadline;
    Context context;
    SharedPreferences sharedPreferences;
    String[] arrayC = {"SSN", "RMK", "RMD", "Velammal Engineering College", "Sathyabama", "St.Joseph's Institute of Technology", "RMKCET", "SRM University", "Velammal institute of Technology","B.S.Abdur Rahman Institute of Science and Technology", "Sri Venkateswara College of Engineering","Meenakshi Sundararajan College","St.Joseph's College of Engineering","VIT Chennai","IIT Madras"};
    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance(String param1, String param2) {
        TaskFragment fragment = new TaskFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        final TextView tv_college = view.findViewById(R.id.tv_college);
        final TextView tv_task = view.findViewById(R.id.tv_task);
        final TextView tv_date = view.findViewById(R.id.tv_date);
        final SwipeRefreshLayout srl = view.findViewById(R.id.srl);
        final SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Log.d("refresh", "in onRefresh");
                    JSONObject jsonObject = new JSONObject();
                    JSONObject args = new JSONObject();
                    jsonObject.put("type", "select");
                    args.put("table", "tasks");
                    JSONArray columns = new JSONArray();
                    columns.put("task_id");
                    columns.put("college");
                    columns.put("task");
                    columns.put("deadline");
                    columns.put("time");
                    args.put("columns", columns);
                    JSONObject where = new JSONObject();
                    JSONObject wh = new JSONObject();
                    wh.put("$eq",sharedPreferences.getString("college",""));
                    where.put("college",wh);
                    args.put("where",where);
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
                                    int max = Integer.MIN_VALUE;
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            if(response.getJSONObject(i).getInt("task_id")>max ){
                                                max = response.getJSONObject(i).getInt("task_id");
                                                college = response.getJSONObject(i).getString("college");
                                                task = response.getJSONObject(i).getString("task");
                                                deadline = response.getJSONObject(i).getString("deadline");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    tv_college.setText(college);
                                    tv_task.setText("Task: "+task);
                                    tv_date.setText("Deadline: "+deadline);
                                    if (response.length() == 0) {
                                        tv_college.setText(sharedPreferences.getString("college",""));
                                        tv_task.setText("No task");
                                        tv_date.setText("");
                                    }
                                    srl.setRefreshing(false);
                                }

                                @Override
                                public void onError(ANError error) {
                                    Log.d("update", error.getErrorBody());
                                    tv_college.setText(sharedPreferences.getString("college",""));
                                    tv_task.setText("No task");
                                    tv_date.setText("");
                                    srl.setRefreshing(false);
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
            final AutoCompleteTextView college = view.findViewById(R.id.autoCompleteTextView);
            college.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapterC = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, arrayC);
            college.setAdapter(adapterC);
            final EditText task = view.findViewById(R.id.task);
            college.setVisibility(View.VISIBLE);
            final EditText date = view.findViewById(R.id.date);
            college.setVisibility(View.VISIBLE);
            final Button taskbutton = view.findViewById(R.id.taskbutton);
            taskbutton.setVisibility(View.VISIBLE);
            taskbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        taskbutton.setEnabled(false);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("college",college.getText().toString());
                        editor.commit();
                        Log.d("post","post clicked");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("task", task.getText().toString());
                        jsonObject.put("college",college.getText().toString());
                        jsonObject.put("deadline",date.getText().toString());
                        jsonObject.put("userid", sharedPreferences.getInt("hasura_id",0));
                        jsonObject.put("token",sharedPreferences.getString("token",""));
                        AndroidNetworking.post("https://api." + getString(R.string.cluster_name) + ".hasura-app.io/task/newtask")
                                .addJSONObjectBody(jsonObject)
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        college.setText("");
                                        task.setText("");
                                        date.setText("");
                                        taskbutton.setEnabled(true);
                                        listener.onRefresh();
                                    }

                                    @Override
                                    public void onError(ANError error) {
                                        Log.d("update",error.getErrorBody());
                                        taskbutton.setEnabled(true);
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
