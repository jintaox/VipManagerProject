package com.jintao.vipmanager.activity

import android.content.Intent
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.R
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.collectIn
import com.jintao.vipmanager.database.launchWithNotLoadingFlow
import com.jintao.vipmanager.databinding.ActivityUserDetailBinding
import com.jintao.vipmanager.dialog.ChangeIntegralDialog
import com.jintao.vipmanager.dialog.CheXiaoDialog
import com.jintao.vipmanager.dialog.CommonAllDialog
import com.jintao.vipmanager.dialog.ConsumeConfirmDialog
import com.jintao.vipmanager.listener.OnIntegralChangeListener
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.GeneralUtils
import com.jintao.vipmanager.viewmodel.UserDetailViewModel
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener

class UserDetailActivity:BaseActivity<ActivityUserDetailBinding>() {

    private var vipUserId:Long = -1
    private lateinit var vipUserInfo:DbVipUserInfo
    private lateinit var mViewModel:UserDetailViewModel
    private lateinit var databaseRepository:DatabaseRepository
    private var isRrfreshHomePage = false
    private var isShowTotalAmount = false
    private var isShowUserAmount = false

    override fun initData() {
        mBinding.title.tvTitleContent.setText("会员详情")
        vipUserId = intent.getLongExtra(AppConstant.VIP_USER_ID, -1)
//        Log.e("AAAAAA",vipUserId.toString())
        databaseRepository = DatabaseRepository()
        mViewModel.queryVipUserInfo(vipUserId)
    }

    override fun initObserve() {
        mViewModel = ViewModelProvider(this).get(UserDetailViewModel::class.java)
        mViewModel.loadUserFlowState.collectIn(this, Lifecycle.State.STARTED) {
            onSuccess = { result ->
                vipUserInfo = result
                mBinding.tvUserName.setText(result.getUserName())
                mBinding.tvPhoneNumber.setText(result.getPhoneNumber())
                mBinding.tvRegisterTime.setText("注册时间:"+result.getRegisterTime())
                mBinding.tvCurrentIntegral.setText(GeneralUtils.getInstence().formatAmount(result.getUserIntegral()))
                mBinding.tvConsumeCount.setText(result.getConsumeNumber().toString())
                if (result.getUserSex().equals("男")) {
                    mBinding.ivUserSex.setImageResource(R.mipmap.iv_square_list_sex_boy)
                }else if (result.getUserSex().equals("女")) {
                    mBinding.ivUserSex.setImageResource(R.mipmap.iv_square_list_sex_girl)
                }

                if (isShowUserAmount) {
                    mBinding.tvConsumeAmount.setText(GeneralUtils.getInstence().formatAmount(result.getCurrentAmount())+"元")
                }
                if (isShowTotalAmount) {
                    mBinding.tvTotalAmount.setText(GeneralUtils.getInstence().formatAmount(result.getTotalAmount())+"元")
                }
                mBinding.tvTotalIntegral.setText(GeneralUtils.getInstence().formatAmount(result.getTotalIntegral()))
                if (result.getUid() != 0) {
                    getUserIntegralInfo(result.getUid())
                }
            }
        }
    }

