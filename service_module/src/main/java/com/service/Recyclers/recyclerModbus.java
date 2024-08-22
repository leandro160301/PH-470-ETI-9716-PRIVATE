package com.service.Recyclers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.service.Balanzas.Interfaz.modbus;
import com.service.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class recyclerModbus extends RecyclerView.Adapter<recyclerModbus.ViewHolder> {


        private int selectedPos = RecyclerView.NO_POSITION;
        private List<modbus> mData;
        private final LayoutInflater mInflater;
        private ItemClickListener mClickListener;
        private int lastPosition = -1;
        private final AppCompatActivity mainActivity;
        TextView baud,databit,stopbit,parity,Slave,numMOD;
        Spinner port;
        // data is pa   ssed into the constructor
        public recyclerModbus(Context context, List<modbus> data, AppCompatActivity mainActivity,ItemClickListener clickListener) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.mainActivity = mainActivity;
            mClickListener = clickListener;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.modbusconfigadapter, parent, false);
            port = view.findViewById(R.id.sp_port);
            numMOD=view.findViewById(R.id.numMOD);
             baud= view.findViewById(R.id.tv_Baud);
             databit = view.findViewById(R.id.tv_Databit);
             stopbit = view.findViewById(R.id.tv_Stopbit);
             parity = view.findViewById(R.id.tv_Parity);
             Slave = view.findViewById(R.id.tv_Slave);
            ArrayList<String> datos = new ArrayList<String>();
            datos.add("Puerto A");
            datos.add("Puerto B");
            datos.add("Puerto C");
            datos.add("RED");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity.getApplicationContext(), android.R.layout.simple_spinner_item, datos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            port.setAdapter(adapter);



            return new ViewHolder(view);
        }
        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //ExampleItem currentItem = mExampleList.get(position);


            holder.numMOD.setText(holder.numMOD.getText().toString()+(position+1));
            if(mData.size()-1>position){
                holder.constraintLayout1.setVisibility(VISIBLE);
                holder.constraintLayout2.setVisibility(GONE);
                String Baud= Integer.toString(mData.get(position).getBaud());
                holder.baud.setText(Baud);
                String Data= Integer.toString(mData.get(position).getDatabit());
                holder.databit.setText(Data);
                String Stop= Integer.toString(mData.get(position).getStopbit());
                holder.stopbit.setText(Stop);
                String Parity= Integer.toString(mData.get(position).getParity());
                holder.parity.setText(Parity);
                String Slave= Integer.toString(mData.get(position).getSlave());
                holder.Slave.setText(Slave);
                holder.port.setSelection(mData.get(position).getPortselected());
            }else{
                holder.constraintLayout1.setVisibility(GONE);
                holder.constraintLayout2.setVisibility(VISIBLE);
            }
                //TextView baud,databit,stopbit,parity,Slave;
                //Spinner port;
            /*if (selectedPos == position){

            }else{

            }*/
            holder.itemView.setOnClickListener(v -> {
                if (mClickListener != null) {
                    mClickListener.onItemClick(v,position);
                }
            });
            setAnimation(holder.itemView, position);

            //holder.itemView.setBackgroundColor(Color.YELLOW);
            holder.itemView.setSelected(selectedPos == position);
        }

        // total number of rows

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView baud,databit,stopbit,parity,Slave,numMOD;
            Spinner port;
            ConstraintLayout constraintLayout1,constraintLayout2;


            ViewHolder(View itemView) {
                super(itemView);
                constraintLayout1=itemView.findViewById(R.id.constraintLayout1);
                constraintLayout2=itemView.findViewById(R.id.constraintLayout2);
                numMOD=itemView.findViewById(R.id.numMOD);
                baud = itemView.findViewById(R.id.tv_Baud);
                databit = itemView.findViewById(R.id.tv_Databit);
                stopbit = itemView.findViewById(R.id.tv_Stopbit);
                parity = itemView.findViewById(R.id.tv_Parity);
                Slave = itemView.findViewById(R.id.tv_Slave);
                port = itemView.findViewById(R.id.sp_port);
                ArrayList<String> datos = new ArrayList<String>();
                datos.add("Puerto A");
                datos.add("Puerto B");
                datos.add("Puerto C");
                datos.add("RED");


                ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity.getApplicationContext(), android.R.layout.simple_spinner_item, datos);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                port.setAdapter(adapter);

                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, getBindingAdapterPosition());
                notifyItemChanged(selectedPos);
                selectedPos = getLayoutPosition();
                notifyItemChanged(selectedPos);
            }
        }

        // convenience method for getting data at click position
        public modbus getItem(int id) {
            return mData.get(id);
        }

        // allows clicks events to be caught
        public void setClickListener(ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }
        public void removeAt(int position) {
            mData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mData.size());
        }
        public void filterList(ArrayList<modbus> filteredList) {
            mData = filteredList;
            notifyDataSetChanged();
        }
    }
