package io.heapy.komodo.execution

/**
 * Provide a [BackOffExecution] that indicates the rate at which
 * an operation should be retried.
 *
 *
 * Users of this interface are expected to use it like this:
 *
 * <pre class="code">
 * BackOffExecution exec = backOff.start();
 *
 * // In the operation recovery/retry loop:
 * long waitInterval = exec.nextBackOff();
 * if (waitInterval == BackOffExecution.STOP) {
 * // do not retry operation
 * }
 * else {
 * // sleep, e.g. Thread.sleep(waitInterval)
 * // retry operation
 * }
 * }</pre>
 *
 * Once the underlying operation has completed successfully,
 * the execution instance can be simply discarded.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @see BackOffExecution
 */
@FunctionalInterface
interface BackOff {

    /**
     * Start a new back off execution.
     * @return a fresh [BackOffExecution] ready to be used
     */
    fun start(): BackOffExecution

}

/**
 * Implementation of [BackOff] that increases the back off period for each
 * retry attempt. When the interval has reached the [ max interval][.setMaxInterval], it is no longer increased. Stops retrying once the
 * [max elapsed time][.setMaxElapsedTime] has been reached.
 *
 *
 * Example: The default interval is {@value #DEFAULT_INITIAL_INTERVAL} ms,
 * the default multiplier is {@value #DEFAULT_MULTIPLIER}, and the default max
 * interval is {@value #DEFAULT_MAX_INTERVAL}. For 10 attempts the sequence will be
 * as follows:
 *
 * <pre>
 * request#     back off
 *
 * 1              2000
 * 2              3000
 * 3              4500
 * 4              6750
 * 5             10125
 * 6             15187
 * 7             22780
 * 8             30000
 * 9             30000
 * 10             30000
</pre> *
 *
 *
 * Note that the default max elapsed time is [Long.MAX_VALUE]. Use
 * [.setMaxElapsedTime] to limit the maximum length of time
 * that an instance should accumulate before returning
 * [BackOffExecution.STOP].
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
class ExponentialBackOff : BackOff {


    /**
     * Return the initial interval in milliseconds.
     */
    /**
     * The initial interval in milliseconds.
     */
    var initialInterval = DEFAULT_INITIAL_INTERVAL

    private var multiplier = DEFAULT_MULTIPLIER

    /**
     * Return the maximum back off time.
     */
    /**
     * The maximum back off time.
     */
    var maxInterval = DEFAULT_MAX_INTERVAL

    /**
     * Return the maximum elapsed time in milliseconds after which a call to
     * [BackOffExecution.nextBackOff] returns [BackOffExecution.STOP].
     */
    /**
     * The maximum elapsed time in milliseconds after which a call to
     * [BackOffExecution.nextBackOff] returns [BackOffExecution.STOP].
     */
    var maxElapsedTime = DEFAULT_MAX_ELAPSED_TIME


    /**
     * Create an instance with the default settings.
     * @see .DEFAULT_INITIAL_INTERVAL
     *
     * @see .DEFAULT_MULTIPLIER
     *
     * @see .DEFAULT_MAX_INTERVAL
     *
     * @see .DEFAULT_MAX_ELAPSED_TIME
     */
    constructor()

    /**
     * Create an instance with the supplied settings.
     * @param initialInterval the initial interval in milliseconds
     * @param multiplier the multiplier (should be greater than or equal to 1)
     */
    constructor(initialInterval: Long, multiplier: Double) {
        checkMultiplier(multiplier)
        this.initialInterval = initialInterval
        this.multiplier = multiplier
    }

    /**
     * The value to multiply the current interval by for each retry attempt.
     */
    fun setMultiplier(multiplier: Double) {
        checkMultiplier(multiplier)
        this.multiplier = multiplier
    }

    /**
     * Return the value to multiply the current interval by for each retry attempt.
     */
    fun getMultiplier(): Double {
        return this.multiplier
    }

    override fun start(): BackOffExecution {
        return ExponentialBackOffExecution()
    }

    private fun checkMultiplier(multiplier: Double) {
//        Assert.isTrue(multiplier >= 1, {
//            "Invalid multiplier '" + multiplier + "'. Should be greater than " +
//                "or equal to 1. A multiplier of 1 is equivalent to a fixed interval."
//        })
    }


    private inner class ExponentialBackOffExecution : BackOffExecution {

        private var currentInterval: Long = -1

        private var currentElapsedTime: Long = 0

        override fun nextBackOff(): Long {
            if (this.currentElapsedTime >= maxElapsedTime) {
                return BackOffExecution.STOP
            }

            val nextInterval = computeNextInterval()
            this.currentElapsedTime += nextInterval
            return nextInterval
        }

        private fun computeNextInterval(): Long {
            val maxInterval = maxInterval
            if (this.currentInterval >= maxInterval) {
                return maxInterval
            } else if (this.currentInterval < 0) {
                val initialInterval = initialInterval
                this.currentInterval = (if (initialInterval < maxInterval)
                    initialInterval
                else
                    maxInterval)
            } else {
                this.currentInterval = multiplyInterval(maxInterval)
            }
            return this.currentInterval
        }

        private fun multiplyInterval(maxInterval: Long): Long {
            var i = this.currentInterval
            i *= getMultiplier().toLong()
            return (if (i > maxInterval) maxInterval else i)
        }


        override fun toString(): String {
            val sb = StringBuilder("ExponentialBackOff{")
            sb.append("currentInterval=").append(if (this.currentInterval < 0) "n/a" else (this.currentInterval).toString() + "ms")
            sb.append(", multiplier=").append(getMultiplier())
            sb.append('}')
            return sb.toString()
        }
    }

    companion object {

        /**
         * The default initial interval.
         */
        val DEFAULT_INITIAL_INTERVAL = 2000L

        /**
         * The default multiplier (increases the interval by 50%).
         */
        val DEFAULT_MULTIPLIER = 1.5

        /**
         * The default maximum back off time.
         */
        val DEFAULT_MAX_INTERVAL = 30000L

        /**
         * The default maximum elapsed time.
         */
        val DEFAULT_MAX_ELAPSED_TIME = java.lang.Long.MAX_VALUE
    }
}

