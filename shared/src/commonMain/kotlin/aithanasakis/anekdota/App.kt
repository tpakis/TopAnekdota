package aithanasakis.anekdota

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusable
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import kotlin.math.roundToInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import topanekdota.shared.generated.resources.Res
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.rememberLazyListState
import topanekdota.shared.generated.resources.bac
import topanekdota.shared.generated.resources.logo
import topanekdota.shared.generated.resources.logo2
import topanekdota.shared.generated.resources.app_icon
import topanekdota.shared.generated.resources.grass
import topanekdota.shared.generated.resources.fav
import topanekdota.shared.generated.resources.face
import topanekdota.shared.generated.resources.cat_0
import topanekdota.shared.generated.resources.cat_1
import topanekdota.shared.generated.resources.cat_2
import topanekdota.shared.generated.resources.cat_3
import topanekdota.shared.generated.resources.cat_4
import topanekdota.shared.generated.resources.cat_5
import topanekdota.shared.generated.resources.cat_6
import topanekdota.shared.generated.resources.cat_7
import topanekdota.shared.generated.resources.cat_8
import topanekdota.shared.generated.resources.cat_9
import topanekdota.shared.generated.resources.cat_10
import topanekdota.shared.generated.resources.anonymous
import topanekdota.shared.generated.resources.arimo
import topanekdota.shared.generated.resources.cardo
import topanekdota.shared.generated.resources.comfortaa
import topanekdota.shared.generated.resources.jura
import topanekdota.shared.generated.resources.novamono
import topanekdota.shared.generated.resources.opensans
import topanekdota.shared.generated.resources.play
import topanekdota.shared.generated.resources.ubuntumono

sealed class Screen {
    object Home : Screen()
    data class CategoryList(val categoryIndex: Int, val categoryName: String, val categoryKey: String) : Screen()
    data class DetailView(val categoryKey: String, val categoryName: String, val initialIndex: Int) : Screen()
}

data class CategoryInfo(
    val index: Int,
    val key: String,
    val nameGreek: String,
    val filename: String
)

val categories = listOf(
    CategoryInfo(0, "diafora", "Διάφορα", "diafora.txt"),
    CategoryInfo(1, "ksanthies", "Ξανθιές", "ksanthies.txt"),
    CategoryInfo(2, "totos", "Τοτός", "totos.txt"),
    CategoryInfo(3, "pontiaka", "Ποντιακά", "pontiaka.txt"),
    CategoryInfo(4, "ponira", "Πονηρά", "ponira.txt"),
    CategoryInfo(5, "annoula", "Μικρή Αννούλα", "annoula.txt"),
    CategoryInfo(6, "chuck", "Chuck Norris", "chuck.txt"),
    CategoryInfo(7, "sintoma", "Σύντομα - Κρυάδες", "sintoma.txt"),
    CategoryInfo(8, "mikres", "Μικρές Αγγελίες", "mikres.txt"),
    CategoryInfo(9, "zodia", "Ζώδια", "zodia.txt"),
    CategoryInfo(10, "agapimena", "Αγαπημένα", "")
)

@Composable
fun getCategoryDrawable(index: Int) = when(index) {
    0 -> Res.drawable.cat_0
    1 -> Res.drawable.cat_1
    2 -> Res.drawable.cat_2
    3 -> Res.drawable.cat_3
    4 -> Res.drawable.cat_4
    5 -> Res.drawable.cat_5
    6 -> Res.drawable.cat_6
    7 -> Res.drawable.cat_7
    8 -> Res.drawable.cat_8
    9 -> Res.drawable.cat_9
    10 -> Res.drawable.cat_10
    else -> Res.drawable.cat_0
}

