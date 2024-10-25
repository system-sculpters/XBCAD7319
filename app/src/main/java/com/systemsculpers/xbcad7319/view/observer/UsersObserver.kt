package com.systemsculpers.xbcad7319.view.observer

import androidx.lifecycle.Observer
import com.systemsculpers.xbcad7319.data.model.User
import com.systemsculpers.xbcad7319.view.adapter.UsersAdapter
import com.systemsculpers.xbcad7319.view.fragment.SelectUserFragment
import com.systemsculpers.xbcad7319.view.fragment.UpdateSelectUserFragment
import com.systemsculpers.xbcad7319.view.fragment.UsersFragment

class UsersObserver(
    private val adapter: UsersAdapter,
    private val usersFragment: UsersFragment?,
    private val selectUserFragment: SelectUserFragment?,
    private val updateSelectUserFragment: UpdateSelectUserFragment?
)
    : Observer<List<User>> {

    // Method called when the observed data changes
    override fun onChanged(value: List<User>) {
        // initialize first element as the create button
        adapter.updateUsers(value)
        usersFragment?.updateUsers(value)
        selectUserFragment?.updateUsers(value)
        updateSelectUserFragment?.updateUsers(value)
    }
}