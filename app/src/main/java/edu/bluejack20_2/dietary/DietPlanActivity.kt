package edu.bluejack20_2.dietary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class DietPlanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet_plan)

        findViewById<Button>(R.id.lose_weight_btn).setOnClickListener {
            val intent = Intent(this, BMIActivity::class.java)
            intent.putExtra("type", "Lose")
            startActivity(intent)
        }
        findViewById<Button>(R.id.maintain_weight_btn).setOnClickListener {
            val intent = Intent(this, BMIActivity::class.java)
            intent.putExtra("type", "Maintain")
            startActivity(intent)
        }
        findViewById<Button>(R.id.gain_weight_btn).setOnClickListener {
            val intent = Intent(this, BMIActivity::class.java)
            intent.putExtra("type", "Gain")
            startActivity(intent)
        }

    }
}