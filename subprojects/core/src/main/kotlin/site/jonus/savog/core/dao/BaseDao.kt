package site.jonus.savog.core.dao

import com.google.common.base.CaseFormat
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionAlias
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.ResultRow
import site.jonus.savog.core.exposed.ColumnExpression
import java.util.regex.Pattern

open class BaseDao {

    fun rowToMap(row: ResultRow, columns: List<Expression<*>>, alias: Map<Expression<*>, String>): Map<String, Any?> {
        return HashMap<String, Any?>(columns.size).apply {
            columns.map { col ->
                val key = when {
                    alias.containsKey(col) -> alias[col]
                    col is Column<*> -> col.name
                    col is ColumnExpression<*> -> col.name
                    col is ExpressionAlias<*> -> col.alias
                    else -> {
                        val builder = QueryBuilder(false)
                        col.toQueryBuilder(builder)
                        builder.toString()
                    }
                }
                val isLowerUnder = Pattern.compile("([_][a-z0-9+])").matcher(key!!).find()
                val name = if (isLowerUnder) CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key) else key
                val value = row[col]
                if (value is EntityID<*>) {
                    this.put(name, value.value)
                } else {
                    this.put(name, value)
                }
            }
        }
    }
}