// Izin memakai API Material3 yang masih eksperimental (TopAppBar, dll.)
@file:OptIn(ExperimentalMaterial3Api::class)

// Nama paket aplikasi, harus sama dengan namespace di build.gradle.kts
package com.example.catatanmahasiswa

// ===== ANDROID & ACTIVITY =====
import android.content.res.Configuration
import android.os.Bundle                                   // Bundle untuk onCreate
import androidx.activity.ComponentActivity                 // Kelas dasar Activity untuk Compose
import androidx.activity.compose.setContent                // setContent { } untuk menampilkan UI Compose

// ===== FOUNDATION =====
import androidx.compose.foundation.*                       // background, clickable, horizontalScroll, verticalScroll
import androidx.compose.foundation.layout.*                // Column, Row, Box, Spacer, padding, size, Arrangement
import androidx.compose.foundation.lazy.LazyColumn         // Daftar scroll yang efisien
import androidx.compose.foundation.lazy.items              // Membuat item LazyColumn dari list
import androidx.compose.foundation.shape.CircleShape       // Bentuk lingkaran
import androidx.compose.foundation.shape.RoundedCornerShape // Bentuk sudut membulat
import androidx.compose.foundation.isSystemInDarkTheme

// ===== IKON =====
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add          // Ikon plus
import androidx.compose.material.icons.filled.Search        // Ikon pencarian
import androidx.compose.material.icons.filled.ArrowDropDown // Ikon panah dropdown
import androidx.compose.material.icons.filled.Check        // Ikon centang
import androidx.compose.material.icons.filled.Close        // Ikon silang
import androidx.compose.material.icons.filled.Delete       // Ikon hapus
import androidx.compose.material.icons.filled.Edit         // Ikon pensil
import androidx.compose.material.icons.filled.Home         // Ikon rumah
import androidx.compose.material.icons.filled.Menu         // Ikon menu
import androidx.compose.material.icons.filled.History       // Ikon riwayat
import androidx.compose.material.icons.filled.ListAlt       // Ikon daftar
import androidx.compose.material.icons.filled.Person       // Ikon profil
import androidx.compose.material.icons.filled.School       // Ikon topi toga / perkuliahan

// ===== MATERIAL 3 =====
import androidx.compose.material3.*                        // Scaffold, Card, Button, NavigationRail, VerticalDivider, dll.

// ===== RUNTIME (state) =====
import androidx.compose.runtime.*                          // remember, mutableStateOf, mutableStateListOf, by
import androidx.compose.runtime.saveable.rememberSaveable  // Seperti remember, tapi tahan rotasi layar

// ===== UI =====
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration     // Membaca lebar layar (HP atau tablet)
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ===== VIEWMODEL =====
import androidx.lifecycle.ViewModel                        // Penyimpan data yang tahan perubahan layar
import androidx.lifecycle.viewmodel.compose.viewModel      // Mengambil ViewModel di Composable

// ===== NAVIGASI =====
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// ===== JAVA (tanggal) =====
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* =====================================================================
 *  1. TEMA (LIGHT & DARK)
 *  Semua komponen memakai MaterialTheme.colorScheme.
 * ===================================================================== */

private val LightColors = lightColorScheme( // Tema terang
    primary = Color(0xFF2563EB),              // Warna utama (Blue)
    onPrimary = Color.White,                  // Teks di atas warna utama
    primaryContainer = Color(0xFFDBEAFE),     // Latar kartu utama
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondaryContainer = Color(0xFFDBEAFE),   // Indikator menu bawah
    background = Color(0xFFF8FAFC),           // Latar halaman
    onBackground = Color(0xFF1E293B),
    surface = Color.White,                    // Latar kartu
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF64748B),     // Teks sekunder
    outline = Color(0xFF94A3B8),
    tertiary = Color(0xFF10B981),             // Warna sukses (hijau)
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF065F46),
    error = Color(0xFFEF4444),                // Warna error (merah)
    onError = Color.White
)

private val DarkColors = darkColorScheme( // Tema gelap
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF1E3A8A),
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondaryContainer = Color(0xFF334155),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF64748B),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF065F46),
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFD1FAE5),
    error = Color(0xFFFCA5A5),
    onError = Color(0xFF7F1D1D)
)

// Membungkus aplikasi dengan tema terang atau gelap
@Composable
fun KampusKuTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

/* =====================================================================
 *  2. MODEL DATA + STATE APLIKASI + VIEWMODEL
 * ===================================================================== */

