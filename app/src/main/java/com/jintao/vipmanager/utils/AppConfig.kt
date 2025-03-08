package com.jintao.vipmanager.utils

object AppConfig {
    const val BASE_RELEASE_URL = "http://192.168.0.123:8080"  //正式地址
    const val DATABASE_FILE_NAME = "vip_user_table.db"
    const val DATABASE_FILE_SHM = "vip_user_table.db-shm"
    const val DATABASE_FILE_WAL = "vip_user_table.db-wal"
    const val BACKUP_DATA_NAME = "backup_data.txt"

    //阿里云OSS文件上传配置
    const val ALIYUN_BASE_URL = "http://vip-user-manager.oss-cn-beijing.aliyuncs.com/"
    const val aliyunEndpoint = ""
    const val aliyunBucketName = ""
    const val aliyunKeyId = ""
    const val aliyunKeySecret = ""
}