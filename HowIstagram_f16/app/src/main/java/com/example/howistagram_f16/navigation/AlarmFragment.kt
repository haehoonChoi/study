package com.example.howistagram_f16.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.howistagram_f16.R
import com.example.howistagram_f16.navigation.model.AlarmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_alarm.view.*
import kotlinx.android.synthetic.main.item_comment.view.*

class AlarmFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_alarm,container,false)
        view.alarmfragment_recyclerview.adapter = AlarmRecyclerviewAdapter()
        view.alarmfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        return view
    }
    inner class AlarmRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        // アラムリスト
        var alarmDTOList : ArrayList<AlarmDTO> = arrayListOf()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid",uid).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                alarmDTOList.clear()
                if (querySnapshot == null)return@addSnapshotListener

                for (snapshot in querySnapshot.documents) {
                    alarmDTOList.add(snapshot.toObject(AlarmDTO :: class.java)!!)
                }
                // データリフレッシュ
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)

            return CustomViewHolder(view)
        }
        inner class CustomViewHolder (view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // 種類により分ける
            var view = holder.itemView

            // イメージ
            FirebaseFirestore.getInstance().collection("profileImages").document(alarmDTOList[position].uid!!).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val url = it.result!!["image"]
                    // プロフィール写真表示
                    Glide.with(view.context).load(url).apply(RequestOptions().circleCrop()).into(view.commentviewitem_imageview_profile)
                }
            }

            when (alarmDTOList[position].kind) {
                // いいねアラム
                0 -> {
                    val str_0 = alarmDTOList[position].userId +  " " + getString(R.string.alarm_favorite)
                    view.commentviewitem_textview_profile.text = str_0
                }
                // コメントアラム
                1 -> {
                    val str_0 = alarmDTOList[position].userId + " " + getString(R.string.alarm_comment) + " of " + alarmDTOList[position].message
                    view.commentviewitem_textview_profile.text = str_0
                }
                // フォローアラム
                2 -> {
                    val str_0 = alarmDTOList[position].userId + " " + getString(R.string.alarm_favorite)
                    view.commentviewitem_textview_profile.text = str_0
                }
            }
            view.commentviewitem_textview_comment.visibility = View.INVISIBLE
        }
    }
}

