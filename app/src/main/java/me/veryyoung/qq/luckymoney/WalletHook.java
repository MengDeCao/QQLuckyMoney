package me.veryyoung.qq.luckymoney;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import org.json.JSONObject;

import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by veryyoung on 2018/6/14.
 */

public class WalletHook {

    public static Activity walletContext = null;


    public void hook(final ClassLoader classLoader) {
        findAndHookMethod("com.tenpay.sdk.activity.NetBaseActivity", classLoader, "c", String.class, Map.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Exception {
                walletContext = (Activity) param.thisObject;
                XposedBridge.log("set walletContext from NetBaseActivity");
                XposedBridge.log("requestUrl:" + param.args[0]);
                XposedBridge.log("requestMap:" + param.args[1]);
            }
        });

        findAndHookMethod("com.qwallet.qqproxy.j", classLoader, "b", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult("");
                    }
                }
        );


        findAndHookMethod("com.tenpay.sdk.c.o", classLoader, "a", String.class, JSONObject.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("co param[0]:" + param.args[0]);
                        XposedBridge.log("co param[1]:" + param.args[1]);

                    }
                }
        );


        findAndHookMethod("com.tenpay.sdk.g.g", classLoader, "a", Context.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("gg context:" + param.args[0]);
                        XposedBridge.log("gg requestString:" + param.args[1]);
                        XposedBridge.log("gg result:" + param.getResult());
                    }
                }
        );

        findAndHookMethod("com.tenpay.sdk.d.j", classLoader, "a", Context.class, String.class, int.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("dja a arg0:" + param.args[0]);
                        XposedBridge.log("dja a arg1:" + param.args[1]);
                        XposedBridge.log("dja a arg2:" + param.args[2]);
                        XposedBridge.log("dja a arg3:" + param.args[3]);
                        XposedBridge.log("dja a result:" + param.getResult());
                    }
                }
        );

        findAndHookMethod("com.tenpay.sdk.d.s", classLoader, "a", Context.class, String.class, Map.class, String.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("dsc arg0:" + param.args[0]);
                        XposedBridge.log("dsc arg1:" + param.args[1]);
                        XposedBridge.log("dsc arg2:" + param.args[2]);
                        XposedBridge.log("dsc arg3:" + param.args[3]);
                        XposedBridge.log("dsc arg4:" + param.args[4]);
                        XposedBridge.log("dsc result:" + param.getResult());

                        XposedBridge.log("dsc b:" + getObjectField(param.thisObject, "b"));
                    }
                }
        );

        findAndHookMethod("com.tenpay.sdk.d.s", classLoader, "a", Context.class, String.class, Map.class, String.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("dsc arg0:" + param.args[0]);
                        XposedBridge.log("dsc arg1:" + param.args[1]);
                        XposedBridge.log("dsc arg2:" + param.args[2]);
                        XposedBridge.log("dsc arg3:" + param.args[3]);
                        XposedBridge.log("dsc arg4:" + param.args[4]);
                        XposedBridge.log("dsc result:" + param.getResult());

                        XposedBridge.log("dsc b:" + getObjectField(param.thisObject, "b"));
                    }
                }
        );

        findAndHookMethod("com.tenpay.sdk.activity.GrapHbActivity", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Exception {
//                walletContext = (Activity) param.thisObject;
//                String uin = (String) getObjectField(param.thisObject, "eb");
//                Map getKeyMap = new HashMap();
//                getKeyMap.put("skey", getObjectField(param.thisObject, "et"));
//                getKeyMap.put("skey_type", "2");
//                getKeyMap.put("uin", uin);
//                getKeyMap.put("trans_seq", "0");
//                Object qwalletResult = callMethod(walletContext, "c", "/cgi-bin/clientv1.0/qwallet.cgi?", getKeyMap);
//                String getKeyRequestUrl = (String) callMethod(qwalletResult, "a", walletContext, "/cgi-bin/clientv1.0/qwallet.cgi?", getKeyMap, uin, true);
//                getKeyRequestUrl = getKeyRequestUrl + "&msgno=" + callStaticMethod(findClass("com.qwallet.qqproxy.QFuncProxy", classLoader), "getMsgNo", uin);
//                String finalGetKeyRequestUrl = getKeyRequestUrl;
//                Object requestCaller = newInstance(findClass("com.tenpay.sdk.g.g", classLoader), new Object[]{null});
//                Bundle keyBundle = (Bundle) callMethod(requestCaller, "a", walletContext, finalGetKeyRequestUrl);
//                String keyJson = (String) callStaticMethod(findClass("com.tenpay.sdk.d.j", classLoader), "a", walletContext, 0, new String(keyBundle.getByteArray("data")));
//                JSONObject keyJsonObject = new JSONObject(keyJson);
//                String skey = keyJsonObject.getString("skey");
//                String transSeq = keyJsonObject.getString("trans_seq");
//
//                Map getQrcodeMap = new HashMap();
//                getQrcodeMap.put("uin", uin);
//                getQrcodeMap.put("trans_seq", transSeq);
//                getQrcodeMap.put("listid", getObjectField(param.thisObject, "g"));
//                getQrcodeMap.put("authkey", skey);
//                getQrcodeMap.put("answer", 2);
//                getQrcodeMap.put("skey_type", "2");
//                getQrcodeMap.put("groupid", getObjectField(param.thisObject, "ao"));
//                getQrcodeMap.put("grouptype", getObjectField(param.thisObject, "ap"));
//                getQrcodeMap.put("senderuin", walletContext.getIntent().getStringExtra("senderuin"));
//                getQrcodeMap.put("groupuin", getObjectField(param.thisObject, "aq"));
//                getQrcodeMap.put("name", getObjectField(param.thisObject, "h"));
//                getQrcodeMap.put("skey", getObjectField(param.thisObject, "et"));
//                getQrcodeMap.put("channel", getObjectField(param.thisObject, "au"));
//                getQrcodeMap.put("hb_from", 0);
//                getQrcodeMap.put("agreement", 0);
//
//                Object getQrcodeRequest = callMethod(walletContext, "c", "https://mqq.tenpay.com/cgi-bin/qr_code/qr_code_generate.cgi?", getQrcodeMap);
//                String getQrcodeRequestUrl = (String) callMethod(getQrcodeRequest, "a", walletContext, "https://mqq.tenpay.com/cgi-bin/hongbao/qpay_hb_na_grap.cgi?", getQrcodeMap, uin, true);
//                getQrcodeRequestUrl = getQrcodeRequestUrl + "&msgno=" + callStaticMethod(findClass("com.qwallet.qqproxy.QFuncProxy", classLoader), "getMsgNo", uin);
//                Bundle qrcodeBundle = (Bundle) callMethod(requestCaller, "a", walletContext, getQrcodeRequestUrl);
//                String qrcodeKeyString = (String) callStaticMethod(findClass("com.tenpay.sdk.d.j", classLoader), "a", walletContext, parseInt(transSeq), new String(qrcodeBundle.getByteArray("data")));
//                XposedBridge.log("qrcodeKeyString:" + qrcodeKeyString);

            }
        });


        findAndHookMethod("com.tenpay.sdk.activity.QrcodePayActivity", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Exception {
                        walletContext = (Activity) param.thisObject;
                        final String uin = (String) getObjectField(param.thisObject, "eb");

//                        final String tmpFilePath = "/sdcard/Download/" + uin + "jine.tmp";
//                        final File tmpFile = new File(tmpFilePath);
//                        if (!tmpFile.exists()) {
//                            tmpFile.createNewFile();
//                        }
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    String lastNumber = "";
//                                    String fileContent = "";
//                                    String numbersString = "";
//                                    if (!TextUtils.isEmpty(lastNumber)) {
//                                        numbersString = numbersString.substring(numbersString.indexOf(lastNumber) + lastNumber.length() + 1);
//                                    }
//                                    String[] amountString = numbersString.split(",");
//                                    BigDecimal hundred = new BigDecimal(100);
//                                    for (String amountStr : amountString) {
//                                        BigDecimal amountDecimal = new BigDecimal(amountStr.trim());
//                                        int amount = amountDecimal.multiply(hundred).intValue();
//                                        Map getKeyMap = new HashMap();
//                                        getKeyMap.put("skey", getObjectField(param.thisObject, "et"));
//                                        getKeyMap.put("skey_type", "2");
//                                        getKeyMap.put("uin", uin);
//                                        getKeyMap.put("trans_seq", "0");
//                                        Object qwalletResult = callMethod(walletContext, "c", "/cgi-bin/clientv1.0/qwallet.cgi?", getKeyMap);
//                                        String getKeyRequestUrl = (String) callMethod(qwalletResult, "a", walletContext, "/cgi-bin/clientv1.0/qwallet.cgi?", getKeyMap, uin, true);
//                                        getKeyRequestUrl = getKeyRequestUrl + "&msgno=" + callStaticMethod(findClass("com.qwallet.qqproxy.QFuncProxy", classLoader), "getMsgNo", uin);
//                                        String finalGetKeyRequestUrl = getKeyRequestUrl;
//                                        Object requestCaller = newInstance(findClass("com.tenpay.sdk.g.g", classLoader), new Object[]{null});
//                                        Bundle keyBundle = (Bundle) callMethod(requestCaller, "a", walletContext, finalGetKeyRequestUrl);
//                                        String keyJson = (String) callStaticMethod(findClass("com.tenpay.sdk.d.j", classLoader), "a", walletContext, 0, new String(keyBundle.getByteArray("data")));
//                                        JSONObject keyJsonObject = new JSONObject(keyJson);
//                                        String skey = keyJsonObject.getString("skey");
//                                        String transSeq = keyJsonObject.getString("trans_seq");
//
//                                        Map getQrcodeMap = new HashMap();
//                                        getQrcodeMap.put("uin", uin);
//                                        getQrcodeMap.put("trans_fee", amount + "");
//                                        getQrcodeMap.put("trans_seq", transSeq);
//                                        getQrcodeMap.put("skey", skey);
//                                        getQrcodeMap.put("skey_type", "0");
//                                        getQrcodeMap.put("type", "1");
//                                        Object getQrcodeRequest = callMethod(walletContext, "c", "https://mqq.tenpay.com/cgi-bin/qr_code/qr_code_generate.cgi?", getQrcodeMap);
//                                        String getQrcodeRequestUrl = (String) callMethod(getQrcodeRequest, "a", walletContext, "https://mqq.tenpay.com/cgi-bin/qr_code/qr_code_generate.cgi?", getQrcodeMap, uin, true);
//                                        getQrcodeRequestUrl = getQrcodeRequestUrl + "&msgno=" + callStaticMethod(findClass("com.qwallet.qqproxy.QFuncProxy", classLoader), "getMsgNo", uin);
//                                        Bundle qrcodeBundle = (Bundle) callMethod(requestCaller, "a", walletContext, getQrcodeRequestUrl);
//                                        String qrcodeKeyString = (String) callStaticMethod(findClass("com.tenpay.sdk.d.j", classLoader), "a", walletContext, parseInt(transSeq), new String(qrcodeBundle.getByteArray("data")));
//                                        String authCode = new JSONObject(qrcodeKeyString).getString("auth_code");
//                                        String result = "https://i.qianbao.qq.com/wallet/sqrcode.htm?m=tenpay&f=wallet&u=" + uin + "&a=1&n=" + getObjectField(param.thisObject, "ec") + "&ac=" + authCode;
//                                        XposedBridge.log("amount:" + amount + ", result:" + result);
//                                    }
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        }).start();

                    }
                }

        );


    }


}