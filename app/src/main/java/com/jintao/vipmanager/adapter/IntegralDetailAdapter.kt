package com.jintao.vipmanager.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jintao.vipmanager.R
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.databinding.ItemIntegralDetailBinding
import com.jintao.vipmanager.utils.GeneralUtils
/**
 * Author: jintao
 * CreateDate: 2023/8/26 14:31
 * Description:
 */
class IntegralDetailAdapter(var consumeList:List<DbUserConsumeInfo>): RecyclerView.Adapter<IntegralDetailAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_integral_detail, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userConsumeInfo = consumeList.get(position)
        if (userConsumeInfo.consumeIntegral!=0f) {
            if (userConsumeInfo.consumeType) {
                holder.itemBind.itemTvIntegral.setText("积分 +"+GeneralUtils.getInstence().formatAmount(userConsumeInfo.consumeIntegral))
                holder.itemBind.itemTvIntegral.setTextColor(Color.parseColor("#277F29"))
            }else {
                holder.itemBind.itemTvIntegral.setText("积分 -"+GeneralUtils.getInstence().formatAmount(userConsumeInfo.consumeIntegral))
                holder.itemBind.itemTvIntegral.setTextColor(Color.parseColor("#FF5E00"))
            }
        }else {
            holder.itemBind.itemTvIntegral.setText("积分   "+GeneralUtils.getInstence().formatAmount(userConsumeInfo.consumeIntegral))
            holder.itemBind.itemTvIntegral.setTextColor(Color.BLACK)
        }
        if (userConsumeInfo.consumeAmount!=0f) {
            if (userConsumeInfo.consumeType) {
                holder.itemBind.itemTvAmount.setText("金额 +"+GeneralUtils.getInstence().formatAmount(userConsumeInfo.consumeAmount))
                holder.itemBind.itemTvAmount.setTextColor(Color.parseColor("#277F29"))
            }else {
                holder.itemBind.itemTvAmount.setText("金额 -"+GeneralUtils.getInstence().formatAmount(userConsumeInfo.consumeAmount))
                holder.itemBind.itemTvAmount.setTextColor(Color.parseColor("#FF5E00"))
            }
        }else {
            holder.itemBind.itemTvAmount.setText("金额   "+GeneralUtils.getInstence().formatAmount(userConsumeInfo.consumeAmount))
            holder.itemBind.itemTvAmount.setTextColor(Color.BLACK)
        }
        holder.itemBind.itemTvBili.setText(userConsumeInfo.converRatio)
        holder.itemBind.itemTvContent.setText(userConsumeInfo.content)
        holder.itemBind.itemTvTime.setText(userConsumeInfo.consumeTime)
        holder.itemView.setOnTouchListener(object :View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event!=null) {
                    when(event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            holder.itemView.setBackgroundColor(Color.rgb(245,245,245))
                        }
                        MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL -> {
                            holder.itemView.setBackgroundColor(Color.WHITE)
                        }
                    }
                }
                return true
            }
        })
    }

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        var itemBind: ItemIntegralDetailBinding

        init {
            itemBind = ItemIntegralDetailBinding.bind(itemView)
        }
    }

    override fun getItemCount(): Int {
        return consumeList.size
    }

    fun setListData(mList: List<DbUserConsumeInfo>) {
        this.consumeList = mList
    }
}