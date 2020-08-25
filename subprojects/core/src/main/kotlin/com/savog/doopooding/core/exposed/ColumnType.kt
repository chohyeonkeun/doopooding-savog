package com.savog.doopooding.core.exposed

import com.vividsolutions.jts.geom.Geometry
import org.geolatte.geom.ByteBuffer
import org.geolatte.geom.ByteOrder
import org.geolatte.geom.codec.Wkb
import org.geolatte.geom.codec.Wkt
import org.geolatte.geom.jts.JTS
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.joda.time.DateTime
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

inline fun <reified OldType : Any, reified NewType : Any> IColumnType.convert(
    noinline converter: (OldType) -> NewType,
    noinline reverseConverter: (NewType) -> OldType,
    oldType: KClass<OldType> = OldType::class,
    newType: KClass<NewType> = NewType::class
): IColumnType {
    return convertInternal(converter, reverseConverter, oldType, newType)
}

fun <OldType : Any, NewType : Any> IColumnType.convertInternal(
    converter: (OldType) -> NewType,
    reverseConverter: (NewType) -> OldType,
    oldType: KClass<OldType>,
    newType: KClass<NewType>
): IColumnType {
    val delegate = this
    return object : IColumnType {

        override var nullable: Boolean
            get() = delegate.nullable
            set(value) { delegate.nullable = value }

        override fun sqlType(): String {
            return delegate.sqlType()
        }

        override fun valueToString(value: Any?): String = when (value) {
            null -> {
                if (!nullable) error("NULL in non-nullable column")
                "NULL"
            }

            else -> {
                nonNullValueToString(value)
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun nonNullValueToString(value: Any): String {
            return if (newType.isInstance(value)) {
                delegate.nonNullValueToString(reverseConverter(value as NewType))
            } else {
                delegate.nonNullValueToString(value)
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun notNullValueToDB(value: Any): Any {
            return if (newType.isInstance(value)) {
                delegate.notNullValueToDB(reverseConverter(value as NewType))
            } else {
                delegate.notNullValueToDB(value)
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun valueFromDB(value: Any): Any {
            val converted = delegate.valueFromDB(value)
            return if (oldType.isInstance(converted)) {
                converter(converted as OldType)
            } else {
                converted
            }
        }
    }
}

inline fun <reified OldType : Any, reified NewType : Any> Column<OldType>.convert(
    noinline converter: (OldType) -> NewType,
    noinline reverseConverter: (NewType) -> OldType,
    oldType: KClass<OldType> = OldType::class,
    newType: KClass<NewType> = NewType::class
): Column<NewType> {
    return this.table.replaceColumn(this, Column(this.table, this.name, this.columnType.convert(converter, reverseConverter, oldType, newType)))
}

/**
 * DATETIME 타입일 때, joda time 라이브러리의 DateTime 대신 java 8의 Instant를 사용할 수 있도록 만들어줍니다.
 */
fun Column<org.joda.time.DateTime>.toJavaInstant(): Column<Instant> {
    return convert({ Instant.ofEpochMilli(it.millis) }, { DateTime(it.toEpochMilli()) })
}

fun Column<org.joda.time.DateTime>.toJavaLocalDate(): Column<LocalDate> {
    return convert({ toLocalDate(it.millis) }, { DateTime(it.toEpochMilli()) })
}

internal fun LocalDate.toEpochMilli(): Long {
    return ZonedDateTime.of(this, LocalTime.MIN, ZoneId.systemDefault()).toInstant().toEpochMilli()
}

internal fun toLocalDate(epochMilli: Long): LocalDate {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault()).toLocalDate()
}

fun Column<Int>.toBoolean(): Column<Boolean> {
    return convert({ it != 0 }, { if (it) { 1 } else { 0 } })
}

inline fun <reified T> Column<Int>.toIntEnumeration(klass: Class<T>, defaultValue: T? = null): Column<T>
        where T : Enum<T>, T : EnumIntColumn {
    return convert({ number -> klass.enumConstants!!.firstOrNull { it.number == number } ?: defaultValue ?: throw IllegalStateException() }, { it.number })
}

inline fun <reified T> Column<String>.toStringEnumeration(klass: Class<T>, defaultValue: T? = null): Column<T>
        where T : Enum<T>, T : EnumStringColumn {
    return convert({ alias -> klass.enumConstants!!.firstOrNull { it.alias == alias } ?: defaultValue ?: throw IllegalStateException() }, { it.alias })
}

fun <T> Column<String>.toSetEnumeration(klass: Class<T>, defaultValue: T? = null): Column<Set<T>>
        where T : Enum<T>, T : EnumStringColumn {
    return convert(
        { values ->
            values.split(",")
                .map { it.trim() }
                .map { value ->
                    klass.enumConstants!!.firstOrNull { it.alias == value } ?: defaultValue ?: throw IllegalStateException()
                }
                .toSet()
        },
        {
            it.joinToString(separator = ",") { enumInstance -> enumInstance.alias }
        }
    )
}

inline fun <reified T> Column<Char>.toCharEnumeration(klass: Class<T>, defaultValue: T? = null): Column<T>
        where T : Enum<T>, T : EnumCharColumn {
    return convert({ alias -> klass.enumConstants!!.firstOrNull { it.alias == alias } ?: defaultValue ?: throw IllegalStateException() }, { it.alias })
}

fun Column<BigDecimal>.toDouble(): Column<Double> {
    return convert({ it.toDouble() }, { BigDecimal(it) })
}

fun Column<String>.toLocalTime(formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")): Column<LocalTime> {
    return convert({ formatter.parse(it, LocalTime::from) }, { formatter.format(it) })
}

abstract class AbstractGeometryColumnType : ColumnType() {
    override fun notNullValueToDB(value: Any): Any {
        val encoder = Wkb.newEncoder(Wkb.Dialect.MYSQL_WKB)
        return when (value) {
            is Geometry -> {
                val geometry = JTS.from(value)
                val buffer = encoder.encode(geometry, ByteOrder.NDR)
                buffer.toByteArray()
            }
            else -> error("$value of ${value::class.qualifiedName} is not valid")
        }
    }

    override fun valueFromDB(value: Any): Any {
        val decoder = Wkb.newDecoder(Wkb.Dialect.MYSQL_WKB)

        return when (value) {
            is ByteArray -> {
                val buffer = ByteBuffer.from(value)
                JTS.to(decoder.decode(buffer))
            }
            is String -> {
                if (value.startsWith("00") || value.startsWith("01")) {
                    val buffer = ByteBuffer.from(value)
                    JTS.to(decoder.decode(buffer))
                } else {
                    val wktDecoder = Wkt.newDecoder(Wkt.Dialect.MYSQL_WKT)
                    JTS.to(wktDecoder.decode(value))
                }
            }
            else -> error("$value of ${value::class.qualifiedName} is not valid")
        }
    }
}

class PolygonColumnType : AbstractGeometryColumnType() {
    override fun sqlType(): String {
        return "POLYGON"
    }
}

class MultiPolygonColumnType : AbstractGeometryColumnType() {
    override fun sqlType(): String {
        return "MULTIPOLYGON"
    }
}

class PointColumnType : AbstractGeometryColumnType() {
    override fun sqlType(): String {
        return "POINT"
    }
}

class TimeColumnType : ColumnType() {
    override fun sqlType(): String {
        return "TIME"
    }

    override fun nonNullValueToString(value: Any): String {
        if (value is String) return value
        if (value is LocalTime) return value.toString()

        val time = when (value) {
            is java.sql.Time -> value.toLocalTime()
            else -> error("Unexpected value: $value of ${value::class.qualifiedName}")
        }

        return time.toString()
    }

    override fun valueFromDB(value: Any): Any = when (value) {
        is LocalTime -> value
        is java.sql.Time -> value.toLocalTime()
        else -> DateTimeFormatter.ISO_LOCAL_TIME.parse(value.toString(), LocalTime::from)
    }

    override fun notNullValueToDB(value: Any): Any {
        if (value is LocalTime) {
            return java.sql.Time.valueOf(value)
        }
        return value
    }
}

class ColumnExpression<T>(private val column: Column<T>) : Expression<T>() {
    val name: String
        get() = column.name

    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder.append(TransactionManager.current().fullIdentity(column))
    }

    override fun toString(): String {
        return "${column.table.tableName}.${column.name}"
    }
}

// select "constant" as field from table .. 형태의 쿼리가 필요할 경우 사용.
class ConstantAliasExpression<T>(private val value: T, private val alias: String) : Expression<T>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder.append("$value as `$alias`")
    }

    override fun toString(): String {
        return "$value as `$alias`"
    }
}

// left join시 NotNull인 Column의 select 결과가 null일 경우 사용.
fun <T : Any> Column<T>.nullable(): Expression<T> {
    return ColumnExpression<T>(this)
}

// DB 암호화, 복호화
class SecureString(colLength: Int = 255, collate: String? = null) : VarCharColumnType(colLength, collate) {
    override fun valueToDB(value: Any?): Any? {
        if (value is String && value.isNotEmpty()) return Cipher.encrypt(
            value
        )
        return null
    }
    override fun valueFromDB(value: Any): Any {
        return Cipher.decrypt(value.toString())
    }
}