// Satu catatan kuliah
data class Catatan(
    val id: Int,
    val judul: String,
    val kategori: String,
    val jam: String,
    val isi: String,
    val tanggal: String
)

// Satu baris jadwal kuliah
data class Jadwal(
    val id: Int,
    val jam: String,
    val matkul: String,
    val kategori: String
)

val daftarKategori = listOf("Perkuliahan", "Praktikum", "Tugas & Proyek", "Ujian")

data class TugasKuliah(
    val id: Int = 1,
    val matkul: String,
    val deskripsi: String,
    val status: String
)

// Jadikan mutable state supaya bisa dihapus dari halaman Riwayat
val daftarTugasKelompok = mutableStateListOf(
    TugasKuliah(1, "Pemrograman Mobile", "Perkuliahan", "Selesai"),
    TugasKuliah(2, "Pemodelan Game", "Praktikum", "Belum Selesai"),
    TugasKuliah(3, "Data Mining", "Perkuliahan", "Selesai"),
    TugasKuliah(4, "Rekayasa Perangkat Lunak", "Tugas & Proyek", "Belum Selesai"),
    TugasKuliah(5, "Pemodelan Game", "Tugas ada di ppt", "Besok")
)

// Tanggal hari ini dalam format Indonesia, contoh "15 Okt 2026"
fun tanggalHariIni(): String =
    SimpleDateFormat("d MMM yyyy", Locale("id", "ID")).format(Date())

// Satu "pusat data" yang dipakai bersama oleh semua layar.
// mutableStateListOf = list yang otomatis memperbarui UI saat isinya berubah.
class AppState { // Pusat Data yg dipakai untuk semua layar
    private var idCatatanBerikut = 5
    private var idJadwalBerikut = 5

    val catatan = mutableStateListOf( // List yg otomatis memperbarui UI saat isinya berubah.
        Catatan(1, "Pemrograman Mobile", "Perkuliahan", "09.00",
            "Hari ini belajar dasar Jetpack Compose, Layout, dan Component State.",
            "15 Okt 2026"),
        Catatan(2, "Pemodelan Game", "Praktikum", "11.00",
            "Merancang logika game loop, character movement, dan physics pada engine game.",
            "14 Okt 2026"),
        Catatan(3, "Data Mining", "Perkuliahan", "13.00",
            "Penerapan algoritma K-Means Clustering, Classification Decision Tree, dan Text Mining.",
            "13 Okt 2026"),
        Catatan(4, "Rekayasa Perangkat Lunak", "Tugas & Proyek", "15.00",
            "Penyusunan dokumen SRS (Software Requirements Specification) dan Diagram UML.",
            "12 Okt 2026")
    )

    val jadwal = mutableStateListOf(
        Jadwal(1, "09.00", "Pemrograman Mobile", "Perkuliahan"),
        Jadwal(2, "11.00", "Pemodelan Game", "Praktikum"),
        Jadwal(3, "13.00", "Data Mining", "Perkuliahan"),
        Jadwal(4, "15.00", "Rekayasa Perangkat Lunak", "Tugas & Proyek")
    )

    // ----- Catatan -----
    fun cariCatatan(id: Int?) = catatan.find { it.id == id }

    // Tambahkan parameter jam di sini
    fun tambahCatatan(judul: String, kategori: String, jam: String, isi: String) {
        catatan.add(0, Catatan(idCatatanBerikut++, judul, kategori, jam, isi, tanggalHariIni()))
    }

    // Tambahkan parameter jam di sini juga
    fun ubahCatatan(id: Int, judul: String, kategori: String, jam: String, isi: String) {
        val i = catatan.indexOfFirst { it.id == id }
        if (i >= 0) {
            catatan[i] = catatan[i].copy(judul = judul, kategori = kategori, jam = jam, isi = isi)
        }
    }

    fun hapusCatatan(id: Int) {
        catatan.removeAll { it.id == id }
    }
    // ----- Jadwal -----
    fun tambahJadwal(jam: String, matkul: String, kategori: String) {
        jadwal.add(Jadwal(idJadwalBerikut++, jam, matkul, kategori))
    }

    fun ubahJadwal(id: Int, jam: String, matkul: String, kategori: String) {
        val i = jadwal.indexOfFirst { it.id == id }
        if (i >= 0) jadwal[i] = jadwal[i].copy(jam = jam, matkul = matkul, kategori = kategori)
    }

    fun hapusJadwal(id: Int) {
        jadwal.removeAll { it.id == id }
    }
}

// Mode panel kanan di tampilan tablet
enum class PanelMode { Kosong, Detail, Tambah, Edit }

