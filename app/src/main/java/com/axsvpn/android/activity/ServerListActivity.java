package com.axsvpn.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.axsvpn.android.AppData;
import com.axsvpn.android.R;
import com.axsvpn.android.model.Server;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerListActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private ServerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText searchText;
    List<Server> servers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        recyclerView = findViewById(R.id.server_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        searchText = findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onQueryTextChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (AppData.isPremium) {
            servers = AppData.servers;
        } else {
            servers = new ArrayList<>();
            for (Server server: AppData.servers) {
                if (server.getPremiumOnly() == 0) {
                    servers.add(server);
                }
            }
        }
        AppData.temporaryServer = servers.get(0);
        mAdapter = new ServerAdapter(servers);
        recyclerView.setAdapter(mAdapter);
    }

    public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ViewHolder> {
        public List<Server> mServers;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mNameText;
            public CheckBox check_select;
            public ImageView flag;

            public ViewHolder(View v) {
                super(v);
                mNameText = v.findViewById(R.id.server_list_item_name_text);
                check_select = v.findViewById(R.id.check_select);
                flag = v.findViewById(R.id.img_flag);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppData.temporaryServer = mServers.get(getAdapterPosition());
                        finish();
                    }
                });
                check_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppData.temporaryServer = mServers.get(getAdapterPosition());
                        finish();
                    }
                });
            }
        }

        public ServerAdapter(List<Server> servers) {
            mServers = servers;
        }

        @Override
        public ServerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_list_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Server server = mServers.get(position);

            if(!server.getName().equals(getResources().getString(R.string.best_location))) {
                Glide
                        .with(getApplicationContext())
                        .load("https://axsvpn.com/images/country/" + server.getCountryCode() + ".png")
                        .centerCrop()
                        .placeholder(R.drawable.enabled)
                        .into(holder.flag);
            }
            holder.mNameText.setText(server.getName());
            if (server.getId() == AppData.selectedServer.getId()) {
                holder.mNameText.setTextColor(getResources().getColor(R.color.colorAccent));
                holder.check_select.setChecked(true);
            } else {
                holder.mNameText.setTextColor(getResources().getColor(R.color.colorPrimary));
                holder.check_select.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return mServers.size();
        }
    }


    public boolean onQueryTextChange(String query) {
        if(servers != null) {
            final ArrayList<Server> filteredModelList = filter(servers, query);
            mAdapter.mServers = filteredModelList;
            mAdapter.notifyDataSetChanged();
        }
        return true;
    }

    private static ArrayList<Server> filter(List<Server> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final ArrayList<Server> filteredModelList = new ArrayList<>();
        for (Server model : models) {
            final String text_name = model.getName().toLowerCase();

            if (text_name.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
