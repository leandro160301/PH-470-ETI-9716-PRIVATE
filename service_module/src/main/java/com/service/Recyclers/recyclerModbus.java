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
import com.service.Balanzas.Interfaz.serviceDevice;
import com.service.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class recyclerModbus extends RecyclerView.Adapter<recyclerModbus.ViewHolder> {


        private int selectedPos = RecyclerView.NO_POSITION;
        private List<serviceDevice> mData;
        private final LayoutInflater mInflater;
        private ItemClickListener mClickListener;
        private int lastPosition = -1;
        private final AppCompatActivity mainActivity;
        TextView baud,databit,stopbit,parity,Slave,numMOD;
        Spinner port;
        int Modelo=0;
        // data is pa   ssed into the constructor
        public recyclerModbus(Context context, List<serviceDevice> data, AppCompatActivity mainActivity,ItemClickListener clickListener,int Modelo) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.mainActivity = mainActivity;
            mClickListener = clickListener;
            this.Modelo=Modelo;

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
            datos.add("Puerto Serie 1");
            datos.add("Puerto Serie 2");
            datos.add("Puerto Serie 3");
            datos.add("TCP/IP");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity.getApplicationContext(), android.R.layout.simple_spinner_item, datos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            port.setAdapter(adapter);



            return new ViewHolder(view);
        }
        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //ExampleItem currentItem = mExampleList.get(position);
            if(mData.size()>position){
                if(!(holder.numMOD.getText().toString().contains(String.valueOf(mData.get(position).getNB()+1)))){
                    if(mData.get(position).getModelo()==3){
                        int x= mData.get(position).getNB()+1;
                        holder.numMOD.setText(("Nº Modbus "+x));
                    }else{
                        int x= mData.get(position).getNB()+1;

                        holder.numMOD.setText(("Nº Balanza "+x));
                    }
                }
                        if((mData.get(position).getModelo()==Modelo && mData.get(position).getID()!=-1)&&(mData.get(position).getSeteo()||(position==0 && !mData.get(position).getSeteo()) )){//(mData.get(position).getTipo()!=-1 && (mData.get(position).getModelo()==Modelo)){
                            holder.constraintLayout1.setVisibility(VISIBLE);
                            holder.constraintLayout2.setVisibility(GONE);
                            ArrayList<String> List = mData.get(position).getDireccion();
                            int lenght=List.size();
                            if(lenght>0) {
                                String Baud = List.get(0);
                                holder.baud.setText(Baud);
                            }
                            holder.baud.setClickable(false);
                            if(lenght>1) {
                                String Data = List.get(1);
                                holder.databit.setText(Data);
                            }
                            holder.databit.setClickable(false);

                            if(lenght>2) {
                                String Stop = List.get(2);
                                holder.stopbit.setText(Stop);
                            }
                            holder.stopbit.setClickable(false);

                            if(lenght>3) {
                                String Parity = List.get(3);
                                holder.parity.setText(Parity);
                            }
                            holder.parity.setClickable(false);
                            String Slave= Integer.toString(mData.get(position).getID());
                            holder.Slave.setText(Slave);
                            holder.Slave.setClickable(false);
                            int salida=0;
                            if(mData.get(position).getSalida().equals("PuertoSerie 1")){
                                salida=0;
                            } else if (mData.get(position).getSalida().equals("PuertoSerie 2")) {
                                salida=1;
                            } else if (mData.get(position).getSalida().equals("PuertoSerie 3")) {
                                salida=2;
                            }else if (mData.get(position).getSalida().contains("IP")){
                                salida=3;
                            }
                            holder.port.setSelection(salida);
                            holder.port.setEnabled(false);
                            holder.port.setClickable(false);
                        }else if(mData.get(position).getTipo()==-1){
                            holder.constraintLayout1.setVisibility(GONE);
                            holder.constraintLayout2.setVisibility(VISIBLE);
                        }else{
                            holder.constraintLayout1.setVisibility(GONE);
                            holder.constraintLayout2.setVisibility(GONE);
                            holder.numMOD.setVisibility(GONE);
                            holder.ultimatelayout.setMaxHeight(0);
                        }

                    }
                holder.itemView.setOnClickListener(v -> {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(v,position,mData.get(position),Modelo);
                    }
                });

             //   setAnimation(holder.itemView, psition);

                 holder.itemView.setSelected(selectedPos == position);
            //holder.itemView.setBackgroundColor(Color.YELLOW);
            //TextView baud,databit,stopbit,parity,Slave;
            //Spinner port;
            /*if (selectedPos == position){

            }else{

            }if(mData.size()==2 && mData.get(position).getNB()){

                if(mData.get(position).getNB()==0 && Modelo!=0 ){
                    holder.constraintLayout1.setVisibility(GONE);
                    holder.constraintLayout2.setVisibility(VISIBLE);
                }
                if(mData.get(position).getNB()==1  && Modelo!=0  ){
                    holder.constraintLayout1.setVisibility(GONE);
                    holder.constraintLayout2.setVisibility(GONE);
                    holder.numMOD.setVisibility(GONE);
                    holder.ultimatelayout.setMaxHeight(0);
                }*/



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
            ConstraintLayout constraintLayout1,constraintLayout2,ultimatelayout;


            ViewHolder(View itemView) {
                super(itemView);
                ultimatelayout=itemView.findViewById(R.id.ultimatelayout);
                constraintLayout1=itemView.findViewById(R.id.constraintLayout1);
                constraintLayout2=itemView.findViewById(R.id.constraintLayout2);
                numMOD=itemView.findViewById(R.id.numMOD);
                baud = itemView.findViewById(R.id.tv_Baud);
                databit = itemView.findViewById(R.id.tv_Databit);
                stopbit = itemView.findViewById(R.id.tv_Stopbit);
                parity = itemView.findViewById(R.id.tv_Parity);
                Slave = itemView.findViewById(R.id.tv_Slave);
                port = itemView.findViewById(R.id.sp_port);

                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, getBindingAdapterPosition(),mData.get(selectedPos),Modelo);
                notifyItemChanged(selectedPos);
                selectedPos = getLayoutPosition();
                notifyItemChanged(selectedPos);
            }
        }

        // convenience method for getting data at click position
        public serviceDevice getItem(int id) {
            return mData.get(id);
        }

        // allows clicks events to be caught
        public void setClickListener(ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position,serviceDevice Device,int ModeloaAgregar);
        }
        public void removeAt(int position) {
            mData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mData.size());
        }
        public void filterList(ArrayList<serviceDevice> filteredList) {
            mData = filteredList;
            notifyDataSetChanged();
        }
    }
