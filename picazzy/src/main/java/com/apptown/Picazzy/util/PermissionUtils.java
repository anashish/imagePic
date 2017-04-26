package com.apptown.Picazzy.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.apptown.Picazzy.R;

import java.util.ArrayList;




/**
 * Created by ${="Ashish"} on 28/6/16.
 */
public class PermissionUtils {
    private String TAG = "Permission Utils";
    private String msgDefault = "Application required following permissions to move forward: ";
    private String msgPermissionDefault = "Permission required to go ahead";
    private ArrayList<String> permissionRequiredList;
    private ArrayList<String> explanationMsgList;
    private String[] permissionArray;
    /**
     * Private Instance Variable
     */
    private static PermissionUtils classInstance = null;

    /**
     * Private Constructor to make this class singleton
     */
    private PermissionUtils() {
    }

    /**
     * Method return the class instance
     *
     * @return PermissionUtils
     */
    public static PermissionUtils getInstance() {
        if (classInstance == null) {
            classInstance = new PermissionUtils();
        }
        return classInstance;
    }

    /**
     * Method call to check is permission granted or not
     *
     * @param context          - activity context
     * @param permissionString - manifest permission like: Manifest.permission.READ_CONTACTS
     * @return boolean
     */
    public boolean hasPermission(Context context, String permissionString) {
        return ContextCompat.checkSelfPermission(context, permissionString) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Method call for request permission
     *
     * @param activity         - activity
     * @param permissionString - manifest permission like: Manifest.permission.READ_CONTACTS
     * @param requestCode      - request code to recognize further
     */
    public void needPermission(final Activity activity, final String permissionString,
                               final int requestCode, final String permissionName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(activity, permissionString)) {
                permissionArray = new String[1];
                permissionArray[0] = permissionString;

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        permissionString)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    String dialogExplanationMsg = permissionName;
                    if (dialogExplanationMsg == null || dialogExplanationMsg.length() < 1) {
                        dialogExplanationMsg = msgPermissionDefault;
                    }else {
                        StringBuilder msgToShow = new StringBuilder();
                        msgToShow.append(msgDefault);
                        msgToShow.append(permissionName);
                        dialogExplanationMsg = msgToShow.toString();
                    }

                    showDialog(activity, dialogExplanationMsg,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    ActivityCompat.requestPermissions(activity, permissionArray, requestCode);
                                }
                            });
                } else {
//                    Logger.getInstance().log_info("No need for explanation dialog!");
                    Log.i(TAG, "No need for explanation dialog!");
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity, permissionArray, requestCode);
                }
            } else {
//                Logger.getInstance().log_info("Permission " + permissionString + " Already Granted!");
                Log.i(TAG, "Permission " + permissionString + " Already Granted!");
            }
        } else {
//            Logger.getInstance().log_info("No need to request permission programmatically!");
            Log.i(TAG, "No need to request permission programmatically!");
        }
    }

    /**
     * Method call for request many permission once - But not recommended by google
     *
     * @param activity         - activity
     * @param permissionString - manifest permission like: Manifest.permission.READ_CONTACTS
     * @param requestCode      - request code to recognize further
     */
    public void needPermission(final Activity activity, String[] permissionString,
                               final int requestCode, String[] permissionNames) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionString.length == permissionNames.length && permissionString.length > 0 && requestCode != 0) {

                permissionRequiredList = new ArrayList<>();
                explanationMsgList = new ArrayList<>();
                for (int x = 0; x < permissionString.length; x++) {
                    if (!hasPermission(activity, permissionString[x])) {
                        permissionRequiredList.add(permissionString[x]);
                        explanationMsgList.add(permissionNames[x]);
                    }
                }

                if(permissionRequiredList.size() < 1){
                    return;
                }

                StringBuilder msgToShow = new StringBuilder();
                msgToShow.append(msgDefault);

                boolean shouldShowDialog = false;

                for (int y = 0; y < explanationMsgList.size(); y++) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionRequiredList.get(y))) {
                        msgToShow.append(explanationMsgList.get(y));
                        msgToShow.append(", ");
                        shouldShowDialog = true;
                    }
                }
                msgToShow.delete(msgToShow.length() - 2, msgToShow.length());
                if (shouldShowDialog) {
                    showDialog(activity, msgToShow.toString(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(activity, permissionRequiredList.toArray(new String[]{}), requestCode);

                                    explanationMsgList = null;
                                    permissionRequiredList = null;
                                }
                            });
                } else {

//                    Logger.getInstance().log_info("No need for explanation dialog!");
                    Log.i(TAG, "No need for explanation dialog!");
                    ActivityCompat.requestPermissions(activity, permissionRequiredList.toArray(new String[]{}), requestCode);

                    explanationMsgList = null;
                    permissionRequiredList = null;
                }



            } else {
//                Logger.getInstance().log_error("size of permission, explanation msg and request code are not same!");
                Log.e(TAG, "size of permission, explanation msg and request code are not same!");
            }
        } else {
//            Logger.getInstance().log_info("No need to request permission programmatically!");
            Log.i(TAG, "No need to request permission programmatically!");
        }
    }

    private void showDialog(Context context, String message, DialogInterface.OnClickListener okListener) {
        try {
            new AlertDialog.Builder(context, R.style.Base_V21_Theme_AppCompat_Light_Dialog)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK", okListener)
                    /*.setNegativeButton(context.getResources().getString(R.string.label_cancel), null)*/
                    .create()
                    .show();
        } catch (Exception ex) {
//            Logger.getInstance().log_error("ErrorDetail while showing dialog!", ex);
            Log.e(TAG, "No need to request permission programmatically!");
        }
    }

    /**
     * Method call to set Explanation Dialog Message
     * @param msgDefault - Explanation message (Should be short)
     */
    public void setMsgDefault(String msgDefault) {
        this.msgDefault = msgDefault;
    }

    /**
     * Method call to set message, If permission Message is empty
     * @param msgPermissionDefault - String message if no permission message
     */
    public void setMsgPermissionDefault(String msgPermissionDefault) {
        this.msgPermissionDefault = msgPermissionDefault;
    }

    /**
     * Method call to set logger tag
     * @param TAG = String logger tag message
     */
    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    /**
     * Method call to clean object from memory
     */
    public void cleanObject() {
        classInstance = null;
    }

}
