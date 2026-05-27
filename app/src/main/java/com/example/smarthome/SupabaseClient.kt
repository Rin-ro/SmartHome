import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    private const val SUPABASE_URL = "https://XXXXX.supabase.co" // Вставьте ваш URL
    private const val SUPABASE_ANON_KEY = "ваш_публичный_anon_ключ" // Вставьте ваш anon ключ

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Postgrest) // Для запросов к базе данных
            install(Auth)      // Для управления пользователями
        }
    }
}