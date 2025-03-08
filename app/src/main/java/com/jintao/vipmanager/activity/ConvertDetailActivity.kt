package com.jintao.vipmanager.activity

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jintao.vipmanager.R
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.bean.DbConvertGoodsInfo
import com.jintao.vipmanager.database.launchWithNotLoadingFlow
import com.jintao.vipmanager.databinding.ActivityConvertDetailBinding
import com.jintao.vipmanager.databinding.ItemConvertGoodsLayoutBinding
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.GeneralUtils

class ConvertDetailActivity : BaseActivity<ActivityConvertDetailBinding>() {

    override fun initData() {
        var vipUserId = intent.getLongExtra(AppConstant.VIP_USER_ID, 0)
        var vipUid = intent.getIntExtra("vip_integral_uid", 0)

        val databaseRepository = DatabaseRepository()

        mBinding.title.tvTitleContent.setText("兑换明细")

        launchWithNotLoadingFlow({ databaseRepository.getVipUserDao().queryUserInfoFromId(vipUserId) }) {
            onSuccess = { result ->
                mBinding.tvCurrentIntegral.setText(GeneralUtils.getInstence().formatAmount(result.getUserIntegral()))
            }
        }

        launchWithNotLoadingFlow({ databaseRepository.getConvertGoodsDao().queryFormUid(vipUid) }) {
            onSuccess = { convertGoodsList ->
                mBinding.tvConverCount.setText(convertGoodsList.size.toString())
                var convertIntegral = 0
                if (convertGoodsList!=null&&convertGoodsList.size!=0) {
                    for (index in 0 until convertGoodsList.size) {
                        val goodsInfo = convertGoodsList.get(index)
                        convertIntegral += goodsInfo.getUseIntegral()
                    }
                    mBinding.tvConvertIntegral.setText(convertIntegral.toString())
                    mBinding.rvGoodsList.layoutManager = LinearLayoutManager(this@ConvertDetailActivity)
                    val dividerItemDecoration =
                        DividerItemDecoration(this@ConvertDetailActivity, DividerItemDecoration.VERTICAL)
                    dividerItemDecoration.setDrawable(
                        ContextCompat.getDrawable(
                            this@ConvertDetailActivity,
                            R.drawable.home_divider_line_shape
                        )!!
                    )
                    mBinding.rvGoodsList.addItemDecoration(dividerItemDecoration)
                    val myAdapter = MyAdapter(convertGoodsList)
                    mBinding.rvGoodsList.adapter = myAdapter
                }else {
                    mBinding.tvConvertIntegral.setText("0")
                }
            }
        }
    }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener { finish() }
    }

    override fun initObserve() {

    }

    override fun getViewBinding(): ActivityConvertDetailBinding {
        return ActivityConvertDetailBinding.inflate(layoutInflater)
    }

    class MyAdapter(var list: List<DbConvertGoodsInfo>):RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_convert_goods_layout, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val convertGoodsInfo = list.get(position)
            holder.itemBind.itemTvJifen.setText("兑换"+convertGoodsInfo.getUseIntegral().toString()+"积分")
            holder.itemBind.itemTvContent.setText(convertGoodsInfo.getDesc())
            holder.itemBind.itemTvTime.setText(convertGoodsInfo.getTime())
            holder.itemView.setOnTouchListener(object :View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (event!=null) {
                        when(event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                holder.itemView.setBackgroundColor(Color.rgb(245,245,245))
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                holder.itemView.setBackgroundColor(Color.WHITE)
                            }
                        }
                    }
                    return true
                }
            })
        }

        override fun getItemCount(): Int {
            return list.size
        }

        class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
            var itemBind: ItemConvertGoodsLayoutBinding
            init {
                itemBind = ItemConvertGoodsLayoutBinding.bind(itemView)
            }
        }
    }
}