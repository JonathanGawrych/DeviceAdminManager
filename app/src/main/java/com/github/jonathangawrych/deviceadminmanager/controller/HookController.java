package com.github.jonathangawrych.deviceadminmanager.controller;

import android.annotation.TargetApi;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcel;
import android.util.Log;
import android.util.Printer;

import java.util.Arrays;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class HookController implements IXposedHookZygoteInit {
	
	private static final String TAG = "DeviceAdminManager";
	private static final boolean OVERBOSE = false;
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		Log.i(TAG, TAG + " loaded");
	
		HookFroyo();
	}
	
	@TargetApi(Build.VERSION_CODES.FROYO)
	private void HookFroyo() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO)
			return;
		
		Log.d(TAG, "Hooking Froyo Admin Methods");
		
		// remember constructor
		attemptHookMethod(DeviceAdminInfo.class, "describeContents");
		attemptHookMethod(DeviceAdminInfo.class, "dump", Printer.class, String.class);
		attemptHookMethod(DeviceAdminInfo.class, "getActivityInfo");
		attemptHookMethod(DeviceAdminInfo.class, "getComponent");
		attemptHookMethod(DeviceAdminInfo.class, "getPackageName");
		attemptHookMethod(DeviceAdminInfo.class, "getReceiverName");
		attemptHookMethod(DeviceAdminInfo.class, "getTagForPolicy", int.class);
		attemptHookMethod(DeviceAdminInfo.class, "isVisible");
		attemptHookMethod(DeviceAdminInfo.class, "loadDescription", PackageManager.class);
		attemptHookMethod(DeviceAdminInfo.class, "loadIcon", PackageManager.class);
		attemptHookMethod(DeviceAdminInfo.class, "loadLabel", PackageManager.class);
		attemptHookMethod(DeviceAdminInfo.class, "toString");
		attemptHookMethod(DeviceAdminInfo.class, "usesPolicy", int.class);
		attemptHookMethod(DeviceAdminInfo.class, "writeToParcel", Parcel.class, int.class);
		
		// DeviceAdminInfo Constants:
		// USES_POLICY_FORCE_LOCK
		// USES_POLICY_LIMIT_PASSWORD
		// USES_POLICY_RESET_PASSWORD
		// USES_POLICY_WATCH_LOGIN
		// USES_POLICY_WIPE_DATA
		// remember Fields
		
		attemptHookMethod(DevicePolicyManager.class, "getCurrentFailedPasswordAttempts");
		attemptHookMethod(DevicePolicyManager.class, "getMaximumFailedPasswordsForWipe", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPasswordMaximumLength", int.class);
		attemptHookMethod(DevicePolicyManager.class, "getPasswordMinimumLength", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPasswordQuality", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "isActivePasswordSufficient");
		attemptHookMethod(DevicePolicyManager.class, "isAdminActive", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "removeActiveAdmin", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "resetPassword", String.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setMaximumFailedPasswordsForWipe", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setMaximumTimeToLock", ComponentName.class, long.class);
		attemptHookMethod(DevicePolicyManager.class, "setPasswordMinimumLength", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setPasswordQuality", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "wipeData", int.class);
		
		// DevicePolicyManager Constants:
		// ACTION_ADD_DEVICE_ADMIN
		// ACTION_SET_NEW_PASSWORD
		// EXTRA_ADD_EXPLANATION
		// EXTRA_DEVICE_ADMIN
		// PASSWORD_QUALITY_ALPHABETIC
		// PASSWORD_QUALITY_ALPHANUMERIC
		// PASSWORD_QUALITY_NUMERIC
		// PASSWORD_QUALITY_SOMETHING
		// PASSWORD_QUALITY_UNSPECIFIED
		// RESET_PASSWORD_REQUIRE_ENTRY
		
		// TODO: hooks into anything but "onReceive" will be overridden
		// Additionally "onReceive" could be overriden causing us to lose control of the receiver
		attemptHookMethod(DeviceAdminReceiver.class, "getManager", Context.class);
		attemptHookMethod(DeviceAdminReceiver.class, "getWho", Context.class);
		attemptHookMethod(DeviceAdminReceiver.class, "onDisableRequested", Context.class, Intent.class);
		attemptHookMethod(DeviceAdminReceiver.class, "onDisabled", Context.class, Intent.class);
		attemptHookMethod(DeviceAdminReceiver.class, "onEnabled", Context.class, Intent.class);
		attemptHookMethod(DeviceAdminReceiver.class, "onPasswordChanged", Context.class, Intent.class);
		attemptHookMethod(DeviceAdminReceiver.class, "onPasswordFailed", Context.class, Intent.class);
		attemptHookMethod(DeviceAdminReceiver.class, "onPasswordSucceeded", Context.class, Intent.class);
		attemptHookMethod(DeviceAdminReceiver.class, "onReceive", Context.class, Intent.class);
		
		// DeviceAdminReceiver Constants:
		// ACTION_DEVICE_ADMIN_DISABLED
		// ACTION_DEVICE_ADMIN_DISABLE_REQUESTED
		// ACTION_DEVICE_ADMIN_ENABLED
		// ACTION_PASSWORD_CHANGED
		// ACTION_PASSWORD_FAILED
		// ACTION_PASSWORD_SUCCEEDED
		// DEVICE_ADMIN_META_DATA
		// EXTRA_DISABLE_WARNING
	}
	
	private static XC_MethodHook passThough(final String name) {
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
	
	private void attemptHookMethod(Class<?> clazz, String methodName, Class... args) {
		attemptHookMethod(clazz, methodName, passThough(methodName), args);
	}
	
	private void attemptHookMethod(Class<?> clazz, String methodName, XC_MethodHook hook, Class... args) {
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
