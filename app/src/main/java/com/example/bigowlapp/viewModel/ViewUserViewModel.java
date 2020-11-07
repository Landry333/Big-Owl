package com.example.bigowlapp.viewModel;

import com.example.bigowlapp.repository.ViewUserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ViewUserViewModel {

   private ViewUserRepository viewUserRepository;

   public ViewUserViewModel(){
       viewUserRepository = new ViewUserRepository();
   }


}
