package kmp.shared.base.domain.error.util

import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kmp.shared.base.domain.error.domain.BackendError
import kmp.shared.base.domain.error.domain.CommonError
import kmp.shared.base.domain.model.Result

/**
 * Maps Ktor [ResponseException] (4xx/5xx with `expectSuccess = true`) to [Result.Error].
 * Rethrowing would surface as uncaught coroutine exceptions on iOS and crash the app.
 */
inline fun <R : Any> runCatchingCommonNetworkExceptions(block: () -> R): Result<R> =
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
        when (e::class.simpleName) { // Handle platform specific exceptions
            "UnknownHostException" -> Result.Error(CommonError.NoNetworkConnection(e))
            else -> throw e // Rethrow exception when it's not matched
        }
    }