@Composable
fun getCustomFontFamily(fontName: String): FontFamily {
    return when (fontName) {
        "Anonymous" -> FontFamily(Font(Res.font.anonymous))
        "Arimo" -> FontFamily(Font(Res.font.arimo))
        "Cardo" -> FontFamily(Font(Res.font.cardo))
        "Comfortaa" -> FontFamily(Font(Res.font.comfortaa))
        "Jura" -> FontFamily(Font(Res.font.jura))
        "NovaMono" -> FontFamily(Font(Res.font.novamono))
        "OpenSans" -> FontFamily(Font(Res.font.opensans))
        "Play" -> FontFamily(Font(Res.font.play))
        "UbuntuMono" -> FontFamily(Font(Res.font.ubuntumono))
        else -> FontFamily.Default
    }
}

val fontNames = listOf(
    "Default",
    "Anonymous",
    "Arimo",
    "Cardo",
    "Comfortaa",
    "Jura",
    "NovaMono",
    "OpenSans",
    "Play",
    "UbuntuMono"
)

val presetBgColors = listOf(
    0xFFFFFFFF to "Καθαρό Λευκό",
    0xFFFDF6E3 to "Κρεμ (Sepia)",
    0xFFEAE6DF to "Ζεστό Χαρτί",
    0xFFE1F5FE to "Απαλό Μπλε",
    0xFFE8F5E9 to "Απαλό Πράσινο",
    0xFF121212 to "Σκούρο Γκρι",
    0xFF0F172A to "Βαθύ Μπλε"
)

