package com.github.jonathangawrych.deviceadminmanager.controller;

import android.util.Log;

import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class HookUtilities {
	
	/*package*/ static final String TAG = "DeviceAdminManager";
	/*package*/ static final boolean OVERBOSE = false;
	
	/*package*/ static XC_MethodHook passThough(final String name) {
		return new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) {
				if (param.hasThrowable())
					Log.d(TAG, "passThough " + name + "(" + Arrays.toString(param.args) + ") threw ", param.getThrowable());
				else
					Log.d(TAG, "passThough " + name + "(" + Arrays.toString(param.args) + ") returned " + param.getResult());
				if (OVERBOSE)
					Log.v(TAG, "passThough " + name + " stack trace", new Exception());
			}
		};
	}
	
	/*package*/ static void attemptHookMethod(Class<?> clazz, String methodName, Class... args) {
		attemptHookMethod(clazz, methodName, passThough(methodName), args);
	}
	
	/*package*/ static void attemptHookMethod(Class<?> clazz, String methodName, XC_MethodHook hook, Class... args) {
		try {
			Object[] argsAndHook = new Object[args.length + 1];
			System.arraycopy(args, 0, argsAndHook, 0, args.length);
			argsAndHook[args.length] = hook;
			
			XposedHelpers.findAndHookMethod(clazz, methodName, argsAndHook);
			Log.v(TAG, "Hooked method " + methodName);
		} catch (NoSuchMethodError e) {
			Log.e(TAG, "Unable to hook method " + methodName + " (api change?):", e);
			XposedBridge.log("Unable to hook method " + methodName + " (api change?)");
			XposedBridge.log(e);
		}
	}
}
