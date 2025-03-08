package com.jintao.vipmanager.activity

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jintao.vipmanager.R
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.bean.UserConvertGoodsInfo
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.launchWithNotLoadingFlow
import com.jintao.vipmanager.databinding.ActivityConvertGoodsTableBinding
import com.jintao.vipmanager.databinding.ItemConvertGoodsTableLayoutBinding
import com.jintao.vipmanager.dialog.CommonAllDialog
import kotlinx.coroutines.launch
import me.jingbin.library.ByRecyclerView
import java.lang.StringBuilder

/**
 * Author: zhanghui
 * CreateDate: 2025/1/24 16:52
 * Description:所有用户兑换积分表
 */
class ConvertGoodsTableActivity :BaseActivity<ActivityConvertGoodsTableBinding>(), ByRecyclerView.OnLoadMoreListener {

    private var pageCount = 0
    private val pageSize = 20
    private lateinit var databaseRepository: DatabaseRepository
    private var convertGoodeList:ArrayList<UserConvertGoodsInfo> = arrayListOf()
    private lateinit var convertGoodeAdapter:MyAdapter

    override fun initData() {
        mBinding.title.tvTitleContent.setText("用户兑换表")
        databaseRepository = DatabaseRepository()
        val linearLayoutManager = LinearLayoutManager(this)
        mBinding.rvConvertGoodeList.layoutManager = linearLayoutManager
        val dividerItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.home_divider_line_shape
            )!!
        )
        mBinding.rvConvertGoodeList.addItemDecoration(dividerItemDecoration)

        launchWithNotLoadingFlow({ databaseRepository.queryAllConvertGoodsList(pageSize,0) }) {
            onSuccess = { dbList ->
                if (dbList.size==pageSize) {
                    mBinding.rvConvertGoodeList.setLoadMoreEnabled(true)
                }else {
                    mBinding.rvConvertGoodeList.setLoadMoreEnabled(false)
                }
                convertGoodeList.addAll(dbList)
                convertGoodeAdapter = MyAdapter(this@ConvertGoodsTableActivity, convertGoodeList)
                mBinding.rvConvertGoodeList.adapter = convertGoodeAdapter
            }
        }
    }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener { finish() }
        mBinding.rvConvertGoodeList.setOnLoadMoreListener(this)
        mBinding.rvConvertGoodeList.setRefreshEnabled(false)
    }

    override fun initObserve() {

    }

    override fun onLoadMore() {
        pageCount++
        launchWithNotLoadingFlow({ databaseRepository.queryAllConvertGoodsList(pageSize,pageCount) }) {
            onSuccess = { dbList ->
                mBinding.rvConvertGoodeList.loadMoreComplete()
                if (dbList.size < pageSize) {
                    mBinding.rvConvertGoodeList.setLoadMoreEnabled(false)
                }
                convertGoodeList.addAll(dbList)
                convertGoodeAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun getViewBinding(): ActivityConvertGoodsTableBinding {
        return ActivityConvertGoodsTableBinding.inflate(layoutInflater)
    }

    class MyAdapter(var activity: Activity, var list: List<UserConvertGoodsInfo>): RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_convert_goods_table_layout, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val convertGoodsInfo = list.get(position)
            val xuhao = (position + 1).toString()
            if (!TextUtils.isEmpty(convertGoodsInfo.getPhoneNumber())) {
                holder.itemBind.itemTvUserName.setText(xuhao+"、"+convertGoodsInfo.getUserName()+convertGoodsInfo.getPhoneNumber())
            }else {
                holder.itemBind.itemTvUserName.setText(xuhao+"、"+convertGoodsInfo.getUserName())
            }

            holder.itemBind.itemTvContent.setText(convertGoodsInfo.getUseIntegral().toString()+"积分-> "+convertGoodsInfo.getDesc())
            holder.itemBind.itemTvTime.setText(convertGoodsInfo.getTime())
            holder.itemView.setOnClickListener {
                showContentDialog(convertGoodsInfo)
            }
        }

        private fun showContentDialog(convertGoodsInfo: UserConvertGoodsInfo) {
            val stringBuilder = StringBuilder()
            stringBuilder.append(convertGoodsInfo.getUseIntegral()).append("积分").append("\r\n")
            stringBuilder.append("兑换-> ").append(convertGoodsInfo.getDesc()).append("\r\n")
            stringBuilder.append(convertGoodsInfo.getTime())
            CommonAllDialog.Builder(activity)
                .setTitleContent(convertGoodsInfo.getUserName()+convertGoodsInfo.getPhoneNumber())
                .setDescContent(stringBuilder.toString())
                .setIsSingleButton(true)
                .build()
        }

        override fun getItemCount(): Int {
            return list.size
        }

        class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var itemBind: ItemConvertGoodsTableLayoutBinding
            init {
                itemBind = ItemConvertGoodsTableLayoutBinding.bind(itemView)
            }
        }

    }
}