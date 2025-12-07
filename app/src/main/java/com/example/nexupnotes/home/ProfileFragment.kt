package com.example.nexupnotes.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nexupnotes.R
import com.example.nexupnotes.auth.LoginActivity
import com.example.nexupnotes.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.annotations.ApiStatus
import androidx.core.net.toUri
import com.google.firebase.BuildConfig
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.serialization.builtins.IntArraySerializer


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()

    private fun load(){
        val user = auth.currentUser
        if(user != null){
            binding.userName.text = user.displayName ?: "User"
            binding.userEmail.text = user.email ?: "unknown"
        }
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.changePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.logout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout!")
                .setMessage("Are you sure to logout?")
                .setPositiveButton("logout"){dialog, which ->
                    auth.signOut()
                    Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show()
                    requireActivity().apply {
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        finish()
                    }
                }
                .setNegativeButton("Cancel"){dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.deleteAccount.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Account!")
                .setMessage("Are you sure to delete account?")
                .setPositiveButton("Delete"){dialog, _ ->
                    auth.currentUser?.delete()?.addOnSuccessListener {
                        Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                        requireActivity().apply {
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            finish()
                        }
                    }
                }
                .setNegativeButton("Cancel"){dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.appVersion.setOnClickListener { showAppVersion() }

        binding.privacyPolicy.setOnClickListener { showPrivacyPolicy() }

        binding.feedback.setOnClickListener { sendFeedback() }

        return binding.root

    }

    @SuppressLint("InflateParams")
    private fun showChangePasswordDialog(){
        val dialog = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val oldPass = dialog.findViewById<EditText>(R.id.oldPassword)
        val newPass = dialog.findViewById<EditText>(R.id.newPassword)

        AlertDialog.Builder(requireContext())
            .setTitle("Change password")
            .setView(dialog)
            .setPositiveButton("Change"){_,_ ->
                val old = oldPass.text.toString().trim()
                val new = newPass.text.toString().trim()

                if (old.isEmpty() || new.isEmpty() || old.length <6 || new.length < 6){
                    Toast.makeText(requireContext(), "Enter the valid password", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                changePassword(old,new)
            }.show()
    }

    private fun changePassword(oldPassword: String, newPassword: String){
        val user = auth.currentUser
        if (user == null){
            Toast.makeText(requireContext(), "user not logged in ", Toast.LENGTH_SHORT).show()
            return
        }

        val credentials = EmailAuthProvider.getCredential(user.email!!,oldPassword)
        user.reauthenticate(credentials)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Password reset successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Wrong current password", Toast.LENGTH_SHORT)
                    .show()
            }



    }


    private fun showPrivacyPolicy(){
        val message = "• We do not collect personal data.\n• All notes & tasks stay only on your device.\n• No data is shared with any third party.\n• You can delete your account anytime."

        AlertDialog.Builder(requireContext())
            .setTitle("Privacy Policy")
            .setMessage(message)
            .setPositiveButton("Close"){dialog, which ->
                dialog.dismiss()
            }.show()
    }


    private fun showAppVersion(){
        val versionName = BuildConfig.VERSION_NAME

        AlertDialog.Builder(requireContext())
            .setTitle("App Version")
            .setMessage(versionName)
            .setPositiveButton("Close"){dialog, which ->
                dialog.dismiss()
            }.show()
    }

    private fun sendFeedback(){
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:".toUri()
        
        intent.putExtra(Intent.EXTRA_EMAIL,arrayOf("sukhveer0000singh@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT,"App Feedback")
        intent.putExtra(Intent.EXTRA_TEXT,"Hi,\n\nLeave your feedback here...")
        
        try{
            requireContext().startActivity(intent)
        }catch (e: Exception){
            Toast.makeText(requireContext(), "mail app not found", Toast.LENGTH_SHORT).show()
        }
    }


}