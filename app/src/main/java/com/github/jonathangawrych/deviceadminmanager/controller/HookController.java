package com.github.jonathangawrych.deviceadminmanager.controller;

import android.annotation.TargetApi;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.util.Log;
import android.util.Printer;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.List;

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
		HookGingerbread();
		HookHoneycomb();
		HookIceCreamSandwich();
		HookJellyBean();
		// Kitkat had no api changes to device admin
		HookLollipop();
		HookMarshmallow();
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
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void HookGingerbread() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD)
			return;
		
		Log.d(TAG, "Hooking Gingerbread Admin Methods");
		
		// DevicePolicyManager Constants:
		// WIPE_EXTERNAL_STORAGE
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void HookHoneycomb() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			return;
		
		Log.d(TAG, "Hooking Honeycomb Admin Methods");
		
		// DeviceAdminInfo Constants:
		// USES_ENCRYPTED_STORAGE
		// USES_POLICY_EXPIRE_PASSWORD
		
		attemptHookMethod(DevicePolicyManager.class, "getPasswordExpiration", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPasswordExpirationTimeout", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPasswordHistoryLength", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPasswordMinimumLetters", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPasswordMinimumLowerCase", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPasswordMinimumNonLetter", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPasswordMinimumNumeric", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPasswordMinimumSymbols", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPasswordMinimumUpperCase", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getStorageEncryption", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getStorageEncryptionStatus");
		attemptHookMethod(DevicePolicyManager.class, "hasGrantedPolicy", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setPasswordExpirationTimeout", ComponentName.class, long.class);
		attemptHookMethod(DevicePolicyManager.class, "setPasswordHistoryLength", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setPasswordMinimumLetters", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setPasswordMinimumLowerCase", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setPasswordMinimumNonLetter", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setPasswordMinimumNumeric", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setPasswordMinimumSymbols", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setPasswordMinimumUpperCase", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setStorageEncryption", ComponentName.class, boolean.class);
		
		// DevicePolicyManager Constants:
		// ACTION_START_ENCRYPTION
		// ENCRYPTION_STATUS_ACTIVATING
		// ENCRYPTION_STATUS_ACTIVE
		// ENCRYPTION_STATUS_INACTIVE
		// ENCRYPTION_STATUS_UNSUPPORTED
		// PASSWORD_QUALITY_COMPLEX
		
		attemptHookMethod(DeviceAdminReceiver.class, "onPasswordExpiring", ComponentName.class, Intent.class);
		
		// DeviceAdminReceiver Constants:
		// ACTION_PASSWORD_EXPIRING
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void HookIceCreamSandwich() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			return;
		
		Log.d(TAG, "Hooking IceCreamSandwich Admin Methods");
		
		// DeviceAdminInfo Constants:
		// USES_POLICY_DISABLE_CAMERA
		
		attemptHookMethod(DevicePolicyManager.class, "getCameraDisabled", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "setCameraDisabled", ComponentName.class, boolean.class);
		
		// DevicePolicyManager Constants:
		// PASSWORD_QUALITY_BIOMETRIC_WEAK
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void HookJellyBean() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
			return;
		
		// DeviceAdminInfo Constants:
		// USES_POLICY_DISABLE_KEYGUARD_FEATURES
		
		Log.d(TAG, "Hooking JellyBean Admin Methods");
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
			return;
		
		Log.v(TAG, "Hooking JellyBean MR1 Admin Methods");
		
		attemptHookMethod(DevicePolicyManager.class, "getKeyguardDisabledFeatures", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "setKeyguardDisabledFeatures", ComponentName.class, int.class);
		
		// DevicePolicyManager Constants:
		// KEYGUARD_DISABLE_FEATURES_ALL
		// KEYGUARD_DISABLE_FEATURES_NONE
		// KEYGUARD_DISABLE_SECURE_CAMERA
		// KEYGUARD_DISABLE_WIDGETS_ALL
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
			return;
		
		Log.v(TAG, "Hooking JellyBean MR2 Admin Methods");
		
		attemptHookMethod(DevicePolicyManager.class, "isDeviceOwnerApp", ComponentName.class);
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void HookLollipop() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
			return;
		
		Log.d(TAG, "Hooking Lollipop Admin Methods");
		
		attemptHookMethod(DevicePolicyManager.class, "addCrossProfileIntentFilter", ComponentName.class, IntentFilter.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "addCrossProfileWidgetProvider", ComponentName.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "addPersistentPreferredActivity", ComponentName.class, IntentFilter.class, ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "addUserRestriction", ComponentName.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "clearCrossProfileIntentFilters", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "clearDeviceOwnerApp", String.class);
		attemptHookMethod(DevicePolicyManager.class, "clearPackagePersistentPreferredActivities", ComponentName.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "clearUserRestriction", ComponentName.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "enableSystemApp", ComponentName.class, IntentFilter.class);
		attemptHookMethod(DevicePolicyManager.class, "enableSystemApp", ComponentName.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "getAccountTypesWithManagementDisabled");
		attemptHookMethod(DevicePolicyManager.class, "getApplicationRestrictions", ComponentName.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "getAutoTimeRequired");
		attemptHookMethod(DevicePolicyManager.class, "getCrossProfileCallerIdDisabled", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getCrossProfileWidgetProviders", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getInstalledCaCerts", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPermittedAccessibilityServices", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPermittedInputMethods", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getScreenCaptureDisabled", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "hasCaCertInstalled", ComponentName.class, byte[].class);
		attemptHookMethod(DevicePolicyManager.class, "installCaCert", ComponentName.class, byte[].class);
		attemptHookMethod(DevicePolicyManager.class, "installKeyPair", ComponentName.class, PrivateKey.class, Certificate.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "isApplicationHidden", ComponentName.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "isLockTaskPermitted", String.class);
		attemptHookMethod(DevicePolicyManager.class, "isMasterVolumeMuted", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "isProfileOwnerApp", String.class);
		attemptHookMethod(DevicePolicyManager.class, "isUninstallBlocked", ComponentName.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "removeCrossProfileWidgetProvider", ComponentName.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "removeUser", ComponentName.class, UserHandle.class);
		attemptHookMethod(DevicePolicyManager.class, "setAccountManagementDisabled", ComponentName.class, String.class, boolean.class);
		attemptHookMethod(DevicePolicyManager.class, "setApplicationHidden", ComponentName.class, String.class, boolean.class);
		attemptHookMethod(DevicePolicyManager.class, "setApplicationRestrictions", ComponentName.class, String.class, Bundle.class);
		attemptHookMethod(DevicePolicyManager.class, "setAutoTimeRequired", ComponentName.class, boolean.class);
		attemptHookMethod(DevicePolicyManager.class, "setCrossProfileCallerIdDisabled", ComponentName.class, boolean.class);
		attemptHookMethod(DevicePolicyManager.class, "setGlobalSetting", ComponentName.class, String.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "setLockTaskPackages", ComponentName.class, String.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "setMasterVolumeMuted", ComponentName.class, boolean.class);
		attemptHookMethod(DevicePolicyManager.class, "setPermittedAccessibilityServices", ComponentName.class, List.class);
		attemptHookMethod(DevicePolicyManager.class, "setPermittedInputMethods", ComponentName.class, List.class);
		attemptHookMethod(DevicePolicyManager.class, "setProfileEnabled", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "setProfileName", ComponentName.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "setRecommendedGlobalProxy", ComponentName.class, ProxyInfo.class);
		attemptHookMethod(DevicePolicyManager.class, "setRestrictionsProvider", ComponentName.class, ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "setScreenCaptureDisabled", ComponentName.class, boolean.class);
		attemptHookMethod(DevicePolicyManager.class, "setSecureSetting", ComponentName.class, String.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "setUninstallBlocked", ComponentName.class, String.class, boolean.class);
		attemptHookMethod(DevicePolicyManager.class, "switchUser", ComponentName.class, UserHandle.class);
		attemptHookMethod(DevicePolicyManager.class, "uninstallAllUserCaCerts", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "uninstallCaCert", ComponentName.class, byte[].class);
		
		// DevicePolicyManager Constants:
		// ACTION_PROVISION_MANAGED_PROFILE
		// EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE
		// EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM
		// EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_COOKIE_HEADER
		// EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION
		// EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME
		// EXTRA_PROVISIONING_EMAIL_ADDRESS
		// EXTRA_PROVISIONING_LOCALE
		// EXTRA_PROVISIONING_LOCAL_TIME
		// EXTRA_PROVISIONING_TIME_ZONE
		// EXTRA_PROVISIONING_WIFI_HIDDEN
		// EXTRA_PROVISIONING_WIFI_PAC_URL
		// EXTRA_PROVISIONING_WIFI_PASSWORD
		// EXTRA_PROVISIONING_WIFI_PROXY_BYPASS
		// EXTRA_PROVISIONING_WIFI_PROXY_HOST
		// EXTRA_PROVISIONING_WIFI_PROXY_PORT
		// EXTRA_PROVISIONING_WIFI_SECURITY_TYPE
		// EXTRA_PROVISIONING_WIFI_SSID
		// FLAG_MANAGED_CAN_ACCESS_PARENT
		// FLAG_PARENT_CAN_ACCESS_MANAGED
		// KEYGUARD_DISABLE_FINGERPRINT
		// KEYGUARD_DISABLE_SECURE_NOTIFICATIONS
		// KEYGUARD_DISABLE_TRUST_AGENTS
		// KEYGUARD_DISABLE_UNREDACTED_NOTIFICATIONS
		// MIME_TYPE_PROVISIONING_NFC
		// PASSWORD_QUALITY_NUMERIC_COMPLEX
		
		attemptHookMethod(DeviceAdminReceiver.class, "onLockTaskModeEntering", ComponentName.class, Intent.class, String.class);
		attemptHookMethod(DeviceAdminReceiver.class, "onLockTaskModeExiting", ComponentName.class, Intent.class);
		attemptHookMethod(DeviceAdminReceiver.class, "onProfileProvisioningComplete", ComponentName.class, Intent.class);
		
		// DeviceAdminReceiver Constants:
		// ACTION_LOCK_TASK_ENTERING
		// ACTION_LOCK_TASK_EXITING
		// ACTION_PROFILE_PROVISIONING_COMPLETE
		// EXTRA_LOCK_TASK_PACKAGE
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1)
			return;
		
		Log.v(TAG, "Hooking Lollipop MR1 Admin Methods");
		
		// DevicePolicyManager Constants:
		// EXTRA_PROVISIONING_ACCOUNT_TO_MIGRATE
		// EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED
		// WIPE_RESET_PROTECTION_DATA
	}
	
	@TargetApi(Build.VERSION_CODES.M)
	private void HookMarshmallow() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
			return;
		
		Log.d(TAG, "Hooking Marshmallow Admin Methods");
		
		attemptHookMethod(DevicePolicyManager.class, "getBluetoothContactSharingDisabled", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getCertInstallerPackage", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getPermissionGrantState", ComponentName.class, String.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "getPermissionPolicy", ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "getSystemUpdatePolicy");
		attemptHookMethod(DevicePolicyManager.class, "getTrustAgentConfiguration", ComponentName.class, ComponentName.class);
		attemptHookMethod(DevicePolicyManager.class, "setBluetoothContactSharingDisabled", ComponentName.class, boolean.class);
		attemptHookMethod(DevicePolicyManager.class, "setCertInstallerPackage", ComponentName.class, String.class);
		attemptHookMethod(DevicePolicyManager.class, "setKeyguardDisabled", ComponentName.class, boolean.class);
		attemptHookMethod(DevicePolicyManager.class, "setPermissionGrantState", ComponentName.class, String.class, String.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setPermissionPolicy", ComponentName.class, int.class);
		attemptHookMethod(DevicePolicyManager.class, "setStatusBarDisabled", ComponentName.class, boolean.class);
		attemptHookMethod(DevicePolicyManager.class, "setSystemUpdatePolicy", ComponentName.class, SystemUpdatePolicy.class);
		attemptHookMethod(DevicePolicyManager.class, "setTrustAgentConfiguration", ComponentName.class, ComponentName.class, PersistableBundle.class);
		attemptHookMethod(DevicePolicyManager.class, "setUserIcon", ComponentName.class, Bitmap.class);
		
		// DevicePolicyManager Constants:
		// EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE
		// EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM
		// EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_COOKIE_HEADER
		// EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION
		// EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME
		// EXTRA_PROVISIONING_EMAIL_ADDRESS
		// EXTRA_PROVISIONING_LOCALE
		// EXTRA_PROVISIONING_LOCAL_TIME
		// EXTRA_PROVISIONING_TIME_ZONE
		// EXTRA_PROVISIONING_WIFI_HIDDEN
		// EXTRA_PROVISIONING_WIFI_PAC_URL
		// EXTRA_PROVISIONING_WIFI_PASSWORD
		// EXTRA_PROVISIONING_WIFI_PROXY_BYPASS
		// EXTRA_PROVISIONING_WIFI_PROXY_HOST
		// EXTRA_PROVISIONING_WIFI_PROXY_PORT
		// EXTRA_PROVISIONING_WIFI_SECURITY_TYPE
		// EXTRA_PROVISIONING_WIFI_SSID
		// FLAG_MANAGED_CAN_ACCESS_PARENT
		// FLAG_PARENT_CAN_ACCESS_MANAGED
		// KEYGUARD_DISABLE_FINGERPRINT
		// KEYGUARD_DISABLE_SECURE_NOTIFICATIONS
		// KEYGUARD_DISABLE_TRUST_AGENTS
		// KEYGUARD_DISABLE_UNREDACTED_NOTIFICATIONS
		// MIME_TYPE_PROVISIONING_NFC
		// PASSWORD_QUALITY_NUMERIC_COMPLEX
		
		attemptHookMethod(DeviceAdminReceiver.class, "onChoosePrivateKeyAlias", Context.class, Intent.class, int.class, Uri.class, String.class);
		attemptHookMethod(DeviceAdminReceiver.class, "onReadyForUserInitialization", Context.class, Intent.class);
		attemptHookMethod(DeviceAdminReceiver.class, "onSystemUpdatePending", Context.class, Intent.class, long.class);
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
