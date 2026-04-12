package cz.tomasbrand.nasagallery.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory actual constructor() {
    private lateinit var context: Context

    constructor(context: Context) : this() {
        this.context = context
    }

    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(NasaDatabase.Schema, context, "nasa_gallery.db")
}
