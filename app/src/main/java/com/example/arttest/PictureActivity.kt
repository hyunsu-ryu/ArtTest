package com.example.arttest

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.icu.util.TimeZone.getRegion
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Gallery
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.metrics.AwsSdkMetrics.getRegion
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region.getRegion
import com.amazonaws.regions.RegionUtils.getRegion
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.Region
import com.amazonaws.util.IOUtils

import com.example.arttest.aws.AwsConstants
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_picture.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.NumberFormatException
import java.nio.file.Files.createFile
import java.text.SimpleDateFormat
import java.util.*

class PictureActivity : AppCompatActivity() {
        val Gallery = 0  // 갤러리 접근 request 코드 생성해주기
        val REQUEST_IMAGE_CAPTURE = 1 //  카메라 사진 촬영 요청코드
        lateinit var curPhotoPath: String // 문자열 형태의 사진 경로 값 ( 초기값을 null로 시작하고 싶을떄)
        var path:String? = null

        private val ACCESS_KEY:String ="AKIA5VZTIAOJ37JVHU5F"       // cloud user key IAM   S3 이미지 넣는 권한.
        private val SECRET_KEY:String = "DqlqPP/1KyYlzouFEF5VTAnPnc1VjAa/lIyDUqje"

        private var s3Client: AmazonS3Client? = null
        private var credentials : BasicAWSCredentials? = null

        // track shoosing image Intent   이미지 선정
        private val CHOOSING_IMAGE_REQUEST = 1234
        private var fileUri: Uri?=null
        private var bitmap: Bitmap? = null



        val REGION: Regions = Regions.US_EAST_2
        val URL:String = "https://yangjae-team02-bucket.s3.us-east-2.amazonawss.com/"  // 이미지 전용 URL
        val URL2:String = "https://xai4s5kf06.execute-api.us-east-2.amazonaws.com/"     // survey전용 URL
        val BUCKET_NAME:String ="yangjae-team02-bucket"     // 권한 체크
        val ARN:String ="arn:aws:s3:::yangjae-team02-bucket"
        val folderpath_image ="image_test"  // 이미지
        val folderPath_servey ="info_test/"        // servey 전용   or  image + servey




//        yangjae-team02-bucket  // 버킷이름
//        https://yangjae-team02-bucket.s3.us-east-2.amazonawss.com/ // S3


        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_picture)

                var logo = supportActionBar             // 해보고 적용되면 다른데 적용하기
                logo?.setIcon(R.drawable.arttest_sub_logo)      // null 일시 표기 안해주기 가능
                logo?.setDisplayUseLogoEnabled(true)
                logo?.setDisplayShowHomeEnabled(true)

                setPermission()  // 최초의 권한을 체크하는 텍스트 수행

                btn_camera.setOnClickListener {
                        takeCapture()  // 기본 카메라 앱을 실행하여 사진 촬영영
               }
                btn_canvas.setOnClickListener {


//                        var user =intent.getStringExtra("user_id").toString()
//
//                        val canvasIntent = Intent(this@PictureActivity,CanvasActivity::class.java)
//                        canvasIntent.putExtra("user",user)
//                        startActivity(canvasIntent)

                        startActivity<CanvasActivity>(

                        )

                }

                btn_gallery.setOnClickListener {
                        loadImage()                     // LoadImage가 먼저 나와야함
                        showChoosingFile()

                }

                btn_next.setOnClickListener {
                        var user =intent.getStringExtra("user_id").toString()   //canvas 들리면 user_name으로 바뀌어야함

                        val surveyIntent = Intent(this@PictureActivity,SurveyActivity::class.java)
                        surveyIntent.putExtra("user",user)
                        startActivity(surveyIntent)
//                        startActivity<SurveyActivity>(
//                        )
                }



                tv_file_name.text = ""    // 다음주에 이거 한번 지워보자!


                btn_upload.setOnClickListener{
                        uploadFile()
                }

                AWSMobileClient.getInstance().initialize(this).execute()

                credentials = BasicAWSCredentials(ACCESS_KEY,SECRET_KEY)
                s3Client = AmazonS3Client(credentials)


        }


