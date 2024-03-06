package com.jintao.vipmanager.activity

import android.content.Intent
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.R
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.database.bean.DbConvertGoodsInfo
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.helper.DbVipUserHelper
import com.jintao.vipmanager.databinding.ActivityUserDetailBinding
import com.jintao.vipmanager.dialog.ChangeIntegralDialog
import com.jintao.vipmanager.dialog.CheXiaoDialog
import com.jintao.vipmanager.dialog.CommonAllDialog
import com.jintao.vipmanager.listener.OnIntegralChangeListener
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.GeneralUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener

class UserDetailActivity:BaseActivity<ActivityUserDetailBinding>() {

    private var vipUserId:Long = -1
    private lateinit var vipUserDao: DbVipUserHelper<DbVipUserInfo>
    private lateinit var convertGoodsDao: DbVipUserHelper<DbConvertGoodsInfo>
    private lateinit var vipUserInfo:DbVipUserInfo
    private var isRrfreshHomePage = false
    private var isShowTotalAmount = false
    private var isShowUserAmount = false

    override fun initData() {
        mBinding.title.tvTitleContent.setText("会员详情")
        vipUserId = intent.getLongExtra(AppConstant.VIP_USER_ID, -1)
//        Log.e("AAAAAA",vipUserId.toString())
        vipUserDao = DbVipUserHelper(this, DbVipUserInfo::class.java).vipUserDao
        convertGoodsDao = DbVipUserHelper(this, DbConvertGoodsInfo::class.java).convertGoodsDao

        getUserInfo()
    }

    private val mUserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode==200) {
                isRrfreshHomePage = true
                getUserInfo()
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
                mBinding.tvTotalAmount.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.totalAmount)+"元")
                mBinding.ivEyeTotalStatus.setImageResource(R.mipmap.amount_eye_show_icon)
            }else {
                mBinding.tvTotalAmount.setText("******")
                mBinding.ivEyeTotalStatus.setImageResource(R.mipmap.amount_eye_hide_icon)
            }
        }
        mBinding.ivEyeUserStatus.setOnClickListener {
            isShowUserAmount = !isShowUserAmount
            if (isShowUserAmount) {
                mBinding.tvConsumeAmount.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.currentAmount)+"元")
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
            var title = vipUserInfo.userName + " "+vipUserInfo.phoneNumber
            XPopup.Builder(this)
                .asCustom(ChangeIntegralDialog(this,title,vipUserId,object :OnIntegralChangeListener{
                    override fun onSuccess() {
                        setResult(200)
                        finish()
                    }
                }))
                .show()
        }
        mBinding.rlIntegralDetail.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                val intent = Intent(this, IntegralDetailActivity::class.java)
                intent.putExtra("vip_integral_uid",vipUserInfo.uid)
                intent.putExtra("vip_integral_number",vipUserInfo.userIntegral)
                intent.putExtra("vip_amount_number",vipUserInfo.currentAmount)
                startActivity(intent)
            }
        }
        mBinding.rlIntegralDuihuan.setOnClickListener {
            val intent = Intent(this, ConvertGoodsActivity::class.java)
            intent.putExtra(AppConstant.VIP_USER_ID,vipUserId)
            intent.putExtra("vip_user_name",vipUserInfo.userName)
            intent.putExtra("vip_phone_number",vipUserInfo.phoneNumber)
            mUserLauncher.launch(intent)
        }
        mBinding.rlDuihuanDetail.setOnClickListener {
            val intent = Intent(this, ConvertDetailActivity::class.java)
            intent.putExtra(AppConstant.VIP_USER_ID,vipUserId)
            intent.putExtra("vip_integral_uid",vipUserInfo.uid)
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

    private fun getUserInfo() {
        vipUserInfo = vipUserDao.queryById(vipUserId)
        mBinding.tvUserName.setText(vipUserInfo.userName)
        mBinding.tvPhoneNumber.setText(vipUserInfo.phoneNumber)
        mBinding.tvRegisterTime.setText("注册时间:"+vipUserInfo.registerTime)
        mBinding.tvCurrentIntegral.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.userIntegral))
        mBinding.tvConsumeCount.setText(vipUserInfo.consumeNumber.toString())
        if (vipUserInfo.userSex.equals("男")) {
            mBinding.ivUserSex.setImageResource(R.mipmap.iv_square_list_sex_boy)
        }else if (vipUserInfo.userSex.equals("女")) {
            mBinding.ivUserSex.setImageResource(R.mipmap.iv_square_list_sex_girl)
        }

        if (isShowUserAmount) {
            mBinding.tvConsumeAmount.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.currentAmount)+"元")
        }
        if (isShowTotalAmount) {
            mBinding.tvTotalAmount.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.totalAmount)+"元")
        }
//        mBinding.tvConsumeAmount.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.currentAmount)+"元")
        mBinding.tvTotalIntegral.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.totalIntegral))
        var convertIntegral = 0f

        val convertGoodsList = convertGoodsDao.queryByConvertGoodsUid(vipUserInfo.uid)
        if (convertGoodsList!=null&&convertGoodsList.size!=0) {
            for (index in 0 until convertGoodsList.size) {
                val goodsInfo = convertGoodsList.get(index)
                convertIntegral += goodsInfo.useIntegral
            }
        }
        mBinding.tvDuihuanIntegral.setText(GeneralUtils.getInstence().formatAmount(convertIntegral)+"分")
        mBinding.tvDuihuanCount.setText("("+convertGoodsList.size.toString()+"次)")
    }

    private fun deleteVipUser(type:Int) {
        showLoadingDialog("正在删除...")
        val consumeUserDao = DbVipUserHelper(this, DbUserConsumeInfo::class.java).userConsumeDao
        consumeUserDao.deleteUidConsumeAll(vipUserInfo.uid)
        convertGoodsDao.deleteUidGoodsAll(vipUserInfo.uid)
        dismissLoadingDialog()
        if (type==1) {
            vipUserDao.delete(vipUserInfo)
            ToastUtils.show("已删除")
        }else {
            vipUserInfo.consumeTime = System.currentTimeMillis()
            vipUserInfo.consumeNumber = 0
            vipUserInfo.lastAmount = 0f
            vipUserInfo.currentAmount = 0f
            vipUserInfo.totalAmount = 0f
            vipUserInfo.totalIntegral = 0f
            vipUserInfo.userIntegral = 0f
            vipUserDao.update(vipUserInfo)
            ToastUtils.show("已清空")
        }

        setResult(200)
        finish()
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