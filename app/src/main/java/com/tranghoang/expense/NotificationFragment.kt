package com.tranghoang.expense


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment : Fragment() {

    var auth = FirebaseAuth.getInstance()
    var snapsListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var snaps: ArrayList<DataSnapshot> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val myView = inflater.inflate(R.layout.fragment_notification, container, false)

        snapsListView = myView.findViewById(R.id.ListView)
        emails = ArrayList()
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, emails)
        snapsListView?.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid).child("snaps").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                emails.add(p0.child("from").value as String)
                snaps.add(p0!!)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {
                var index = 0;
                for(snap: DataSnapshot in snaps) {
                    if(snap.key == p0?.key) {
                        snaps.removeAt(index)
                        emails.removeAt(index)
                    }
                    index++
                }
                adapter.notifyDataSetChanged()
            }


        })

        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val snapshot = snaps.get(position)
            var intent = Intent(context, ViewNotiActivity::class.java)
            intent.putExtra("addMoney", snapshot.child("addMoney").value as String)
//            intent.putExtra("imageURL", snapshot.child("imageURL").value as String)
//            intent.putExtra("message", snapshot.child("message").value as String)
//            intent.putExtra("snapKey", snapshot.key)
            startActivity(intent)

        }

        return myView
    }

}

