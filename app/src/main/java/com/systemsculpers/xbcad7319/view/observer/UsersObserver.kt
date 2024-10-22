package com.systemsculpers.xbcad7319.view.observer

import androidx.lifecycle.Observer
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.User
import com.systemsculpers.xbcad7319.view.adapter.PropertyAdapter
import com.systemsculpers.xbcad7319.view.adapter.UsersAdapter

class UsersObserver (private val adapter: UsersAdapter)
    : Observer<List<User>> {

    // Method called when the observed data changes
    override fun onChanged(value: List<User>) {
        // initialize first element as the create button
        adapter.updateUsers(value)
    }
}