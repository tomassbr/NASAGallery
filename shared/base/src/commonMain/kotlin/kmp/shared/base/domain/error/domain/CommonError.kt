package kmp.shared.base.domain.error.domain

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kmp.shared.base.MR
import kmp.shared.base.domain.model.ErrorResult

/**
 * Error type used anywhere in the project. Contains subclasses for common exceptions that can happen anywhere
 * @param throwable optional [Throwable] parameter used for debugging or crash reporting
 */
sealed class CommonError(localizedMessage: StringDesc, throwable: Throwable? = null) : ErrorResult(
    localizedMessage = localizedMessage,
    throwable = throwable,
) {
    class NoNetworkConnection(throwable: Throwable?) : CommonError(
        localizedMessage = MR.strings.error_no_internet_connection.desc(),
        throwable = throwable,
    )

    class RateLimited(throwable: Throwable? = null) : CommonError(
        localizedMessage = MR.strings.error_nasa_rate_limit.desc(),
        throwable = throwable,
    )

    class HttpRequestFailed(
        val statusCode: Int,
        throwable: Throwable? = null,
    ) : CommonError(
        localizedMessage = MR.strings.error_nasa_request_failed.desc(),
        throwable = throwable,
    )

    /**
     * Unexpected failure (e.g. serialization, IO) mapped from [runCatchingCommonNetworkExceptions].
     */
    class Unexpected(throwable: Throwable? = null) : CommonError(
        localizedMessage = MR.strings.unknown_error.desc(),
        throwable = throwable,
    )

    data object Unknown : CommonError(localizedMessage = MR.strings.unknown_error.desc())
}
