package com.wj.sampleproject.viewmodel

import cn.wj.android.base.databinding.BindingField
import cn.wj.android.base.ext.tagableScope
import cn.wj.android.base.tools.isNotUrl
import cn.wj.android.logger.Logger
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wj.sampleproject.R
import com.wj.sampleproject.base.mvvm.BaseViewModel
import com.wj.sampleproject.constants.EVENT_COLLECTION_REFRESH_COLLECTED_WEB
import com.wj.sampleproject.ext.snackbarMsg
import com.wj.sampleproject.model.ProgressModel
import com.wj.sampleproject.model.SnackbarModel
import com.wj.sampleproject.model.UiCloseModel
import com.wj.sampleproject.repository.CollectRepository
import kotlinx.coroutines.launch

/**
 * 编辑收藏网站 ViewModel
 *
 * - 创建时间：2019/10/17
 *
 * @author 王杰
 */
class EditCollectedWebViewModel
/**
 * @param collectRepository 收藏相关数据仓库
 */
constructor(private val collectRepository: CollectRepository)
    : BaseViewModel() {

    /** 网站 id */
    var id = ""

    /** 标题文本 */
    val titleStr = BindingField("")

    /** 网站名 */
    val webName = BindingField("")

    /** 网站链接 */
    val webLink = BindingField("")

    /** 关闭按钮点击  */
    val onCloseClick = {
        uiCloseData.postValue(UiCloseModel())
    }

    /** 消极按钮点击  */
    val onNegativeClick = {
        uiCloseData.postValue(UiCloseModel())
    }

    /** 积极按钮点击  */
    val onPositiveClick = fun() {
        if (webName.get().isNullOrBlank()) {
            snackbarData.postValue(SnackbarModel(R.string.app_please_enter_web_name))
            return
        }
        if (webLink.get().isNullOrBlank()) {
            snackbarData.postValue(SnackbarModel(R.string.app_please_enter_web_link))
            return
        }
        if (webLink.get().isNotUrl()) {
            snackbarData.postValue(SnackbarModel(R.string.app_please_enter_full_url))
            return
        }
        if (id.isBlank()) {
            // 新建
            collectWeb()
        } else {
            // 编辑
            editCollectedWeb()
        }
    }

    /**
     * 收藏网站
     */
    private fun collectWeb() {
        tagableScope.launch {
            try {
                // 显示弹窗
                progressData.postValue(ProgressModel())
                // 请求数据
                val result = collectRepository.collectWeb(webName.get().orEmpty(), webLink.get().orEmpty())
                if (result.success()) {
                    // 收藏成功，刷新
                    LiveEventBus.get(EVENT_COLLECTION_REFRESH_COLLECTED_WEB).post(Any())
                    // 关闭弹窗
                    uiCloseData.postValue(UiCloseModel())
                } else {
                    // 收藏失败，提示
                    snackbarData.postValue(SnackbarModel(result.errorMsg))
                }
            } catch (throwable: Throwable) {
                // 请求异常，提示
                snackbarData.postValue(throwable.snackbarMsg)
                Logger.t("NET").e(throwable, "collectWeb")
            } finally {
                // 隐藏弹窗
                progressData.postValue(null)
            }
        }
    }

    /**
     * 编辑收藏网站
     */
    private fun editCollectedWeb() {
        tagableScope.launch {
            try {
                // 显示弹窗
                progressData.postValue(ProgressModel())
                // 请求数据
                val result = collectRepository.editCollectedWeb(id, webName.get().orEmpty(), webLink.get().orEmpty())
                if (result.success()) {
                    // 编辑成功，刷新
                    LiveEventBus.get(EVENT_COLLECTION_REFRESH_COLLECTED_WEB).post(Any())
                    // 关闭弹窗
                    uiCloseData.postValue(UiCloseModel())
                } else {
                    // 编辑失败，提示
                    snackbarData.postValue(SnackbarModel(result.errorMsg))
                }
            } catch (throwable: Throwable) {
                // 请求异常，提示
                snackbarData.postValue(throwable.snackbarMsg)
                Logger.t("NET").e(throwable, "collectWeb")
            } finally {
                // 隐藏弹窗
                progressData.postValue(null)
            }
        }
    }
}