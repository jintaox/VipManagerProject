package com.jintao.vipmanager.activity

import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import com.hjq.gson.factory.GsonFactory
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.MyApplication
import com.jintao.vipmanager.adapter.VipUserAdapter
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.bean.UserOperateRecordInfo
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.collectIn
import com.jintao.vipmanager.database.dao.DbVipUserInfoDao
import com.jintao.vipmanager.database.launchWithLoadingNotFlow
import com.jintao.vipmanager.database.launchWithNotLoadingFlow
import com.jintao.vipmanager.databinding.ActivityHomeBinding
import com.jintao.vipmanager.dialog.ChangeIntegralDialog
import com.jintao.vipmanager.dialog.CheXiaoDialog
import com.jintao.vipmanager.dialog.ConsumeConfirmDialog
import com.jintao.vipmanager.listener.OnIntegralChangeListener
import com.jintao.vipmanager.listener.OnVipItemClickListener
import com.jintao.vipmanager.myview.ClearEditText
import com.jintao.vipmanager.viewmodel.HomeViewModel
import com.jintao.vipmanager.utils.*
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener
import me.jingbin.library.ByRecyclerView

class HomeActivity : BaseActivity<ActivityHomeBinding>(), ByRecyclerView.OnLoadMoreListener {

    override fun setStatusBarColor() = "#13BEFF"
    override fun setStatusBarDarkFont() = false

    private lateinit var homeViewModel:HomeViewModel
    private lateinit var vipUserAdapter: VipUserAdapter
    private lateinit var databaseRepository: DatabaseRepository
    private lateinit var vipUserDao: DbVipUserInfoDao
    private val vipUserList = mutableListOf<DbVipUserInfo>()
    private val findUserList = mutableListOf<DbVipUserInfo>()
    private val CACHE_INTERVAL_TIME = 1000 * 60 * 3
    private var isDefaultMode = true
    private val pageSize = 20
    private var pageCount = 0
    private var lastUploadTime = 0L

