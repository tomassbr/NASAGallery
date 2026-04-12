package kmp.shared.base.domain.error.util

import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kmp.shared.base.domain.error.domain.BackendError
import kmp.shared.base.domain.error.domain.CommonError
import kmp.shared.base.domain.model.Result
import kotlinx.coroutines.CancellationException

/**
 * Heuristic connectivity detection for KMP: JVM uses [UnknownHostException]; Darwin often wraps errors in
 * [Throwable.cause] or message text. Avoids fragile string checks where a class name match suffices.
 */
private fun Throwable.isLikelyConnectivityFailure(): Boolean {
    var current: Throwable? = this
    while (current != null) {
        when (current::class.simpleName) {
            "UnknownHostException",
            "ConnectException",
            "SocketTimeoutException",
            -> return true
        }
        val msg = current.message
        if (msg != null) {
            if (msg.contains("Unable to resolve host", ignoreCase = true)) return true
            if (msg.contains("Could not connect", ignoreCase = true)) return true
            if (msg.contains("Network is unreachable", ignoreCase = true)) return true
            // NSURLErrorNotConnectedToInternet, -1009, etc.
            if (msg.contains("not connected to internet", ignoreCase = true)) return true
            if (msg.contains("NSURLErrorDomain", ignoreCase = true) && msg.contains("-1009")) return true
        }
        current = current.cause
    }
    return false
}

/**
 * Maps Ktor [ResponseException] (4xx/5xx with `expectSuccess = true`) to [Result.Error].
 * Rethrowing would surface as uncaught coroutine exceptions on iOS and crash the app.
 */
suspend fun <R : Any> runCatchingCommonNetworkExceptions(block: suspend () -> R): Result<R> =
    try {
        Result.Success(block())
    } catch (e: ResponseException) {
        when (e.response.status) {
            HttpStatusCode.Unauthorized -> Result.Error(
                BackendError.NotAuthorized(e.response.toString(), e),
            )
            HttpStatusCode.TooManyRequests -> Result.Error(CommonError.RateLimited(e))
            else -> Result.Error(CommonError.HttpRequestFailed(e.response.status.value, e))
        }
    } catch (e: Throwable) {
        when {
            e is CancellationException -> throw e
            e.isLikelyConnectivityFailure() -> Result.Error(CommonError.NoNetworkConnection(e))
            else -> Result.Error(CommonError.Unexpected(e))
        }
    }
