import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class DatabaseHealthCheck(private val dataSource: DataSource) {

    @EventListener(ApplicationReadyEvent::class)
    fun checkDatabaseConnection() {
        var attempts = 0
        val maxAttempts = 10
        val delayMs = 2000L

        while (attempts < maxAttempts) {
            try {
                dataSource.connection.use { conn ->
                    if (conn.isValid(5)) {
                        println("Database connection established to jeeb_store")
                        return
                    }
                }
            } catch (e: Exception) {
                println("Waiting for MySQL at localhost:3306... Attempt ${attempts + 1}/$maxAttempts")
                Thread.sleep(delayMs)
                attempts++
            }
        }
        throw RuntimeException("Failed to connect to MySQL after $maxAttempts attempts")
    }
}