package com.cristobalbernal.lacasanostraapk.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cristobalbernal.lacasanostraapk.R;
import com.cristobalbernal.lacasanostraapk.interfaces.IAPIService;
import com.cristobalbernal.lacasanostraapk.modelos.Producto;
import com.cristobalbernal.lacasanostraapk.modelos.Tipo;
import com.cristobalbernal.lacasanostraapk.rest.RestClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Home extends Fragment {
    private IAPIService iapiService;
    public Fragment_Home(){
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iapiService = RestClient.getInstance();
        Button button = view.findViewById(R.id.btCartaHome);
        getProductos();
        getTipos();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager();
                manager.beginTransaction()
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .replace(R.id.content_frame, Fragment_Carta.class, null)
                        .commit();
            }
        });
    }

    private void getProductos(){
        iapiService.getProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(@NonNull Call<List<Producto>> call, @NonNull Response<List<Producto>> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    for(Producto producto: response.body()) {
                        Log.i("Productos", producto.toString());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Producto>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getTipos(){
        iapiService.getTipo().enqueue(new Callback<List<Tipo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tipo>> call, @NonNull Response<List<Tipo>> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    for(Tipo Tipo: response.body()) {
                        Log.i("Productos", Tipo.toString());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tipo>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



}