    private val mUserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode==200) {//默认成功返回刷新
                setRefreshDate()
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

    override fun initObserve() {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        homeViewModel.loadDatabaseState.collectIn(this, Lifecycle.State.CREATED) {
            onSuccess = { initStatus ->
                if (initStatus) {
                    initPageData()
                }
            }
        }
    }

    override fun initData() {
        homeViewModel.initDataBaseFile(this)

        //读取文件缓存
        if (MyApplication.cacheOperateRecordList ==null || MyApplication.cacheOperateRecordList.size == 0) {
            val backupFilePath = StreamUtils.getBackupFilePath(this)
            if (!TextUtils.isEmpty(backupFilePath)) {
                val readerContent = StreamUtils.readerFile(backupFilePath)
                val operateRecordType = object : TypeToken<List<UserOperateRecordInfo>>() {}.type
                val tempList = GsonFactory.getSingletonGson().fromJson<List<UserOperateRecordInfo>>(readerContent, operateRecordType)
                if (tempList != null && tempList.size != 0) {
                    if (tempList.size > 100) {
                        val subList = tempList.subList(0, 50)
                        MyApplication.cacheOperateRecordList.addAll(subList)
                    }else {
                        MyApplication.cacheOperateRecordList.addAll(tempList)
                    }
                }
            }
        }
    }

    private fun initPageData() {
        databaseRepository = DatabaseRepository()
        vipUserDao = databaseRepository.getVipUserDao()
        val linearLayoutManager = LinearLayoutManager(this)
        mBinding.rvUserList.layoutManager = linearLayoutManager
        mBinding.rvUserList.setOnLoadMoreListener(this)
        mBinding.rvUserList.setRefreshEnabled(false)
        mBinding.rvUserList.setLoadMoreEnabled(false)

        vipUserAdapter = VipUserAdapter(vipUserList, object : OnVipItemClickListener {
            override fun onItemClick(userInfo: DbVipUserInfo,type:Int) {
                if (GeneralUtils.getInstence().onClickEnable()) {
                    if (type==1) {
                        val intent = Intent(this@HomeActivity, UserDetailActivity::class.java)
                        intent.putExtra(AppConstant.VIP_USER_ID,userInfo.getId())
                        mUserLauncher.launch(intent)
                    }else if (type==2) {
                        XPopup.Builder(this@HomeActivity)
                            .asCustom(CheXiaoDialog(this@HomeActivity,"撤销记录: "+userInfo.getUserName(),"撤销最后一条消费记录？",object :
                                OnConfirmListener {
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

    override fun onResume() {
        super.onResume()
        homeViewModel.checkUploadDataBase(this)
    }

    override fun initListener() {
        mBinding.ivTitleRight.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                mUserLauncher.launch(Intent(this@HomeActivity, AddUserActivity::class.java))
            }
        }
        mBinding.ivTitlePerson.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                launchWithLoadingNotFlow({ vipUserDao.getUserCount() }) {
                    onSuccess = { userCount ->
                        val intent = Intent(this@HomeActivity, SettingActivity::class.java)
                        intent.putExtra(AppConstant.VIP_USER_NUMBER, userCount)
                        startActivity(intent)
                    }
                }
            }
        }
        mBinding.ivTitleFunction.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                val intent = Intent(this@HomeActivity, FunctionActivity::class.java)
                startActivity(intent)
            }
        }
        mBinding.cetInputMsg.setOnTextChangeListener(object : ClearEditText.OnTextChangeListener{
            override fun setText(content: String) {
                if (!TextUtils.isEmpty(content)) {
                    isDefaultMode = false
                    findInputContentUsers(content)
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

        mBinding.btnAddUser.setOnClickListener {
            launchWithLoadingNotFlow({ homeViewModel.testBatchAddUsers(20) }) {
                onSuccess = {
                    ToastUtils.show("生成完成")
                    setRefreshDate()
                }
            }
        }
    }

    private fun findInputContentUsers(content: String) {
        val isNumberic = GeneralUtils.getInstence().isNumberic(content)
        launchWithNotLoadingFlow({ databaseRepository.queryByLikeContent(isNumberic,content) }) {
            onSuccess = { queryWhereList ->
                if (isNumberic && content.length == 4) {
                    findUserList.clear()
                    queryWhereList.forEach {
                        val back4Bit = it.getPhoneNumber().substring(it.getPhoneNumber().length - 4)
                        if (back4Bit.equals(content)) {
                            findUserList.add(0, it)
                        }else {
                            findUserList.add(it)
                        }
                    }
                    vipUserAdapter.setListData(content, findUserList)
                }else {
                    vipUserAdapter.setListData(content, queryWhereList)
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
            }
        }
    }

    private fun showChangeDialog(userInfo: DbVipUserInfo) {
        var title = userInfo.getUserName() + " " +userInfo.getPhoneNumber()
        XPopup.Builder(this@HomeActivity)
            .asCustom(ChangeIntegralDialog(this@HomeActivity,title,userInfo.getId(),object : OnIntegralChangeListener {
                override fun onSuccess() {
                    setRefreshDate()
                    if (MyApplication.uploadBackupCount >= 5 && System.currentTimeMillis() - lastUploadTime > 60 * 1000) {
                        lastUploadTime = System.currentTimeMillis()
                        MyApplication.uploadBackupCount = 0
                        homeViewModel.uploadBackupFile(this@HomeActivity)
                    }
                }

                override fun onRepeatConfirm(type: Int, thisAmount: Float) {
                    showRepeatConfirmDialog(userInfo.getId(), title,type, thisAmount)
                }
            }))
            .show()
    }

    private fun showRepeatConfirmDialog(
        vipUserId: Long,
        title: String,
        type: Int,
        thisAmount: Float
    ) {
        XPopup.Builder(this@HomeActivity)
            .asCustom(ConsumeConfirmDialog(this@HomeActivity,vipUserId,title,type,thisAmount,object : OnIntegralChangeListener {
                override fun onSuccess() {
                    setRefreshDate()
                }

                override fun onRepeatConfirm(type: Int, thisAmount: Float) {

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

            launchWithNotLoadingFlow({ databaseRepository.queryByLikeDate(todayDate)}) {
                onSuccess = { todayConsumeList ->
                    var negativeConsume = 0f
                    for (index in 0 until todayConsumeList.size) {
                        val consumeInfo = todayConsumeList.get(index)
                        if (consumeInfo.getConsumeType() == 1) {
                            MyApplication.todayCount ++
                            MyApplication.todayConsume += consumeInfo.getConsumeAmount()
                        }else {
                            negativeConsume += consumeInfo.getConsumeAmount()
                        }
                    }
                    if (negativeConsume!=0f) {
                        if (MyApplication.todayConsume > negativeConsume) {
                            MyApplication.todayConsume = MyApplication.todayConsume - negativeConsume
                        }else {
                            MyApplication.todayConsume = 0f
                        }
                    }

                    if (MyApplication.todayConsume < 0f) {
                        mBinding.tvTitleContent.setText("今日营收：0笔，0元")
                    }else {
                        mBinding.tvTitleContent.setText("今日营收："+MyApplication.todayCount.toString()+"笔，"+GeneralUtils.getInstence().formatAmount(MyApplication.todayConsume)+"元")
                    }
                }
            }
        }else {
            if (MyApplication.todayConsume < 0f) {
                mBinding.tvTitleContent.setText("今日营收：0笔，0元")
            }else {
                mBinding.tvTitleContent.setText("今日营收："+MyApplication.todayCount.toString()+"笔，"+GeneralUtils.getInstence().formatAmount(MyApplication.todayConsume)+"元")
            }
        }
    }

    private fun revokeLastConsume(userInfo: DbVipUserInfo) {
        //先查询，如果以是最后一条，则重置位0
        launchWithNotLoadingFlow({ databaseRepository.queryByConsumeUidLast(userInfo.getUid(),2) }) {
            onSuccess = { consumeLasts ->
                if (consumeLasts!=null&&consumeLasts.size!=0) {
                    val lastInfo = consumeLasts.get(0)
                    homeViewModel.deleteConsumeInfo(consumeLasts.get(0))
                    if (consumeLasts.size==1) {//只有最后一条
                        userInfo.setExchangeNumber(0f)
                        userInfo.setTotalAmount(0f)
                        userInfo.setCurrentAmount(0f)
                        userInfo.setUserIntegral(0f)
                        userInfo.setTotalIntegral(0f)
                        userInfo.setConsumeTime( GeneralUtils.getInstence().dataToTimestamp(lastInfo.getConsumeTime()))
                        userInfo.setConsumeNumber(0)
                        homeViewModel.updateUserInfo(userInfo)
                    }else {//正常两条以上
                        val twoInfo = consumeLasts.get(1)//只用来给新的最后一条数据赋值
                        val saveJifen = MmkvUtil.getString(AppConstant.INTEGRAL_CONVERT_NUMBER, "1").toFloat()
                        var lastJifen = lastInfo.getConsumeAmount() * saveJifen
                        if (lastInfo.getConsumeType() == 1) {
                            val totalAmount = CalculatorUtils.getInstence().subtract(userInfo.getTotalAmount(),lastInfo.getConsumeAmount())
                            val currentAmount = CalculatorUtils.getInstence().subtract(userInfo.getCurrentAmount(),lastInfo.getConsumeAmount())
                            val totalIntegral = CalculatorUtils.getInstence().subtract(userInfo.getTotalIntegral(),lastJifen)
                            val currentIntegral = CalculatorUtils.getInstence().subtract(userInfo.getUserIntegral(),lastJifen)
                            userInfo.setTotalAmount(totalAmount.toFloat())
                            userInfo.setCurrentAmount(currentAmount.toFloat())
                            userInfo.setUserIntegral(currentIntegral.toFloat())
                            userInfo.setTotalIntegral(totalIntegral.toFloat())
                            userInfo.setConsumeNumber(userInfo.getConsumeNumber()-1)
                        }else {
                            val totalAmount = CalculatorUtils.getInstence().add(userInfo.getTotalAmount(),lastInfo.getConsumeAmount())
                            val currentAmount = CalculatorUtils.getInstence().add(userInfo.getCurrentAmount(),lastInfo.getConsumeAmount())
                            val totalIntegral = CalculatorUtils.getInstence().add(userInfo.getTotalIntegral(),lastJifen)
                            val currentIntegral = CalculatorUtils.getInstence().add(userInfo.getUserIntegral(),lastJifen)
                            userInfo.setTotalAmount(totalAmount.toFloat())
                            userInfo.setCurrentAmount(currentAmount.toFloat())
                            userInfo.setUserIntegral(currentIntegral.toFloat())
                            userInfo.setTotalIntegral(totalIntegral.toFloat())
                        }
                        userInfo.setExchangeNumber(twoInfo.getConsumeAmount())
                        userInfo.setConsumeTime(GeneralUtils.getInstence().dataToTimestamp(twoInfo.getConsumeTime()))
                        homeViewModel.updateUserInfo(userInfo)
                    }

                    if (MyApplication.cacheOperateRecordList !=null && MyApplication.cacheOperateRecordList.size != 0) {
                        val operateRecordIterator = MyApplication.cacheOperateRecordList.iterator()
                        while (operateRecordIterator.hasNext()){
                            val operateRecordInfo = operateRecordIterator.next()
                            if (operateRecordInfo.createTime == lastInfo.getConsumeTime() && operateRecordInfo.uid == lastInfo.getUid()) {
                                operateRecordIterator.remove()
                            }
                        }
                    }

                    ToastUtils.show("已撤销最后一条消费记录")
                    //这个0是让今日营收统计重新计算
                    MyApplication.todayConsume = 0f
                    setRefreshDate()
                }else {
                    ToastUtils.show("请添加消费记录在来吧")
                }
            }
        }
    }

    private fun setRefreshDate() {
        vipUserList.clear()
        pageCount = 0
        launchWithNotLoadingFlow({ databaseRepository.queryPageReverseList(pageSize,pageCount) }) {
            onSuccess = { dbList ->
                if (dbList.size==pageSize) {
                    mBinding.rvUserList.setLoadMoreEnabled(true)
                }
                vipUserList.addAll(dbList)
                mBinding.rvUserList.scrollToPosition(0)
                vipUserAdapter.notifyDataSetChanged()
                mBinding.cetInputMsg.setText("")
                showTitleAmount()
            }
        }
    }

    override fun onLoadMore() {
        pageCount++
        launchWithNotLoadingFlow({ databaseRepository.queryPageReverseList(pageSize,pageCount) }) {
            onSuccess = { dbList ->
                mBinding.rvUserList.loadMoreComplete()
                if (dbList.size < pageSize) {
                    mBinding.rvUserList.setLoadMoreEnabled(false)
                }
                vipUserList.addAll(dbList)
                vipUserAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onBackPressed() {
        if (isDefaultMode) {
            super.onBackPressed()
        }else {
            mBinding.cetInputMsg.setText("")
        }
    }

    override fun getViewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onDestroy() {
        homeViewModel.checkSaveDatabaseToSdcard(this)
        super.onDestroy()
    }
}