    private val mUserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode==200) {
                isRrfreshHomePage = true
                mViewModel.queryVipUserInfo(vipUserId)
            }
        }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener {
            if (isRrfreshHomePage) {
                setResult(200)
            }
            finish()
        }

        mBinding.ivEyeTotalStatus.setOnClickListener {
            isShowTotalAmount = !isShowTotalAmount
            if (isShowTotalAmount) {
                mBinding.tvTotalAmount.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.getTotalAmount())+"元")
                mBinding.ivEyeTotalStatus.setImageResource(R.mipmap.amount_eye_show_icon)
            }else {
                mBinding.tvTotalAmount.setText("******")
                mBinding.ivEyeTotalStatus.setImageResource(R.mipmap.amount_eye_hide_icon)
            }
        }
        mBinding.ivEyeUserStatus.setOnClickListener {
            isShowUserAmount = !isShowUserAmount
            if (isShowUserAmount) {
                mBinding.tvConsumeAmount.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.getCurrentAmount())+"元")
                mBinding.ivEyeUserStatus.setImageResource(R.mipmap.amount_eye_show_icon)
            }else {
                mBinding.tvConsumeAmount.setText("******")
                mBinding.ivEyeUserStatus.setImageResource(R.mipmap.amount_eye_hide_icon)
            }
        }
        mBinding.clUserInfo.setOnClickListener{
            val intent = Intent(this, ChangeVipInfoActivity::class.java)
            intent.putExtra(AppConstant.VIP_USER_ID,vipUserId)
            mUserLauncher.launch(intent)
        }

        mBinding.rlChangeIntegral.setOnClickListener {
            var title = vipUserInfo.getUserName() + " "+vipUserInfo.getPhoneNumber()
            XPopup.Builder(this)
                .asCustom(ChangeIntegralDialog(this,title,vipUserId,object :OnIntegralChangeListener{
                    override fun onSuccess() {
                        setResult(200)
                        finish()
                    }

                    override fun onRepeatConfirm(type: Int, thisAmount: Float) {
                        showRepeatConfirmDialog(vipUserId,title,type,thisAmount)
                    }
                }))
                .show()
        }
        mBinding.rlIntegralDetail.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                val intent = Intent(this, IntegralDetailActivity::class.java)
                intent.putExtra("vip_integral_uid",vipUserInfo.getUid())
                intent.putExtra("vip_integral_number",vipUserInfo.getUserIntegral())
                intent.putExtra("vip_amount_number",vipUserInfo.getCurrentAmount())
                startActivity(intent)
            }
        }
        mBinding.rlIntegralDuihuan.setOnClickListener {
            val intent = Intent(this, ConvertGoodsActivity::class.java)
            intent.putExtra(AppConstant.VIP_USER_ID,vipUserId)
            intent.putExtra("vip_user_name",vipUserInfo.getUserName())
            intent.putExtra("vip_phone_number",vipUserInfo.getPhoneNumber())
            mUserLauncher.launch(intent)
        }
        mBinding.rlDuihuanDetail.setOnClickListener {
            val intent = Intent(this, ConvertDetailActivity::class.java)
            intent.putExtra(AppConstant.VIP_USER_ID,vipUserId)
            intent.putExtra("vip_integral_uid",vipUserInfo.getUid())
            startActivity(intent)
        }

        mBinding.rlClearUserdata.setOnClickListener {
            val desc = "该用户所有数据都会清空，所有数据都会变成初始状态，并且不能恢复？"
            XPopup.Builder(this)
                .asCustom(CheXiaoDialog(this,"清空用户数据",desc,object :
                    OnConfirmListener {
                    override fun onConfirm() {
                        deleteVipUser(2)
                    }
                }))
                .show()
        }

        mBinding.stvDeleteUser.setOnClickListener {
            val desc = "会员用户删除后不可恢复，确认删除？"
            XPopup.Builder(this)
                .asCustom(CheXiaoDialog(this,"删除会员",desc,object :
                    OnConfirmListener {
                    override fun onConfirm() {
                        deleteVipUser(1)
                    }
                }))
                .show()
        }
        mBinding.ivTotalHelp1.setOnClickListener {
            CommonAllDialog.Builder(this)
                .setTitleContent("当前积分说明")
                .setDescContent("当前积分是可以使用的积分")
                .setIsSingleButton(true)
                .build()
        }
        mBinding.ivTotalHelp2.setOnClickListener {
            CommonAllDialog.Builder(this)
                .setTitleContent("总积分说明")
                .setDescContent("总积分 = 当前积分 + 已兑换积分。\n总积分仅供参考，不可使用")
                .setIsSingleButton(true)
                .build()
        }
    }

    private fun getUserIntegralInfo(uid:Int) {
        launchWithNotLoadingFlow({databaseRepository.getConvertGoodsDao().queryFormUid(uid)}) {
            onSuccess = { convertGoodsList ->
                var exchangeIntegral = 0f
                if (convertGoodsList!=null&&convertGoodsList.size!=0) {
                    for (index in 0 until convertGoodsList.size) {
                        val goodsInfo = convertGoodsList.get(index)
                        exchangeIntegral += goodsInfo.getUseIntegral()
                    }
                }
                mBinding.tvDuihuanIntegral.setText(GeneralUtils.getInstence().formatAmount(exchangeIntegral)+"分")
                mBinding.tvDuihuanCount.setText("("+convertGoodsList.size.toString()+"次)")
            }
        }
    }

    private fun showRepeatConfirmDialog(
        vipUserId: Long,
        title: String,
        type: Int,
        thisAmount: Float
    ) {
        XPopup.Builder(this)
            .asCustom(ConsumeConfirmDialog(this,vipUserId,title,type,thisAmount,object : OnIntegralChangeListener {
                override fun onSuccess() {
                    setResult(200)
                    finish()
                }

                override fun onRepeatConfirm(type: Int, thisAmount: Float) {

                }
            }))
            .show()
    }

    private fun deleteVipUser(type:Int) {
        showLoadingDialog("正在删除...")
        mViewModel.deleteUserConsumeData(vipUserInfo.getUid())
        mViewModel.deleteUserConvertGoodsData(vipUserInfo.getUid())
        if (type==1) {
            mViewModel.deleteUserInfo(vipUserInfo)
            mBinding.tvUserName.postDelayed({
                dismissLoadingDialog()
                ToastUtils.show("已删除")
                setResult(200)
                finish()
            },1000)
        }else {
            vipUserInfo.setConsumeTime(System.currentTimeMillis())
            vipUserInfo.setConsumeNumber(0)
            vipUserInfo.setExchangeNumber(0f)
            vipUserInfo.setCurrentAmount(0f)
            vipUserInfo.setTotalAmount(0f)
            vipUserInfo.setTotalIntegral(0f)
            vipUserInfo.setUserIntegral(0f)
            mViewModel.updateUserInfo(vipUserInfo)
            mBinding.tvUserName.postDelayed({
                dismissLoadingDialog()
                ToastUtils.show("已清空")
                setResult(200)
                finish()
            },1000)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK&&isRrfreshHomePage) {
            setResult(200)
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun getViewBinding(): ActivityUserDetailBinding {
        return ActivityUserDetailBinding.inflate(layoutInflater)
    }
}