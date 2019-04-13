package model

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import pranav.utils.OnResultListener
import pranav.utils.StoreUtils


@Suppress("unused")
data class PostSchema internal constructor(
		override val map: MutableMap<String, Any?>,
		override var documentName: String? = null
) : GenericSchema() {

	override val collectionName: String get() = COLLECTION_NAME

	var title: String by map
	var description: String by map
	var price: Int by map
	var category: String by map

	var postedBy: String? by map
	var boughtBy: String? by map
	var isBought: Boolean by map

	constructor(
			title: String,
			description: String,
			price: Int,
			category: String,
			postedBy: String
	) : this(mutableMapOf(
			"title" to title,
			"description" to description,
			"price" to price,
			"category" to category,
			"postedBy" to postedBy,
			"boughtBy" to null,
			"isBought" to false
	))

	fun getId(): String? = documentName

	fun setPurchased() {
		TODO("Set isBought to true and boughtBy to user_id")
	}

	companion object {
		const val COLLECTION_NAME = "/posts"

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


