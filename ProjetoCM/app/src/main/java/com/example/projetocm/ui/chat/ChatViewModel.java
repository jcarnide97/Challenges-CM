package com.example.projetocm.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projetocm.models.ChatMessage;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<List<ChatMessage>> globalChatMessages;
    private MutableLiveData<ChatMessage> globalChatNewMessage;

    public LiveData<List<ChatMessage>> getGlobalChatMessages() {
        if (globalChatMessages == null)
            globalChatMessages = new MutableLiveData<>();
        return globalChatMessages;
    }

    public void setGlobalChatMessages(List<ChatMessage> chatMessages) {
        this.globalChatMessages.setValue(chatMessages);
    }

    public LiveData<ChatMessage> getGlobalChatNewMessage() {
        if (globalChatNewMessage == null)
            globalChatNewMessage = new MutableLiveData<>();
        return globalChatNewMessage;
    }

    public void setGlobalChatNewMessage(ChatMessage message) {
        if (globalChatNewMessage == null)
            globalChatNewMessage = new MutableLiveData<>();
        globalChatNewMessage.setValue(message);
    }

}