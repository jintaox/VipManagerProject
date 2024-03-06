package com.jintao.vipmanager.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jintao.vipmanager.R
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.databinding.ItemUserInfoBinding
import com.jintao.vipmanager.listener.OnVipItemClickListener
import com.jintao.vipmanager.utils.GeneralUtils

class VipUserAdapter(var list: List<DbVipUserInfo>, var listener: OnVipItemClickListener) :RecyclerView.Adapter<VipUserAdapter.UserViewHolder>() {

    private var richText = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_info, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val vipUserInfo = list.get(position)
        if (TextUtils.isEmpty(richText)) {
            holder.itemBind.itemTvPhone.setText(vipUserInfo.phoneNumber)
            holder.itemBind.itemTvName.setText(vipUserInfo.userName)
        }else {
            val isNumberic = GeneralUtils.getInstence().isNumberic(richText)
            if (isNumberic) {
                GeneralUtils.getInstence().setRichText(vipUserInfo.phoneNumber,richText,holder.itemBind.itemTvPhone)
                holder.itemBind.itemTvName.setText(vipUserInfo.userName)
            }else {
                GeneralUtils.getInstence().setRichText(vipUserInfo.userName,richText,holder.itemBind.itemTvName)
                holder.itemBind.itemTvPhone.setText(vipUserInfo.phoneNumber)
            }
        }

        if (position%2==0) {
            holder.itemView.setBackgroundColor(Color.rgb(223,230,229))
        }else {
            holder.itemView.setBackgroundColor(Color.rgb(235,235,235))
        }
        holder.itemBind.itemTvIntegral.setText("积分:"+GeneralUtils.getInstence().formatAmount(vipUserInfo.userIntegral))
        holder.itemBind.itemConsumeCount.setText("消费:"+vipUserInfo.consumeNumber+"笔")
        val theConsume = GeneralUtils.getInstence().formatAmount(vipUserInfo.lastAmount)
        holder.itemBind.itemRecentTime.setText("最近:"+theConsume+"元  "+GeneralUtils.getInstence().timeFormat(vipUserInfo.consumeTime))
        holder.itemView.setOnClickListener {
            listener.onItemClick(vipUserInfo,0)
        }
        holder.itemBind.stvLookDetail.setOnClickListener {
            listener.onItemClick(vipUserInfo,1)
        }
        holder.itemView.setOnLongClickListener(object :View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                listener.onItemClick(vipUserInfo,2)
                return true
            }
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setListData(richText:String,mList: List<DbVipUserInfo>) {
        this.list = mList
        this.richText = richText
    }

    class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var itemBind:ItemUserInfoBinding
        init {
            itemBind = ItemUserInfoBinding.bind(itemView)
        }
    }
}