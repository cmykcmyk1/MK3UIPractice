package com.cmykcmyk.mk3uipractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cmykcmyk.mk3uipractice.ui.theme.MK3UIPracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*val locale = Locale("en", "US")
        Locale.setDefault(locale)
        val configuration = applicationContext.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        applicationContext.createConfigurationContext(configuration)*/



        setContent {
            MK3UIPracticeTheme {
                RootScreen()
            }
        }
    }
}
