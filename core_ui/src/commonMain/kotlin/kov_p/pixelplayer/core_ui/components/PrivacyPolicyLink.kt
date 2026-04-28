package kov_p.pixelplayer.core_ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import kov_p.pixelplayer.core_design.pixelColors
import kov_p.pixelplayer.core_design.pixelTypography
import org.jetbrains.compose.resources.stringResource
import pixelplayer.core_ui.generated.resources.Res
import pixelplayer.core_ui.generated.resources.privacy_policy
import pixelplayer.core_ui.generated.resources.privacy_policy_url

@Composable
fun PrivacyPolicyLink(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUrl = stringResource(Res.string.privacy_policy_url)
    Text(
        modifier = modifier,
        textAlign = TextAlign.Center,
        text = buildAnnotatedString {
            withLink(
                LinkAnnotation.Url(
                    url = privacyPolicyUrl,
                    styles = TextLinkStyles(style = SpanStyle(color = Color(0xff3474eb))),
                    linkInteractionListener = { uriHandler.openUri(privacyPolicyUrl) },
                )
            ) {
                append(stringResource(Res.string.privacy_policy))
            }
        },
        style = pixelTypography.bodyLarge,
        color = pixelColors.onBackground,
    )
}
