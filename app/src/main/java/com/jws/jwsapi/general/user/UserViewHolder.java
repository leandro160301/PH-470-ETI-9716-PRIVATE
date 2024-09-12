package com.jws.jwsapi.general.user;

import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.ItemUsuariosRecyclerBinding;

import java.util.List;
import java.util.Objects;


public class UserViewHolder extends RecyclerView.ViewHolder {
    final ItemUsuariosRecyclerBinding binding;
    UserButtonClickListener userButtonClickListener;

    public UserViewHolder(ItemUsuariosRecyclerBinding binding,UserButtonClickListener userButtonClickListener) {
        super(binding.getRoot());
        this.binding = binding;
        this.userButtonClickListener = userButtonClickListener;
    }

    public void bind (UserModel user, List<UserModel> userModelList, int position) {
        binding.tvUsuario.setText(user.getName());
        binding.tvTipo.setText(user.getType());
        binding.tvDocumento.setText(String.valueOf(user.getId()));
        binding.tvNumero.setText(user.getCode());
        binding.lnEliminar.setOnClickListener(view -> userButtonClickListener.deleteUser(userModelList, position));

        if (Objects.equals(user.getType(), "Supervisor")) {
            binding.idIVCourseImage.setImageResource(R.drawable.icono_supervisor_negro);
        } else if (Objects.equals(user.getType(), "Operador")) {
            binding.idIVCourseImage.setImageResource(R.drawable.usuario_icono_negro);
        }
        binding.tvUsuario.setTextColor(Color.BLACK);
    }
}
