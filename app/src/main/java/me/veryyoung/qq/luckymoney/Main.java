package me.veryyoung.qq.luckymoney;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import dalvik.system.BaseDexClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.os.SystemClock.sleep;
import static android.widget.Toast.LENGTH_LONG;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findFirstFieldByExactType;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static java.lang.String.valueOf;
import static me.veryyoung.qq.luckymoney.HideModule.hideModule;
import static me.veryyoung.qq.luckymoney.XposedUtils.findFieldByClassAndTypeAndName;
import static me.veryyoung.qq.luckymoney.XposedUtils.findResultByMethodNameAndReturnTypeAndParams;
import static me.veryyoung.qq.luckymoney.enums.PasswordStatus.CLOSE;
import static me.veryyoung.qq.luckymoney.enums.PasswordStatus.SEND;
import static me.veryyoung.qq.luckymoney.enums.ReplyStatus.ALL;
import static me.veryyoung.qq.luckymoney.enums.ReplyStatus.GOT;
import static me.veryyoung.qq.luckymoney.enums.ReplyStatus.MISSED;


public class Main implements IXposedHookLoadPackage {

    public static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";

    /**
     * search  "qwallet.cgi", "qpay_gate.cgi",
     */
    public static final String REQUEST_UTIL = "com.tenpay.sdk.d.j";

    public static final String REQUEST_CALLER = "com.tenpay.sdk.g.g";

    private static long msgUid;
    private static String senderuin;
    private static String frienduin;
    private static String from;
    private static int istroop;
    private static String selfuin;
    private static Context globalContext;
    private static Object HotChatManager;
    private static Object TicketManager;
    private static Object TroopManager;
    private static Object DiscussionManager;
    private static Object FriendManager;
    private static Object globalQQInterface = null;
    private static int n = 1;


