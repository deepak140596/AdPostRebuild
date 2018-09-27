package com.sapicons.deepak.k2psap.Fragments

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.sapicons.deepak.k2psap.Activities.NavigationActivity

import com.sapicons.deepak.k2psap.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_post.*

class PostFragment : Fragment() {

    var mContext : Context ?= null
    var imgUrlOne = ""
    var imgUrlTwo = ""
    var imgUrlThree = ""
    var imgUrlFour = ""
    var imgUrlFive = ""

    var imgUriOne : Uri? = null
    var imgUriTwo : Uri? = null
    var imgUriThree : Uri? = null
    var imgUriFour : Uri? = null
    var imgUriFive : Uri? = null

    var imgClicked = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity.title = "Post an Ad"
        // Inflate the layout for this fragment
        mContext = activity

        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db= FirebaseFirestore.getInstance()
        val docRef = db.collection("categories")

        initialiseUI(view)

    }

    fun initialiseUI(view: View?){

        post_image_one_ib.setOnClickListener({
            imgClicked = 1
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this.activity,this@PostFragment)
        })

        post_image_two_ib.setOnClickListener({
            imgClicked = 2
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(activity,this@PostFragment)

        })
        post_image_three_ib.setOnClickListener({
            imgClicked = 3
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(activity,this@PostFragment)
        })
        post_image_four_ib.setOnClickListener({
            imgClicked = 4
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(activity,this@PostFragment)
        })
        post_image_five_ib.setOnClickListener({
            imgClicked = 5
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(activity,this@PostFragment)
        })


        post_done_fab.setOnClickListener({
            Toasty.success(this.activity,"clicked FAB").show()
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            var result = CropImage.getActivityResult(data)
            if(resultCode == RESULT_OK){

                var picUri = result.uri
                setUriForImages(picUri)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d("URI", "Result Error: ${result.error}")
            }
        }

    }


    fun setUriForImages(picUri: Uri){

        Log.d("POST_FRAG", "URI: $picUri")

        when (imgClicked) {
            1 -> {
                imgUriOne = picUri
                Glide.with(this.activity).load(picUri).into(post_image_one_ib)
            }
            2 -> {
                imgUriTwo = picUri
                Glide.with(this.activity).load(picUri).into(post_image_two_ib)
            }
            3 -> {
                imgUriThree = picUri
                Glide.with(this.activity).load(picUri).into(post_image_three_ib)
            }
            4 -> {
                imgUriFour = picUri
                Glide.with(this.activity).load(picUri).into(post_image_four_ib)
            }
            5 -> {
                imgUriFive = picUri
                Glide.with(this.activity).load(picUri).into(post_image_five_ib)
            }
        }
    }
}
