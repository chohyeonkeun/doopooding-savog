package com.savog.doopooding.core.exposed

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.DEFAULT_ISOLATION_LEVEL
import org.jetbrains.exposed.sql.transactions.DEFAULT_REPETITION_ATTEMPTS
import org.jetbrains.exposed.sql.transactions.TransactionInterface
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.jdbc.datasource.ConnectionHolder
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.transaction.support.DefaultTransactionStatus
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.sql.Connection
import javax.sql.DataSource

class SpringExposedTransactionManager(
    dataSource: DataSource,
    @Volatile override var defaultIsolationLevel: Int = DEFAULT_ISOLATION_LEVEL,
    @Volatile override var defaultRepetitionAttempts: Int = DEFAULT_REPETITION_ATTEMPTS,
    private val logging: Boolean = false
) : DataSourceTransactionManager(dataSource), TransactionManager {

    private val db = Database.connect(dataSource) { this }
    private val currentThreadOuterManager = ThreadLocal<TransactionManager>()

    override fun doBegin(transaction: Any, definition: TransactionDefinition) {
        super.doBegin(transaction, definition)
        if (TransactionSynchronizationManager.hasResource(dataSource!!)) {
            currentOrNull() ?: initTransaction(null)
        }
    }

    override fun doCleanupAfterCompletion(transaction: Any) {
        super.doCleanupAfterCompletion(transaction)
        if (!TransactionSynchronizationManager.hasResource(dataSource!!)) {
            TransactionSynchronizationManager.unbindResourceIfPossible(this)
        }
        TransactionManager.resetCurrent(currentThreadOuterManager.get())
    }

    override fun doSuspend(transaction: Any): Any {
        TransactionSynchronizationManager.unbindResourceIfPossible(this)
        return super.doSuspend(transaction)
    }

    override fun doCommit(status: DefaultTransactionStatus) {
        currentOrNull()?.commit()
    }

    override fun newTransaction(isolation: Int, outerTransaction: Transaction?): Transaction {
        val tDefinition = if (dataSource!!.connection.transactionIsolation != isolation) {
            DefaultTransactionDefinition().apply { isolationLevel = isolation }
        } else null

        getTransaction(tDefinition)

        return currentOrNull() ?: initTransaction(outerTransaction)
    }

    private fun initTransaction(outerTransaction: Transaction?): Transaction {
        val connection = (TransactionSynchronizationManager.getResource(dataSource!!) as ConnectionHolder).connection
        val transactionImpl = SpringTransaction(
            connection,
            db,
            currentOrNull(),
            outerTransaction?.transactionIsolation ?: defaultIsolationLevel
        )
        currentThreadOuterManager.set(TransactionManager.manager)
        TransactionManager.resetCurrent(this)
        return Transaction(transactionImpl).apply {
            TransactionSynchronizationManager.bindResource(this@SpringExposedTransactionManager, this)
            if (logging) {
                this.addLogger(Slf4jSqlDebugLogger)
            }
        }
    }

    override fun currentOrNull(): Transaction? = TransactionSynchronizationManager.getResource(this) as Transaction?

    private class SpringTransaction(
        override val connection: Connection,
        override val db: Database,
        override val outerTransaction: Transaction?,
        override val transactionIsolation: Int
    ) : TransactionInterface {

        override fun commit() {
            connection.run {
                if (!autoCommit) {
                    commit()
                }
            }
        }

        override fun rollback() {
            connection.rollback()
        }

        override fun close() { }
    }
}