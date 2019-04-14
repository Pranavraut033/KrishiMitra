package pranav.lib

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.paytm.pgsdk.PaytmClientCertificate
import com.paytm.pgsdk.PaytmPGService
import model.PostSchema
import model.UserSchema
import java.util.*
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import pranav.utilities.Logger
import org.json.JSONObject
import pranav.utils.OnResultListener


/**
 * Created on 13-04-19 at 20:05 by Pranav Raut.
 * For KrishiMitra
 */
class PaytmHelper(private val activity: Activity) {

	fun initPayment(
			post: PostSchema,
			user: UserSchema,
			onResultListener:
			OnResultListener<Bundle?>
	) {
		val service: PaytmPGService = PaytmPGService.getStagingService()
		val paramsMap: MutableMap<String, String> = mutableMapOf(
				"MID" to "usbQfk25225733568373",
				"CHANNEL_ID" to "WAP",
				"INDUSTRY_TYPE_ID" to "Retail",
				"WEBSITE" to "WEBSTAGING"
		)
		paramsMap["ORDER_ID"] = "${post.getId()!!}_${System.currentTimeMillis()}"
		paramsMap["CALLBACK_URL"] = "https://securegw-stage.paytm" +
				".in/theia/paytmCallback?ORDER_ID=" + post.getId()!!
		paramsMap["CUST_ID"] = user.uid
		paramsMap["MOBILE_NO"] = user.phoneNumber ?: "7777777777"
		paramsMap["EMAIL"] = user.email
		paramsMap["TXN_AMOUNT"] = post.price.toString()

		generateHash(paramsMap, Response.Listener {
			val jsonObject = JSONObject(it)
			paramsMap["CHECKSUMHASH"] = jsonObject.getString("CHECKSUMHASH")

			val order = PaytmOrder(paramsMap as HashMap<String, String>?)
			val certificate = PaytmClientCertificate(user.uid, post.getId()!!)

			service.initialize(order, certificate)

			service.startPaymentTransaction(
					activity,
					true,
					true,
					object : PaytmPaymentTransactionCallback {
						override fun onTransactionResponse(inResponse: Bundle?) {
							Logger.d(inResponse)
							onResultListener.onResult(inResponse)
						}

						override fun clientAuthenticationFailed(inErrorMessage: String?) {
							Toast.makeText(
									activity,
									"Authentication failed: Server error $inErrorMessage",
									Toast.LENGTH_LONG
							).show()
							onResultListener.onResult(null);
						}

						override fun someUIErrorOccurred(inErrorMessage: String?) {
							Toast.makeText(
									activity,
									"UI Error $inErrorMessage",
									Toast.LENGTH_LONG
							).show()
							onResultListener.onResult(null);
						}

						override fun onTransactionCancel(inErrorMessage: String?, inResponse: Bundle?) {
							Toast.makeText(
									activity,
									"Transaction cancelled $inErrorMessage",
									Toast.LENGTH_SHORT
							).show()
							onResultListener.onResult(null);
						}

						override fun networkNotAvailable() {
							Toast.makeText(
									activity,
									"Network connection error: Check your internet connectivity",
									Toast.LENGTH_LONG
							).show()
							onResultListener.onResult(null);
						}

						override fun onErrorLoadingWebPage(iniErrorCode: Int, inErrorMessage: String?,
														   inFailingUrl: String?) {
							Toast.makeText(
									activity,
									"Unable to load webpage $inErrorMessage",
									Toast.LENGTH_LONG
							).show()
							onResultListener.onResult(null);
						}

						override fun onBackPressedCancelTransaction() {
							Toast.makeText(
									activity,
									"User cancelled the transaction",
									Toast.LENGTH_SHORT
							).show()
							onResultListener.onResult(null)
						}
					}
			)
		})
	}

	private fun generateHash(
			paramsMap: MutableMap<String, String>,
			listener: Response.Listener<String>
	) {
		val queue = Volley.newRequestQueue(activity)
		val url = "https://truthful-place.000webhostapp.com/generateChecksum.php"
		val stringRequest = object : StringRequest(
				Method.POST,
				url,
				listener,
				Response.ErrorListener {
					Logger.w("Something Went wrong", e = it)
				}
		) {
			override fun getParams(): MutableMap<String, String> = paramsMap

		}

		queue.add(stringRequest)
	}
}
