package com.sapicons.deepak.k2psap.Others;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sapicons.deepak.k2psap.R;

/**
 * Created by Deepak Prasad on 22-01-2018.
 */

public class RateUs {

    // Insert your Application Title
    private final static String TITLE = "K2PB";

    // Insert your Application Package Name
    private final static String PACKAGE_NAME ="com.sapicons.deepak.k2psap";

    // TODO change prompt parameters while release

    // Day until the Rate Us Dialog Prompt(Default 2 Days)
    private final static int DAYS_UNTIL_PROMPT = 2;

    // App launches until Rate Us Dialog Prompt(Default 5 Launches)
    private final static int LAUNCHES_UNTIL_PROMPT = 7;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("rateus", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        Log.e("RATE LAUNCH COUNT",launch_count+"");
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        Log.e("RATE first launch",date_firstLaunch+"");
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= (date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000))) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

    public static void showRateDialog(final Context mContext,
                                      final SharedPreferences.Editor editor) {

        AlertDialog.Builder b=new AlertDialog.Builder(mContext);
        b.setTitle("Enjoying Book Xtore?");

        b.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog.Builder c=new AlertDialog.Builder(mContext);
                c.setTitle("How about rating on Playstore, then?")
                        .setPositiveButton("Ok, sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                        .parse("market://details?id=" + PACKAGE_NAME)));
                                if (editor != null) {
                                    editor.putBoolean("dontshowagain", true);
                                    editor.commit();
                                }
                            }
                        }).setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }
                    }
                })
                .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                c.create().show();
            }
        }).setNegativeButton("Not Really", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog.Builder c=new AlertDialog.Builder(mContext);
                c.setTitle("Would you mind giving us some feedback?")
                        .setPositiveButton("Ok, sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlertDialog.Builder d=new AlertDialog.Builder(mContext);
                                View customView= View.inflate(mContext, R.layout.custom_send_feedback,null);

                                final EditText body=(EditText)customView.findViewById(R.id.feedback_body_id);

                                d.setView(customView);
                                d.setTitle("Feedback").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {


                                    }
                                }).setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String postId = System.currentTimeMillis()+"";
                                        DocumentReference feedbacks= FirebaseFirestore.getInstance().
                                                collection("feedbacks").document(postId);
                                        String feedback_body=body.getText().toString();
                                        FeedbackItem item=new FeedbackItem(postId,feedback_body);

                                        feedbacks.set(item).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("RATE_US","Failed to send feedback. Error: "+e);
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (editor != null) {
                                                    editor.putBoolean("dontshowagain", true);
                                                    editor.commit();
                                                }
                                            }
                                        });

                                        Toast.makeText(mContext, "Thanks for Feedback!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                d.create().show();

                                /*if (editor != null) {
                                    editor.putBoolean("dontshowagain", true);
                                    editor.commit();
                                }*/
                            }
                }).setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }

                    }
                }).setNeutralButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                c.create().show();

            }

        });

        b.create().show();

    }
}

