package com.br.scancard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.systemBars)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CreditCardView()

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonColors(
                containerColor = Color(0xFF2478ED),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF2478ED),
                disabledContentColor = Color.White
            )
        ) {
            Text(text = "Scan Card")
        }
    }
}

@Composable
fun CreditCardView(
    cardNumber: String = "1111 2222 3333 4444",
    cardholderName: String = "MARIA SILVA",
    expiryDate: String = "07/30",
    cvv: String = "123",
    flagResId: Int? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(184.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFC7C7C7),
                        Color(0xFF666666)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Card Flag Image
            if (flagResId != null) {
                Image(
                    painter = painterResource(id = flagResId),
                    contentDescription = "Card flag",
                    modifier = Modifier
                        .height(24.dp)
                        .padding(bottom = 20.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(44.dp))
            }

            // Card Number
            Text(
                text = cardNumber,
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Bottom Row: Name, Validity, CVV
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Name Section
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                ) {
                    Text(
                        text = "Name",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = cardholderName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Validity Section
                Column(
                    modifier = Modifier.padding(end = 28.dp)
                ) {
                    Text(
                        text = "Validity",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = expiryDate,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // CVV Section
                Column {
                    Text(
                        text = "CVV",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = cvv,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}