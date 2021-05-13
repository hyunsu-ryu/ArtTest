package com.example.arttest

import android.app.Activity
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
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.metrics.AwsSdkMetrics.getRegion
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
import com.example.arttest.aws.AWSUtils
import com.example.arttest.aws.AwsConstants
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_picture.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PictureActivity : AppCompatActivity(), AWSUtils.OnAwsImageUploadListener{
        val Gallery = 0  // 갤러리 접근 request 코드 생성해주기
        val REQUEST_IMAGE_CAPTURE = 1 //  카메라 사진 촬영 요청코드
        lateinit var curPhotoPath: String // 문자열 형태의 사진 경로 값 ( 초기값을 null로 시작하고 싶을떄)

//        yangjae-team02-bucket  // 버킷이름
//        https://yangjae-team02-bucket.s3.us-east-2.amazonawss.com/ // S3



        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_picture)

                setPermission()  // 최초의 권한을 체크하는 텍스트 수행

                btn_camera.setOnClickListener {
                        takeCapture()  // 기본 카메라 앱을 실행하여 사진 촬영영
               }
                btn_gallery.setOnClickListener { loadImage() }

                btn_next.setOnClickListener {

                        uploadImage()
                        // 여기안에 서버로 보내는 코드를 작성하자

                        startActivity<SurveyActivity>(
                        )
                }


                               // 여기서 사진이 업로드됨

//                iv_picture.setImageBitmap(onDownloadClick())


        }


//        fun onUploadClick(v: View): Bitmap? {
//                uploadWithTransferUtility()
//        }
//
//        fun uploadWithTransferUtility(fileName: String, file: File) {
//
//                val credentialsProvider = CognitoCachingCredentialsProvider(
//                        applicationContext,
//                        "ap-northeast-2:167efb36-dea5-4724-935d-0c419fc48f12", // 자격 증명 풀 ID
//                        Regions.AP_NORTHEAST_2 // 리전
//                )
//
//                TransferNetworkLossHandler.getInstance(applicationContext)
//
//                val transferUtility = TransferUtility.builder()
//                        .context(GSApplicationClass.getInstance())  // 클래스명 입력 필요
//                        .defaultBucket("AWS_STORAGE_BUCKET_NAME")   // 버킷의 이름 을 입력해야함
//                        .s3Client(AmazonS3Client(credentialsProvider, Region.getRegion(Regions.AP_NORTHEAST_2)))
//                        .build()
//
//                /* Store the new created Image file path */
//
//                val uploadObserver = transferUtility.upload("BUCKET_PATH/${fileName}", file, CannedAccessControlList.PublicRead)
//
//                //CannedAccessControlList.PublicRead 읽기 권한 추가
//
//                // Attach a listener to the observer
//                uploadObserver.setTransferListener(object : TransferListener {
//                        override fun onStateChanged(id: Int, state: TransferState) {
//                                if (state == TransferState.COMPLETED) {
//                                        // Handle a completed upload
//                                }
//                        }
//
//                        override fun onProgressChanged(id: Int, current: Long, total: Long) {
//                                val done = (((current.toDouble() / total) * 100.0).toInt())
//                                Log.d("MYTAG", "UPLOAD - - ID: $id, percent done = $done")
//                        }
//
//                        override fun onError(id: Int, ex: Exception) {
//                                Log.d("MYTAG", "UPLOAD ERROR - - ID: $id - - EX: ${ex.message.toString()}")
//                        }
//                })
//
//                // If you prefer to long-poll for updates
//                if (uploadObserver.state == TransferState.COMPLETED) {
//                        /* Handle completion */
//
//                }
//        }








        private fun uploadImage(){
                val image = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(image, 100)
        }



        ///  갤러리에서 이미지 가져오는 방법
        private fun loadImage(){
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT

                startActivityForResult((Intent.createChooser(intent,"Load Picture")),Gallery)
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

                // 사진 upload 시 실행되는 녀석임 requestcode가 100일때
                if(resultCode == Activity.RESULT_OK && requestCode == 100){
                        val imageUri = data?.data
                        val path: String? = getPath(imageUri!!) // URI로부터 파일 경로 얻는 것

                        AWSUtils(this,path!!, this, AwsConstants.folderPath).beginUpload()
                }


        }

        //getPath from URI  이부분 해결해야함
        protected fun getPath(uri: Uri): String{
                var uri = uri
                var selection: String? = null
                var selectionArgs:Array<String>? = null

                if(DocumentsContract.isDocumentUri(applicationContext, uri)){
                        if(isExternalStorageDocument(uri)){
                                val docId =DocumentsContract.getDocumentId(uri)
                                val split = docId(":".toRegex()).dropLastWhile{ it.isEmpty() }.toTypedArray()

                        }
                }


        }


        private fun isExternalStorageDocument(uri: Uri): Boolean{
                return "com.android.externalstorage.documents" == uri.authority
        }

        private fun isDownloadsDocument(uri: Uri): Boolean{
                return "com.android.externalstorage.documents" == uri.authority
        }
        private fun isMediaDocument(uri: Uri): Boolean{
                return "com.android.externalstorage.documents" == uri.authority
        }




        // 갤러리에 저장하는 기능능
       private fun savePhoto(bitmap: Bitmap) {
                val folderPath = Environment.getExternalStorageDirectory().absolutePath +"/Pictures/"  // 사진폴더로 저장하기위한 경로 선언
                val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val fileName = "${timestamp}.jpeg"
                val folder = File(folderPath)
                // 현재 해당 경로에서 폴더가 존재하는지 검사
                if(!folder.isDirectory){
                        folder.mkdirs() // 해당 경로에 자동으로 폴더 만들기
                }

                // 실질적인 저장 처리 함   (결과물 산출하는거)
                val out = FileOutputStream(folderPath +fileName)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , out)
                Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()   // 토스트 확인용
        }

        override fun showProgressDialog() {
                progressBar.visibility = View.VISIBLE
                btn_next.visibility = View.INVISIBLE
        }

        override fun hideProgressDialog() {
                progressBar.visibility = View.INVISIBLE
                btn_next.visibility = View.VISIBLE
        }

        override fun onSuccess(imgUrl: String) {
                Toast.makeText(this,"File upload complete",Toast.LENGTH_SHORT).show()
                println("Final signed url:"+imgUrl)
        }

        override fun onError(errorMsg: String) {
                TODO("Not yet implemented")
        }


}