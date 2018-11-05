package me.veryyoung.qq.luckymoney;

import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by veryyoung on 2018/6/14.
 */

public class WalletHook {


    public void hook(final ClassLoader classLoader) {

        findAndHookMethod("com.qwallet.qqproxy.j", classLoader, "b", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult("");
                    }
                }
        );

//        findAndHookMethod(REQUEST_ENCODER, classLoader, "a", String.class, String.class, int.class, String.class, new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        XposedBridge.log("string:" + param.args[0]);
//                        XposedBridge.log("string:" + param.args[1]);
//                        XposedBridge.log("int:" + param.args[2]);
//                        XposedBridge.log("string:" + param.args[3]);
//                        XposedBridge.log("result:" + param.getResult());
//                    }
//                }
//        );
//
//        findAndHookMethod(REQUEST_ENCODER, classLoader, "a", String.class, String.class, int.class, String.class, Map.class, new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        XposedBridge.log("e string:" + param.args[0]);
//                        XposedBridge.log("e string:" + param.args[1]);
//                        XposedBridge.log("e int:" + param.args[2]);
//                        XposedBridge.log("e string:" + param.args[3]);
//                        XposedBridge.log("e map:" + param.args[4]);
//                        XposedBridge.log("e result:" + param.getResult());
//
//                    }
//                }
//        );


    }


}