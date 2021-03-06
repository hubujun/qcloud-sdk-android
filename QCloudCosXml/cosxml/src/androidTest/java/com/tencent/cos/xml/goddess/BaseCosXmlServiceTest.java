/*
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.tencent.cos.xml.goddess;

import android.content.Context;
import androidx.annotation.Nullable;

import com.tencent.cos.xml.BuildConfig;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.QServer;
import com.tencent.cos.xml.common.Region;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

/**
 * 和 CosXmlService 相关的测试都应该继承于这个类，来提供一些基础的能力
 *
 * Created by rickenwang on 2019/9/11.
 */
public abstract class BaseCosXmlServiceTest {

    protected final String TAG = "UnitTest";

    private Context context;

    // 测试地域固定为 ap-guangzhou
    private final String region = Region.AP_Guangzhou.getRegion() ;

    private String secretId;

    private String secretKey;

    protected String uin;

    protected String appid;

    protected String bucket;

    protected CosXmlService cosXmlService;

    private String localDirectory;

    protected void initCosXmlService(Context context) {

        this.context = context;

        uin = BuildConfig.COS_UIN;
        appid = BuildConfig.COS_APPID;

        secretId = BuildConfig.COS_SECRET_ID;
        secretKey = BuildConfig.COS_SECRET_KEY;

        bucket = "android-ut-temp-bucket";

        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .isHttps(true)
                .setAppidAndRegion(appid, region)
                .setDebuggable(true)
                .builder();
        cosXmlService = new CosXmlService(context, cosXmlServiceConfig,
                new ShortTimeCredentialProvider(secretId,secretKey,3600 * 10)); // 有效期为 10 个小时

        localDirectory = QServer.localParentDirectory(context).getAbsolutePath();
    }




    protected void clearCosXmlService() {

        // 清除本地的缓存文件
        deleteDir(new File(localDirectory));
    }

    @Nullable public String newLocalFile(long fileLength) {

        String filePath = localDirectory + File.separator + System.currentTimeMillis() + ".txt";

        if (createFile(filePath, fileLength)) {
            return filePath;
        }

        return null;
    }

    public static boolean deleteFile(String filePath){
        if(filePath != null){
            File file = new File(filePath);
            if(file.exists()){
                return file.delete();
            }
        }
        return false;
    }

    public static boolean createFile(String filePath, long fileLength) {

        try {
            RandomAccessFile accessFile = new RandomAccessFile(filePath, "rws");
            accessFile.setLength(fileLength);
            accessFile.write(new Random().nextInt(200));
            accessFile.seek(fileLength/2);
            accessFile.write(new Random().nextInt(200));
            accessFile.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }



}
