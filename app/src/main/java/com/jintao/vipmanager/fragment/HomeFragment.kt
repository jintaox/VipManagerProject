package com.jintao.vipmanager.fragment

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.MyApplication
import com.jintao.vipmanager.activity.AddUserActivity
import com.jintao.vipmanager.activity.HomeActivity
import com.jintao.vipmanager.activity.SettingActivity
import com.jintao.vipmanager.activity.UserDetailActivity
import com.jintao.vipmanager.adapter.VipUserAdapter
import com.jintao.vipmanager.base.BaseFragment
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.helper.DbVipUserHelper
import com.jintao.vipmanager.databinding.FragmentHomeBinding
import com.jintao.vipmanager.dialog.ChangeIntegralDialog
import com.jintao.vipmanager.dialog.CheXiaoDialog
import com.jintao.vipmanager.listener.OnIntegralChangeListener
import com.jintao.vipmanager.listener.OnVipItemClickListener
import com.jintao.vipmanager.myview.ClearEditText
import com.jintao.vipmanager.utils.*
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener
import me.jingbin.library.ByRecyclerView

/**
 * Author: jintao
 * CreateDate: 2023/8/23 20:45
 * Description:
 */
class HomeFragment:BaseFragment<FragmentHomeBinding>(), ByRecyclerView.OnLoadMoreListener {

    private lateinit var vipUserAdapter:VipUserAdapter
    private lateinit var homeActivity:HomeActivity
    private lateinit var vipUserDao: DbVipUserHelper<DbVipUserInfo>
    private var vipUserList:ArrayList<DbVipUserInfo> = arrayListOf()
    private val CACHE_INTERVAL_TIME = 1000 * 60 * 3
    private var isDefaultMode = true
    private val pageSize = 20
    private var pageCount = 0

    override fun initData() {
        homeActivity = context as HomeActivity
        vipUserDao = DbVipUserHelper(homeActivity, DbVipUserInfo::class.java).vipUserDao
        val linearLayoutManager = LinearLayoutManager(homeActivity)
        mBinding.rvUserList.layoutManager = linearLayoutManager
        mBinding.rvUserList.setOnLoadMoreListener(this)
        mBinding.rvUserList.setRefreshEnabled(false)
        mBinding.rvUserList.setLoadMoreEnabled(false)

        vipUserAdapter = VipUserAdapter(vipUserList, object : OnVipItemClickListener {
            override fun onItemClick(userInfo: DbVipUserInfo,type:Int) {
                if (GeneralUtils.getInstence().onClickEnable()) {
                    if (type==1) {
                        val intent = Intent(homeActivity, UserDetailActivity::class.java)
                        intent.putExtra(AppConstant.VIP_USER_ID,userInfo.id)
                        mUserLauncher.launch(intent)
                    }else if (type==2) {
                        XPopup.Builder(homeActivity)
                            .asCustom(CheXiaoDialog(homeActivity,"撤销记录: "+userInfo.userName,"撤销最后一条消费记录？",object :OnConfirmListener{
                                override fun onConfirm() {
                                    revokeLastConsume(userInfo)
                                }
                            }))
                            .show()
                    }else {
                        showChangeDialog(userInfo)
                    }
                }
            }
        })
        mBinding.rvUserList.adapter = vipUserAdapter

        setRefreshDate()
    }