/**
 * Represent a particular back-off execution.
 *
 *
 * Implementations do not need to be thread safe.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @see BackOff
 */
@FunctionalInterface
interface BackOffExecution {

    /**
     * Return the number of milliseconds to wait before retrying the operation
     * or [.STOP] ({@value #STOP}) to indicate that no further attempt
     * should be made for the operation.
     */
    fun nextBackOff(): Long

    companion object {

        /**
         * Return value of [.nextBackOff] that indicates that the operation
         * should not be retried.
         */
        val STOP: Long = -1
    }

}

/**
 * A simple [BackOff] implementation that provides a fixed interval
 * between two attempts and a maximum number of retries.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
class FixedBackOff : BackOff {

    /**
     * Return the interval between two attempts in milliseconds.
     */
    /**
     * Set the interval between two attempts in milliseconds.
     */
    var interval = DEFAULT_INTERVAL

    /**
     * Return the maximum number of attempts in milliseconds.
     */
    /**
     * Set the maximum number of attempts in milliseconds.
     */
    var maxAttempts = UNLIMITED_ATTEMPTS


    /**
     * Create an instance with an interval of {@value #DEFAULT_INTERVAL}
     * ms and an unlimited number of attempts.
     */
    constructor() {}

    /**
     * Create an instance.
     * @param interval the interval between two attempts
     * @param maxAttempts the maximum number of attempts
     */
    constructor(interval: Long, maxAttempts: Long) {
        this.interval = interval
        this.maxAttempts = maxAttempts
    }

    override fun start(): BackOffExecution {
        return FixedBackOffExecution()
    }


    private inner class FixedBackOffExecution : BackOffExecution {

        private var currentAttempts: Long = 0

        override fun nextBackOff(): Long {
            this.currentAttempts++
            return if (this.currentAttempts <= maxAttempts) {
                interval
            } else {
                BackOffExecution.STOP
            }
        }

        override fun toString(): String {
            val sb = StringBuilder("FixedBackOff{")
            sb.append("interval=").append(this@FixedBackOff.interval)
            val attemptValue = if (this@FixedBackOff.maxAttempts == java.lang.Long.MAX_VALUE)
                "unlimited"
            else
                this@FixedBackOff.maxAttempts.toString()
            sb.append(", currentAttempts=").append(this.currentAttempts)
            sb.append(", maxAttempts=").append(attemptValue)
            sb.append('}')
            return sb.toString()
        }
    }

    companion object {

        /**
         * The default recovery interval: 5000 ms = 5 seconds.
         */
        val DEFAULT_INTERVAL: Long = 5000

        /**
         * Constant value indicating an unlimited number of attempts.
         */
        val UNLIMITED_ATTEMPTS = java.lang.Long.MAX_VALUE
    }

}

