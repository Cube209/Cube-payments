package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Creator
import com.example.data.model.Cube
import com.example.data.model.Payout
import com.example.data.model.SecurityLog
import com.example.data.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        Creator::class,
        Cube::class,
        Transaction::class,
        Payout::class,
        SecurityLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CubeDatabase : RoomDatabase() {
    abstract fun creatorDao(): CreatorDao
    abstract fun cubeDao(): CubeDao
    abstract fun transactionDao(): TransactionDao
    abstract fun payoutDao(): PayoutDao
    abstract fun securityLogDao(): SecurityLogDao

    companion object {
        @Volatile
        private var INSTANCE: CubeDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CubeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CubeDatabase::class.java,
                    "cube_payment_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(db: CubeDatabase) {
            val demoCreatorId = "creator-alex-prime"
            val demoCreator = Creator(
                id = demoCreatorId,
                username = "alex_prime",
                email = "alex@cubepay.io",
                passwordHash = "demo123",
                displayName = "Alex Vance",
                bio = "Digital Product Creator & 3D Artist. Building the future of frictionless digital commerce.",
                avatarId = 1,
                usernameColor = "#00F0FF",
                hasUsernameColorPack = true,
                hasDecorationPack = true,
                bankName = "Chase Private Client",
                bankAccountNumber = "•••• 9402",
                bankRouting = "021000021",
                balance = 1420.00,
                isEmailVerified = true,
                isTwoFactorEnabled = true,
                createdAt = System.currentTimeMillis() - 86400000L * 30
            )
            db.creatorDao().insertCreator(demoCreator)

            // Seed sample cubes
            val cube1 = Cube(
                id = "cb-3d-masterclass",
                creatorId = demoCreatorId,
                name = "Blender 3D Cyber Masterclass",
                description = "Complete 12-hour video curriculum with project assets, shaders, and isometric lighting kits.",
                colorHex = "#00F0FF",
                price = 49.00,
                currency = "USD",
                status = "PUBLISHED",
                expirationType = "NEVER",
                expiresAt = 0L,
                borderStyle = "GLOWING",
                backgroundStyle = "GRADIENT_CYBER",
                fontStyle = "CYBER",
                effectStyle = "SPARKLE",
                themeStyle = "CYBERPUNK",
                avatarId = 1,
                createdAt = System.currentTimeMillis() - 86400000L * 5
            )

            val cube2 = Cube(
                id = "cb-dev-consult",
                creatorId = demoCreatorId,
                name = "1-on-1 Architecture Consultation",
                description = "Direct 60-minute technical deep dive into scalable Kotlin, microservices, or mobile apps.",
                colorHex = "#8B5CF6",
                price = 150.00,
                currency = "USD",
                status = "PUBLISHED",
                expirationType = "CUSTOM",
                // Expires in 7 days
                expiresAt = System.currentTimeMillis() + 86400000L * 7,
                borderStyle = "GOLDEN",
                backgroundStyle = "GRADIENT_SPACE",
                fontStyle = "ELEGANT",
                effectStyle = "NEON_PULSE",
                themeStyle = "OBSIDIAN_GOLD",
                avatarId = 2,
                createdAt = System.currentTimeMillis() - 86400000L * 2
            )

            val cube3 = Cube(
                id = "cb-synth-bundle",
                creatorId = demoCreatorId,
                name = "Retro Synthwave Sample Pack",
                description = "Over 400 royalty-free drum stems, bass loops, and analog synth presets.",
                colorHex = "#EC4899",
                price = 19.99,
                currency = "EUR",
                status = "PAUSED",
                expirationType = "NEVER",
                expiresAt = 0L,
                borderStyle = "NEON",
                backgroundStyle = "GRADIENT_SUNSET",
                fontStyle = "PLAYFUL",
                effectStyle = "NONE",
                themeStyle = "NEON_GENESIS",
                avatarId = 3,
                createdAt = System.currentTimeMillis() - 86400000L * 10
            )

            db.cubeDao().insertCube(cube1)
            db.cubeDao().insertCube(cube2)
            db.cubeDao().insertCube(cube3)

            // Seed sample transactions
            val tx1 = Transaction(
                id = "tx-98214-a",
                cubeId = cube1.id,
                creatorId = demoCreatorId,
                cubeName = cube1.name,
                buyerName = "Elena Rostova",
                buyerEmail = "elena.design@gmail.com",
                amount = 49.00,
                currency = "USD",
                processingFee = 1.72,
                netAmount = 47.28,
                status = "COMPLETED",
                paymentMethod = "Apple Pay / Credit Card",
                createdAt = System.currentTimeMillis() - 86400000L * 2
            )

            val tx2 = Transaction(
                id = "tx-98215-b",
                cubeId = cube2.id,
                creatorId = demoCreatorId,
                cubeName = cube2.name,
                buyerName = "Marcus Zhao",
                buyerEmail = "marcus.zhao@techcorp.io",
                amount = 150.00,
                currency = "USD",
                processingFee = 4.65,
                netAmount = 145.35,
                status = "COMPLETED",
                paymentMethod = "Google Pay",
                createdAt = System.currentTimeMillis() - 3600000L * 14
            )

            db.transactionDao().insertTransaction(tx1)
            db.transactionDao().insertTransaction(tx2)

            // Seed security log
            db.securityLogDao().insertLog(
                SecurityLog(
                    id = UUID.randomUUID().toString(),
                    creatorId = demoCreatorId,
                    eventType = "LOGIN",
                    description = "Successful biometric session authentication",
                    ipLocation = "San Francisco, CA, US",
                    deviceInfo = "Pixel 8 Pro • Android 14"
                )
            )
        }
    }
}
