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
import com.jintao.vipmanager.database.bean.DbConvertGoodsInfo
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.helper.DbVipUserHelper
import com.jintao.vipmanager.databinding.ActivityConvertDetailBinding
import com.jintao.vipmanager.databinding.ItemConvertGoodsLayoutBinding
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.GeneralUtils

class ConvertDetailActivity : BaseActivity<ActivityConvertDetailBinding>() {

    private lateinit var vipUserDao: DbVipUserHelper<DbVipUserInfo>
    private lateinit var convertGoodsDao: DbVipUserHelper<DbConvertGoodsInfo>
    private lateinit var vipUserInfo:DbVipUserInfo

    override fun initData() {
        var vipUserId = intent.getLongExtra(AppConstant.VIP_USER_ID, 0)
        var vipUid = intent.getIntExtra("vip_integral_uid", 0)

        vipUserDao = DbVipUserHelper(this, DbVipUserInfo::class.java).vipUserDao
        convertGoodsDao = DbVipUserHelper(this, DbConvertGoodsInfo::class.java).convertGoodsDao

        mBinding.title.tvTitleContent.setText("兑换明细")

        vipUserInfo = vipUserDao.queryById(vipUserId)

        mBinding.tvCurrentIntegral.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.userIntegral))

        var convertIntegral = 0
        val convertGoodsList = convertGoodsDao.queryByConvertGoodsUid(vipUid)
        mBinding.tvConverCount.setText(convertGoodsList.size.toString())

        if (convertGoodsList!=null&&convertGoodsList.size!=0) {
            for (index in 0 until convertGoodsList.size) {
                val goodsInfo = convertGoodsList.get(index)
                convertIntegral += goodsInfo.useIntegral
            }
            mBinding.tvConvertIntegral.setText(convertIntegral.toString())
            mBinding.rvGoodsList.layoutManager = LinearLayoutManager(this)
            val dividerItemDecoration =
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(
                ContextCompat.getDrawable(
                    this,
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

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener { finish() }
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
            holder.itemBind.itemTvJifen.setText("兑换"+convertGoodsInfo.useIntegral.toString()+"积分")
            holder.itemBind.itemTvContent.setText(convertGoodsInfo.desc)
            holder.itemBind.itemTvTime.setText(convertGoodsInfo.time)
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