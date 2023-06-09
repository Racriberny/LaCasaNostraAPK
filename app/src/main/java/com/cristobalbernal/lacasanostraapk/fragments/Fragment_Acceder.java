package com.cristobalbernal.lacasanostraapk.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.cristobalbernal.lacasanostraapk.MainActivity;
import com.cristobalbernal.lacasanostraapk.R;
import com.cristobalbernal.lacasanostraapk.Utils.HashGenerator;
import com.cristobalbernal.lacasanostraapk.interfaces.IAPIService;
import com.cristobalbernal.lacasanostraapk.modelos.Usuario;
import com.cristobalbernal.lacasanostraapk.rest.RestClient;
import com.google.android.material.textfield.TextInputEditText;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Acceder extends Fragment {
    private TextInputEditText correo;
    private TextInputEditText contrasena;
    private IAPIService iapiService;

    private SharedPreferences sharedPref;

    public Fragment_Acceder(){
        super(R.layout.fragment_acceder);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iapiService = RestClient.getInstance();
        correo = view.findViewById(R.id.etEmailLogin);
        contrasena = view.findViewById(R.id.etContrasenyaLogin);
        Button login = view.findViewById(R.id.btnLogin);
        sharedPref = getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correoElectronico = Objects.requireNonNull(correo.getText()).toString();
                String password = null;
                try {
                    password = Objects.requireNonNull(HashGenerator.getSHAString(String.valueOf(contrasena.getText())));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }

                if (correoElectronico.isEmpty()){
                    correo.setError("Se requiere un correo electronico");
                    correo.requestFocus();
                    return;
                }else if (password.isEmpty()){
                    contrasena.setError("Se requiere una contraseña");
                    contrasena.requestFocus();
                    return;
                }

                Usuario usuarioIniciado = new Usuario(correoElectronico,password);
                Call<Usuario> booleanCall = iapiService.logUsuario(usuarioIniciado);

                String finalPassword = password;
                booleanCall.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(@NonNull Call<Usuario> call, @NonNull Response<Usuario> response) {
                        if (response.body() !=null) {
                            Toast.makeText(getContext(), R.string.login, Toast.LENGTH_SHORT).show();
                            saveCredentials(correoElectronico, finalPassword);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("activo",response.body());
                            requireActivity().startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), "No has inicion sesion", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Usuario> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        String savedUsername = sharedPref.getString("username", "");
        String savedPassword = sharedPref.getString("password", "");

        if (!savedUsername.equals("") && !savedPassword.equals("")) {
            correo.setText(savedUsername);
            Toast.makeText(getContext(),"Introduce la contraeña!!!",Toast.LENGTH_SHORT).show();
        }



        view.findViewById(R.id.tvRegistrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager();
                manager.beginTransaction()
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .replace(R.id.content_frame, Fragment_Registrar.class, null)
                        .commit();
            }
        });
    }
    private void saveCredentials(String username, String password) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

}
