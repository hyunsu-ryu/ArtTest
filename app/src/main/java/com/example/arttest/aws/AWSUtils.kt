package com.example.arttest.aws

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.amazonaws.HttpMethod
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ResponseHeaderOverrides
import java.io.File
import java.lang.Exception
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AWSUtils(private val context: Context, private val filepath:String, val onAwsImageupLoadListener: OnAwsImageUploadListener, val filepathKey:String) {

    private var image: File? = null
    private var mTransferUtility: TransferUtility? =null

    private var sS3Client: AmazonS3Client? =null
    private var sCredProvider: CognitoCachingCredentialsProvider? = null


    // 권한부여 (아이디 체크하는 공간)
    private fun getCredProvider(context: Context):CognitoCachingCredentialsProvider?{
        if(sCredProvider ==null){
            sCredProvider = CognitoCachingCredentialsProvider(context.applicationContext,AwsConstants.COGNITO_IDENTITY_ID, AwsConstants.REGION)
        }
        return sCredProvider
    }

     // client 확인과정임
    private fun getS3Client(context: Context?) : AmazonS3Client? {
        if (sS3Client == null) {
            sS3Client = AmazonS3Client(getCredProvider(context!!))
            sS3Client!!.setRegion(Region.getRegion(Regions.US_EAST_2))
        }
         return sS3Client
    }


    // 전달하는 녀석인데 client 와   context라는 전역변수를 취해준다.
    private fun getTransferUtility(context: Context): TransferUtility?{
        if(mTransferUtility ==null)
            mTransferUtility = TransferUtility(
                getS3Client(context.applicationContext),
                context.applicationContext
            )
        return mTransferUtility
    }

    fun beginUpload(){

        if(TextUtils.isEmpty(filepath)){
            onAwsImageupLoadListener.onError("Could not find the filepath of the selected file")
            return
        }

        onAwsImageupLoadListener.showProgressDialog()
        val file = File(filepath)
        image = file

        try{
            val observer = getTransferUtility(context)?.upload(
                AwsConstants.BUCKET_NAME, // 버킷의 이름
                filepathKey +file.name, image  // file name with folder path
            )
            observer?.setTransferListener(UploadListener())
        }catch (e: Exception){
            e.printStackTrace()
            onAwsImageupLoadListener.hideProgressDialog()
        }
    }

    private  fun generateS3SignedUrl(path: String?): String?{

        val calender = Calendar.getInstance()
        calender.add(Calendar.DAY_OF_YEAR,1)
        val tomorrow = calender.time

        val dateFormat: DateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val tomorrowAsString = dateFormat.format(tomorrow)

        val EXPIRY_DATE = tomorrowAsString // 최대 7일 허용
        val mFile = File(path)

        val s3client: AmazonS3Client? = getS3Client(context)

        val expiration = Date()
        var msec: Long = expiration.time

        msec += 1000 * 6000 * 6000.toLong() // 1시간임

        val format: DateFormat =SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val date: Date?

        try {
            date = format.parse(EXPIRY_DATE)
            expiration.time = date.time
        }catch (e: ParseException){
            e.printStackTrace()
            expiration.time = msec
        }


        val overrideHeader = ResponseHeaderOverrides()
        overrideHeader.contentType = "image/jpeg"

        val mediaUrl: String = mFile.name

        val generatePresignedUrlRequest = GeneratePresignedUrlRequest(AwsConstants.BUCKET_NAME, filepathKey+mediaUrl)
        generatePresignedUrlRequest.method = HttpMethod.GET
        generatePresignedUrlRequest.expiration = expiration
        generatePresignedUrlRequest.responseHeaders = overrideHeader

        return sS3Client!!.generatePresignedUrl(generatePresignedUrlRequest).toString()

    }

    private inner class UploadListener : TransferListener{

        override fun onError(id: Int, ex: Exception?) {
            onAwsImageupLoadListener.hideProgressDialog()
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

        }

        override fun onStateChanged(id: Int, newState: TransferState) {

            if(newState == TransferState.COMPLETED){
                onAwsImageupLoadListener.hideProgressDialog()

                val finalImageUrl = AwsConstants.URL + filepathKey + image!!.name
                onAwsImageupLoadListener.onSuccess(generateS3SignedUrl(finalImageUrl)!!)

//                image!!.delete() // 나중에 업로드하고 지우고싶을때씀
            }else if(newState == TransferState.CANCELED || newState == TransferState.FAILED){
                onAwsImageupLoadListener.hideProgressDialog()
                onAwsImageupLoadListener.onError("Error in uploading file.")
            }

        }

    }

    interface OnAwsImageUploadListener{
        fun showProgressDialog()
        fun hideProgressDialog()
        fun onSuccess(imgUrl:String)
        fun onError(errorMsg: String)
    }


}