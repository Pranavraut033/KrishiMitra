package model

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import pranav.utils.OnResultListener
import pranav.utils.StoreUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@Suppress("unused")
data class PostSchema internal constructor(
		override val map: MutableMap<String, Any?>,
		override var documentName: String? = null
) : GenericSchema() {

	override val collectionName: String get() = COLLECTION_NAME

	fun getId(): String? = documentName
	var title: String by map
	var description: String by map
	var price: Int by map
	var category: String by map
	var photo: String by map
	var timestamp: String by map

	var postedBy: String? by map
	var boughtBy: String? by map
	var isBought: Boolean by map

	constructor(
			title: String,
			description: String?,
			price: Int,
			category: String,
			photo: String?,
			postedBy: String
	) : this(mutableMapOf(
			"title" to title,
			"description" to description,
			"price" to price,
			"category" to category,
			"photo" to photo,
			"timestamp" to System.currentTimeMillis().toString(),
			"postedBy" to postedBy,
			"boughtBy" to null,
			"isBought" to false
	))

	fun buy(user: UserSchema) {
		boughtBy = user.uid
		isBought = true
	}

	fun getDate(): String {
		return dateFormat.format(Date(timestamp.toLong()))
	}

	fun getTime(): String {
		return timeFormat.format(Date(timestamp.toLong()))
	}

	companion object {
		const val COLLECTION_NAME = "/posts"
		var dateFormat: DateFormat = SimpleDateFormat("dd MMMM yy", Locale.ENGLISH)
		var timeFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

		fun get(storeUtils: StoreUtils, uid: String,
				onResultListener: OnResultListener<PostSchema>):
				DocumentReference =
				storeUtils.document(
						uid, COLLECTION_NAME, OnSuccessListener {
					val post = if (it.data != null) PostSchema(it.data!!, it.id) else null
					onResultListener.onResult(post)
				}, OnFailureListener {
					onResultListener.onResult(null)
				})
	}
}


