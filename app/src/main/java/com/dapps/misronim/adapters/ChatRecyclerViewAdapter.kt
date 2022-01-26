package com.dapps.misronim.adapters

import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.dapps.misronim.model.Message
import com.dapps.misronim.ui.FullImageActivity
import com.google.firebase.auth.FirebaseAuth


private const val SEND_LAYOUT = 0
private const val RECEIVED_LAYOUT = 1

class ChatRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var receiverUserPic: String
    private lateinit var messageList: List<Message>
    private lateinit var currentUserPic: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val viewHolder: ViewHolder
        val view: View

        viewHolder = if (viewType == SEND_LAYOUT) {

            view = LayoutInflater.from(parent.context)
                .inflate(com.dapps.misronim.R.layout.sent_message_row, parent, false)
            SentViewHolder(view)

        } else {

            view = LayoutInflater.from(parent.context)
                .inflate(com.dapps.misronim.R.layout.recieved_message_row, parent, false)
            ReceivedViewHolder(view)

        }

        return viewHolder

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder.itemViewType == SEND_LAYOUT) {

            val currentMessage = messageList[position]
            holder as SentViewHolder
            holder.bindSentRow(currentMessage)


        } else {

            val currentMessage = messageList[position]
            holder as ReceivedViewHolder
            holder.bindReceivedRow(currentMessage)

        }


    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.sender))
            SEND_LAYOUT
        else
            RECEIVED_LAYOUT
    }

    inner class SentViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindSentRow(message: Message) {

            val sentMessageTextView =
                itemView.findViewById<TextView>(com.dapps.misronim.R.id.sentMessage)
            val sentImage = itemView.findViewById<ImageView>(com.dapps.misronim.R.id.sentImage)
            val profileImage =
                itemView.findViewById<ImageView>(com.dapps.misronim.R.id.sentMessageProfilePicture)
            val sentIsSeenImageTextView =
                itemView.findViewById<TextView>(com.dapps.misronim.R.id.sentIsSeenImageTextView)
            val sentIsSeenTextView =
                itemView.findViewById<TextView>(com.dapps.misronim.R.id.sentIsSeenTextView)

            if (message.message.equals("Sent you an image") && !message.url.equals("")) {

                sentMessageTextView.visibility = View.GONE
                sentIsSeenImageTextView.visibility = View.VISIBLE
                sentIsSeenTextView.visibility = View.GONE
                sentImage.visibility = View.VISIBLE
                //Glide.with(itemView.rootView).load(message.url).into(sentImage)

//                Glide.with(itemView.rootView).load(message.url)
//                    .apply(
//                        RequestOptions()
//                            .fitCenter()
//                            .format(DecodeFormat.PREFER_ARGB_8888)
//                            .override(Target.SIZE_ORIGINAL)
//                    )
//                    .into(sentImage)

                Glide.with(itemView.rootView).load(message.url)
                    .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
                    .error(com.dapps.misronim.R.drawable.error_icon)
                    .placeholder(com.dapps.misronim.R.drawable.loading_icon)
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            @Nullable e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    }).into(sentImage)



                if (adapterPosition == messageList.size - 1) {
                    sentIsSeenImageTextView.visibility = View.VISIBLE
                    sentIsSeenTextView.visibility = View.GONE

                    if (message.seen == true) {
                        sentIsSeenImageTextView.text = "Seen"
                    } else {
                        sentIsSeenImageTextView.text = "Sent"
                    }


                } else {
                    sentIsSeenImageTextView.visibility = View.GONE
                }

                sentImage.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Download",
                        "Cancel"
                    )

                    val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(itemView.context, FullImageActivity::class.java)
                            intent.putExtra("imageUrl", message.url)
                            itemView.context.startActivity(intent)
                        } else if (which == 1) {

                            Toast.makeText(
                                itemView.rootView.context,
                                "Downloading Image...",
                                Toast.LENGTH_SHORT
                            ).show()
                            downloadFiles(
                                itemView.context,
                                "Test Image",
                                ".jpeg",
                                Environment.DIRECTORY_DOWNLOADS,
                                message.url
                            )

                        } else if (which == 2) {
                            dialog.dismiss()
                        }
                    })
                    builder.show()
                }

            } else {

                sentMessageTextView.visibility = View.VISIBLE
                sentMessageTextView.text = message.message
                sentIsSeenImageTextView.visibility = View.GONE

                if (adapterPosition == messageList.size - 1) {
                    sentIsSeenTextView.visibility = View.VISIBLE
                    sentIsSeenImageTextView.visibility = View.GONE

                    if (message.seen == true) {
                        sentIsSeenTextView.text = "Seen"
                    } else {
                        sentIsSeenTextView.text = "Sent"
                    }

                }

            }


            Glide.with(itemView.rootView).load(currentUserPic).into(profileImage)


        }

    }

    private fun downloadFiles(
        context: Context,
        fileName: String,
        fileExtension: String,
        destinationDirectory: String,
        Url: String?
    ) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(Url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(destinationDirectory, fileName + fileExtension)
        downloadManager.enqueue(request)
    }

    inner class ReceivedViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindReceivedRow(message: Message) {

            val receiveMessageTextView =
                itemView.findViewById<TextView>(com.dapps.misronim.R.id.receivedMessage)
            val receiveImage =
                itemView.findViewById<ImageView>(com.dapps.misronim.R.id.receivedImage)
            val receiveProfileImage =
                itemView.findViewById<ImageView>(com.dapps.misronim.R.id.receivedMessageProfileImage)

            if (message.message.equals("Sent you an image") && !message.url.equals("")) {

                receiveMessageTextView.visibility = View.GONE
                receiveImage.visibility = View.VISIBLE

                Glide.with(itemView.rootView).load(message.url).into(receiveImage)


            } else {

                receiveMessageTextView.visibility = View.VISIBLE
                receiveMessageTextView.text = message.message

            }

            Glide.with(itemView.rootView).load(receiverUserPic).into(receiveProfileImage)


        }

    }

    fun getMessageList(): List<Message> {
        return messageList
    }

    fun setMessagesList(newList: List<Message>, userProfilePic: String, userProfilePic1: String) {
        messageList = newList
        currentUserPic = userProfilePic
        receiverUserPic = userProfilePic1
        notifyDataSetChanged()
    }


}