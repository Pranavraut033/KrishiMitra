package model

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import pranav.utils.OnResultListener
import pranav.utils.StoreUtils
import com.google.firebase.auth.FirebaseAuth;
import pranav.utilities.Logger


@Suppress("unused")
data class UserSchema internal constructor(
		val uid: String,
		override val map: MutableMap<String, Any?>
) : GenericSchema() {

	override var documentName: String? = uid
	override val collectionName: String get() = COLLECTION_NAME

	var name: String by map
	var phoneNumber: String? by map
	var email: String by map
	var bookmarks: List<String>? by map

	constructor(uid: String,
				name: String,
				phoneNumber: String? = null,
				email: String? = null)
			: this(uid, mutableMapOf(
			"name" to name,
			"phoneNumber" to phoneNumber,
			"email" to email,
			"bookmarks" to listOf<String>()))


	fun addBookmark(postUid: String): Nothing {
		TODO("NOT YET IMPLEMENTED")
	}

	override fun toString(): String = "$name ($email)"

	companion object {
		var CURRENT_USER: UserSchema? = null

		init {
			val t = FirebaseAuth.getInstance()
			t.uid?.let {
				get(StoreUtils(), it)
			}
			Logger.d("Set User")
		}

		const val COLLECTION_NAME = "/users"
		val DOCUMENT_NAME: String get() = CURRENT_USER?.uid ?: ""

		fun get(storeUtils: StoreUtils, uid: String,
				onResultListener: OnResultListener<UserSchema>? = null): DocumentReference =
				storeUtils.document(
						uid, COLLECTION_NAME, OnSuccessListener {
					CURRENT_USER = if (it.data != null) UserSchema(it.id, it.data!!) else null
					onResultListener?.onResult(CURRENT_USER)
				}, OnFailureListener {
					onResultListener?.onResult(null)
				})
	}
}
