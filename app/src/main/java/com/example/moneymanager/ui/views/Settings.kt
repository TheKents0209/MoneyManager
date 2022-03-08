package com.example.moneymanager.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.chillibits.simplesettings.core.SimpleSettings
import com.chillibits.simplesettings.item.SimpleSwitchPreference

@Composable
fun SettingsScreen() {
    Column(Modifier.fillMaxSize()) {
            Text(text = "SettingsScreen")
        SimpleSettings(LocalContext.current).show {
            Section {
                title = "Test section"
                for (i in 1..4) {
                    SwitchPref {
                        title = "Test 1.$i"
                        summary = "This is a Test 1.$i"
                        defaultValue = if(i % 2 == 0) SimpleSwitchPreference.ON else SimpleSwitchPreference.OFF
                    }
                }
                if(true) {
                    TextPref {
                        title = "Test 2"
                        summary = "This is a Test 2"
                    }
                }
                /*...*/
            }
            Section {
                InputPref {
                    title = "Test 3"
                    summary = "This is a Test 3"
                }
                /*...*/
            }
            /*...*/
        }
    }
}

