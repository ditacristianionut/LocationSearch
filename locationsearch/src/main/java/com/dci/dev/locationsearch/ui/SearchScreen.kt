package com.dci.dev.locationsearch.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dci.dev.locationsearch.R
import com.dci.dev.locationsearch.domain.model.Location

data class ColorPalette(
    val primaryAccentColor: Color,
    val secondaryAccentColor: Color,
    val primaryTextColor: Color,
    val secondaryTextColor: Color,
    val surfaceColor: Color,
    val backgroundColor: Color,
    val errorColor: Color,
)

private val colorPaletteOne = ColorPalette(
    primaryAccentColor = Color(0xFF0077C0),
    secondaryAccentColor = Color(0xFFC7EEFF),
    primaryTextColor = Color(0xFF1D242B),
    secondaryTextColor = Color(0xFFadb5bd),
    surfaceColor = Color(0xFFF2F2F2),
    backgroundColor = Color(0xFFFFFFFF),
    errorColor = Color(0xFFef233c)
)

private val colorPalette: ColorPalette
    get() = colorPaletteOne

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchQuery: String,
    searchResults: List<Location>,
    screenState: ScreenState,
    onSearchQueryChange: (String) -> Unit,
    search: () -> Unit,
    onItemSelected: (Location) -> Unit,
    onBackPressed: () -> Unit = {}
) {

    val showResults = remember(searchResults) {
        searchResults.isNotEmpty()
    }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var isFocused by remember {
        mutableStateOf(false)
    }

    val isLoading = screenState == ScreenState.Loading

    val isInSearchMode = isFocused || isLoading || searchResults.isNotEmpty()

    val backgroundColor by animateColorAsState(
        targetValue = if (isInSearchMode) Color.White else colorPalette.surfaceColor,
        animationSpec = tween(
            durationMillis = 150,
            easing = LinearEasing
        ),
        label = ""
    )
    val outlineColor by animateColorAsState(
        targetValue = if (isInSearchMode) colorPalette.secondaryTextColor else Color.Transparent,
        animationSpec = tween(
            durationMillis = 150,
            easing = LinearEasing
        ),
        label = ""
    )
    val leadingIconColor by animateColorAsState(
        targetValue = if (isInSearchMode) colorPalette.primaryTextColor else colorPalette.secondaryTextColor,
        animationSpec = tween(
            durationMillis = 150,
            easing = LinearEasing
        ),
        label = ""
    )
    val radius by animateDpAsState(
        targetValue = if (isInSearchMode) 0.dp else 8.dp,
        animationSpec = tween(
            durationMillis = 150,
            easing = LinearEasing
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorPalette.backgroundColor)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Icon(
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBackPressed() },
                    imageVector = Icons.Default.ArrowBack,
                    tint = colorPalette.primaryTextColor,
                    contentDescription = null
                )

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            isFocused = it.isFocused
                        },
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    textStyle = TextStyle(
                        color = colorPalette.primaryTextColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    ),
                    maxLines = 1,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            tint = leadingIconColor,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(visible = searchQuery.length >= 3) {

                            val alpha = if (isLoading) 0.5f else 1f

                            Row(
                                modifier = Modifier
                                    .width(IntrinsicSize.Min)
                                    .alpha(alpha),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {

                                IconButton(
                                    onClick = {
                                        if (!isLoading) onSearchQueryChange("")
                                    }
                                ) {

                                    Icon(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color.LightGray)
                                            .padding(2.dp)
                                            .alpha(0.8f),
                                        imageVector = Icons.Default.Close,
                                        tint = colorPalette.primaryTextColor,
                                        contentDescription = null
                                    )
                                }

                                IconButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(34.dp)
                                        .background(
                                            color = colorPalette.primaryAccentColor,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(vertical = 6.dp, horizontal = 12.dp)
                                        .weight(0.2f, true),
                                    onClick = {
                                        if (!isLoading) {
                                            focusManager.clearFocus()
                                            search()
                                        }
                                    }
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.search),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.search_hint),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        placeholderColor = colorPalette.secondaryTextColor,
                        cursorColor = colorPalette.secondaryTextColor,
                        containerColor = backgroundColor,
                        unfocusedLeadingIconColor = colorPalette.secondaryTextColor,
                        focusedLeadingIconColor = Color.Transparent,
                        unfocusedIndicatorColor = outlineColor,
                        focusedIndicatorColor = outlineColor
                    ),
                    shape = RoundedCornerShape(radius)
                )
            }

            AnimatedVisibility(visible = isFocused && searchQuery.length < 3) {
                Text(
                    modifier = Modifier
                        .padding(top = 6.dp, start = 52.dp)
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.location_hint_minimum_size),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorPalette.errorColor,
                    textAlign = TextAlign.Start
                )
            }

            AnimatedVisibility(
                visible = screenState == ScreenState.Idle,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.fillMaxWidth(0.65f),
                        contentScale = ContentScale.Inside,
                        painter = painterResource(id = R.drawable.lib_map_isometric),
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(
                visible = screenState == ScreenState.Error,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier.fillMaxWidth(0.65f),
                        contentScale = ContentScale.Inside,
                        painter = painterResource(id = R.drawable.lib_page_not_found),
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.lib_api_error),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorPalette.errorColor,
                        textAlign = TextAlign.Center
                    )
                }
            }

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 4 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 4 })
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation()
                }
            }

            AnimatedVisibility(
                visible = showResults && !isLoading,
                enter = fadeIn(animationSpec = tween(durationMillis = 375)) +
                        expandVertically(animationSpec = tween(durationMillis = 375))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    items(searchResults) { item ->
                        SearchResultRowView(
                            location = item,
                            onClick = onItemSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultRowView(
    location: Location,
    onClick: (Location) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    radius = 8.dp,
                    color = colorPalette.secondaryAccentColor.copy(alpha = 0.7f)
                )
            ) { onClick(location) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = colorPalette.secondaryAccentColor,
                    shape = CircleShape
                )
                .padding(6.dp)
            ,
            imageVector = Icons.Outlined.LocationOn,
            tint = colorPalette.primaryTextColor,
            contentDescription = null
        )

        Text(
            text = location.niceName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = colorPalette.primaryTextColor
        )
    }
}


