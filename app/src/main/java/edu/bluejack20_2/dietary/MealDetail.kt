package edu.bluejack20_2.dietary

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView

class MealDetail : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)
        var menuId = getIntent().getExtras()?.get("menuId") as String?

//        findViewById<OutlineTextView>(R.id.textView).setOutlineWidth(TypedValue.COMPLEX_UNIT_PX, 3f)
//        findViewById<OutlineTextView>(R.id.textView).setOutlineColor()

        Log.wtf("thismenuu", menuId)

        findViewById<ImageView>(R.id.back).setOnClickListener{
            finish()
        }


    }
}