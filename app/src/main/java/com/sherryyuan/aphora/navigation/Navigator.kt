package com.sherryyuan.aphora.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.scopes.ActivityRetainedScoped

@ActivityRetainedScoped
class Navigator(startDestination: Any) {
    val backStack: SnapshotStateList<Any> = mutableStateListOf(startDestination)

    fun goTo(destination: NavKey) {
        if (destination is ClearTop) {
            val destinationIndex = backStack.indexOfLast { it == destination }
            if (destinationIndex == -1) {
                backStack.add(destination)
            } else {
                backStack.removeRange(destinationIndex + 1, backStack.size)
            }
        } else {
            backStack.add(destination)
        }
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}