// Draft form: teks yang sedang diketik disimpan di sini, bukan di dalam layar.
// Karena hidup di ViewModel, teks tidak hilang saat layout HP <-> tablet berganti.
class FormDraft { // Menyimpan teks yg sedang diketik agar tidak hilang saat layar diputar
    var judul by mutableStateOf("")
    var kategori by mutableStateOf(daftarKategori.first())
    var jam by mutableStateOf("08.00") // <-- Tambahan state jam
    var isi by mutableStateOf("")
    var cobaSimpan by mutableStateOf(false)

    private var untukId: Int? = null   // id catatan yang sedang diedit (null = catatan baru)
    private var aktif = false          // true = draft sedang dipakai

    // Aman dipanggil berulang: hanya mengisi ulang kalau form berganti target
    fun siapkan(id: Int?, awal: Catatan?) {
        if (aktif && untukId == id) return
        judul = awal?.judul ?: ""
        kategori = awal?.kategori ?: daftarKategori.first()
        jam = awal?.jam ?: "08.00"
        isi = awal?.isi ?: ""
        cobaSimpan = false
        untukId = id
        aktif = true
    }

    // Dipanggil setelah Simpan atau Batal, supaya form berikutnya mulai kosong
    fun bersihkan() {
        aktif = false
        cobaSimpan = false
    }
}

// ViewModel: bertahan saat rotasi, split-screen, atau perubahan ukuran jendela
class AppViewModel : ViewModel() { // Membungkus semua dan bertahan saat rotasi atau perubahan layar
    val state = AppState()
    var tab by mutableStateOf("beranda")
    var panel by mutableStateOf(PanelMode.Kosong)
    var idDipilih by mutableStateOf<Int?>(null)
    val draft = FormDraft()

    fun buka(mode: PanelMode, id: Int? = null) {
        panel = mode
        idDipilih = id
        val awal = if (mode == PanelMode.Edit) state.cariCatatan(id) else null
        draft.siapkan(id, awal)
    }
}

/* =====================================================================
 *  3. ACTIVITY + NAVIGASI
 * ===================================================================== */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { // Menampilkan UI Compose
            // 0 = ikuti sistem, 1 = terang, 2 = gelap
            var modeTema by rememberSaveable { mutableStateOf(0) } // Mode tema yg mengikuti sistem
            val gelap = when (modeTema) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }
            KampusKuTheme(darkTheme = gelap) {
                KampusKuApp(modeTema = modeTema, onModeTemaChange = { modeTema = it })
            }
        }
    }
}
// Memilih layout: tablet (dua panel) atau HP (navigasi biasa)
@Composable
fun KampusKuApp(modeTema: Int, onModeTemaChange: (Int) -> Unit) {
    val vm: AppViewModel = viewModel()

    // Ubah batasan angka dari 600 menjadi lebih besar (misal 840)
    // atau hapus pengecekan tablet jika ingin HP selalu mode mobile
    @Suppress("ConfigurationScreenWidthHeight")
    val isLargeScreen = LocalConfiguration.current.screenWidthDp >= 840 // Memakai tablet app

    if (isLargeScreen) {
        TabletApp(vm, modeTema, onModeTemaChange)
    } else {
        // Ini akan tetap menggunakan navigasi HP (Mobile) baik posisi Portrait maupun Landscape
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "beranda") { // Rute
            composable("beranda") { BerandaScreen(vm.state, navController) }
            composable("tambah") { FormCatatanScreen(vm, navController, idCatatan = null) }
            composable(
                "edit/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                FormCatatanScreen(vm, navController, idCatatan = entry.arguments?.getInt("id"))
            }
            composable("riwayat") { RiwayatScreen(vm.state, navController) }
            composable("profil") { ProfilScreen(vm.state, navController, modeTema, onModeTemaChange) }
        }
    }
}

// Kembali ke Beranda tanpa menumpuk layar
fun NavController.keBeranda() {
    navigate("beranda") {
        popUpTo("beranda")
        launchSingleTop = true
    }
}

/* =====================================================================
 *  4. KOMPONEN YANG DIPAKAI ULANG
 * ===================================================================== */

