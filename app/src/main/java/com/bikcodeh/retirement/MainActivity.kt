package com.bikcodeh.retirement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bikcodeh.retirement.databinding.ActivityMainBinding
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import java.lang.Exception
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        AppCenter.start(
            application,
            "10a41918-af79-4746-8add-2b8b3c772d1e",
            Analytics::class.java,
            Crashes::class.java
        )

        with(_binding) {
            calculateButton.setOnClickListener {
                val interestRate = interestEditText.text.toString().toFloat()
                val currentAge = ageEditText.text.toString().toInt()
                val retirementAge = retirementEditText.text.toString().toInt()
                val monthly = monthlySavingsEditText.text.toString().toFloat()
                val current = currentEditText.text.toString().toFloat()

                val properties: HashMap<String, String> = HashMap<String, String>().apply {
                    put("interest_rate", interestRate.toString())
                    put("current_age", currentAge.toString())
                    put("retirement_age", retirementAge.toString())
                    put("monthly", monthly.toString())
                    put("current", current.toString())
                }

                if (interestRate <= 0) {
                    Analytics.trackEvent("wrong_interest_rate", properties)
                }

                if (retirementAge <= currentAge) {
                    Analytics.trackEvent("wrong_age", properties)
                }

                val futureSavings = calculateRetirement(
                    interestRate,
                    current,
                    monthly,
                    (retirementAge - currentAge) * 12
                )
                resultTextView.text =
                    "At the current rate of $interestRate, saving $monthly with your current monthly savings you will have \$${
                        String.format(
                            "%f",
                            futureSavings
                        )
                    } by $retirementAge."
            }
        }
    }

    private fun calculateRetirement(
        interestRate: Float,
        currentSavings: Float,
        monthly: Float,
        numMonths: Int
    ): Float {
        var futureSavings = currentSavings * (1 + (interestRate / 100 / 12)).pow(numMonths)
        for (i in 1..numMonths) {
            futureSavings += monthly * (1 + (interestRate / 100 / 12)).pow(i)
        }
        return futureSavings
    }
}
