package com.jws.jwsapi.core.user;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.databinding.ItemUsuariosRecyclerBinding;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    List<UserModel> userElements;
    UserButtonClickListener userButtonClickListener;

    public UserAdapter(List<UserModel> data, UserButtonClickListener userButtonClickListener) {
        this.userElements = data;
        this.userButtonClickListener = userButtonClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemUsuariosRecyclerBinding binding = ItemUsuariosRecyclerBinding.inflate(inflater, parent, false);
        return new UserViewHolder(binding,userButtonClickListener);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserModel user = userElements.get(position);
        holder.bind(user, userElements,position);

    }

    @Override
    public int getItemCount() {
        return userElements.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<UserModel> filteredList) {
        userElements = filteredList;
        notifyDataSetChanged();
    }
}