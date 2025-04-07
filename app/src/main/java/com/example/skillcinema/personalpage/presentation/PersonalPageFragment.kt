package com.example.skillcinema.personalpage.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil3.compose.AsyncImage
import com.example.skillcinema.R
import com.example.skillcinema.data.models.PersonInfoFilmDto
import com.example.skillcinema.personalpage.data.PersonInfoDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PersonalPageFragment : Fragment() {


    private val args: PersonalPageFragmentArgs by navArgs()
    private val viewModel: PersonalPageViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPersonInfo(args.staffId)
        }

        return ComposeView(requireContext()).apply {
            setContent {
                PersonalPageScreen(viewModel, findNavController())
            }

        }
    }


    @Composable
    fun PersonalPageScreen(
        viewModel: PersonalPageViewModel,
        navController: NavController,
        modifier: Modifier = Modifier
    ) {
//        val pageState by viewModel.personalPageStateFlow.collectAsStateWithLifecycle()
        val personInfo by viewModel.personInfoFlow.collectAsStateWithLifecycle()

        Column(
            modifier = modifier.padding(start = dimensionResource(R.dimen.screen_margin)),
            horizontalAlignment = Alignment.Start
        ) {
            ArtistCard(
                imageUrl = personInfo?.posterUrl,
                name = personInfo?.nameRu ?: personInfo?.nameEn ?: getString(R.string.no_name),
                profession = personInfo?.profession ?: "",
                modifier = modifier,
            )

            Text(
                text = getString(R.string.staff_personal_page_title),
                style = MaterialTheme.typography.headlineLarge
            )


        }
    }


    @Composable
    fun ArtistCard(
        imageUrl: String?,
        name: String,
        profession: String,
        modifier: Modifier
    ) {
        val photoSize = modifier.size(width = 146.dp, height = 201.dp)
        val roundedCorner = RoundedCornerShape(MaterialTheme.shapes.medium.topStart)

        Row(
            verticalAlignment = Alignment.Top,
            modifier = modifier
        ) {
            if (imageUrl == null) {
                Box(
                    modifier = photoSize
                        .clip(roundedCorner)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.image_placeholder),
                        contentDescription = "Image placeholder",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Test",
                    modifier =
                        photoSize.clip(roundedCorner)
                )
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = modifier.padding(start = dimensionResource(R.dimen.small_gap)),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = profession,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = modifier.padding(
                        top = dimensionResource(
                            R.dimen.smallest_gap
                        )
                    )
                )
            }
        }
    }

    @Composable
    fun FilmCard(
        imageUrl: String?,
        modifier: Modifier = Modifier,
        haveSeenMarker: Boolean = true,
    ) {
        val photoSize = modifier.size(width = 146.dp, height = 201.dp)
        val roundedCorner = RoundedCornerShape(MaterialTheme.shapes.medium.topStart)

        Column(
        ) {
            Box(
                modifier = photoSize
                    .then(
                        if (haveSeenMarker) {
                            modifier.drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Blue.copy(alpha = 0.5f)
                                        )
                                    ),
                                    blendMode = BlendMode.SrcOver
                                )
                            }
                        } else {
                            modifier
                        }
                    )
            ){
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Film poster",
                    modifier = photoSize
                        .clip(roundedCorner),
                    )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewFilmCard(
        @PreviewParameter(PersonInfoDtoProvider::class) personInfo: PersonInfoDto
    ) {
        FilmCard(personInfo.films?.get(0)?.posterUrl)
    }


    @Preview(showBackground = true)
    @Composable
    fun PreviewArtistCard(
        @PreviewParameter(PersonInfoDtoProvider::class) personInfo: PersonInfoDto
    ) {
        ArtistCard(
            imageUrl = personInfo.posterUrl,
            name = personInfo.nameRu ?: personInfo.nameEn ?: "No Name",
            profession = personInfo.profession ?: "",
            modifier = Modifier
        )
    }


    @Preview(showBackground = true)
    @Composable
    fun PreviewPersonalPageScreenMock(
        @PreviewParameter(PersonInfoDtoProvider::class) personInfo: PersonInfoDto
    ) {
        Column(
            modifier = Modifier.padding(start = dimensionResource(R.dimen.screen_margin)),
            horizontalAlignment = Alignment.Start
        ) {
            ArtistCard(
                imageUrl = personInfo.posterUrl,
                name = personInfo.nameRu ?: personInfo.nameEn ?: "No Name",
                profession = personInfo.profession ?: "",
                modifier = Modifier,
            )
        }
    }

}

class PersonInfoDtoProvider : PreviewParameterProvider<PersonInfoDto> {
    override val values: Sequence<PersonInfoDto> = sequenceOf(
        PersonInfoDto(
            personId = 1,
            nameRu = "Василий Иванов",
            nameEn = "Ivan Ivanov",
            posterUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7c/Fringilla_coelebs_chaffinch_male_edit2.jpg/1200px-Fringilla_coelebs_chaffinch_male_edit2.jpg",
            profession = "Актёр",
            films = List(20) {
                PersonInfoFilmDto(
                    filmId = it,
                    nameRu = "Фильм $it",
                    rating = "$it.0",
                    posterUrl = "https://static.wikia.nocookie.net/james-camerons-avatar/images/3/33/%D0%9B%D0%BE%E2%80%99%D0%B0%D0%BA.jpg/revision/latest?cb=20230120063420&path-prefix=ru",
                    year = 2000 + it,
                    nameEn = "Film $it",
                    description = "description it",
                    professionKey = "Акртёр $it",
                    posterUrlPreview = null,
                    genre = "Драмма",
                )
            }
        )
    )
}


