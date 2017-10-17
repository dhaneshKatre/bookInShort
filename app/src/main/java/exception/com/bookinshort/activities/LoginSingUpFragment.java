package exception.com.bookinshort.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import exception.com.bookinshort.R;

/**
 * Created by MihirPC on 14-10-2017.
 */

public class LoginSingUpFragment extends Fragment {
    public LoginSingUpFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_login_sing_up_fragment, container, false);
    }

}
