package com.biz4solutions.provider.subscription.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;

public class RegistrationViewModel extends ViewModel {
    public final MutableLiveData<ArrayList<String>> occupationLiveList;

    private RegistrationViewModel() {
        super();
        occupationLiveList = new MutableLiveData<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("doctor");
        arrayList.add("raju");
        occupationLiveList.setValue(arrayList);
    }

    //spinner item select listener
    public void onOccupationSelectItem(@SuppressWarnings("unused") AdapterView<?> parent, @SuppressWarnings("unused") View view, int pos, @SuppressWarnings("unused") long id) {
        Toast.makeText(view.getContext(), "" + occupationLiveList.getValue().get(pos), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }


    @SuppressWarnings("unchecked")
    public static class RegistrationFactory extends ViewModelProvider.NewInstanceFactory {

        public RegistrationFactory() {
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new RegistrationViewModel();
        }
    }
}
