package pranav.utils

import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import model.GenericSchema
import pranav.utilities.Logger

class StoreUtils constructor(
		private val a: AppCompatActivity? = null,
		val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

	data class Condition(var field: String, var symbol: String, var value: Any) {

		internal class SymbolNotFoundException(override val message: String? = "") :
				Exception("Symbol not found: $message")
	}

	fun <T : GenericSchema> insertData(
			schema: GenericSchema,
			successListener: OnSuccessListener<Void>? = null,
			failureListener: OnFailureListener? = null): T =
			insertData(db, schema, successListener, failureListener, a)

	fun <T : GenericSchema> update(
			schema: GenericSchema,
			successListener: OnSuccessListener<Void>? = null,
			failureListener: OnFailureListener? = null): T =
			update(db, schema, successListener, failureListener, a)

	fun collection(collectionPath: String,
				   vararg conditions: Condition,
				   successListener: OnSuccessListener<List<DocumentSnapshot>>? = null,
				   failureListener: OnFailureListener? = null,
				   source: Source = Source.DEFAULT): CollectionReference =
			collection(db, collectionPath, *conditions,
					successListener = successListener, failureListener = failureListener,
					a = a, source = source)

	fun document(documentPath: String,
				 collectionPath: String,
				 successListener: OnSuccessListener<DocumentSnapshot>? = null,
				 failureListener: OnFailureListener? = null,
				 source: Source = Source.DEFAULT): DocumentReference =
			document(db, documentPath, collectionPath, successListener,
					failureListener, a, source)


	companion object {
		private const val TAG = "StoreUtils"
		var logger = Logger(false, tag = TAG)

		fun <T : GenericSchema> insertData(
				db: FirebaseFirestore = FirebaseFirestore.getInstance(),
				schema: GenericSchema,
				successListener: OnSuccessListener<Void>? = null,
				failureListener: OnFailureListener? = null,
				a: AppCompatActivity? = null
		): T {
			val task = db.collection(schema.collectionName).let {
				if (TextUtils.isEmpty(schema.documentName)) {
					it.document().let { ref ->
						schema.documentName = ref.id
						ref.set(schema.map)
					}
				} else it.document(schema.documentName!!).set(schema.map)
			}

			val l = OnCompleteListener<Void> { tk ->
				if (tk.isSuccessful) {
					tk.result
					logger.d("Successfully inserted, $schema!")
					logger.v("Result: ${tk.result}")
					successListener?.onSuccess(tk.result)
				} else {
					logger.w("Failed", e = tk.exception)
					failureListener?.onFailure(tk.exception!!)
				}
			}
			if (a == null) task.addOnCompleteListener(l)
			else task.addOnCompleteListener(a, l)

			@Suppress("UNCHECKED_CAST")
			return schema as T
		}

		fun <T : GenericSchema> update(
				db: FirebaseFirestore = FirebaseFirestore.getInstance(),
				schema: GenericSchema,
				successListener: OnSuccessListener<Void>? = null,
				failureListener: OnFailureListener? = null,
				a: AppCompatActivity? = null
		): T {
			val task = db.collection(schema.collectionName).let {
				if (TextUtils.isEmpty(schema.documentName)) {
					it.document().let { ref ->
						schema.documentName = ref.id
						ref.set(schema.map)
					}
				} else it.document(schema.documentName!!).update(schema.map)
			}

			val l = OnCompleteListener<Void> { tk ->
				if (tk.isSuccessful) {
					tk.result
					logger.d("Successfully updated, $schema!")
					logger.v("Result: ${tk.result}")
					successListener?.onSuccess(tk.result)
				} else {
					logger.w("Failed", e = tk.exception)
					failureListener?.onFailure(tk.exception!!)
				}
			}
			if (a == null) task.addOnCompleteListener(l)
			else task.addOnCompleteListener(a, l)

			@Suppress("UNCHECKED_CAST")
			return schema as T
		}

		fun document(db: FirebaseFirestore = FirebaseFirestore.getInstance(),
					 collectionPath: String,
					 documentPath: String,
					 successListener: OnSuccessListener<DocumentSnapshot>? = null,
					 failureListener: OnFailureListener? = null,
					 a: AppCompatActivity?,
					 source: Source = Source.DEFAULT): DocumentReference =
				db.collection(documentPath).document(collectionPath).also { documentRef ->
					val task = documentRef.get(source)
					val l = OnCompleteListener<DocumentSnapshot> { tk ->
						if (tk.isSuccessful) {
							successListener?.onSuccess(tk.result)
							logger.d("Success in retrieving data")
							logger.v("Result", tk.result?.data)
						} else {
							logger.w(e = tk.exception)
							failureListener?.onFailure(tk.exception!!)
						}
					}
					if (a == null) task.addOnCompleteListener(l)
					else task.addOnCompleteListener(a, l)
				}


		fun collection(db: FirebaseFirestore = FirebaseFirestore.getInstance(),
					   collectionPath: String,
					   vararg conditions: Condition,
					   successListener: OnSuccessListener<List<DocumentSnapshot>>? = null,
					   failureListener: OnFailureListener? = null,
					   a: AppCompatActivity?,
					   source: Source = Source.DEFAULT): CollectionReference =
				db.collection(collectionPath).also { collectionRef ->
					conditions.forEach {
						val field = it.field
						val value = it.value
						when (it.symbol) {
							"==" -> collectionRef.whereEqualTo(field, value)
							">=" -> collectionRef.whereGreaterThanOrEqualTo(field, value)
							">" -> collectionRef.whereGreaterThan(field, value)
							"<=" -> collectionRef.whereLessThanOrEqualTo(field, value)
							"<" -> collectionRef.whereLessThan(field, value)
							"in" -> collectionRef.whereArrayContains(field, value)
							else -> throw Condition.SymbolNotFoundException("'${it.symbol}'")
						}
					}
					val task = collectionRef.get(source)
					val l = OnCompleteListener<QuerySnapshot> { tk ->
						if (tk.isSuccessful) {
							successListener?.onSuccess(tk.result?.documents)
							logger.d("Success in retrieving data")
							logger.v("Result", tk.result)
						} else {
							logger.w(e = tk.exception)
							failureListener?.onFailure(tk.exception!!)
						}
					}
					if (a == null) task.addOnCompleteListener(l)
					else task.addOnCompleteListener(a, l)
				}

	}
}