@Preview
@Composable
fun LoadingAnimation(
    modifier: Modifier = Modifier,
    indicatorSize: Dp = 48.dp,
    circleColors: List<Color> = listOf(
        Color(0xFF5851D8),
        Color(0xFF833AB4),
        Color(0xFFC13584),
        Color(0xFFE1306C),
        Color(0xFFFD1D1D),
        Color(0xFFF56040),
        Color(0xFFF77737),
        Color(0xFFFCAF45),
        Color(0xFFFFDC80),
        Color(0xFF5851D8)
    ),
    animationDuration: Int = 1000
) {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val rotateAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    CircularProgressIndicator(
        modifier = modifier
            .size(size = indicatorSize)
            .rotate(degrees = rotateAnimation)
            .border(
                width = 4.dp,
                brush = Brush.sweepGradient(circleColors),
                shape = CircleShape
            ),
        progress = 1f,
        strokeWidth = 2.dp,
    )
}

@Preview
@Composable
fun SearchResultRowPreview() {
    val location = Location(
        id = 0,
        name = "Barcelona",
        region = "Catalonia",
        country = "Romania",
        latitude = 44.0,
        longitude = 22.5,
        remoteId = null
    )
    SearchResultRowView(location) {}
}

@Preview
@Composable
fun SearchScreenIdlePreview() {
    SearchScreen(
        searchQuery = "",
        searchResults = listOf(),
        onSearchQueryChange = {},
        search = {},
        screenState = ScreenState.Idle,
        onItemSelected = {}
    )
}

@Preview
@Composable
fun SearchScreenLoadingPreview() {
    SearchScreen(
        searchQuery = "",
        searchResults = listOf(),
        onSearchQueryChange = {},
        search = {},
        screenState = ScreenState.Loading,
        onItemSelected = {}
    )
}

@Preview
@Composable
fun SearchScreenErrorPreview() {
    SearchScreen(
        searchQuery = "",
        searchResults = listOf(),
        onSearchQueryChange = {},
        search = {},
        screenState = ScreenState.Error,
        onItemSelected = {}
    )
}

@Preview
@Composable
fun SearchScreenPreview() {
    val location = Location(
        id = 0,
        name = "Barcelona",
        region = "Catalonia",
        country = "Romania",
        latitude = 44.0,
        longitude = 22.5,
        remoteId = null,
    )

    SearchScreen(
        searchQuery = "Barc",
        searchResults = listOf(location, location, location, location),
        onSearchQueryChange = {},
        search = {},
        screenState = ScreenState.Success,
        onItemSelected = {}
    )
}
