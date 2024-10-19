package com.systemsculpers.xbcad7319.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.Message
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentCreateValuationBinding
import com.systemsculpers.xbcad7319.databinding.FragmentMessagesBinding
import com.systemsculpers.xbcad7319.view.adapter.MessageAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeAdapter


class MessagesFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentMessagesBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        setUpUserDetails()


        return binding.root
    }

    // Function to set up user details and observe the view model for transaction updates
    private fun setUpUserDetails() {
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            setMessages(user.id)
            // Observe the view model to get transactions based on the user ID
            //observeViewModel(token, user.id)
        } else {
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }
    // Sets up the color picker RecyclerView
    private fun setMessages(userId: String) {
        val dummyMessages = listOf(
            Message(
                id = "1",
                chatId = "chat_001",
                senderId = "H6IIUSfcivgzGZsEGSMQer8mZUv2", // Your message
                timestamp = System.currentTimeMillis(),
                text = "Hey, how are you?"
            ),
            Message(
                id = "2",
                chatId = "chat_001",
                senderId = "other_user_id", // Other user's message
                timestamp = System.currentTimeMillis(),
                text = "I'm good, thanks! How about you?"
            ),
            Message(
                id = "3",
                chatId = "chat_001",
                senderId = "H6IIUSfcivgzGZsEGSMQer8mZUv2",
                timestamp = System.currentTimeMillis(),
                text = "I'm doing well, just working on a project."
            ),
            Message(
                id = "4",
                chatId = "chat_001",
                senderId = "other_user_id",
                timestamp = System.currentTimeMillis(),
                text = "That sounds interesting. What kind of project?"
            ),
            Message(
                id = "5",
                chatId = "chat_001",
                senderId = "H6IIUSfcivgzGZsEGSMQer8mZUv2",
                timestamp = System.currentTimeMillis(),
                text = "It's a mobile app development project."
            ),
            Message(
                id = "6",
                chatId = "chat_001",
                senderId = "other_user_id",
                timestamp = System.currentTimeMillis(),
                text = "Nice! What features does it have?"
            ),
            Message(
                id = "7",
                chatId = "chat_001",
                senderId = "H6IIUSfcivgzGZsEGSMQer8mZUv2",
                timestamp = System.currentTimeMillis(),
                text = "It has chat functionality, notifications, and more. It has chat functionality, notifications, and more."
            ),
            Message(
                id = "8",
                chatId = "chat_001",
                senderId = "other_user_id",
                timestamp = System.currentTimeMillis(),
                text = "That sounds cool! Is it almost done?"
            ),
            Message(
                id = "9",
                chatId = "chat_001",
                senderId = "H6IIUSfcivgzGZsEGSMQer8mZUv2",
                timestamp = System.currentTimeMillis(),
                text = "Almost! Just polishing some features now."
            ),
            Message(
                id = "10",
                chatId = "chat_001",
                senderId = "other_user_id",
                timestamp = System.currentTimeMillis(),
                text = "Good luck with it! Let me know when it's done."
            )
        )

        messageAdapter = MessageAdapter(userId)
        binding.messages.adapter = messageAdapter
        binding.messages.layoutManager = LinearLayoutManager(requireContext()) // 3 icons per row

        // Adapter to display available colors and handle color selection
        //messageAdapter.notifyItemInserted(dummyMessages.size - 1)
        //binding.messages.scrollToPosition(dummyMessages.size - 1)

        messageAdapter.updateMessages(dummyMessages)
    }

}