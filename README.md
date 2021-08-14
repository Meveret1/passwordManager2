# passwordManager2
指纹加密
第一次启动应用进行指纹加密，并且获取密文和IV
加密密钥，解密之后用密钥解密数据展示出来

Cipher cipher = result.getCryptoObject().getCipher();
byte[] encrypted = cipher.doFinal(data.getBytes());
byte[] IV = cipher.getIV();
String se = Base64.encodeToString(encrypted, Base64.URL_SAFE);
String siv = Base64.encodeToString(IV, Base64.URL_SAFE);
