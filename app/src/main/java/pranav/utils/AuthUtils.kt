package pranav.utils

import android.app.Activity
import android.net.Uri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import pranav.utilities.Logger
import java.util.concurrent.TimeUnit

/**
 * Created on 25-01-19 at 11:20 by Pranav Raut.
 * For ProVeteran
 */

class AuthUtils constructor(private val a: Activity,
							val auth: FirebaseAuth = FirebaseAuth.getInstance(),
							verbose: Boolean = false, debug: Boolean = false) {
	private val logger: Logger = Logger(verbose, debug, TAG)

	constructor(a: Activity,
				auth: FirebaseAuth = FirebaseAuth.getInstance(), verbose: Boolean) :
			this(a, auth, verbose, verbose)

	fun signInUsingEmail(email: String,
						 password: String,
						 successListener: OnSuccessListener<AuthResult>? = null,
						 failureListener: OnFailureListener? = null) {
		val task = auth.signInWithEmailAndPassword(email, password)
		val l = OnCompleteListener<AuthResult> {
			if (it.isSuccessful) {
				logger.d("Sign in success!")
				logger.v("Result ${it.result}")
				successListener?.onSuccess(it.result)
			} else {
				logger.w("Error in signing in", e = it.exception)
				failureListener?.onFailure(it.exception!!)
			}
		}
		task.addOnCompleteListener(a, l)
	}

	fun signInUsingNumber(phoneNumber: String,
						  callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null) {
		val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

			override fun onVerificationCompleted(credential: PhoneAuthCredential) {
				logger.d(credential)

				auth.signInWithCredential(credential)
				callback?.onVerificationCompleted(credential)
			}

			override fun onVerificationFailed(e: FirebaseException) {
				logger.w(e = e)
				callback?.onVerificationFailed(e)
			}

			override fun onCodeSent(verificationId: String?,
									token: PhoneAuthProvider.ForceResendingToken) {
				logger.d(verificationId)
				callback?.onCodeSent(verificationId, token)
			}
		}

		PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, timeoutTime,
				TimeUnit.MINUTES, a, callbacks)
	}

	fun createUserUsingEmail(email: String, password: String,
							 successListener: OnSuccessListener<AuthResult>? = null,
							 failureListener: OnFailureListener? = null) {
		auth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(a) { task ->
					if (task.isSuccessful) {
						logger.d("User successfully Created!")
						logger.v("Result: ${task.result}")
						successListener?.onSuccess(task.result)
					} else {
						logger.w("Error creating user", e = task.exception)
						failureListener?.onFailure(task.exception!!)
					}
				}

	}

	fun getLoginMethodsFor(email: String,
						   onResultListener: OnResultListener<List<String>>? = null) {
		auth.fetchSignInMethodsForEmail(email)
				.addOnCompleteListener(a) { task ->
					val methods = task.result?.signInMethods

					if (task.isSuccessful && task.result != null)
						logger.d("Available Sign in Methods: $methods")
					else logger.w("Query Failed", e = task.exception)

					onResultListener?.onResult(methods)
				}
	}

	fun updateProfile(name: String, uri: Uri? = null,
					  successListener: OnSuccessListener<Void?>? = null,
					  failureListener: OnFailureListener? = null) {
		currentUser!!
				.updateProfile(UserProfileChangeRequest.Builder()
						.setDisplayName(name)
						.setPhotoUri(uri)
						.build())
				.addOnCompleteListener {
					if (it.isSuccessful) {
						logger.d("Profile updated successfully!")
						successListener?.onSuccess(it.result)
					} else {
						logger.w("Failed To update profile", e = it.exception)
						failureListener?.onFailure(it.exception!!)
					}
				}
	}

	val currentUser: FirebaseUser?
		get() = auth.currentUser


	companion object {
		const val TAG = "AuthUtils"
		private const val timeoutTime = 3L
	}
}
