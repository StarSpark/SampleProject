package com.wj.sampleproject.mvvm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.wj.android.base.ext.orEmpty
import com.wj.sampleproject.base.mvvm.BaseViewModel
import com.wj.sampleproject.entity.ArticleEntity
import com.wj.sampleproject.entity.NavigationListEntity
import com.wj.sampleproject.ext.snackbarMsg
import com.wj.sampleproject.ext.toSnackbarMsg
import com.wj.sampleproject.repository.SystemRepository
import kotlinx.coroutines.launch

/**
 * 导航 ViewModel
 */
class NavigationViewModel
/**
 * @param repository 体系相关数据仓库
 */
constructor(private val repository: SystemRepository)
    : BaseViewModel() {

    override fun onCreate(source: LifecycleOwner) {
        super.onCreate(source)

        // 获取导航数据
        getNavigationList()
    }

    /** TODO 导航条目点击 */
    val onNavigationItemClick: (ArticleEntity) -> Unit = { item ->
        snackbarData.postValue(item.title.toSnackbarMsg())
    }

    /** 列表数据 */
    val listData = MutableLiveData<ArrayList<NavigationListEntity>>()

    /**
     * 获取导航列表
     */
    private fun getNavigationList() {
        viewModelScope.launch {
            try {
                val result = repository.getNavigationList()
                if (result.success()) {
                    // 获取成功
                    listData.postValue(result.data.orEmpty())
                } else {
                    snackbarData.postValue(result.toError())
                }
            } catch (throwable: Throwable) {
                snackbarData.postValue(throwable.snackbarMsg)
            }
        }
    }
}