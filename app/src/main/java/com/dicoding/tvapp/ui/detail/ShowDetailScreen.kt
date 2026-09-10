package com.dicoding.tvapp.ui.detail

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.dicoding.tvapp.data.model.CastMember
import com.dicoding.tvapp.data.model.Episode
import com.dicoding.tvapp.data.model.Season
import com.dicoding.tvapp.data.model.TvShowDetail
import com.dicoding.tvapp.ui.theme.AccentBlue
import com.dicoding.tvapp.ui.theme.DarkBackground
import com.dicoding.tvapp.ui.theme.DarkCard
import com.dicoding.tvapp.ui.theme.DarkCardBorder
import com.dicoding.tvapp.ui.theme.GreenBadgeBg
import com.dicoding.tvapp.ui.theme.GreenBadgeBorder
import com.dicoding.tvapp.ui.theme.GreenBadgeText
import com.dicoding.tvapp.ui.theme.PillPurple
import com.dicoding.tvapp.ui.theme.RatingBoxBg
import com.dicoding.tvapp.ui.theme.StarYellow
import com.dicoding.tvapp.ui.theme.TextMuted
import com.dicoding.tvapp.ui.theme.TextPrimary
import com.dicoding.tvapp.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    showId: Int,
    onBack: () -> Unit,
    viewModel: ShowDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(showId) {
        viewModel.loadShowDetail(showId)
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            val showTitle = when (val state = uiState) {
                is ShowDetailUiState.Success -> state.show.name
                else -> ""
            }

            TopAppBar(
                title = {
                    Text(
                        text = showTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    if (uiState is ShowDetailUiState.Success) {
                        val show = (uiState as ShowDetailUiState.Success).show
                        IconButton(onClick = { shareShow(context, show) }) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DarkCard)
                                    .border(1.dp, DarkCardBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = AccentBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ShowDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        color = AccentBlue,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ShowDetailUiState.Error -> {
                    DetailErrorContent(
                        message = state.message,
                        onRetry = { viewModel.loadShowDetail(showId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ShowDetailUiState.Success -> {
                    DetailContent(show = state.show)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(show: TvShowDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Poster Section with Gradient Overlay & Genre Pills
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            // Background Image
            AsyncImage(
                model = show.image?.original ?: show.image?.medium,
                contentDescription = show.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark Gradient Overlay at Bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                DarkBackground.copy(alpha = 0.5f),
                                DarkBackground
                            ),
                            startY = 60f
                        )
                    )
            )

            // Genre Chips floating at bottom of banner
            if (!show.genres.isNullOrEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    show.genres.forEach { genre ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PillPurple)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = genre,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Details Body
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            // Show Title
            Text(
                text = show.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata Row: Rating, Premiered, Runtime
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Rating Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RatingBoxBg)
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "★",
                            color = StarYellow,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "${show.rating?.average ?: "N/A"} ",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "/ 10",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                // Premiered Date
                show.premiered?.let { date ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📅 ", fontSize = 12.sp)
                        Text(
                            text = date,
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                // Runtime
                show.runtime?.let { runtime ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⏱️ ", fontSize = 12.sp)
                        Text(
                            text = "${runtime}m",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Synopsis Header with HTML Stripped Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Synopsis",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // HTML Stripped Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(GreenBadgeBg)
                        .border(1.dp, GreenBadgeBorder, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "✨",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "HTML Stripped",
                            color = GreenBadgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clean Synopsis Text
            val cleanSummary = show.summary?.let {
                HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
            } ?: "Tidak ada ringkasan sinopsis."

            Text(
                text = cleanSummary,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    InfoRow(label = "Status", value = show.status ?: "N/A")
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(label = "Network", value = show.network?.name ?: "N/A")
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(label = "Language", value = show.language ?: "N/A")
                }
            }

            // BONUS SECTION: Cast (Pemeran)
            val castList = show.embedded?.cast
            if (!castList.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                CastSection(castList = castList)
            }

            // BONUS SECTION: Seasons & Episodes (Musim & Episode)
            val seasons = show.embedded?.seasons
            val episodes = show.embedded?.episodes
            if (!seasons.isNullOrEmpty() && !episodes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                SeasonsAndEpisodesSection(seasons = seasons, episodes = episodes)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMuted, fontSize = 13.sp)
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// BONUS COMPOSABLE: Cast Section
@Composable
private fun CastSection(castList: List<CastMember>) {
    Column {
        Text(
            text = "Cast & Crew (${castList.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(castList.take(15)) { member ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    AsyncImage(
                        model = member.person.image?.medium ?: member.person.image?.original,
                        contentDescription = member.person.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(RatingBoxBg)
                            .border(2.dp, DarkCardBorder, CircleShape)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = member.person.name,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = member.character.name,
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// BONUS COMPOSABLE: Seasons and Episodes Section
@Composable
private fun SeasonsAndEpisodesSection(
    seasons: List<Season>,
    episodes: List<Episode>
) {
    var selectedSeasonNumber by remember {
        mutableIntStateOf(seasons.firstOrNull()?.number ?: 1)
    }

    Column {
        Text(
            text = "Seasons & Episodes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Season Selector Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            seasons.forEach { season ->
                val isSelected = season.number == selectedSeasonNumber
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) AccentBlue else DarkCard)
                        .border(
                            1.dp,
                            if (isSelected) AccentBlue else DarkCardBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedSeasonNumber = season.number }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Season ${season.number}",
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Episodes in Selected Season
        val seasonEpisodes = episodes.filter { it.season == selectedSeasonNumber }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            seasonEpisodes.forEach { ep ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkCard.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Episode Image or placeholder
                        AsyncImage(
                            model = ep.image?.medium,
                            contentDescription = ep.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(70.dp)
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(RatingBoxBg)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "E${ep.number}. ${ep.name}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            ep.rating?.average?.let { rate ->
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "★ $rate",
                                    color = StarYellow,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gagal memuat detail",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("Coba Lagi", color = Color.White)
        }
    }
}

private fun shareShow(context: Context, show: TvShowDetail) {
    val plainSummary = show.summary?.let {
        HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
    } ?: "No summary available."

    val shareText = buildString {
        appendLine("🎬 ${show.name}")
        appendLine()
        appendLine(plainSummary)
        show.url?.let {
            appendLine()
            appendLine("Watch more: $it")
        }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, show.name)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}
