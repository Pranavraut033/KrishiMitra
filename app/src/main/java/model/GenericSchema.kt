package model

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import pranav.utils.OnResultListener
import pranav.utils.StoreUtils

abstract class GenericSchema {
	/**
	 * If _**documentName**_ is __empty/null__ auto id will be generated
	 */
	abstract var documentName: String?
	abstract val collectionName: String
	abstract val map: MutableMap<String, Any?>

	fun <T : GenericSchema> insert(
			storeUtils: StoreUtils,
			successListener: OnSuccessListener<Void>? = null,
			failureListener: OnFailureListener? = null)
			: T = storeUtils.insertData(
				this,
				successListener,
				failureListener
		)

	fun <T : GenericSchema> update(
			storeUtils: StoreUtils,
			successListener: OnSuccessListener<Void>? = null,
			failureListener: OnFailureListener? = null
	): T = storeUtils.update(
			this,
			successListener,
			failureListener
	)
}