//                if (intent.hasExtra("id")) {
//
//
//                }

        private fun uploadFile() {

                var user_id =intent.getStringExtra("user_id").toString() //null 이 되버림

                if (fileUri != null) {
//                        val fileName = edt_file_name.text.toString()
                        val fileName = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()).toString()  // 이거 되면 edt  지워버리기

                        if (!validateInputFileName(fileName)) {
                                return
                        }

                        val file = File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                "/" + fileName
                        )

                        createFile(applicationContext, fileUri!!, file)

                        val transferUtility = TransferUtility.builder()
                                .context(applicationContext)
                                .awsConfiguration(AWSMobileClient.getInstance().configuration)
                                .s3Client(s3Client)
//                                .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                                .build()



                        val uploadObserver = transferUtility.upload(
                                "$user_id/" + fileName + "." + getFileExtension(fileUri), file
                        )

                        uploadObserver.setTransferListener(object : TransferListener {

                                override fun onStateChanged(id: Int, state: TransferState) {
                                        if (TransferState.COMPLETED == state) {
                                                Toast.makeText(
                                                        applicationContext,
                                                        "업로드 완료!",
                                                        Toast.LENGTH_SHORT
                                                ).show()

                                                file.delete()
                                        } else if (TransferState.FAILED == state) {
                                                file.delete()
                                        }
                                }

                                override fun onProgressChanged(
                                        id: Int,
                                        bytesCurrent: Long,
                                        bytesTotal: Long
                                ) {
                                        val percentDonef =
                                                bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                                        val percentDone = percentDonef.toInt()

                                        tv_file_name.text =                                                                             // 여기 두줄 없애고 위에 정의 없애고, xml 에서 없애기
                                                "ID:$id|bytesCurrent: $bytesCurrent|bytesTotal: $bytesTotal|$percentDone%"

                                }

                                override fun onError(id: Int, ex: Exception) {
                                        ex.printStackTrace()
                                }

                        })
                }
        }

//        override fun onClick(view: View) {
//                        val i = view.id
//
//                        if (i == R.id.btn_gallery) {
//                                showChoosingFile()
//                        } else if (i == R.id.btn_upload) {
//                                uploadFile()
//                        }
//        }

        private fun showChoosingFile() {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSING_IMAGE_REQUEST)
        }



        private fun getFileExtension(uri: Uri?): String? {
                        val contentResolver = contentResolver
                        val mime = MimeTypeMap.getSingleton()

                        return mime.getExtensionFromMimeType(contentResolver.getType(uri!!))
        }

        private fun validateInputFileName(fileName: String): Boolean {

                        if (TextUtils.isEmpty(fileName)) {
                                Toast.makeText(this, "Enter file name!", Toast.LENGTH_SHORT).show()
                                return false
                        }

                        return true
        }

        private fun createFile(context: Context, srcUri: Uri, dstFile: File) {
                        try {
                                val inputStream = context.contentResolver.openInputStream(srcUri) ?: return
                                val outputStream = FileOutputStream(dstFile)
                                IOUtils.copy(inputStream, outputStream)
                                inputStream.close()
                                outputStream.close()
                        } catch (e: IOException) {
                                e.printStackTrace()
                        }

        }









