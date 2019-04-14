package pranav.utils

import android.app.Activity
import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.*
import pranav.utilities.Logger
import java.io.File

/**
 * Created on 14-04-19 at 0:42 by Pranav Raut.
 * For KrishiMitra
 */
class StorageUtils(private val activity: Activity, storage: FirebaseStorage) {

	private val rootReference = storage.reference.root

	fun uploadFileFromUri(
			file: Uri,
			path: String = "/",
			onSuccessListener: OnSuccessListener<UploadTask.TaskSnapshot>? = null,
			onFailureListener: OnFailureListener? = null,
			onProgressListener: OnResultListener<Int>? = null
	) {
		val fileReference = rootReference.child("$path/${file.lastPathSegment}")
		val uploadTask = fileReference.putFile(file)

		uploadTask.addOnCompleteListener(activity) {
			if (it.isSuccessful) {
				onSuccessListener?.onSuccess(it.result)
				logger.d("Byte Uploaded", it.result?.totalByteCount)
			} else {
				logger.w("Something went wrong", e = it.exception)
				onFailureListener?.onFailure(it.exception!!)
			}
		}.addOnProgressListener(activity) { task ->
			onProgressListener?.onResult(((100.0 * task.bytesTransferred) / task.totalByteCount).toInt())
		}
	}

	fun downloadFile(
			filePath: String,
			file: File,
			onSuccessListener: OnSuccessListener<FileDownloadTask.TaskSnapshot>? = null,
			onFailureListener: OnFailureListener? = null,
			onProgressListener: OnResultListener<Int>? = null
	) {
		val fileRef = rootReference.child(filePath)

		fileRef.getFile(file).addOnCompleteListener(activity) {
			if (it.isSuccessful) {
				onSuccessListener?.onSuccess(it.result)
				logger.d("Byte Uploaded", it.result?.totalByteCount)
			} else {
				logger.w("Something went wrong", e = it.exception)
				onFailureListener?.onFailure(it.exception!!)
			}
		}.addOnProgressListener(activity) { task ->
			onProgressListener
					?.onResult(((100 * task.bytesTransferred) / task.totalByteCount).toInt())
		}
	}

	companion object {
		val logging = true
		private val logger: Logger = Logger(logging, logging, AuthUtils.TAG)
		const val TAG = "StorageUtils"
	}
}
