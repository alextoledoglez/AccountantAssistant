package com.personal.accountantAssistant.ui.menu.components

import android.widget.ImageView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.ui.theme.Dimens

@Composable
fun UserDataSection(user: UserModel?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AndroidView(
            factory = { ctx ->
                ImageView(ctx).apply {
                    minimumWidth = Dimens.iconSize.value.toInt()
                    minimumHeight = Dimens.iconSize.value.toInt()
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            },
            update = { imageView ->
                Glide.with(context)
                    .load(user?.photoPath?.toUri())
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .circleCrop()
                    .into(imageView)
            },
            modifier = Modifier
                .size(Dimens.iconSize)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Dimens.spacingSm)
        ) {
            Text(
                text = user?.name.orEmpty().uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = Dimens.textMd,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = user?.email.orEmpty(),
                fontSize = Dimens.textMd,
                fontWeight = FontWeight.Bold
            )
        }
    }
}