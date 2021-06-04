package com.example.arttest.aws

import com.amazonaws.regions.Regions

object AwsConstants {

    val BUCKET_NAME:String ="yangjae-team02-bucket"     // 권한 체크

    val ARN:String ="arn:aws:s3:::yangjae-team02-bucket"

    val ACCESS_KEY:String ="AKIA5VZTIAOJ37JVHU5F"       // cloud user key IAM   S3 이미지 넣는 권한.
    val SECRET_KEY:String = "DqlqPP/1KyYlzouFEF5VTAnPnc1VjAa/lIyDUqje"

    val REGION: Regions = Regions.US_EAST_2

    val URL:String = "https://yangjae-team02-bucket.s3.us-east-2.amazonawss.com/"  // 이미지 전용 URL

    val URL2:String = "https://xai4s5kf06.execute-api.us-east-2.amazonaws.com/"     // survey전용 URL



    val folderpath_image ="image_test"  // 이미지

    val folderPath_servey ="info_test/"        // servey 전용   or  image + servey



    // 가로세로비율율 추가해보기
}