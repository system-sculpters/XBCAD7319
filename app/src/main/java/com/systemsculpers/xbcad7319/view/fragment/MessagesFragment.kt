package com.systemsculpers.xbcad7319.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.ChatController
import com.systemsculpers.xbcad7319.data.model.Chat
import com.systemsculpers.xbcad7319.data.model.Message
import com.systemsculpers.xbcad7319.data.model.SendMessage
import com.systemsculpers.xbcad7319.data.model.Valuation
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

    private var messages: MutableList<Message>? = mutableListOf()

    private lateinit var chatController: ChatController

    // Declare chatId
    private var chat: Chat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            messages = it.getParcelableArrayList(MESSAGES_ARG)
            chat = it.getParcelable(CHAT_ARG) // Extract chatId from arguments
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        chatController = ViewModelProvider(this).get(ChatController::class.java)

        setUpUserDetails()

        binding.submitButton.setOnClickListener{
            sendMessage()
        }

        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(chat?.participantsDetails?.email!!)
    }

    // Function to set up user details and observe the view model for transaction updates
    private fun setUpUserDetails() {
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            getMessages(user.id)
            // Observe the view model to get transactions based on the user ID
            //observeViewModel(token, user.id)
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }
    // Sets up the color picker RecyclerView
    private fun getMessages(userId: String) {


        messageAdapter = MessageAdapter(userId)
        binding.messages.adapter = messageAdapter
        binding.messages.layoutManager = LinearLayoutManager(requireContext()) // 3 icons per row

        messages?.let { messageAdapter.updateMessages(it) }
    }

    private fun sendMessage(){
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            observeViewModel(token, user.id)
            // Observe the view model to get transactions based on the user ID
            //observeViewModel(token, user.id)
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }


    // Method to observe the ViewModel for transaction-related data and status updates
    private fun observeViewModel(token: String, userId: String) {
        Log.d("token", "token: ${token}")
        val messageText = binding.message.text.toString()


        // Validate user input before sending data to the server
        if (messageText.isEmpty() || chat?.id.isNullOrEmpty()) {
            return
        }

        val newMessage = SendMessage(chatId = chat!!.id, senderId = userId, text = messageText)
        // Observe the status of the transaction fetching operation
        chatController.status.observe(viewLifecycleOwner) { status ->
            // Handle changes in the status (indicates success or failure)

            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Success: Dismiss the progress dialog
                //progressDialog.dismiss()
                Log.d("status", "successful")
//                val updateMessage = Message(chatId = chat!!.id, senderId = userId, text = messageText)
//                messageAdapter.addMessage(updateMessage)

                binding.message.setText("")
            } else {
                Log.d("status", "fail")
                // Failure: Dismiss the progress dialog
                //progressDialog.dismiss()
                // Optionally handle failure case (e.g., show an error message)
            }
        }

        // Observe any messages from the ViewModel
        chatController.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Log the message for debugging purposes
            Log.d("Valuations message", message)

            // Check for specific messages that indicate a timeout or network issue
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and attempt to reconnect
                Log.d("failed retrieval", "Retry...")

                chatController.sendMessage(token, newMessage)

            }
        }

        getUpdatedMessages(userId)

        // Set up the message RecyclerView
        //getUpdatedMessages(userId)
        // Initial call to fetch all transactions for the user
        chatController.sendMessage(token, newMessage)
    }



    // Sets up the message RecyclerView
    private fun getUpdatedMessages(userId: String) {
        // Initialize the message adapter only once
        if (!::messageAdapter.isInitialized) {
            messageAdapter = MessageAdapter(userId)
            binding.messages.adapter = messageAdapter
            binding.messages.layoutManager = LinearLayoutManager(requireContext())
        }

        // Update messages in adapter with the latest data
        //messages?.let { messageAdapter.updateMessages(it) }

        // Observe new incoming messages from the ViewModel
        chatController.messageList.observe(viewLifecycleOwner) { newMessage ->
            newMessage?.let {
                // Add new message to the list and notify the adapter
                //messages = messages?.toMutableList() // Convert to MutableList

                messages?.clear()

                // Sort the new messages by timestamp (assuming messages have a 'timestamp' field)
                val sortedMessages = it.messages.sortedBy { message -> message.timestamp }

                // Add the sorted messages to the list
                messages?.addAll(sortedMessages)

                newMessage.messages.let { it1 -> messageAdapter.updateMessages(it1) }

                //messages?.addAll(it.messages) // Add new message to the list
                messageAdapter.notifyItemInserted(messages!!.size - 1) // Notify adapter
                binding.messages.smoothScrollToPosition(messages!!.size - 1) // Scroll to the latest message
            }
        }
    }


    companion object {
        const val MESSAGES_ARG = "messages"
        const val CHAT_ARG = "chat" // New constant for chatId argument

        // Factory method to create a new instance of this fragment with a list of messages and chatId
        @JvmStatic
        fun newInstance(messages: List<Message>, chat: Chat) =
            MessagesFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(MESSAGES_ARG, ArrayList(messages))
                    putParcelable(CHAT_ARG, chat) // Pass chatId to the fragment
                }
            }
    }
}