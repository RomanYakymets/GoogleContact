package com.example.roma.testtask.data;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.roma.testtask.ContactInfoActivity;
import com.example.roma.testtask.EditContactActivity;
import com.example.roma.testtask.R;
import com.example.roma.testtask.model.Contact;
import com.google.api.services.people.v1.PeopleService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder> {

    private static final String TAG = "PeopleAdapter";
    private List<Contact> contacts;
    private PeopleService peopleService;
    private ContactData contactData;

    public PeopleAdapter(Context context, List<Contact> contacts, PeopleService peopleService) {
        this.contacts = contacts;
        this.peopleService = peopleService;
        contactData = new ContactData(context);
    }

    @Override
    public PeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new PeopleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PeopleViewHolder holder, int position) {

        String name = "";
        if(contacts.get(position).getFirstName() != null){
            name = contacts.get(position).getFirstName();
        }
        if(contacts.get(position).getLastName() != null){
            name = name + " " + contacts.get(position).getLastName();
        }
        holder.name.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(v.getContext(), ContactInfoActivity.class);
                    intent.putExtra("Contact", contacts.get(holder.getAdapterPosition()));
                    v.getContext().startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
//                final WeakReference<Context> weakContext = new WeakReference<>(view.getContext());
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.menu_context_item);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.contextMenuEdit:

                                Intent intent= new Intent(view.getContext(), EditContactActivity.class);
                                intent.putExtra("contact", contacts.get(holder.getAdapterPosition()));
                                view.getContext().startActivity(intent);

                                break;
                            case R.id.contextMenuDelete:

                                new PeoplesAsync().execute(
                                        contacts.get(holder.getAdapterPosition()).getContactId()
                                );

                                contacts.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(),contacts.size());
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    class PeopleViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        private PeopleViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.list_item_name);
        }
    }

    public void clearData() {
        contacts.clear();
        notifyDataSetChanged();
    }

    public void sortData() {
        Collections.sort(contacts);
        notifyDataSetChanged();
    }

    private class PeoplesAsync extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                contactData.deleteContact(params[0]);
                peopleService.people()
                        .deleteContact(params[0])
                        .execute();

            }catch (IOException e){
                Log.d(TAG, "IOException" + e.getMessage());
            }

            return null;
        }
    }
}