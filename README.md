# passwordManager2
指纹加密
第一次启动应用进行指纹加密，并且获取密文和IV
加密密钥，解密之后用密钥解密数据展示出来

Cipher cipher = result.getCryptoObject().getCipher();
byte[] encrypted = cipher.doFinal(data.getBytes());
byte[] IV = cipher.getIV();
String se = Base64.encodeToString(encrypted, Base64.URL_SAFE);
String siv = Base64.encodeToString(IV, Base64.URL_SAFE);

1 先有密码加密
2 然后选择是否开启指纹

xml
iv  String
se  String
fingerSwitch  boolean
passlen String
AESMD5Key String


                        随机生成key---MD5--得到AESkey

   设置密码       userkey（明文）---MD5Key---加密MD5key（自己加密自己）
   开启指纹加密    加密的MD5key---解密的MD5key----输入userkey加密验证MD5key-------用指纹加密MD5key

   密码解密       userkey ---MD5Key----AES解密操作
   指纹解密       解密的MD5key