    private val mUserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode==200) {//默认成功返回刷新
                setRefreshDate()
                mBinding.cetInputMsg.setText("")
            }else if (it.resultCode==201) {//如果添加已存在用户，则直接显示
                if (it.data!=null) {
                    val phoneNumber = it.data!!.getStringExtra("addpage_phone_number")
                    if (!TextUtils.isEmpty(phoneNumber)) {
                        mBinding.cetInputMsg.setText(phoneNumber)
                        mBinding.cetInputMsg.setSelection(phoneNumber!!.length)
                    }
                }
            }
        }

    override fun initListener() {
        mBinding.ivTitleRight.setOnClickListener {
            mUserLauncher.launch(Intent(homeActivity, AddUserActivity::class.java))
        }
        mBinding.ivTitlePerson.setOnClickListener {
            val intent = Intent(homeActivity, SettingActivity::class.java)
            intent.putExtra(AppConstant.VIP_USER_NUMBER, vipUserDao.queryVipUserCount())
            startActivity(intent)
        }
        mBinding.cetInputMsg.setOnTextChangeListener(object :ClearEditText.OnTextChangeListener{
            override fun setText(content: String) {
                if (!TextUtils.isEmpty(content)) {
                    isDefaultMode = false
                    val isNumberic = GeneralUtils.getInstence().isNumberic(content)
                    val queryWhereList = vipUserDao.queryByLikeContent(isNumberic,content)
                    if (isNumberic&&content.length==4) {
                        val newList = arrayListOf<DbVipUserInfo>()
                        queryWhereList.forEach {
                            val back4Bit = it.phoneNumber.substring(it.phoneNumber.length - 4)
                            if (back4Bit.equals(content)) {
                                newList.add(0,it)
                            }else {
                                newList.add(it)
                            }
                        }
                        vipUserAdapter.setListData(content,newList)
                    }else {
                        vipUserAdapter.setListData(content,queryWhereList)
                    }
                    if (queryWhereList.size==0) {
                        if (mBinding.tvTipNull.visibility == View.GONE) {
                            mBinding.tvTipNull.visibility = View.VISIBLE
                        }
                    }else {
                        if (mBinding.tvTipNull.visibility == View.VISIBLE) {
                            mBinding.tvTipNull.visibility = View.GONE
                        }
                    }
                    vipUserAdapter.notifyDataSetChanged()
                }else {
                    if (!isDefaultMode) {
                        isDefaultMode = true
                        if (mBinding.tvTipNull.visibility == View.VISIBLE) {
                            mBinding.tvTipNull.visibility = View.GONE
                        }
                        vipUserAdapter.setListData("",vipUserList)
                        vipUserAdapter.notifyDataSetChanged()
                    }
                }
            }
        })

        //添加测试数据入口
        mBinding.btnAddUser.setOnClickListener {
            val size = 500//添加500条
            val phoneList = StreamUtils.generatePhoneNum(size)
            val nameList = RandomNameUtils.getNameList(size)
            val mutableListOf = mutableListOf<DbVipUserInfo>()
            for (index in 0 until size) {
                val phoneInfo = phoneList.get(index)
                val isPhoneAvai = vipUserDao.queryByVipPhoneAvai(phoneInfo)
                if (isPhoneAvai) {
                    var generateUid = GeneralUtils.getInstence().generateRandomUid(phoneInfo).toInt()
                    val queryByVipUidAvai = vipUserDao.queryByVipUidAvai(generateUid)
                    if (!queryByVipUidAvai) {
                        generateUid = GeneralUtils.getInstence().generateRandomUid(phoneInfo).toInt()
                    }
                    val currentTime = System.currentTimeMillis()
                    val dbVipUserInfo = DbVipUserInfo()
                    dbVipUserInfo.userName = nameList.get(index)
                    dbVipUserInfo.phoneNumber = phoneInfo
                    dbVipUserInfo.uid = generateUid

                    dbVipUserInfo.lastAmount = 0f
                    dbVipUserInfo.totalAmount = 0f
                    dbVipUserInfo.currentAmount = 0f
                    dbVipUserInfo.userIntegral = 0f
                    dbVipUserInfo.totalIntegral = 0f
                    dbVipUserInfo.consumeTime = currentTime
                    dbVipUserInfo.registerTime = GeneralUtils.getInstence().timeFormat(currentTime)
                    dbVipUserInfo.consumeNumber = 0
                    mutableListOf.add(dbVipUserInfo)
                }
            }
            val startTime = System.currentTimeMillis()
            vipUserDao.insertMultiple(mutableListOf)
            val stopTime = System.currentTimeMillis() - startTime
            Log.e("AAAAAA","耗时："+stopTime.toString())
            ToastUtils.show("生成完成")
            setRefreshDate()
        }
    }

    private fun showChangeDialog(userInfo: DbVipUserInfo) {
        var title = userInfo.userName + " " +userInfo.phoneNumber
        XPopup.Builder(homeActivity)
            .asCustom(ChangeIntegralDialog(homeActivity,title,userInfo.id,object : OnIntegralChangeListener {
                override fun onSuccess() {
                    setRefreshDate()
                    mBinding.cetInputMsg.setText("")
                }
            }))
            .show()
    }

    private fun showTitleAmount() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - MyApplication.cacheHomeTime > CACHE_INTERVAL_TIME||MyApplication.todayConsume==0f) {
            MyApplication.cacheHomeTime = currentTime
            MyApplication.todayConsume = 0f
            MyApplication.todayCount = 0
            val todayDate = GeneralUtils.getInstence().getTodayDateStr()
            val todayConsumeList = vipUserDao.queryByLikeDate(todayDate)
            var negativeConsume = 0f
            for (index in 0 until todayConsumeList.size) {
                val consumeInfo = todayConsumeList.get(index)
                if (consumeInfo.consumeType) {
                    MyApplication.todayCount ++
                    MyApplication.todayConsume += consumeInfo.consumeAmount
                }else {
                    negativeConsume += consumeInfo.consumeAmount
                }
            }
            if (negativeConsume!=0f) {
                if (MyApplication.todayConsume > negativeConsume) {
                    MyApplication.todayConsume = MyApplication.todayConsume - negativeConsume
                }else {
                    MyApplication.todayConsume = 0f
                }
            }
        }
        if (MyApplication.todayConsume < 0f) {
            mBinding.tvTitleContent.setText("今日营收：0笔，0元")
        }else {
            mBinding.tvTitleContent.setText("今日营收："+MyApplication.todayCount.toString()+"笔，"+GeneralUtils.getInstence().formatAmount(MyApplication.todayConsume)+"元")
        }

    }

    private fun revokeLastConsume(userInfo: DbVipUserInfo) {
        //先查询，如果以是最后一条，则重置位0
        val consumeLasts = vipUserDao.queryByConsumeUidLast(userInfo.uid,2)
        if (consumeLasts!=null&&consumeLasts.size!=0) {
            val lastInfo = consumeLasts.get(0)
            vipUserDao.deleteConsume(consumeLasts.get(0))
            if (consumeLasts.size==1) {//只有最后一条
                userInfo.lastAmount = 0f
                userInfo.totalAmount = 0f
                userInfo.currentAmount = 0f
                userInfo.userIntegral = 0f
                userInfo.totalIntegral = 0f
                userInfo.consumeTime = GeneralUtils.getInstence().dataToTimestamp(lastInfo.consumeTime)
                userInfo.consumeNumber = 0
                vipUserDao.update(userInfo)
            }else {//正常两条以上
                val twoInfo = consumeLasts.get(1)//只用来给新的最后一条数据赋值
                val saveJifen = MmkvUtil.getString(AppConstant.INTEGRAL_CONVERT_NUMBER, "1").toFloat()
                var lastJifen = lastInfo.consumeAmount * saveJifen
                if (lastInfo.consumeType) {
                    val totalAmount = CalculatorUtils.getInstence().subtract(userInfo.totalAmount,lastInfo.consumeAmount)
                    val currentAmount = CalculatorUtils.getInstence().subtract(userInfo.currentAmount,lastInfo.consumeAmount)
                    val totalIntegral = CalculatorUtils.getInstence().subtract(userInfo.totalIntegral,lastJifen)
                    val currentIntegral = CalculatorUtils.getInstence().subtract(userInfo.userIntegral,lastJifen)
                    userInfo.totalAmount = totalAmount.toFloat()
                    userInfo.currentAmount = currentAmount.toFloat()
                    userInfo.userIntegral = currentIntegral.toFloat()
                    userInfo.totalIntegral = totalIntegral.toFloat()
                    userInfo.consumeNumber = userInfo.consumeNumber-1
                }else {
                    val totalAmount = CalculatorUtils.getInstence().add(userInfo.totalAmount,lastInfo.consumeAmount)
                    val currentAmount = CalculatorUtils.getInstence().add(userInfo.currentAmount,lastInfo.consumeAmount)
                    val totalIntegral = CalculatorUtils.getInstence().add(userInfo.totalIntegral,lastJifen)
                    val currentIntegral = CalculatorUtils.getInstence().add(userInfo.userIntegral,lastJifen)
                    userInfo.totalAmount = totalAmount.toFloat()
                    userInfo.currentAmount = currentAmount.toFloat()
                    userInfo.userIntegral = currentIntegral.toFloat()
                    userInfo.totalIntegral = totalIntegral.toFloat()
                }
                userInfo.lastAmount = twoInfo.consumeAmount
                userInfo.consumeTime = GeneralUtils.getInstence().dataToTimestamp(twoInfo.consumeTime)
                vipUserDao.update(userInfo)
            }
            ToastUtils.show("已撤销最后一条消费记录")
            //这个0是让今日营收统计重新计算
            MyApplication.todayConsume = 0f
            setRefreshDate()
        }else {
            ToastUtils.show("请添加消费记录在来吧")
        }
    }

    private fun setRefreshDate() {
        vipUserList.clear()
        pageCount = 0
        val dbList = vipUserDao.queryPageReverseList(pageSize,pageCount)
        if (dbList.size==pageSize) {
            mBinding.rvUserList.setLoadMoreEnabled(true)
        }
        vipUserList.addAll(dbList)
        mBinding.rvUserList.scrollToPosition(0)
        vipUserAdapter.notifyDataSetChanged()

        showTitleAmount()
    }

    override fun onLoadMore() {
        pageCount++
        val dbList = vipUserDao.queryPageReverseList(pageSize,pageCount)
        mBinding.rvUserList.loadMoreComplete()
        if (dbList.size<pageSize) {
            mBinding.rvUserList.setLoadMoreEnabled(false)
        }
        vipUserList.addAll(dbList)
        vipUserAdapter.notifyDataSetChanged()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater,container,false)
    }

    fun setBackPressed():Boolean {
        if (isDefaultMode) {
            return false
        }else {
            mBinding.cetInputMsg.setText("")
            return true
        }
    }
}