// Bilah judul di atas. Jika onBack diisi, tampil panah kembali; jika tidak, ikon menu.
@Composable
fun AppTopBar(title: String, onBack: (() -> Unit)? = null, onMenuClick: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = {
                if (onBack != null) onBack()
                else onMenuClick?.invoke() // Memanggil aksi saat ikon garis tiga diklik
            }) {
                Icon(
                    imageVector = if (onBack != null) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu,
                    contentDescription = if (onBack != null) "Kembali" else "Menu"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

// Menu bawah (hanya dipakai di tampilan HP)
@Composable
fun AppBottomBar(navController: NavController, aktif: String) { // Menampilkan menu
    val ruteAktif = aktif
    val menu = listOf(
        Triple("beranda", "Beranda", Icons.Default.Home),
        Triple("riwayat", "Riwayat", Icons.Default.History),
        Triple("profil", "Profil", Icons.Default.Person)
    )
    NavigationBar {
        NavigationBarItem(
            selected = ruteAktif == "beranda",
            onClick = { navController.navigate("beranda") { popUpTo("beranda") } },
            icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
            label = { Text("Beranda") }
        )
        // Tombol tengah menjadi Riwayat
        NavigationBarItem(
            selected = ruteAktif == "riwayat",
            onClick = { navController.navigate("riwayat") { popUpTo("beranda") } },
            icon = { Icon(Icons.Default.History, contentDescription = "Riwayat") },
            label = { Text("Riwayat") }
        )
        NavigationBarItem(
            selected = ruteAktif == "profil",
            onClick = { navController.navigate("profil") { popUpTo("beranda") } },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil") }
        )
    }
}

// Warna tiap kategori
@Composable
fun warnaKategori(kategori: String): Color = when (kategori) { // <-- Tambahkan warna tiap kategori
    "Perkuliahan" -> MaterialTheme.colorScheme.primary
    "Praktikum" -> MaterialTheme.colorScheme.tertiary
    "Tugas & Proyek" -> Color(0xFFF2994A)
    else -> MaterialTheme.colorScheme.error
}

// Kotak berisi huruf pertama judul
@Composable
fun AvatarHuruf(teks: String, warna: Color, ukuran: Dp = 44.dp) { // <-- Ukuran kotak
    Box(
        modifier = Modifier
            .size(ukuran)
            .clip(RoundedCornerShape(12.dp))
            .background(warna.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        Text(teks.take(1).uppercase(), fontWeight = FontWeight.Bold, color = warna)
    }
}

// Label di atas kolom isian
@Composable
fun LabelForm(teks: String) { // <-- Teks label
    Text(
        text = teks,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

/* =====================================================================
 *  5. BERANDA
 * ===================================================================== */

// Layar Beranda versi HP: kerangka (Scaffold) + isi daftar
@Composable
fun BerandaScreen(state: AppState, navController: NavController) {
    var tampilkanDialogInfo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Catatan Mahasiswa",
                onMenuClick = { tampilkanDialogInfo = true } // <-- Saat garis 3 dipencet, dialog muncul!
            )
        },
        bottomBar = { AppBottomBar(navController, "beranda") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("tambah") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Default.Add, contentDescription = "Tambah catatan") }
        }
    ) { padding ->
        BerandaList(
            state = state,
            onItemClick = { navController.navigate("detail/${it.id}") },
            modifier = Modifier.padding(padding).fillMaxSize()
        )
    }

    // Dialog Info Pembuat (Kreativitas & Fungsionalitas Tambahan)
    if (tampilkanDialogInfo) {
        AlertDialog(
            onDismissRequest = { tampilkanDialogInfo = false },
            title = { Text("Tentang Aplikasi") },
            text = {
                Text("Aplikasi Catatan Mahasiswa\nNama: Shabira Sylvani Maulidia\nNIM: 147, 148, 149\nUniversitas Muhammadiyah Sidoarjo")
            },
            confirmButton = {
                Button(onClick = { tampilkanDialogInfo = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}
// Isi daftar dipisah dari Scaffold supaya bisa dipakai di panel kiri tablet
@Composable
fun BerandaList(
    state: AppState,
    onItemClick: (Catatan) -> Unit,
    modifier: Modifier = Modifier
) {
    // State untuk menyimpan teks yang diketik di kolom pencarian
    var queryPencarian by remember { mutableStateOf("") }

    // Memfilter daftar catatan berdasarkan judul atau isi yang dicocokkan dengan teks pencarian
    val catatanTersaring = state.catatan.filter {
        it.judul.contains(queryPencarian, ignoreCase = true) ||
                it.isi.contains(queryPencarian, ignoreCase = true)
    }

    LazyColumn( // Membuat daftar yang efisien
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BannerSambutan(jumlah = state.catatan.size) }

        // --- 1. FITUR SEARCH BAR (Kolom Pencarian) ---
        item {
            OutlinedTextField(
                value = queryPencarian,
                onValueChange = { queryPencarian = it },
                placeholder = { Text("Cari catatan perkuliahan...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari"
                    )
                },
                trailingIcon = {
                    if (queryPencarian.isNotEmpty()) {
                        IconButton(onClick = { queryPencarian = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Reset")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { JadwalCard(state) }

        item {
            Text(
                "Catatan Terbaru",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // --- 2. INDIKATOR KOSONG YANG MENARIK (Empty State) ---
        if (state.catatan.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Belum ada catatan kuliah",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Tekan tombol + di bawah untuk mulai mencatat aktivitas perkuliahanmu!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else if (catatanTersaring.isEmpty()) {
            // Kondisi jika kata kunci pencarian tidak ditemukan
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Catatan dengan kata kunci \"$queryPencarian\" tidak ditemukan.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            // Menampilkan daftar catatan yang sudah difilter
            items(catatanTersaring, key = { it.id }) { c ->
                CatatanItem(c) { onItemClick(c) }
            }
        }

        item { Spacer(Modifier.height(88.dp)) }
    }
}
// Kartu sambutan
@Composable
fun BannerSambutan(jumlah: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Hai Mahasiswa! 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Kamu punya $jumlah catatan perkuliahan. Yuk catat aktivitasmu hari ini!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "Gambar Mahasiswa",
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Satu kartu catatan di daftar
@Composable
fun CatatanItem(catatan: Catatan, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Judul Catatan
            Text(
                text = catatan.judul,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Isi Catatan
            Text(
                text = catatan.isi,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(catatan.jam, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(catatan.tanggal, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(catatan.kategori, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
/* ----- Jadwal kuliah yang bisa ditambah / diubah / dihapus ----- */

@Composable
fun JadwalCard(state: AppState) {
    // null = dialog tertutup, id -1 = tambah jadwal baru, selain itu = ubah jadwal
    var jadwalDiedit by remember { mutableStateOf<Jadwal?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Jadwal Kuliah",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { jadwalDiedit = Jadwal(-1, "", "", "Perkuliahan") }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Tambah")
                }
            }

            if (state.jadwal.isEmpty()) {
                Text(
                    "Belum ada jadwal.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Diurutkan otomatis berdasarkan jam
            state.jadwal.sortedBy { it.jam }.forEach { j ->
                JadwalRow(
                    jadwal = j,
                    onEdit = { jadwalDiedit = j },
                    onHapus = { state.hapusJadwal(j.id) }
                )
            }
        }
    }

    // Dialog muncul jika jadwalDiedit tidak null
    jadwalDiedit?.let { j ->
        JadwalDialog(
            awal = j,
            onBatal = { jadwalDiedit = null },
            onSimpan = { jam, matkul, kategori ->
                if (j.id == -1) state.tambahJadwal(jam, matkul, kategori)
                else state.ubahJadwal(j.id, jam, matkul, kategori)
                jadwalDiedit = null
            }
        )
    }
}

// Satu baris jadwal
@Composable
fun JadwalRow(jadwal: Jadwal, onEdit: () -> Unit, onHapus: () -> Unit) {
    val warna = warnaKategori(jadwal.kategori)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(warna.copy(alpha = 0.12f))
            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 5.dp, height = 36.dp)
                .clip(RoundedCornerShape(50))
                .background(warna)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                jadwal.matkul,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "${jadwal.jam}  •  ${jadwal.kategori}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Ubah jadwal", tint = MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = onHapus) {
            Icon(Icons.Default.Delete, contentDescription = "Hapus jadwal", tint = MaterialTheme.colorScheme.error)
        }
    }
}

// Dialog input/edit jadwal
@Composable
fun JadwalDialog(
    awal: Jadwal,
    onBatal: () -> Unit,
    onSimpan: (jam: String, matkul: String, kategori: String) -> Unit
) {
    var jam by rememberSaveable { mutableStateOf(awal.jam) }
    var matkul by rememberSaveable { mutableStateOf(awal.matkul) }
    var kategori by rememberSaveable { mutableStateOf(awal.kategori) }

    AlertDialog(
        onDismissRequest = onBatal,
        title = { Text(if (awal.id == -1) "Tambah Jadwal" else "Ubah Jadwal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = jam,
                    onValueChange = { jam = it },
                    label = { Text("Jam (contoh: 09.00)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = matkul,
                    onValueChange = { matkul = it },
                    label = { Text("Mata kuliah") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    daftarKategori.forEach { k ->
                        FilterChip(
                            selected = kategori == k,
                            onClick = { kategori = k },
                            label = { Text(k) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSimpan(jam.trim(), matkul.trim(), kategori) },
                enabled = jam.isNotBlank() && matkul.isNotBlank()
            ) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onBatal) { Text("Batal") } }
    )
}

/* =====================================================================
 *  6. TAMBAH / EDIT CATATAN
 * ===================================================================== */

@Composable
fun FormCatatanScreen(vm: AppViewModel, navController: NavController, idCatatan: Int?) {
    val draft = vm.draft
    val catatanLama = idCatatan?.let { vm.state.cariCatatan(it) }

    // Mempersiapkan data form jika sedang mode edit atau tambah baru.
    // Teks draft disimpan di dalam ViewModel (FormDraft), sehingga BERTAHAN saat rotasi layar
    // (Portrait <-> Landscape) tanpa keluar dari halaman Tambah Catatan.
    LaunchedEffect(idCatatan) {
        draft.siapkan(idCatatan, catatanLama)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (idCatatan == null) "Tambah Catatan" else "Edit Catatan",
                onBack = {
                    draft.bersihkan()
                    navController.popBackStack()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Input Judul Mata Kuliah
            OutlinedTextField(
                value = draft.judul,
                onValueChange = { draft.judul = it },
                label = { Text("Judul Mata Kuliah") },
                placeholder = { Text("Contoh: Pemrograman Mobile") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Pilihan Kategori
            Text("Kategori", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                daftarKategori.forEach { kat ->
                    FilterChip(
                        selected = draft.kategori == kat,
                        onClick = { draft.kategori = kat },
                        label = { Text(kat) }
                    )
                }
            }

            // --- 3. MASUKKAN INPUT JAM DI SINI ---
            OutlinedTextField(
                value = draft.jam,
                onValueChange = { draft.jam = it },
                label = { Text("Jam (Contoh: 08.00)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // 4. Input Isi Catatan
            OutlinedTextField(
                value = draft.isi,
                onValueChange = { draft.isi = it },
                label = { Text("Isi Catatan") },
                placeholder = { Text("Tulis catatan di sini...") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    draft.cobaSimpan = true
                    if (draft.judul.isNotBlank()) {
                        if (idCatatan == null) {
                            vm.state.tambahCatatan(draft.judul, draft.kategori, draft.jam, draft.isi)
                        } else {
                            vm.state.ubahCatatan(idCatatan, draft.judul, draft.kategori, draft.jam, draft.isi)
                        }
                        draft.bersihkan()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Simpan Catatan", fontWeight = FontWeight.Bold)
            }

            // Tombol Batal (Opsional tapi disarankan agar ada)
            OutlinedButton(
                onClick = {
                    draft.bersihkan()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Batal")
            }

            // Spacer bawah agar tombol tidak tertutup bilah navigasi bawah
            Spacer(Modifier.height(80.dp))
        }
    }
}

// Isi form dipisah dari Scaffold, dipakai di HP dan di panel kanan tablet.
@Composable
fun FormCatatanContent(
    state: AppState,
    draft: FormDraft,
    idCatatan: Int?,
    onSelesai: () -> Unit,
    modifier: Modifier = Modifier
) {
    val awal = state.cariCatatan(idCatatan)
    draft.siapkan(idCatatan, awal)   // Isi draft hanya jika form berganti target

    var menuTerbuka by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LabelForm("Judul Mata Kuliah")
        OutlinedTextField(
            value = draft.judul,
            onValueChange = { draft.judul = it },
            placeholder = { Text("Contoh: Pemrograman Mobile") },
            singleLine = true,
            isError = draft.cobaSimpan && draft.judul.isBlank(),
            supportingText = {
                if (draft.cobaSimpan && draft.judul.isBlank()) Text("Judul tidak boleh kosong")
            },
            modifier = Modifier.fillMaxWidth()
        )

        LabelForm("Kategori")
        Box {
            OutlinedButton(
                onClick = { menuTerbuka = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(draft.kategori, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = menuTerbuka, onDismissRequest = { menuTerbuka = false }) {
                daftarKategori.forEach { k ->
                    DropdownMenuItem(
                        text = { Text(k) },
                        onClick = {
                            draft.kategori = k
                            menuTerbuka = false
                        }
                    )
                }
            }
        }

        LabelForm("Isi Catatan")
        OutlinedTextField(
            value = draft.isi,
            onValueChange = { draft.isi = it },
            placeholder = { Text("Tulis catatan di sini...") },
            minLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Tombol Simpan: tambah catatan baru atau ubah catatan yang ada
        Button(
            onClick = {
                draft.cobaSimpan = true
                if (draft.judul.isNotBlank()) {
                    if (awal == null) {
                        state.tambahCatatan(draft.judul.trim(), draft.kategori, draft.jam.trim().ifBlank { "08.00" }, draft.isi.trim())
                    } else {
                        state.ubahCatatan(awal.id, draft.judul.trim(), draft.kategori, draft.jam.trim().ifBlank { "08.00" }, draft.isi.trim())
                    }
                    draft.bersihkan()   // Kosongkan draft setelah tersimpan
                    onSelesai()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Simpan Catatan")
        }

        // Tombol Batal: buang draft lalu keluar dari form
        OutlinedButton(
            onClick = {
                draft.bersihkan()
                onSelesai()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Close, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Batal")
        }
    }
}

/* =====================================================================
 *  7. DETAIL CATATAN
 * ===================================================================== */

// Layar Detail versi HP: kerangka + isi
@Composable
fun DetailScreen(state: AppState, navController: NavController, id: Int) {
    Scaffold(
        topBar = { AppTopBar("Detail Catatan", onBack = { navController.popBackStack() }) }
    ) { padding ->
        DetailContent(
            state = state,
            id = id,
            onEdit = { navController.navigate("edit/$id") },
            onDihapus = { navController.popBackStack() },
            modifier = Modifier.padding(padding)
        )
    }
}

// Isi detail dipisah dari Scaffold, dipakai di HP dan di panel kanan tablet
@Composable
fun DetailContent(
    state: AppState,
    id: Int,
    onEdit: () -> Unit,
    onDihapus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = state.cariCatatan(id)
    var konfirmasiHapus by rememberSaveable { mutableStateOf(false) }

    if (c == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Catatan tidak ditemukan", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        val warna = warnaKategori(c.kategori)
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarHuruf(c.judul, warna, 52.dp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            c.judul,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            c.tanggal,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            LabelForm("Kategori")
            Surface(
                shape = RoundedCornerShape(50),
                color = warna.copy(alpha = 0.2f)
            ) {
                Text(
                    c.kategori,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = warna,
                    fontWeight = FontWeight.SemiBold
                )
            }

            LabelForm("Isi Catatan")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = c.isi.ifBlank { "(tidak ada isi catatan)" },
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Edit")
                }
                Button(
                    onClick = { konfirmasiHapus = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Hapus")
                }
            }
        }
    }

    // Dialog konfirmasi hapus
    if (konfirmasiHapus) {
        AlertDialog(
            onDismissRequest = { konfirmasiHapus = false },
            title = { Text("Hapus catatan?") },
            text = { Text("Catatan yang dihapus tidak bisa dikembalikan.") },
            confirmButton = {
                Button(
                    onClick = {
                        konfirmasiHapus = false
                        onDihapus()
                        state.hapusCatatan(id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) { Text("Hapus") }
            },
            dismissButton = { TextButton(onClick = { konfirmasiHapus = false }) { Text("Batal") } }
        )
    }
}

/* =====================================================================
 *  8. PROFIL (pengaturan tema terang/gelap)
 * ===================================================================== */

// Layar Profil versi HP: kerangka + isi
@Composable
fun ProfilScreen(
    state: AppState,
    navController: NavController,
    modeTema: Int,
    onModeTemaChange: (Int) -> Unit
) {
    Scaffold(
        topBar = { AppTopBar("Profil") },
        bottomBar = { AppBottomBar(navController, "profil") }
    ) { padding ->
        ProfilContent(state, modeTema, onModeTemaChange, Modifier.padding(padding))
    }
}

// Isi profil dipisah dari Scaffold, dipakai di HP dan di tablet
@Composable
fun ProfilContent(
    state: AppState,
    modeTema: Int,
    onModeTemaChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Kartu profil + statistik
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    "Mahasiswa",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "KampusKu - Catatan Mahasiswa",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Catatan", state.catatan.size.toString())
                    StatItem("Jadwal", state.jadwal.size.toString())
                }
            }
        }

        // Kartu pilihan tema
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Tampilan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Ikuti Sistem", "Terang", "Gelap").forEachIndexed { index, label ->
                        FilterChip(
                            selected = modeTema == index,
                            onClick = { onModeTemaChange(index) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Daftar Tugas & Kelompok Kuliah",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                daftarTugasKelompok.forEach { tugas ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    tugas.matkul,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    tugas.status,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (tugas.status) {
                                        "Hari ini" -> MaterialTheme.colorScheme.tertiary
                                        "Sudah lewat" -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                tugas.deskripsi,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // TAMBAHKAN INI DI BAGIAN PALING BAWAH SUPAYA TIDAK TERTUTUP BAR BAWAH
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun RiwayatScreen(state: AppState, navController: NavController) {
    Scaffold(
        topBar = { AppTopBar("Riwayat Tugas & Kuliah") },
        bottomBar = { AppBottomBar(navController, "riwayat") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Daftar Tugas & Kelompok Kuliah",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            daftarTugasKelompok.forEach { tugas ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Garis warna vertikal di sebelah kiri (seperti di Beranda)
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.primary)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Teks Kiri (Judul, Jam, & Deskripsi)
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = tugas.matkul,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "08.00", // Sesuaikan dengan variabel jam jika ada
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = tugas.deskripsi,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Tombol Aksi Kanan (Edit & Hapus)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { navController.navigate("edit/${tugas.id}") }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(onClick = { daftarTugasKelompok.remove(tugas) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

// Angka statistik
@Composable
fun StatItem(label: String, nilai: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            nilai,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/* =====================================================================
 *  9. LAYOUT TABLET (dua panel)
 *  Kiri: daftar catatan. Kanan: detail / form tambah / form edit.
 * ===================================================================== */

@Composable
fun TabletApp(vm: AppViewModel, modeTema: Int, onModeTemaChange: (Int) -> Unit) {
    val state = vm.state

    Scaffold(
        topBar = { AppTopBar("Catatan Mahasiswa") }
    ) { padding ->
        Row(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Menu samping (NavigationRail untuk Tablet)
            NavigationRail {
                NavigationRailItem(
                    selected = vm.tab == "beranda",
                    onClick = { vm.tab = "beranda" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda") }
                )

                Spacer(Modifier.height(8.dp))

                NavigationRailItem(
                    selected = vm.tab == "riwayat",
                    onClick = { vm.tab = "riwayat" },
                    icon = { Icon(Icons.Default.History, contentDescription = "Riwayat") },
                    label = { Text("Riwayat") }
                )

                Spacer(Modifier.height(8.dp))

                NavigationRailItem(
                    selected = vm.tab == "profil",
                    onClick = { vm.tab = "profil" },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil") }
                )
            }

            if (vm.tab == "profil") {
                ProfilContent(state, modeTema, onModeTemaChange, Modifier.weight(1f).fillMaxHeight())
            } else if (vm.tab == "riwayat") {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Daftar Tugas & Kelompok Kuliah",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    daftarTugasKelompok.forEach { tugas ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        tugas.matkul,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        tugas.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = when (tugas.status) {
                                            "Hari ini" -> MaterialTheme.colorScheme.tertiary
                                            "Sudah lewat" -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    tugas.deskripsi,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                // Panel kiri (60%): daftar catatan
                BerandaList(
                    state = state,
                    onItemClick = { vm.buka(PanelMode.Detail, it.id) },
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                )

                // Garis pemisah
                VerticalDivider()

                // Panel kanan (40%): isi tergantung mode
                Box(
                    Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                ) {
                    when (vm.panel) {
                        PanelMode.Kosong -> Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Pilih catatan untuk melihat detail",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        PanelMode.Detail -> DetailContent(
                            state = state,
                            id = vm.idDipilih ?: -1,
                            onEdit = { vm.buka(PanelMode.Edit, vm.idDipilih) },
                            onDihapus = { vm.buka(PanelMode.Kosong) }
                        )

                        PanelMode.Tambah -> FormCatatanContent(
                            state = state,
                            draft = vm.draft,
                            idCatatan = null,
                            onSelesai = { vm.buka(PanelMode.Kosong) }
                        )

                        PanelMode.Edit -> FormCatatanContent(
                            state = state,
                            draft = vm.draft,
                            idCatatan = vm.idDipilih,
                            onSelesai = { vm.buka(PanelMode.Detail, vm.idDipilih) }
                        )
                    }
                }
            }
        }
    }
}/* =====================================================================
 *  10. PREVIEW
 * ===================================================================== */

@Preview(showBackground = true, showSystemUi = true, name = "HP Terang", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewHP() {
    KampusKuTheme(darkTheme = isSystemInDarkTheme()) { KampusKuApp(modeTema = 0, onModeTemaChange = {}) }
}

// Preview tampilan tablet sesuai spesifikasi tablet (Landscape 1280x800)
@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.TABLET,
    name = "Tablet Mode Terang",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun PreviewTablet() {
    KampusKuTheme(darkTheme = false) { KampusKuApp(modeTema = 1, onModeTemaChange = {}) }
}