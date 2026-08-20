package com.surgex.app.ui.screens.rider

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgex.app.ui.theme.SurgeBlack
import com.surgex.app.ui.theme.SurgeWhite

private const val PREF_PAYMENT_METHOD = "preferred_payment_method"

@Composable
fun PaymentMethodsScreen(
    preferences: SharedPreferences,
    onBack: () -> Unit
) {
    var selected by remember {
        mutableStateOf(preferences.getString(PREF_PAYMENT_METHOD, "CASH") ?: "CASH")
    }
    var showReminder by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurgeBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    color = SurgeWhite,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(end = 16.dp)
                )
                Text(
                    text = "Payment Methods",
                    color = SurgeWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Choose how you want to pay for rides",
                color = Color(0xFF888888),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(28.dp))

            PaymentOptionCard(
                title = "Cash",
                subtitle = "Pay the driver directly in cash",
                icon = "💵",
                selected = selected == "CASH",
                onClick = {
                    selected = "CASH"
                    preferences.edit().putString(PREF_PAYMENT_METHOD, "CASH").apply()
                    showReminder = true
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            PaymentOptionCard(
                title = "Card",
                subtitle = "Secure card payment (API ready)",
                icon = "💳",
                selected = selected == "CARD",
                onClick = {
                    selected = "CARD"
                    preferences.edit().putString(PREF_PAYMENT_METHOD, "CARD").apply()
                    showReminder = true
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF111111)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How commissions work",
                        color = SurgeWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Cash rides: 15% commission is deducted from the driver’s earnings (or waived if the driver is on the R500 weekly plan).\n\n• Card rides: Full fare is processed via card. Commission is handled automatically.",
                        color = Color(0xFFAAAAAA),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (showReminder) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0A1A0A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "✓", color = Color(0xFF76FF03), fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Payment method updated",
                                color = Color(0xFF76FF03),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "You will pay with ${if (selected == "CASH") "Cash" else "Card"} on your next ride.",
                                color = Color(0xFF4A7A00),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PaymentOptionCard(
    title: String,
    subtitle: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color(0xFF76FF03) else Color(0xFF222222),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        color = if (selected) Color(0xFF0F1A0F) else Color(0xFF121212),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = SurgeWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF888888),
                    fontSize = 13.sp
                )
            }
            if (selected) {
                Text(text = "✓", color = Color(0xFF76FF03), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