    private void dohook(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        new DonateHook().hook(loadPackageParam);

        findAndHookConstructor("dalvik.system.BaseDexClassLoader", loadPackageParam.classLoader, String.class, File.class, String.class, ClassLoader.class, new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args[0].toString().endsWith("qwallet_plugin.apk")) {
                            new WalletHook().hook((BaseDexClassLoader) param.thisObject);
                        }
                    }
                }
        );

        findAndHookMethod("com.tencent.mobileqq.data.MessageForQQWalletMsg", loadPackageParam.classLoader, "doParse", new
                XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (!PreferencesUtils.open() || msgUid == 0) {
                            return;
                        }
                        msgUid = 0;

                        int messageType = (int) getObjectField(param.thisObject, "messageType");
                        XposedBridge.log("messageType:" + messageType);
                        if (messageType == 6 && PreferencesUtils.password() == CLOSE) {
                            return;
                        }
                        if (13 == messageType) {
                            return;
                        }

                        Object mQQWalletRedPacketMsg = getObjectField(param.thisObject, "mQQWalletRedPacketMsg");
                        String redPacketId = getObjectField(mQQWalletRedPacketMsg, "redPacketId").toString();
                        String authkey = (String) getObjectField(mQQWalletRedPacketMsg, "authkey");
                        Object SessionInfo = newInstance(findClass("com.tencent.mobileqq.activity.aio.SessionInfo", loadPackageParam.classLoader));
                        findFieldByClassAndTypeAndName(findClass("com.tencent.mobileqq.activity.aio.SessionInfo", loadPackageParam.classLoader), String.class, "a").set(SessionInfo, frienduin);
                        findFieldByClassAndTypeAndName(findClass("com.tencent.mobileqq.activity.aio.SessionInfo", loadPackageParam.classLoader), Integer.TYPE, "a").setInt(SessionInfo, istroop);
                        Object QQWalletTransferMsgElem = XposedHelpers.getObjectField(mQQWalletRedPacketMsg, "elem");
                        String password = XposedHelpers.getObjectField(QQWalletTransferMsgElem, "title").toString();
                        Object messageParam = newInstance(findClass("com.tencent.mobileqq.activity.ChatActivityFacade$SendMsgParams", loadPackageParam.classLoader));

                        if (selfuin.equals(senderuin) && PreferencesUtils.self()) {
                            return;
                        }

                        String group = PreferencesUtils.group();
                        if (!TextUtils.isEmpty(group)) {
                            for (String group1 : group.split(",")) {
                                if (frienduin.equals(group1) || senderuin.equals(group1)) {
                                    if (istroop == 1 && senderuin.equals(group1)) {
                                        from = "指定人不抢" + "\n" + "来自群:" + getObjectField(findResultByMethodNameAndReturnTypeAndParams(TroopManager, "a", "com.tencent.mobileqq.data.TroopInfo", frienduin), "troopname") + "\n" + "来自:" + getObjectField(callMethod(FriendManager, "c", group1), "name");
                                    } else if (istroop == 1) {
                                        from = "指定群不抢" + "\n" + "来自群:" + getObjectField(findResultByMethodNameAndReturnTypeAndParams(TroopManager, "a", "com.tencent.mobileqq.data.TroopInfo", group1), "troopname");
                                    } else {
                                        from = "指定人不抢" + "\n" + "来自:" + getObjectField(callMethod(FriendManager, "c", group1), "name");
                                    }
                                    toast(from);
                                    return;
                                }
                            }
                        }

                        String keywords = PreferencesUtils.keywords();
                        if (!TextUtils.isEmpty(keywords)) {
                            for (String keywords1 : keywords.split(",")) {
                                if (password.contains(keywords1)) {
                                    toast("关键词不抢" + "\n" + "关键词:" + keywords1);
                                    return;
                                }
                            }
                        }


                        if (PreferencesUtils.delay()) {
                            sleep(PreferencesUtils.delayTime());
                        }

                        ClassLoader walletClassLoader = (ClassLoader) callStaticMethod(findClass("com.tencent.mobileqq.pluginsdk.PluginStatic", loadPackageParam.classLoader), "getOrCreateClassLoader", globalContext, "qwallet_plugin.apk");
                        Object requestCaller = newInstance(findClass(REQUEST_CALLER, walletClassLoader), new Object[]{null});

                        String skey = (String) callMethod(TicketManager, "getSkey", selfuin);
                        int random = Math.abs(new Random().nextInt()) % 16;
                        StringBuffer requestUrl = new StringBuffer()
                                .append("uin").append("=").append(selfuin)
                                .append("&").append("listid").append("=").append(redPacketId)
                                .append("&").append("authkey").append("=").append(authkey)
                                .append("&").append("skey_type").append("=").append(2)
                                .append("&").append("groupid").append("=").append(istroop == 0 ? selfuin : frienduin)
                                .append("&").append("grouptype").append("=").append(getGroupType())
                                .append("&").append("groupuin").append("=").append(getGroupuin(messageType))
                                .append("&").append("name").append("=").append(getObjectField(callMethod(FriendManager, "c", selfuin), "name"))
                                .append("&").append("skey").append("=").append(skey)
                                .append("&").append("channel").append("=").append(getObjectField(mQQWalletRedPacketMsg, "redChannel"));
                        if (13 != messageType) {
                            requestUrl.append("&").append("senderuin").append("=").append(senderuin)
                                    .append("&").append("hb_from").append("=").append(0);

                        }
                        requestUrl.append("&").append("agreement").append("=").append(0);

                        String reqText = (String) callStaticMethod(findClass(REQUEST_UTIL, walletClassLoader), "a", globalContext, "https://mqq.tenpay.com/cgi-bin/hongbao/qpay_hb_na_grap.cgi?ver=2.0&chv=3", random, requestUrl.toString());

                        String openLuckyMoneyUrl = new StringBuffer("https://mqq.tenpay.com/cgi-bin/hongbao/qpay_hb_na_grap.cgi?ver=2.0&chv=3")
                                .append("&").append("req_text").append("=").append(reqText)
                                .append("&").append("skey").append("=").append(skey)
                                .append("&").append("skey_type").append("=").append(2)
                                .append("&").append("random").append("=").append(random)
                                .append("&msgno=" + generateNo(selfuin))
                                .toString();


                        Bundle hbResponseBundle = (Bundle) callMethod(requestCaller, "a", globalContext, openLuckyMoneyUrl);
                        String hbResponse = (String) callStaticMethod(findClass(REQUEST_UTIL, walletClassLoader), "a", globalContext, random, new String(hbResponseBundle.getByteArray("data")));

                        XposedBridge.log("hbResponse:" + hbResponse);
                        JSONObject jsonobject = new JSONObject(hbResponse);
                        String name = jsonobject.getJSONObject("send_object").optString("send_name");
                        int state = jsonobject.optInt("state");
                        if (istroop == 1) {
                            from = "来自:" + name + "\n" + "来自群:" + getObjectField(findResultByMethodNameAndReturnTypeAndParams(TroopManager, "a", "com.tencent.mobileqq.data.TroopInfo", frienduin), "troopname");
                        } else if (istroop == 5) {
                            from = "来自:" + name + "\n" + "来自热聊:" + getObjectField(findResultByMethodNameAndReturnTypeAndParams(HotChatManager, "a", "com.tencent.mobileqq.data.HotChatInfo", frienduin), "name");
                        } else if (istroop == 3000) {
                            from = "来自:" + name + "\n" + "来自讨论组:" + getObjectField(findResultByMethodNameAndReturnTypeAndParams(DiscussionManager, "a", "com.tencent.mobileqq.data.DiscussionInfo", frienduin), "discussionName");
                        } else {
                            from = "来自:" + name;
                        }

                        if (state == 0) {
                            double amount = ((double) jsonobject.getJSONObject("recv_object").getInt("amount")) / 100.0d;
                            toast("QQ红包帮你抢到了" + amount + "元" + "\n" + from);
                            if (PreferencesUtils.reply() == GOT || PreferencesUtils.reply() == ALL && !TextUtils.isEmpty(PreferencesUtils.gotReply()) && messageType != 8) {
                                callStaticMethod(findClass("com.tencent.mobileqq.activity.ChatActivityFacade", loadPackageParam.classLoader), "a", globalQQInterface, globalContext, SessionInfo, PreferencesUtils.gotReply(), new ArrayList(), messageParam);
                            }

                        } else if (state == 2) {

                            if (messageType != 8) {
                                toast("没抢到" + "\n" + from);
                                if (PreferencesUtils.reply() == MISSED || PreferencesUtils.reply() == ALL && !TextUtils.isEmpty(PreferencesUtils.missedReply()) && messageType != 8) {
                                    callStaticMethod(findClass("com.tencent.mobileqq.activity.ChatActivityFacade", loadPackageParam.classLoader), "a", globalQQInterface, globalContext, SessionInfo, PreferencesUtils.missedReply(), new ArrayList(), messageParam);
                                }
                            }

                        }

                        if (6 == messageType && PreferencesUtils.password() == SEND) {
                            callStaticMethod(findClass("com.tencent.mobileqq.activity.ChatActivityFacade", loadPackageParam.classLoader), "a", globalQQInterface, globalContext, SessionInfo, password, new ArrayList(), messageParam);
                        }
                    }
                }
        );


        findAndHookMethod("com.tencent.mobileqq.app.MessageHandlerUtils", loadPackageParam.classLoader, "a",
                "com.tencent.mobileqq.app.QQAppInterface",
                "com.tencent.mobileqq.data.MessageRecord", Boolean.TYPE, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (!PreferencesUtils.open()) {
                            return;
                        }
                        int msgtype = (int) getObjectField(param.args[1], "msgtype");
                        if (msgtype == -2025) {
                            msgUid = (long) getObjectField(param.args[1], "msgUid");
                            senderuin = (String) getObjectField(param.args[1], "senderuin");
                            frienduin = getObjectField(param.args[1], "frienduin").toString();
                            istroop = (int) getObjectField(param.args[1], "istroop");
                            selfuin = getObjectField(param.args[1], "selfuin").toString();
                        }
                    }
                }

        );

        findAndHookMethod("com.tencent.mobileqq.activity.PublicTransFragmentActivity", loadPackageParam.classLoader, "b",
                Context.class, Intent.class, Class.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("PublicTransFragmentActivity param[0]:" + param.args[0]);
                        XposedBridge.log("PublicTransFragmentActivity param[1]:" + param.args[1]);
                        XposedBridge.log("PublicTransFragmentActivity param[2]:" + param.args[2]);
                    }
                }

        );

        findAndHookMethod("com.tencent.mobileqq.activity.PublicTransFragmentActivity", loadPackageParam.classLoader, "b",
                Activity.class, Intent.class, Class.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("PublicTransFragmentActivity param[0]:" + param.args[0]);
                        XposedBridge.log("PublicTransFragmentActivity param[1]:" + param.args[1]);
                        XposedBridge.log("PublicTransFragmentActivity param[2]:" + param.args[2]);
                        XposedBridge.log("PublicTransFragmentActivity param[3]:" + param.args[3]);
                    }
                }

        );


        findAndHookMethod("com.tencent.mobileqq.activity.SplashActivity", loadPackageParam.classLoader, "doOnCreate", Bundle.class, new

                XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        globalContext = (Context) param.thisObject;
                        globalQQInterface = findFirstFieldByExactType(findClass("com.tencent.mobileqq.activity.SplashActivity", loadPackageParam.classLoader), findClass("com.tencent.mobileqq.app.QQAppInterface", loadPackageParam.classLoader)).get(param.thisObject);

                    }
                }

        );


        findAndHookConstructor("mqq.app.TicketManagerImpl", loadPackageParam.classLoader, "mqq.app.AppRuntime", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                TicketManager = param.thisObject;
            }
        });


        findAndHookConstructor("com.tencent.mobileqq.app.HotChatManager", loadPackageParam.classLoader, "com.tencent.mobileqq.app.QQAppInterface", new

                XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        HotChatManager = param.thisObject;
                    }
                }
        );

        findAndHookConstructor("com.tencent.mobileqq.app.TroopManager", loadPackageParam.classLoader, "com.tencent.mobileqq.app.QQAppInterface", new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam methodHookParam) {
                TroopManager = methodHookParam.thisObject;
            }
        });

        findAndHookConstructor("com.tencent.mobileqq.app.DiscussionManager", loadPackageParam.classLoader, "com.tencent.mobileqq.app.QQAppInterface", new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam methodHookParam) {
                DiscussionManager = methodHookParam.thisObject;
            }
        });

        findAndHookConstructor("com.tencent.mobileqq.app.FriendsManager", loadPackageParam.classLoader, "com.tencent.mobileqq.app.QQAppInterface", new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam methodHookParam) {
                FriendManager = methodHookParam.thisObject;
            }
        });

    }


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if (loadPackageParam.packageName.equals(QQ_PACKAGE_NAME)) {
            hideModule(loadPackageParam);

            int ver = Build.VERSION.SDK_INT;
            if (ver < 21) {
                findAndHookMethod("com.tencent.common.app.BaseApplicationImpl", loadPackageParam.classLoader, "onCreate", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        dohook(loadPackageParam);
                    }
                });
            } else {
                dohook(loadPackageParam);
            }
        }
    }


    private int getGroupType() throws IllegalAccessException {
        int grouptype = 0;
        if (istroop == 3000) {
            grouptype = 2;

        } else if (istroop == 1) {
            Map map = (Map) findFirstFieldByExactType(HotChatManager.getClass(), Map.class).get(HotChatManager);
            if (map != null & map.containsKey(frienduin)) {
                grouptype = 5;
            } else {
                grouptype = 1;
            }
        } else if (istroop == 0) {
            grouptype = 0;
        } else if (istroop == 1004) {
            grouptype = 4;

        } else if (istroop == 1000) {
            grouptype = 3;

        } else if (istroop == 1001) {
            grouptype = 6;
        }
        return grouptype;
    }

    private String getGroupuin(int messageType) throws InvocationTargetException, IllegalAccessException {
        if (messageType != 6 && messageType != 13) {
            return senderuin;
        }
        if (istroop == 1) {
            return (String) getObjectField(findResultByMethodNameAndReturnTypeAndParams(TroopManager, "a", "com.tencent.mobileqq.data.TroopInfo", frienduin), "troopcode");
        } else if (istroop == 5) {
            return (String) getObjectField(findResultByMethodNameAndReturnTypeAndParams(HotChatManager, "a", "com.tencent.mobileqq.data.HotChatInfo", frienduin), "troopCode");
        }
        return senderuin;
    }

    private String generateNo(String selfuin) {
        StringBuilder stringBuilder = new StringBuilder(selfuin);
        stringBuilder.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        String count = valueOf(n++);
        int length = (28 - stringBuilder.length()) - count.length();
        for (int i = 0; i < length; i++) {
            stringBuilder.append("0");
        }
        stringBuilder.append(count);
        return stringBuilder.toString();
    }

    private void toast(final String content) {
        if (PreferencesUtils.amount()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(globalContext, content, LENGTH_LONG).show();
                }
            });

        }
    }


}
