package org.jetbrains.exposed.rdbc

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.nio.ByteBuffer
import java.sql.*
import java.sql.Array
import java.sql.Date
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Suppress("TooManyFunctions")
internal class ResultSetEmulator(internal val rows: List<List<Pair<String, Any?>>>) : ResultSet {

    private val iterator = rows.iterator()
    private var currentRow: List<Any?>? = null
    private val columnsMapping by lazy {
        rows.firstOrNull().orEmpty().mapIndexed { index, value -> value.first to index + 1 }
            .groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            ).filterValues { it.size == 1 }.mapValues { it.value.single() }
    }

    override fun <T : Any?> unwrap(iface: Class<T>?): T = TODO()

    override fun isWrapperFor(iface: Class<*>?): Boolean {
        TODO("not implemented")
    }

    override fun close() {
        /* nothing */
    }

    override fun next(): Boolean {
        return if (iterator.hasNext()) {
            currentRow = iterator.next().map { it.second }
            true
        } else {
            currentRow = null
            false
        }
    }

    private fun <T : Any> get(columnIndex: Int) = currentRow?.get(columnIndex - 1) as? T
    private fun <T : Any> get(columnName: String) = columnsMapping[columnName]?.let { currentRow?.get(it - 1) } as? T

    override fun wasNull(): Boolean {
        TODO("not implemented")
    }

    override fun getString(columnIndex: Int): String? = get(columnIndex)

    override fun getString(columnLabel: String): String? = get(columnLabel)

    override fun getBoolean(columnIndex: Int): Boolean {
        TODO("not implemented")
    }

    override fun getBoolean(columnLabel: String?): Boolean {
        TODO("not implemented")
    }

    override fun getByte(columnIndex: Int): Byte {
        TODO("not implemented")
    }

    override fun getByte(columnLabel: String?): Byte {
        TODO("not implemented")
    }

    override fun getShort(columnIndex: Int): Short {
        TODO("not implemented")
    }

    override fun getShort(columnLabel: String?): Short {
        TODO("not implemented")
    }

    override fun getInt(columnIndex: Int): Int = get(columnIndex)!!

    override fun getInt(columnLabel: String): Int = get(columnLabel)!!

    override fun getLong(columnIndex: Int): Long = get(columnIndex)!!

    override fun getLong(columnLabel: String): Long = get(columnLabel)!!

    override fun getFloat(columnIndex: Int): Float {
        TODO("not implemented")
    }

    override fun getFloat(columnLabel: String?): Float {
        TODO("not implemented")
    }

    override fun getDouble(columnIndex: Int): Double {
        TODO("not implemented")
    }

    override fun getDouble(columnLabel: String?): Double {
        TODO("not implemented")
    }

    override fun getBigDecimal(columnIndex: Int, scale: Int): BigDecimal {
        TODO("not implemented")
    }

    override fun getBigDecimal(columnLabel: String?, scale: Int): BigDecimal {
        TODO("not implemented")
    }

    override fun getBigDecimal(columnIndex: Int): BigDecimal? = get(columnIndex)

    override fun getBigDecimal(columnLabel: String?): BigDecimal {
        TODO("not implemented")
    }

    override fun getBytes(columnIndex: Int): ByteArray? {
        val value = get<Any>(columnIndex) ?: return null
        return (value as? ByteBuffer)?.array() ?: value as ByteArray
    }

    override fun getBytes(columnLabel: String): ByteArray? {
        val value = get<Any>(columnLabel) ?: return null
        return (value as? ByteBuffer)?.array() ?: value as ByteArray
    }

    override fun getDate(columnIndex: Int): Date {
        TODO("not implemented")
    }

    override fun getDate(columnLabel: String?): Date {
        TODO("not implemented")
    }

    override fun getDate(columnIndex: Int, cal: Calendar?): Date {
        TODO("not implemented")
    }

    override fun getDate(columnLabel: String?, cal: Calendar?): Date {
        TODO("not implemented")
    }

    override fun getTime(columnIndex: Int): Time {
        TODO("not implemented")
    }

    override fun getTime(columnLabel: String?): Time {
        TODO("not implemented")
    }

    override fun getTime(columnIndex: Int, cal: Calendar?): Time {
        TODO("not implemented")
    }

    override fun getTime(columnLabel: String?, cal: Calendar?): Time {
        TODO("not implemented")
    }

    override fun getTimestamp(columnIndex: Int): Timestamp? {
        return when (val value = get<Any>(columnIndex)) {
            is Instant -> Timestamp(value.toEpochMilli())
            is LocalDateTime -> Timestamp(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            else -> null
        }
    }

    override fun getTimestamp(columnLabel: String?): Timestamp {
        TODO("not implemented")
    }

    override fun getTimestamp(columnIndex: Int, cal: Calendar?): Timestamp {
        TODO("not implemented")
    }

    override fun getTimestamp(columnLabel: String?, cal: Calendar?): Timestamp {
        TODO("not implemented")
    }

    override fun getAsciiStream(columnIndex: Int): InputStream {
        TODO("not implemented")
    }

    override fun getAsciiStream(columnLabel: String?): InputStream {
        TODO("not implemented")
    }

    override fun getUnicodeStream(columnIndex: Int): InputStream {
        TODO("not implemented")
    }

    override fun getUnicodeStream(columnLabel: String?): InputStream {
        TODO("not implemented")
    }

    override fun getBinaryStream(columnIndex: Int): InputStream? {
        return get<java.nio.ByteBuffer>(columnIndex)?.array()?.inputStream()
    }

    override fun getBinaryStream(columnLabel: String): InputStream? {
        return get<java.nio.ByteBuffer>(columnLabel)?.array()?.inputStream()
    }

    override fun getWarnings(): SQLWarning {
        TODO("not implemented")
    }

    override fun clearWarnings() {
        TODO("not implemented")
    }

    override fun getCursorName(): String {
        TODO("not implemented")
    }

    override fun getMetaData(): ResultSetMetaData {
        TODO("not implemented")
    }

    override fun getObject(columnIndex: Int): Any? {
        return when (val value = get<Any>(columnIndex)) {
            is io.r2dbc.spi.Clob -> runBlocking {
                value.stream().awaitFirst()
            }
            else -> value
        }
    }

    override fun getObject(columnLabel: String): Any? = get(columnLabel)

    override fun getObject(columnIndex: Int, map: MutableMap<String, Class<*>>?): Any {
        TODO("not implemented")
    }

    override fun getObject(columnLabel: String?, map: MutableMap<String, Class<*>>?): Any {
        TODO("not implemented")
    }

    override fun <T : Any?> getObject(columnIndex: Int, type: Class<T>?): T {
        TODO("not implemented")
    }

    override fun <T : Any?> getObject(columnLabel: String?, type: Class<T>?): T {
        TODO("not implemented")
    }

    override fun findColumn(columnLabel: String): Int {
        return columnsMapping[columnLabel] ?: throw SQLException("Can't find $columnLabel in result set")
    }

    override fun getCharacterStream(columnIndex: Int): Reader {
        TODO("not implemented")
    }

    override fun getCharacterStream(columnLabel: String?): Reader {
        TODO("not implemented")
    }

    override fun isBeforeFirst(): Boolean {
        TODO("not implemented")
    }

    override fun isAfterLast(): Boolean {
        TODO("not implemented")
    }

    override fun isFirst(): Boolean {
        TODO("not implemented")
    }

    override fun isLast(): Boolean {
        TODO("not implemented")
    }

    override fun beforeFirst() {
        TODO("not implemented")
    }

    override fun afterLast() {
        TODO("not implemented")
    }

    override fun first(): Boolean {
        TODO("not implemented")
    }

    override fun last(): Boolean {
        TODO("not implemented")
    }

    override fun getRow(): Int {
        TODO("not implemented")
    }

    override fun absolute(row: Int): Boolean {
        TODO("not implemented")
    }

    override fun relative(rows: Int): Boolean {
        TODO("not implemented")
    }

    override fun previous(): Boolean {
        TODO("not implemented")
    }

    override fun setFetchDirection(direction: Int) {
        TODO("not implemented")
    }

    override fun getFetchDirection(): Int {
        TODO("not implemented")
    }

    override fun setFetchSize(rows: Int) {
        TODO("not implemented")
    }

    override fun getFetchSize(): Int {
        TODO("not implemented")
    }

    override fun getType(): Int {
        TODO("not implemented")
    }

    override fun getConcurrency(): Int {
        TODO("not implemented")
    }

    override fun rowUpdated(): Boolean {
        TODO("not implemented")
    }

    override fun rowInserted(): Boolean {
        TODO("not implemented")
    }

    override fun rowDeleted(): Boolean {
        TODO("not implemented")
    }

    override fun updateNull(columnIndex: Int) {
        TODO("not implemented")
    }

    override fun updateNull(columnLabel: String?) {
        TODO("not implemented")
    }

    override fun updateBoolean(columnIndex: Int, x: Boolean) {
        TODO("not implemented")
    }

    override fun updateBoolean(columnLabel: String?, x: Boolean) {
        TODO("not implemented")
    }

    override fun updateByte(columnIndex: Int, x: Byte) {
        TODO("not implemented")
    }

    override fun updateByte(columnLabel: String?, x: Byte) {
        TODO("not implemented")
    }

    override fun updateShort(columnIndex: Int, x: Short) {
        TODO("not implemented")
    }

    override fun updateShort(columnLabel: String?, x: Short) {
        TODO("not implemented")
    }

    override fun updateInt(columnIndex: Int, x: Int) {
        TODO("not implemented")
    }

    override fun updateInt(columnLabel: String?, x: Int) {
        TODO("not implemented")
    }

    override fun updateLong(columnIndex: Int, x: Long) {
        TODO("not implemented")
    }

    override fun updateLong(columnLabel: String?, x: Long) {
        TODO("not implemented")
    }

    override fun updateFloat(columnIndex: Int, x: Float) {
        TODO("not implemented")
    }

    override fun updateFloat(columnLabel: String?, x: Float) {
        TODO("not implemented")
    }

    override fun updateDouble(columnIndex: Int, x: Double) {
        TODO("not implemented")
    }

    override fun updateDouble(columnLabel: String?, x: Double) {
        TODO("not implemented")
    }

    override fun updateBigDecimal(columnIndex: Int, x: BigDecimal?) {
        TODO("not implemented")
    }

    override fun updateBigDecimal(columnLabel: String?, x: BigDecimal?) {
        TODO("not implemented")
    }

    override fun updateString(columnIndex: Int, x: String?) {
        TODO("not implemented")
    }

    override fun updateString(columnLabel: String?, x: String?) {
        TODO("not implemented")
    }

    override fun updateBytes(columnIndex: Int, x: ByteArray?) {
        TODO("not implemented")
    }

    override fun updateBytes(columnLabel: String?, x: ByteArray?) {
        TODO("not implemented")
    }

    override fun updateDate(columnIndex: Int, x: Date?) {
        TODO("not implemented")
    }

    override fun updateDate(columnLabel: String?, x: Date?) {
        TODO("not implemented")
    }

    override fun updateTime(columnIndex: Int, x: Time?) {
        TODO("not implemented")
    }

    override fun updateTime(columnLabel: String?, x: Time?) {
        TODO("not implemented")
    }

    override fun updateTimestamp(columnIndex: Int, x: Timestamp?) {
        TODO("not implemented")
    }

    override fun updateTimestamp(columnLabel: String?, x: Timestamp?) {
        TODO("not implemented")
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?, length: Int) {
        TODO("not implemented")
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?, length: Int) {
        TODO("not implemented")
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?, length: Long) {
        TODO("not implemented")
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?, length: Long) {
        TODO("not implemented")
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?) {
        TODO("not implemented")
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?) {
        TODO("not implemented")
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?, length: Int) {
        TODO("not implemented")
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?, length: Int) {
        TODO("not implemented")
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?, length: Long) {
        TODO("not implemented")
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?, length: Long) {
        TODO("not implemented")
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?) {
        TODO("not implemented")
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?) {
        TODO("not implemented")
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?, length: Int) {
        TODO("not implemented")
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?, length: Int) {
        TODO("not implemented")
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?, length: Long) {
        TODO("not implemented")
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?, length: Long) {
        TODO("not implemented")
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?) {
        TODO("not implemented")
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?) {
        TODO("not implemented")
    }

    override fun updateObject(columnIndex: Int, x: Any?, scaleOrLength: Int) {
        TODO("not implemented")
    }

    override fun updateObject(columnIndex: Int, x: Any?) {
        TODO("not implemented")
    }

    override fun updateObject(columnLabel: String?, x: Any?, scaleOrLength: Int) {
        TODO("not implemented")
    }

    override fun updateObject(columnLabel: String?, x: Any?) {
        TODO("not implemented")
    }

    override fun insertRow() {
        TODO("not implemented")
    }

    override fun updateRow() {
        TODO("not implemented")
    }

    override fun deleteRow() {
        TODO("not implemented")
    }

    override fun refreshRow() {
        TODO("not implemented")
    }

    override fun cancelRowUpdates() {
        TODO("not implemented")
    }

    override fun moveToInsertRow() {
        TODO("not implemented")
    }

    override fun moveToCurrentRow() {
        TODO("not implemented")
    }

    override fun getStatement(): Statement? = null

    override fun getRef(columnIndex: Int): Ref {
        TODO("not implemented")
    }

    override fun getRef(columnLabel: String?): Ref {
        TODO("not implemented")
    }

    override fun getBlob(columnIndex: Int): Blob {
        TODO("not implemented")
    }

    override fun getBlob(columnLabel: String?): Blob {
        TODO("not implemented")
    }

    override fun getClob(columnIndex: Int): Clob {
        TODO("not implemented")
    }

    override fun getClob(columnLabel: String?): Clob {
        TODO("not implemented")
    }

    override fun getArray(columnIndex: Int): Array {
        TODO("not implemented")
    }

    override fun getArray(columnLabel: String?): Array {
        TODO("not implemented")
    }

    override fun getURL(columnIndex: Int): URL {
        TODO("not implemented")
    }

    override fun getURL(columnLabel: String?): URL {
        TODO("not implemented")
    }

    override fun updateRef(columnIndex: Int, x: Ref?) {
        TODO("not implemented")
    }

    override fun updateRef(columnLabel: String?, x: Ref?) {
        TODO("not implemented")
    }

    override fun updateBlob(columnIndex: Int, x: Blob?) {
        TODO("not implemented")
    }

    override fun updateBlob(columnLabel: String?, x: Blob?) {
        TODO("not implemented")
    }

    override fun updateBlob(columnIndex: Int, inputStream: InputStream?, length: Long) {
        TODO("not implemented")
    }

    override fun updateBlob(columnLabel: String?, inputStream: InputStream?, length: Long) {
        TODO("not implemented")
    }

    override fun updateBlob(columnIndex: Int, inputStream: InputStream?) {
        TODO("not implemented")
    }

    override fun updateBlob(columnLabel: String?, inputStream: InputStream?) {
        TODO("not implemented")
    }

    override fun updateClob(columnIndex: Int, x: Clob?) {
        TODO("not implemented")
    }

    override fun updateClob(columnLabel: String?, x: Clob?) {
        TODO("not implemented")
    }

    override fun updateClob(columnIndex: Int, reader: Reader?, length: Long) {
        TODO("not implemented")
    }

    override fun updateClob(columnLabel: String?, reader: Reader?, length: Long) {
        TODO("not implemented")
    }

    override fun updateClob(columnIndex: Int, reader: Reader?) {
        TODO("not implemented")
    }

    override fun updateClob(columnLabel: String?, reader: Reader?) {
        TODO("not implemented")
    }

    override fun updateArray(columnIndex: Int, x: Array?) {
        TODO("not implemented")
    }

    override fun updateArray(columnLabel: String?, x: Array?) {
        TODO("not implemented")
    }

    override fun getRowId(columnIndex: Int): RowId {
        TODO("not implemented")
    }

    override fun getRowId(columnLabel: String?): RowId {
        TODO("not implemented")
    }

    override fun updateRowId(columnIndex: Int, x: RowId?) {
        TODO("not implemented")
    }

    override fun updateRowId(columnLabel: String?, x: RowId?) {
        TODO("not implemented")
    }

    override fun getHoldability(): Int {
        TODO("not implemented")
    }

    override fun isClosed(): Boolean = true

    override fun updateNString(columnIndex: Int, nString: String?) {
        TODO("not implemented")
    }

    override fun updateNString(columnLabel: String?, nString: String?) {
        TODO("not implemented")
    }

    override fun updateNClob(columnIndex: Int, nClob: NClob?) {
        TODO("not implemented")
    }

    override fun updateNClob(columnLabel: String?, nClob: NClob?) {
        TODO("not implemented")
    }

    override fun updateNClob(columnIndex: Int, reader: Reader?, length: Long) {
        TODO("not implemented")
    }

    override fun updateNClob(columnLabel: String?, reader: Reader?, length: Long) {
        TODO("not implemented")
    }

    override fun updateNClob(columnIndex: Int, reader: Reader?) {
        TODO("not implemented")
    }

    override fun updateNClob(columnLabel: String?, reader: Reader?) {
        TODO("not implemented")
    }

    override fun getNClob(columnIndex: Int): NClob {
        TODO("not implemented")
    }

    override fun getNClob(columnLabel: String?): NClob {
        TODO("not implemented")
    }

    override fun getSQLXML(columnIndex: Int): SQLXML {
        TODO("not implemented")
    }

    override fun getSQLXML(columnLabel: String?): SQLXML {
        TODO("not implemented")
    }

    override fun updateSQLXML(columnIndex: Int, xmlObject: SQLXML?) {
        TODO("not implemented")
    }

    override fun updateSQLXML(columnLabel: String?, xmlObject: SQLXML?) {
        TODO("not implemented")
    }

    override fun getNString(columnIndex: Int): String {
        TODO("not implemented")
    }

    override fun getNString(columnLabel: String?): String {
        TODO("not implemented")
    }

    override fun getNCharacterStream(columnIndex: Int): Reader {
        TODO("not implemented")
    }

    override fun getNCharacterStream(columnLabel: String?): Reader {
        TODO("not implemented")
    }

    override fun updateNCharacterStream(columnIndex: Int, x: Reader?, length: Long) {
        TODO("not implemented")
    }

    override fun updateNCharacterStream(columnLabel: String?, reader: Reader?, length: Long) {
        TODO("not implemented")
    }

    override fun updateNCharacterStream(columnIndex: Int, x: Reader?) {
        TODO("not implemented")
    }

    override fun updateNCharacterStream(columnLabel: String?, reader: Reader?) {
        TODO("not implemented")
    }
}
