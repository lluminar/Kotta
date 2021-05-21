package net.lidia.iessochoa.kotta.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.widget.ImageView;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.ui.home.Filters;
import net.lidia.iessochoa.kotta.ui.home.HomeFragment;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     BottomSheetNavigationFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class BottomSheetNavigationFragment extends BottomSheetDialogFragment {
    public final static String EXTRA_DATOS_RESULTADO = "datos";

    private static final String ARG_ITEM_COUNT = "item_count";

    public static BottomSheetNavigationFragment newInstance() {
        final BottomSheetNavigationFragment fragment = new BottomSheetNavigationFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //Bottom Sheet Callback
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN)
                dismiss();
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            //check the slide offset and change the visibility of close button
            if (slideOffset > 0.5) closeButton.setVisibility(View.VISIBLE);
            else closeButton.setVisibility(View.GONE);
        }
    };

    private ImageView closeButton;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Get the content View
        View contentView = View.inflate(getContext(), R.layout.bottom_navigation_drawer, null);
        dialog.setContentView(contentView);

        NavigationView navigationView = contentView.findViewById(R.id.navigation_view);

        //implement navigation menu item click event
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navRock:
                    String datos= getString(R.string.Rock);
                    Intent intent = new Intent(getActivity(), Filters.class);
                    intent.putExtra(EXTRA_DATOS_RESULTADO, datos);
                    startActivity(intent);
                    dismiss();
                    break;
                case R.id.navPop:
                    datos= getString(R.string.Pop);
                    intent = new Intent(getActivity(), Filters.class);
                    intent.putExtra(EXTRA_DATOS_RESULTADO, datos);
                    startActivity(intent);
                    dismiss();
                    break;
                case R.id.navClassic:
                    datos= getString(R.string.Clasica);
                    intent = new Intent(getActivity(), Filters.class);
                    intent.putExtra(EXTRA_DATOS_RESULTADO, datos);
                    startActivity(intent);
                    dismiss();

                    break;
                case R.id.navVideoGames:
                    datos= getString(R.string.Videojuegos);
                    intent = new Intent(getActivity(), Filters.class);
                    intent.putExtra(EXTRA_DATOS_RESULTADO, datos);
                    startActivity(intent);
                    dismiss();
                    break;
                case R.id.navFilm:
                    datos= getString(R.string.Peliculas);
                    intent = new Intent(getActivity(), Filters.class);
                    intent.putExtra(EXTRA_DATOS_RESULTADO, datos);
                    startActivity(intent);
                    dismiss();
                    break;
                case R.id.navBalads:
                    datos= getString(R.string.Baladas);
                    intent = new Intent(getActivity(), Filters.class);
                    intent.putExtra(EXTRA_DATOS_RESULTADO, datos);
                    startActivity(intent);
                    dismiss();
                    break;
            }
            return false;
        });

        closeButton = contentView.findViewById(R.id.close_image_view);
        closeButton.setOnClickListener(view -> {
            //dismiss bottom sheet
            dismiss();
        });

        //Set the coordinator layout behavior
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        //Set callback
        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }
}