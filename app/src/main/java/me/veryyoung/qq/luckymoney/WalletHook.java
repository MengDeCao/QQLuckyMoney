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


    }


}