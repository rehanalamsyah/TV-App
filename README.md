# TVApp — TV Show Browser

A modern Android TV show browser app built with **Kotlin + Jetpack Compose**, using the [TVMaze public API](https://www.tvmaze.com/api).

> 🎬 Video walkthrough: *https://drive.google.com/file/d/1T4WAfCLPrvLpqA4D8BJ0OEuycY5F9JIT/view?usp=sharing*

---

## Features

- 📺 **List Screen (Dark Navy Modern UI)**
  - Top header dengan judul acara dan badge counter total shows (misal: `240 shows`)
  - **Search Bar real-time** ("Cari judul atau genre...") untuk pencarian instan
  - **Horizontal Cards** menampilkan:
    - Poster film dengan rasio portrait 2:3
    - Judul acara TV
    - Rating badge kotak (`★ 6.6`) dan tahun premiere
    - Daftar genre dipisahkan tanda baca titik tengah (`•`)
    - Tautan interaktif `Detail →`
- 📄 **Detail Screen (Rich Media & Stats)**
  - Hero backdrop poster dengan efek dark gradient overlay
  - Genre pills dengan styling rounded purple/indigo
  - Rating chip `★ 6.6 / 10`, tanggal rilis `📅`, dan estimasi durasi `⏱️`
  - **Synopsis Section** dengan badge modern `✨ HTML Stripped` (menggunakan `HtmlCompat.fromHtml`)
  - Quick metadata info card: Status penayangan, Network penyiar, dan Bahasa
- 🌟 **Bonus Features Terpenuhi (Musim, Episode & Pemeran)**
  - **Cast & Crew Section**: LazyRow horizontal kartu profil pemeran dengan foto avatar bundar, nama aktor/aktris, dan nama peran karakter
  - **Seasons & Episodes Section**: Tab seleksi musim (Season 1, Season 2, dst.) dan daftar episode lengkap per musimnya
- ⚡ **3 State UI Eksplisit**: Loading spinner, Error view dengan tombol Coba Lagi (Retry), dan Success view
- 🔗 **Share Action**: Membagikan judul, ringkasan teks bersih, dan URL show via Android share sheet

---

## How to Run

### Prerequisites
- Android Studio Hedgehog atau versi yang lebih baru
- Android SDK API 24+
- Java 17 atau Java 20/21 (Corretto JDK)

### Steps
1. Clone repository ini:
   ```bash
   git clone <repo-url>
   cd TVApp
   ```
2. Buka project di Android Studio.
3. Tunggu hingga proses Gradle Sync selesai.
4. Jalankan aplikasi di emulator atau perangkat fisik (API 24+):
   - Klik tombol **Run ▶** (Shift + F10)
   - Atau install via terminal: `./gradlew installDebug`

### Run Tests
```bash
./gradlew test
```

---

## Architecture

Aplikasi ini menggunakan pola arsitektur **MVVM (Model-View-ViewModel)** dengan **Repository Pattern**.

```
com.dicoding.tvapp/
├── data/
│   ├── model/              # Data classes: TvShow, TvShowDetail, Season, Episode, CastMember
│   ├── remote/             # Retrofit interface (TvMazeApiService) + singleton (RetrofitInstance)
│   └── repository/         # TvShowRepository — menangani query embed data API
├── ui/
│   ├── list/               # ShowListViewModel + ShowListScreen (Search & Filter)
│   ├── detail/             # ShowDetailViewModel + ShowDetailScreen (Hero banner, Cast, Episodes)
│   └── theme/              # Theme.kt, Color.kt (Dark Slate & Navy Palette)
├── navigation/             # NavGraph.kt — Navigation Compose dengan route type-safe
└── MainActivity.kt         # Entry point dengan Edge-To-Edge dan TVAppTheme
```

### Key Decisions

| Keputusan | Alasan / Rasional |
|---|---|
| **Jetpack Compose + Material 3** | UI modern deklaratif yang modular dan konsisten dengan Dark Palette |
| **MVVM + StateFlow** | Memisahkan business logic dari UI, reactive state flow yang lifecycle-safe |
| **TVMaze Embed Query** | Mengambil detail show, musim, episode, dan cast sekaligus dalam 1 request cepat |
| **HtmlCompat** | Membersihkan tag HTML dari ringkasan tanpa perlu library eksternal berat |
| **Coil Compose** | Image loading async yang ringan dan native untuk Compose dengan auto-cache |
| **Instant Search with `combine`** | Menggabungkan StateFlow list show dengan query pencarian secara reaktif |

---

## What I'd Improve with More Time

1. **Pagination** — Menambahkan infinite scroll untuk memuat seluruh katalog API TVMaze
2. **Hilt Dependency Injection** — Menggunakan Hilt untuk manajemen dependency injection yang lebih skalabel
3. **Offline Caching (Room DB)** — Menyimpan show favorit dan cache offline untuk akses tanpa kuota
4. **Shimmer Skeleton Effect** — Menambahkan animasi placeholder saat state loading pertama kali

---

## Dependencies

| Library | Versi | Fungsi |
|---|---|---|
| Retrofit & Converter Gson | 2.11.0 | HTTP Client & JSON Parser |
| OkHttp Logging Interceptor | 4.12.0 | HTTP Network Logger |
| Coil Compose | 2.7.0 | Async Image Loader |
| Navigation Compose | 2.9.0 | In-app Navigation |
| ViewModel Compose | 2.10.0 | MVVM Lifecycle State |
| Kotlinx Coroutines | 1.10.2 | Asynchronous Programming |
