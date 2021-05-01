package com.personal.accountantAssistant.utils

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
        if (ParserUtils.isNullObject(props)) {
            println("Properties are null !!")
        } else {
            props.setProperty("mail.store.protocol", "imaps")

            /*Log.d(TAG, "Transport: " + props.getProperty("mail.transport.protocol"));
            Log.d(TAG, "Store: " + props.getProperty("mail.store.protocol"));
            Log.d(TAG, "Host: " + props.getProperty("mail.imap.host"));
            Log.d(TAG, "Authentication: " + props.getProperty("mail.imap.auth"));
            Log.d(TAG, "Port: " + props.getProperty("mail.imap.port"));*/
        }
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