val presetTextColors = listOf(
    0xFF000000 to "Μαύρο",
    0xFF1A1A1A to "Σκούρο Γκρι",
    0xFF003366 to "Μπλε",
    0xFF004D40 to "Σκούρο Πράσινο",
    0xFFE2E8F0 to "Απαλό Γκρι",
    0xFFFFFFFF to "Λευκό"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(context: Any? = null) {
    val settingsManager = remember {
        val storage = createKeyValueStorage(context)
        SettingsManager(storage)
    }

    val repository = remember {
        val database = createJokeDatabase(context)
        JokeRepository(database)
    }

    var isDbReady by remember { mutableStateOf(false) }
    LaunchedEffect(repository, settingsManager) {
        repository.ensureDatabasePopulated(settingsManager)
        isDbReady = true
    }

    val navigationStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val currentScreen = navigationStack.last()

    PlatformBackHandler(enabled = navigationStack.size > 1) {
        navigationStack.removeAt(navigationStack.lastIndex)
    }

    // Dialog state
    var showAboutDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showAddJokeDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    
    // Dynamic Settings States
    var jokeFontSize by remember { mutableStateOf(settingsManager.fontSize) }
    var jokeFontName by remember { mutableStateOf(settingsManager.fontName) }
    var jokeFontColor by remember { mutableStateOf(settingsManager.fontColor) }
    var jokeBgColor by remember { mutableStateOf(settingsManager.backgroundColor) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    MaterialTheme(
        colorScheme = if (jokeBgColor == 0xFF121212L || jokeBgColor == 0xFF0F172AL) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                if (!isDbReady) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF0F60A8))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Φόρτωση Ανεκδότων...\nΠαρακαλώ περιμένετε!",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F60A8),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                } else {
                    AnimatedContent(
                        targetState = currentScreen,
                        modifier = Modifier.fillMaxSize(),
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { screen ->
                        when (screen) {
                            is Screen.Home -> {
                                HomeScreen(
                                    onSelectCategory = { catInfo ->
                                        navigationStack.add(
                                            Screen.CategoryList(
                                                catInfo.index,
                                                catInfo.nameGreek,
                                                catInfo.key
                                            )
                                        )
                                    }
                                )
                            }
                            is Screen.CategoryList -> {
                                CategoryListScreen(
                                    categoryName = screen.categoryName,
                                    categoryKey = screen.categoryKey,
                                    repository = repository,
                                    onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                                    onSelectJoke = { index ->
                                        navigationStack.add(
                                            Screen.DetailView(
                                                screen.categoryKey,
                                                screen.categoryName,
                                                index
                                            )
                                        )
                                    },
                                    onShowAddJoke = { showAddJokeDialog = true },
                                    onShowSettings = { showSettingsDialog = true },
                                    onShowAbout = { showAboutDialog = true },
                                    onJokeLongPressed = { joke ->
                                        val isFavorite = !joke.isFavorite
                                        repository.toggleFavorite(joke, isFavorite)
                                        scope.launch {
                                            val msg = if (isFavorite) "Προστέθηκε στα αγαπημένα!" else "Αφαιρέθηκε από τα αγαπημένα!"
                                            snackbarHostState.showSnackbar(msg)
                                        }
                                    }
                                )
                            }
                            is Screen.DetailView -> {
                                DetailViewScreen(
                                    categoryKey = screen.categoryKey,
                                    categoryName = screen.categoryName,
                                    initialIndex = screen.initialIndex,
                                    repository = repository,
                                    fontSize = jokeFontSize,
                                    fontName = jokeFontName,
                                    textColor = Color(jokeFontColor),
                                    bgColor = Color(jokeBgColor),
                                    settingsManager = settingsManager,
                                    onBack = { navigationStack.removeAt(navigationStack.lastIndex) },
                                    onShowSettings = { showSettingsDialog = true },
                                    onShowAbout = { showAboutDialog = true },
                                    onShowHelp = { showHelpDialog = true },
                                    onShowSnackbar = { msg ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(msg)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // ABOUT DIALOG
                if (showAboutDialog) {
                    AlertDialog(
                        onDismissRequest = { showAboutDialog = false },
                        icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF0F60A8)) },
                        title = { Text("Σχετικά με την Εφαρμογή", fontWeight = FontWeight.Bold) },
                        text = {
                            Column {
                                Text("Στην ανάπτυξη της εφαρμογής συμμετέχουν οι:", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("• Γιώργος Ενεχηλίδης")
                                Text("• Κωνσταντίνος Μουτζίκης")
                                Text("• Χρήστος Καραδήμος")
                                Text("• Τσακιρίδης Θάνος")
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Σας ευχαριστούμε πολύ,\nκαλή διασκέδαση!", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showAboutDialog = false }) {
                                Text("Κλείσιμο", color = Color(0xFF0F60A8))
                            }
                        }
                    )
                }

                // HELP DIALOG
                if (showHelpDialog) {
                    AlertDialog(
                        onDismissRequest = { showHelpDialog = false },
                        icon = { Icon(Icons.Default.Help, contentDescription = null, tint = Color(0xFF0F60A8)) },
                        title = { Text("Βοήθεια / Οδηγίες", fontWeight = FontWeight.Bold) },
                        text = {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                Text("Λίγες οδηγίες:", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("1) Μπορείτε να προσθέσετε ένα ανέκδοτο στη λίστα με τα αγαπημένα με δύο τρόπους:")
                                Text("• Πατώντας παρατεταμένα πάνω του στη λίστα.")
                                Text("• Πατώντας το αστεράκι (κάτω δεξιά) όταν το διαβάζετε.")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("2) Μπορείτε να προσθέσετε ένα δικό σας ανέκδοτο στην εφαρμογή (μπαίνει αυτόματα στα αγαπημένα):")
                                Text("• Πηγαίνετε σε οποιαδήποτε κατηγορία, πατάτε το κουμπί '+' στην κορυφή και το γράφετε εκεί.")
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Καλή διασκέδαση!", fontWeight = FontWeight.Bold, color = Color(0xFF0F60A8))
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showHelpDialog = false }) {
                                Text("Κατάλαβα", color = Color(0xFF0F60A8))
                            }
                        }
                    )
                }

                // ADD CUSTOM JOKE DIALOG
                if (showAddJokeDialog) {
                    var customText by remember { mutableStateOf("") }
                    AlertDialog(
                        onDismissRequest = { showAddJokeDialog = false },
                        icon = { Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF0F60A8)) },
                        title = { Text("Προσθήκη Δικού σας Ανέκδοτου", fontWeight = FontWeight.Bold) },
                        text = {
                            Column {
                                Text("Το ανέκδοτό σας θα αποθηκευτεί στην κατηγορία 'Αγαπημένα'.", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = customText,
                                    onValueChange = { customText = it },
                                    label = { Text("Γράψτε το ανέκδοτο εδώ...") },
                                    minLines = 4,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (customText.trim().length < 40) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Το ελάχιστο μήκος είναι 40 χαρακτήρες!")
                                        }
                                    } else if (!customText.contains(" ")) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Το ανέκδοτο πρέπει να περιέχει κενά ανάμεσα στις λέξεις!")
                                        }
                                    } else {
                                        scope.launch {
                                            repository.addCustomJoke(customText)
                                            snackbarHostState.showSnackbar("Το ανέκδοτο προστέθηκε στα αγαπημένα!")
                                            showAddJokeDialog = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F60A8))
                            ) {
                                Text("Προσθήκη")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddJokeDialog = false }) {
                                Text("Άκυρο", color = Color(0xFF757575))
                            }
                        }
                    )
                }

                // SETTINGS DIALOG (FONT SIZE, COLOR, BACKGROUND COLOR)
                if (showSettingsDialog) {
                    AlertDialog(
                        onDismissRequest = { showSettingsDialog = false },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF0F60A8)) },
                        title = { Text("Ρυθμίσεις Εμφάνισης", fontWeight = FontWeight.Bold) },
                        text = {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                Text("Μέγεθος Γραμμάτων: $jokeFontSize", fontWeight = FontWeight.Bold)
                                Slider(
                                    value = jokeFontSize.toFloat(),
                                    onValueChange = { jokeFontSize = it.toInt() },
                                    valueRange = 12f..32f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF0F60A8),
                                        activeTrackColor = Color(0xFF0F60A8)
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Γραμματοσειρά", fontWeight = FontWeight.Bold)
                                var expandedFontDropdown by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.fillMaxWidth().clickable { expandedFontDropdown = true }.padding(8.dp).background(Color.LightGray.copy(alpha = 0.2f)).padding(8.dp)) {
                                    Text(jokeFontName, fontFamily = getCustomFontFamily(jokeFontName))
                                    DropdownMenu(
                                        expanded = expandedFontDropdown,
                                        onDismissRequest = { expandedFontDropdown = false }
                                    ) {
                                        fontNames.forEach { font ->
                                            DropdownMenuItem(
                                                text = { Text(font, fontFamily = getCustomFontFamily(font)) },
                                                onClick = {
                                                    jokeFontName = font
                                                    expandedFontDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Χρώμα Κειμένου", fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    presetTextColors.forEach { (colorLong, name) ->
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(colorLong))
                                                .clickable { jokeFontColor = colorLong }
                                                .graphicsLayer {
                                                    if (jokeFontColor == colorLong) {
                                                        scaleX = 1.2f
                                                        scaleY = 1.2f
                                                    }
                                                }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Χρώμα Φόντου", fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    presetBgColors.forEach { (colorLong, name) ->
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(colorLong))
                                                .clickable { jokeBgColor = colorLong }
                                                .graphicsLayer {
                                                    if (jokeBgColor == colorLong) {
                                                        scaleX = 1.2f
                                                        scaleY = 1.2f
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    settingsManager.fontSize = jokeFontSize
                                    settingsManager.fontName = jokeFontName
                                    settingsManager.fontColor = jokeFontColor
                                    settingsManager.backgroundColor = jokeBgColor
                                    showSettingsDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F60A8))
                            ) {
                                Text("Αποθήκευση")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showSettingsDialog = false }) {
                                Text("Άκυρο", color = Color(0xFF757575))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onSelectCategory: (CategoryInfo) -> Unit
) {
    val pageCount = 10000 * categories.size
    val pagerState = rememberPagerState(initialPage = pageCount / 2, pageCount = { pageCount })
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val facts = remember {
        listOf(
            "Οι νυχτερίδες όταν βγαίνουν από μια σπηλιά πάνε πάντα αριστερά.",
            "Η καρδιά της γαλάζιας φάλαινας είναι τόσο μεγάλη όσο ένα αυτοκίνητο.",
            "Το σαγόνι της γάτας δεν μπορεί να κινηθεί δεξιά και αριστερά.",
            "Τα κοράκια μπορούν να αναγνωρίσουν και να θυμηθούν ανθρώπινα πρόσωπα.",
            "Οι πεταλούδες γεύονται με τα πόδια τους.",
            "Η μέλισσα πρέπει να επισκεφθεί 2 εκατομμύρια λουλούδια για να φτιάξει μισό κιλό μέλι.",
            "Το νύχι του αντίχειρα μεγαλώνει πιο αργά από όλα τα άλλα νύχια.",
            "Η γλώσσα του χαμαιλέοντα έχει διπλάσιο μήκος από το σώμα του.",
            "Οι στρουθοκάμηλοι μπορούν να τρέξουν γρηγορότερα από τα άλογα.",
            "Η Σαχάρα μεγαλώνει κατά περίπου 1 χιλιόμετρο κάθε μήνα."
        )
    }
    var showFactDialog by remember { mutableStateOf(false) }
    var currentFact by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F60A8))
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionLeft -> {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                            true
                        }
                        Key.DirectionRight -> {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                            true
                        }
                        Key.Enter, Key.NumPadEnter -> {
                            val currentCat = categories[pagerState.currentPage % categories.size]
                            onSelectCategory(currentCat)
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F60A8))
                    .statusBarsPadding()
                    .height(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.bac),
                    contentDescription = "App Logo",
                    modifier = Modifier.height(80.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            var pressedPageIndex by remember { mutableStateOf<Int?>(null) }

            // CATEGORY SLIDING PANELS CAROUSEL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                if (down.type == PointerType.Mouse) {
                                    var totalChange = 0f
                                    var isDragging = false
                                    
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        if (event.changes.any { !it.pressed && it.previousPressed }) {
                                            break
                                        }
                                        
                                        event.changes.forEach { change ->
                                            if (change.type == PointerType.Mouse) {
                                                val distance = (change.position - change.previousPosition).getDistance()
                                                totalChange += distance
                                                
                                                if (totalChange > 10f) {
                                                    isDragging = true
                                                    pressedPageIndex = null
                                                    pagerState.dispatchRawDelta(- (change.position.x - change.previousPosition.x))
                                                    change.consume()
                                                }
                                            }
                                        }
                                    }
                                    
                                    if (isDragging) {
                                        val targetPage = (pagerState.currentPage + pagerState.currentPageOffsetFraction)
                                            .roundToInt()
                                            .coerceIn(0, pageCount - 1)
                                        scope.launch {
                                            pagerState.animateScrollToPage(targetPage)
                                        }
                                    }
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth().aspectRatio(680f / 440f),
                        contentPadding = PaddingValues(0.dp)
                    ) { page ->
                        val cat = categories[page % categories.size]
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    val pageOffset = (
                                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                            )
                                    alpha = 1f - (pageOffset * pageOffset * 0.4f).coerceIn(0f, 1f)
                                    scaleY = 1f - (pageOffset * pageOffset * 0.15f).coerceIn(0f, 1f)
                                    val isPressed = pressedPageIndex == page
                                    if (isPressed) {
                                        scaleX *= 0.98f
                                        scaleY *= 0.98f
                                    }
                                }
                                .pointerInput(cat) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val down = awaitFirstDown(requireUnconsumed = true)
                                            pressedPageIndex = page
                                            var isConsumed = false
                                            
                                            while (true) {
                                                val event = awaitPointerEvent(PointerEventPass.Final)
                                                if (event.changes.any { it.isConsumed }) {
                                                    isConsumed = true
                                                    if (pressedPageIndex == page) {
                                                        pressedPageIndex = null
                                                    }
                                                }
                                                if (event.changes.any { !it.pressed && it.previousPressed }) {
                                                    break
                                                }
                                            }
                                            
                                            if (pressedPageIndex == page) {
                                                pressedPageIndex = null
                                            }
                                            if (!isConsumed) {
                                                onSelectCategory(cat)
                                            }
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(0.dp),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(getCategoryDrawable(cat.index)),
                                    contentDescription = cat.nameGreek,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillWidth
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // PAGER INDICATORS (DOTS) Below Pager
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(categories.size) { iteration ->
                            val color = if (pagerState.currentPage % categories.size == iteration) Color.White else Color.White.copy(alpha = 0.4f)
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(8.dp)
                            )
                        }
                    }
                }
            }

            // BOTTOM BANNER BOX
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFF0F60A8)),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Wooden Sign
                Image(
                    painter = painterResource(Res.drawable.logo2),
                    contentDescription = "Το ήξερες ότι...",
                    modifier = Modifier
                        .padding(bottom = 12.dp) // Lift it slightly so grass covers the bottom of the posts
                        .height(110.dp)
                        .fillMaxWidth(0.8f)
                        .clickable {
                            currentFact = facts.random()
                            showFactDialog = true
                        },
                    contentScale = ContentScale.Fit
                )

                // Grass decoration on top
                Image(
                    painter = painterResource(Res.drawable.grass),
                    contentDescription = "Bottom Decor Grass",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }

    if (showFactDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showFactDialog = false }
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Warning/Info Icon in a circle
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "!",
                            style = TextStyle(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Light,
                                color = Color(0xFFE57373)
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Το ήξερες ότι...",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = currentFact,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Close Button
                        Button(
                            onClick = { showFactDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCCCCCC)), // Gray
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text("Κλείσιμο", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        // More Button
                        Button(
                            onClick = {
                                var nextFact = facts.random()
                                while (nextFact == currentFact) {
                                    nextFact = facts.random()
                                }
                                currentFact = nextFact
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)), // Orange-red/brown
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text("Κι άλλο!", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryListScreen(
    categoryName: String,
    categoryKey: String,
    repository: JokeRepository,
    onBack: () -> Unit,
    onSelectJoke: (Int) -> Unit,
    onShowAddJoke: () -> Unit,
    onShowSettings: () -> Unit,
    onShowAbout: () -> Unit,
    onJokeLongPressed: suspend (Joke) -> Unit
) {
    val scope = rememberCoroutineScope()
    var rawJokes = remember { mutableStateListOf<Joke>() }
    var jokesList = remember { mutableStateListOf<Joke>() }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    suspend fun reloadJokes() {
        val dbJokes = repository.getJokes(categoryKey)
        rawJokes.clear()
        rawJokes.addAll(dbJokes)
        
        jokesList.clear()
        if (searchQuery.isEmpty()) {
            jokesList.addAll(dbJokes)
        } else {
            jokesList.addAll(dbJokes.filter { it.text.contains(searchQuery, ignoreCase = true) })
        }
    }

    LaunchedEffect(categoryKey, searchQuery) {
        reloadJokes()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // HEADER BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F60A8))
                    .statusBarsPadding()
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Πίσω", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = categoryName,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { isSearching = !isSearching }) {
                            Icon(Icons.Default.Search, contentDescription = "Αναζήτηση", tint = Color.White)
                        }
                        IconButton(onClick = onShowAddJoke) {
                            Icon(Icons.Default.Add, contentDescription = "Προσθήκη", tint = Color.White)
                        }
                        IconButton(onClick = onShowSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Ρυθμίσεις", tint = Color.White)
                        }
                    }
                }
            }

            // SEARCH TEXT FIELD
            if (isSearching) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Αναζήτηση ανέκδοτου...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    }
                )
            }

            // LIST OF JOKES
            if (jokesList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (categoryKey == "agapimena") "Δεν έχετε αποθηκευμένα αγαπημένα!" else "Δεν βρέθηκαν ανέκδοτα!",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            } else {
                val listState = rememberLazyListState()
                val coroutineScope = rememberCoroutineScope()
                val flingBehavior = ScrollableDefaults.flingBehavior()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    if (down.type == PointerType.Mouse) {
                                        val velocityTracker = VelocityTracker()
                                        velocityTracker.addPosition(down.uptimeMillis, down.position)
                                        var totalDeltaY = 0f
                                        var isDragging = false
                                        
                                        while (true) {
                                            val event = awaitPointerEvent(PointerEventPass.Initial)
                                            val anyPressed = event.changes.any { it.pressed }
                                            if (!anyPressed) {
                                                if (isDragging) {
                                                    val velocityY = velocityTracker.calculateVelocity().y
                                                    if (kotlin.math.abs(velocityY) > 100f) {
                                                        coroutineScope.launch {
                                                            listState.scroll {
                                                                with(flingBehavior) {
                                                                    performFling(-velocityY)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                break
                                            }
                                            
                                            event.changes.forEach { change ->
                                                if (change.type == PointerType.Mouse) {
                                                    velocityTracker.addPosition(change.uptimeMillis, change.position)
                                                    val deltaY = change.position.y - change.previousPosition.y
                                                    totalDeltaY += deltaY
                                                    
                                                    if (isDragging) {
                                                        listState.dispatchRawDelta(-deltaY)
                                                        change.consume()
                                                    } else if (kotlin.math.abs(totalDeltaY) > 8f) {
                                                        isDragging = true
                                                        change.consume()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(jokesList) { index, joke ->
                            val itemNumber = index + 1
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = { onSelectJoke(index) },
                                        onLongClick = { 
                                            scope.launch {
                                                onJokeLongPressed(joke)
                                                reloadJokes()
                                            }
                                        }
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (joke.isCustom) Color(0xFFE8F5E9) else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF0F60A8).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$itemNumber",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F60A8),
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = joke.text,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        style = TextStyle(
                                            fontSize = 15.sp,
                                            color = Color.Black
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (joke.isFavorite) {
                                        Icon(Icons.Default.Star, contentDescription = "Favorite", tint = Color(0xFFFFB300))
                                    }
                                }
                            }
                        }
                    }

                    PlatformScrollbar(
                        state = listState,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                    )
                }
            }

            // BOTTOM GRASS DECOR
            Image(
                painter = painterResource(Res.drawable.grass),
                contentDescription = "Bottom Grass Decor",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Composable
fun DetailViewScreen(
    categoryKey: String,
    categoryName: String,
    initialIndex: Int,
    repository: JokeRepository,
    fontSize: Int,
    fontName: String,
    textColor: Color,
    bgColor: Color,
    settingsManager: SettingsManager,
    onBack: () -> Unit,
    onShowSettings: () -> Unit,
    onShowAbout: () -> Unit,
    onShowHelp: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    var jokes = remember { mutableStateListOf<Joke>() }
    var currentIndex by remember { mutableStateOf(initialIndex) }

    LaunchedEffect(categoryKey) {
        val dbJokes = repository.getJokes(categoryKey)
        jokes.clear()
        jokes.addAll(dbJokes)
    }

    if (jokes.isEmpty() || currentIndex >= jokes.size) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF0F60A8))
        }
        return
    }

    val currentJoke = jokes[currentIndex]
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var showGestureTutorial by remember { mutableStateOf(settingsManager.showGestureOverlay) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // HEADER BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F60A8))
                    .statusBarsPadding()
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Πίσω", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$categoryName - ${currentIndex + 1}/${jokes.size}",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = onShowHelp) {
                            Icon(Icons.Default.Help, contentDescription = "Βοήθεια", tint = Color.White)
                        }
                        IconButton(onClick = onShowAbout) {
                            Icon(Icons.Default.Info, contentDescription = "Σχετικά", tint = Color.White)
                        }
                        IconButton(onClick = onShowSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Ρυθμίσεις", tint = Color.White)
                        }
                    }
                }
            }

            // READING JOKE TEXT AREA (Scrollable & Swipable simulated via Next/Prev buttons & Double-Tap gesture)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                val halfWidth = constraints.maxWidth / 2
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(jokes, currentIndex) {
                            detectTapGestures(
                                onDoubleTap = { offset ->
                                    if (offset.x < halfWidth) {
                                        if (currentIndex > 0) {
                                            currentIndex--
                                        } else {
                                            currentIndex = jokes.size - 1
                                        }
                                    } else {
                                        if (currentIndex < jokes.size - 1) {
                                            currentIndex++
                                        } else {
                                            currentIndex = 0
                                        }
                                    }
                                }
                            )
                        }
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = currentJoke.text,
                        style = TextStyle(
                            color = textColor,
                            fontSize = fontSize.sp,
                            fontFamily = getCustomFontFamily(fontName),
                            lineHeight = (fontSize + 6).sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ACTION CONTROLS BOTTOM BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F60A8).copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // PREVIOUS BUTTON
                    Button(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                            } else {
                                currentIndex = jokes.size - 1
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F60A8))
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Προηγούμενο")
                        Text("Προηγ.")
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // FAVORITE STAR BUTTON
                        IconButton(
                            onClick = {
                                scope.launch {
                                    val isFavorite = !currentJoke.isFavorite
                                    repository.toggleFavorite(currentJoke, isFavorite)
                                    val updatedJoke = currentJoke.copy(isFavorite = isFavorite)
                                    jokes[currentIndex] = updatedJoke
                                    onShowSnackbar(if (isFavorite) "Προστέθηκε στα αγαπημένα!" else "Αφαιρέθηκε από τα αγαπημένα!")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentJoke.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Favorite star",
                                tint = if (currentJoke.isFavorite) Color(0xFFFFB300) else Color(0xFF0F60A8),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // COPY/SHARE BUTTON
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(currentJoke.text))
                                onShowSnackbar("Το ανέκδοτο αντιγράφηκε στο clipboard!")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Copy/Share",
                                tint = Color(0xFF0F60A8),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // NEXT BUTTON
                    Button(
                        onClick = {
                            if (currentIndex < jokes.size - 1) {
                                currentIndex++
                            } else {
                                currentIndex = 0
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F60A8))
                    ) {
                        Text("Επόμ.")
                        Icon(Icons.Default.ChevronRight, contentDescription = "Επόμενο")
                    }
                }
            }
        }

        if (showGestureTutorial) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .pointerInput(Unit) {
                        detectTapGestures {
                            settingsManager.showGestureOverlay = false
                            showGestureTutorial = false
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Οδηγός Πλοήγησης",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Μπορείτε να αλλάξετε ανέκδοτο με διπλό πάτημα (double tap) στις πλευρές της οθόνης.",
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left half
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color.White.copy(alpha = 0.15f), shape = CircleShape)
                                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "2x",
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Διπλό Πάτημα\nΑριστερά",
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Προηγούμενο\nανέκδοτο",
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        // Divider Line
                        Canvas(
                            modifier = Modifier
                                .height(120.dp)
                                .width(2.dp)
                        ) {
                            val strokeWidth = 2f
                            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            drawLine(
                                color = Color.White.copy(alpha = 0.3f),
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = strokeWidth,
                                pathEffect = dashPathEffect
                            )
                        }

                        // Right half
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color.White.copy(alpha = 0.15f), shape = CircleShape)
                                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "2x",
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Διπλό Πάτημα\nΔεξιά",
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Επόμενο\nανέκδοτο",
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = {
                            settingsManager.showGestureOverlay = false
                            showGestureTutorial = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F60A8),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                        modifier = Modifier.widthIn(min = 160.dp)
                    ) {
                        Text(
                            text = "Κατάλαβα!",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}