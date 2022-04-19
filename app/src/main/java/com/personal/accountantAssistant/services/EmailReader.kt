package com.personal.accountantAssistant.services

import android.util.Log
import javax.mail.*

class EmailReader(user: String?, password: String?) : Authenticator() {
    //private static final String TAG = "GMailReader";
    private val mailhost = "imap.gmail.com"
    private var session: Session? = null
    private var store: Store? = null

    @Synchronized
    @Throws(Exception::class)
    fun readMail(): Array<Message?> {
        var messages = arrayOf<Message?>()
        try {
            val folder = store?.getFolder("Inbox")
            folder?.open(Folder.READ_ONLY)
            /* TODO to rework
            Message[] msgs = folder.getMessages(1, 10);
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(msgs, fp);
            */messages = folder?.messages as Array<Message?>
        } catch (e: Exception) {
            Log.e("readMail", e.message, e)
        }
        return messages
    }

    init {
        val props = System.getProperties()
        try {
            session = Session.getDefaultInstance(props, null)
            store = session?.getStore("imaps")
            store?.connect(mailhost, user, password)
            println("Store: " + store.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}