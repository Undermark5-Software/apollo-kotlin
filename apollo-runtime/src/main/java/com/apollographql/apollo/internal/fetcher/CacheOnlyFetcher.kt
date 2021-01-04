package com.apollographql.apollo.internal.fetcher

import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Response.Companion.builder
import com.apollographql.apollo.api.internal.ApolloLogger
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ResponseFetcher
import com.apollographql.apollo.interceptor.ApolloInterceptor
import com.apollographql.apollo.interceptor.ApolloInterceptor.CallBack
import com.apollographql.apollo.interceptor.ApolloInterceptor.FetchSourceType
import com.apollographql.apollo.interceptor.ApolloInterceptor.InterceptorRequest
import com.apollographql.apollo.interceptor.ApolloInterceptor.InterceptorResponse
import com.apollographql.apollo.interceptor.ApolloInterceptorChain
import java.util.concurrent.Executor

/**
 * Signals the apollo client to **only** fetch the data from the normalized cache. If it's not present in the
 * normalized cache or if an exception occurs while trying to fetch it from the normalized cache, an empty [ ] is sent back with the [com.apollographql.apollo.api.Operation] info
 * wrapped inside.
 */
class CacheOnlyFetcher : ResponseFetcher {
  override fun provideInterceptor(apolloLogger: ApolloLogger?): ApolloInterceptor? {
    return CacheOnlyInterceptor()
  }

  private class CacheOnlyInterceptor : ApolloInterceptor {
    override fun interceptAsync(request: InterceptorRequest, chain: ApolloInterceptorChain,
                                dispatcher: Executor, callBack: CallBack) {
      val cacheRequest = request.toBuilder().fetchFromCache(true).build()
      chain.proceedAsync(cacheRequest, dispatcher, object : CallBack {
        override fun onResponse(response: InterceptorResponse) {
          callBack.onResponse(response)
        }

        override fun onFailure(e: ApolloException) {
          // Cache only returns null instead of throwing when the cache is empty
          callBack.onResponse(cacheMissResponse(request.operation))
          callBack.onCompleted()
        }

        override fun onCompleted() {
          callBack.onCompleted()
        }

        override fun onFetch(sourceType: FetchSourceType?) {
          callBack.onFetch(sourceType)
        }
      })
    }

    override fun dispose() {
      //no-op
    }

    fun cacheMissResponse(operation: Operation<*>?): InterceptorResponse {
      return InterceptorResponse(null, builder<Operation.Data>(operation!!).fromCache(true).build(), null)
    }
  }
}