//////////////////////////////////////////////////////////////////// 카메라 및 갤러리 가져오기들


        ///  갤러리에서 이미지 가져오는 방법
        private fun loadImage(){
                val intent = Intent()
                intent.type = "image/*"   // 이미지 전체에서 선정하겠다라는뜻임.
                intent.action = Intent.ACTION_GET_CONTENT

                startActivityForResult((Intent.createChooser(intent,"Load Picture")), Gallery)           // 갤러리 내에서 가져올 것이다.
        }



        // 카메라 촬영기능
        private fun takeCapture() {
                // 기본 카메라 앱 실행
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        takePictureIntent.resolveActivity(packageManager)?.also{
                                val photoFile: File? = try{
                                        createImageFile()
                                } catch (ex: IOException){
                                        null
                                }
                                photoFile?.also{
                                        val photoURI: Uri =FileProvider.getUriForFile(
                                                this,
                                                "com.example.arttest.fileprovider",
                                                it
                                        )

                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)

                                        // 기존 카메라앱 찍는 액티비티에서 CATPURE를 여기 PictureActivity로 받아오는 과정이기에 ForResult를쓰는것임
                                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                                }
                        }
                }

        }


        // 이미지 파일을 생성하는 과정  (임시파일 개념)
        private fun createImageFile(): File {
                val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                // 비어있을수있다는 뜻알려줌
                val storageDir: File?  = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                return File.createTempFile("JPEG_${timestamp}_",".jpg",storageDir)
                        .apply { curPhotoPath = absolutePath }
        }



        // 테드 퍼미션 설정
        private fun setPermission() {
                val permission = object : PermissionListener {
                        override fun onPermissionGranted() {    //설정해놓은 권한들이 허용되었을 경우 이곳을 수행
                                Toast.makeText(this@PictureActivity, "권한 허용되었습니다.", Toast.LENGTH_SHORT).show()
                        }

                        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) { // 설정해놓은 권한들이 거부된 경우 이곳을 수행
                                Toast.makeText(this@PictureActivity, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                        }

                }

                TedPermission.with(this)
                        .setPermissionListener(permission)
                        .setRationaleMessage("사용하시려면 권한을 허용해주세요")
                        .setDeniedMessage("권한을 거부하셨습니다. 앱설정-> [권한] 허용해주세요")
                        .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
                        .check()

        }



        // startactivitForResult를 통해 기본 카메라앱으로 부터 사진 받아온 결과 값임
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                super.onActivityResult(requestCode, resultCode, data)


                //이미지를 성공적으로 가져왔다면 안드로이드 버젼에 따라 배치주는 기능
                if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){

                        val bitmap: Bitmap
                        val file = File(curPhotoPath)  // 현재 사진의 절대경로를 저장함
                        if(Build.VERSION.SDK_INT < 28) {  // 안드로이드 9.0(pie) 버전보다 낮을 경우
                                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))

                                App.bitmap = bitmap  // 비트맵을 여기안에 넣고
                                IV_PICTURE.setImageBitmap(bitmap)
//                                IV_PICTURE.setImageBitmap(bitmap)   // 원래꺼 : bitmap이라는 사진 결과를 이미지 view에 set 해주는것 = 이미지 뷰에 기록이 됨
                        }else{
                                val decode = ImageDecoder.createSource(
                                        this.contentResolver,
                                        Uri.fromFile(file)   // 버전이 다르면 다르게 가져와야하는 차이 존재
                                )
                                bitmap = ImageDecoder.decodeBitmap(decode) // 디코드 해줌!

                                IV_PICTURE.setImageBitmap(bitmap)
                                App.bitmap = bitmap
//                                IV_PICTURE.setImageBitmap(bitmap)  // 원래 이녀석임

                        }

                        savePhoto(bitmap)
                }
                if(requestCode ==Gallery){
                        if(resultCode == Activity.RESULT_OK){
                                var dataUri = data?.data
                                try{
                                        var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,dataUri)
                                        App.bitmap = bitmap  // 이걸로 전역변수화시킴
                                        IV_PICTURE.setImageBitmap(bitmap)

                                }catch (e:Exception){
                                        Toast.makeText(this,"$e", Toast.LENGTH_SHORT).show()

                                }
                        }else{

                        }
                }


                /// 이미지 보내주는 기능
                bitmap?.recycle()

                if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                        fileUri = data.data
                        try {
                                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
                        } catch (e: IOException) {
                                e.printStackTrace()
                        }

                }





        }



        // 갤러리에 저장하는 기능능
       private fun savePhoto(bitmap: Bitmap) {
                val folderPath = Environment.getExternalStorageDirectory().absolutePath +"/Pictures/"  // 사진폴더로 저장하기위한 경로 선언
                val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val fileName = "${timestamp}.jpeg"
                val folder = File(folderPath)
                                                                // 현재 해당 경로에서 폴더가 존재하는지 검사
                if(!folder.isDirectory){
                        folder.mkdirs()                         // 해당 경로에 자동으로 폴더 만들기
                }

                // 실질적인 저장 처리 함   (결과물 산출하는거)
                val out = FileOutputStream(folderPath +fileName)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , out)
                Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()   // 토스트 확인용
        }



}

