package com.jhuo.taskmanager.task_manager.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface ConnectivityObserver {
    fun observe(): Flow<Status>
    suspend fun isOnline(): Boolean

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}

class NetworkConnectivityObserver @Inject constructor(
    private val context: Context
) : ConnectivityObserver {
    override fun observe(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    trySend(ConnectivityObserver.Status.Available)
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    trySend(ConnectivityObserver.Status.Losing)
                }

                override fun onLost(network: Network) {
                    trySend(ConnectivityObserver.Status.Lost)
                }

                override fun onUnavailable() {
                    trySend(ConnectivityObserver.Status.Unavailable)
                }
            }

            manager.registerDefaultNetworkCallback(callback)
            awaitClose {
                manager.unregisterNetworkCallback(callback)
            }
        }
    }

    override suspend fun isOnline(): Boolean {
        return try {
            val connection = context.getSystemService(ConnectivityManager::class.java)
                .activeNetworkInfo
            connection != null && connection.isConnected
        } catch (e: Exception) {
            false
        }
    }
}